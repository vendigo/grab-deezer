package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.client.DeezerClientWrapper;
import com.github.vendigo.grabdeezer.dto.AlbumDto;
import com.github.vendigo.grabdeezer.dto.ArtistDto;
import com.github.vendigo.grabdeezer.dto.ResultDto;
import com.github.vendigo.grabdeezer.dto.TrackDto;
import com.github.vendigo.grabdeezer.entity.AlbumEntity;
import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ArtistDeezerService {

    private static final int DEFAULT_PAGE_SIZE = 100;

    private final DeezerClientWrapper deezerClient;

    public ArtistEntity loadArtist(ArtistEntity artist) {
        try {
            List<AlbumDto> albumDtos = loadAlbumDtos(artist.getId());
            List<AlbumEntity> albumEntities = loadAlbums(albumDtos);
            artist.setAlbums(albumEntities);
            artist.setLastUpdateTime(LocalDateTime.now());
            artist.setFullLoaded(true);
        } catch (Exception ex) {
            artist.setFailedToLoad(true);
        }

        return artist;
    }

    private List<AlbumEntity> loadAlbums(List<AlbumDto> albumDtos) {
        List<Pair<AlbumDto, List<TrackDto>>> albums = albumDtos
                .stream()
                .map(this::loadAlbum)
                .toList();
        return ArtistMapper.mapAlbums(albums);
    }

    private List<AlbumDto> loadAlbumDtos(Long artistId) {
        List<AlbumDto> albums = new ArrayList<>();
        ResultDto<AlbumDto> result;
        int page = 0;

        do {
            result = deezerClient.loadAlbums(artistId, DEFAULT_PAGE_SIZE, page++);
            albums.addAll(result.data());
        } while (result.next() != null);

        return albums;
    }

    private Pair<AlbumDto, List<TrackDto>> loadAlbum(AlbumDto album) {
        List<TrackDto> tracks = deezerClient.loadTracks(album.id()).data().stream()
                .map(track -> deezerClient.loadTrack(track.id()))
                .toList();
        return Pair.of(album, tracks);
    }

    public ArtistDto preloadArtist(long artistId) {
        return deezerClient.loadArtist(artistId);
    }

    public List<ArtistEntity> preloadArtists(Set<Long> artistsId) {
        return artistsId.stream()
                .map(deezerClient::loadArtist)
                .map(ArtistMapper::mapPreloadArtist)
                .toList();
    }

    public List<TrackDto> loadArtistsTopTracks(List<ArtistEntity> artists) {
        return Flux.fromIterable(artists)
                .publishOn(Schedulers.boundedElastic())
                .parallel(3)
                .map(this::loadArtistTopTracks)
                .flatMap(Flux::fromIterable)
                .sequential()
                .collectList()
                .block();
    }

    public List<ArtistDto> loadChartArtists(int page) {
        return deezerClient.loadChartArtists(DEFAULT_PAGE_SIZE, page).data();
    }

    private List<TrackDto> loadArtistTopTracks(ArtistEntity artist) {
        Long artistId = artist.getId();
        artist.setTopLoaded(true);
        return deezerClient.loadTopTracks(artistId, DEFAULT_PAGE_SIZE).data();
    }

    public ArtistEntity updateArtist(ArtistEntity artist) {
        log.info("About to update artist: {}", artist.getName());
        ArtistDto latestDto = deezerClient.loadArtist(artist.getId());

        artist.setFansCount(latestDto.fans());
        artist.setName(latestDto.name());
        artist.setPicture(latestDto.picture());
        artist.setAlbumsCount(latestDto.albums());

        try {
            Set<Long> existentAlbumsIds = artist.getAlbums().stream()
                    .map(AlbumEntity::getId)
                    .collect(Collectors.toSet());
            List<AlbumDto> newAlbumDtos = loadAlbumDtos(artist.getId()).stream()
                    .filter(albumDto -> !existentAlbumsIds.contains(albumDto.id()))
                    .filter(albumDto -> albumDto.releaseDate().isAfter(artist.getLastUpdateTime().toLocalDate()))
                    .toList();
            if (!newAlbumDtos.isEmpty()) {
                List<AlbumEntity> newAlbums = loadAlbums(newAlbumDtos);
                artist.getAlbums().addAll(newAlbums);
                log.info("Loaded {} new albums", newAlbums.size());
            } else {
                log.info("No new albums found");
            }
        } catch (Exception ex) {
            log.error("Unable to update artist: {}) {}", artist.getId(), artist.getName(), ex);
        }


        artist.setLastUpdateTime(LocalDateTime.now());
        return artist;
    }
}
