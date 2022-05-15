package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.client.DeezerClientWrapper;
import com.github.vendigo.grabdeezer.dto.AlbumDto;
import com.github.vendigo.grabdeezer.dto.ArtistDto;
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
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@AllArgsConstructor
public class ArtistDeezerService {

    private static final int DEFAULT_PAGE_SIZE = 100;

    private final DeezerClientWrapper deezerClient;

    public ArtistEntity loadArtist(ArtistEntity artist) {
        List<Pair<AlbumDto, List<TrackDto>>> albums = deezerClient.loadAlbums(artist.getId(), DEFAULT_PAGE_SIZE)
                .data()
                .stream()
                .map(this::loadAlbumTracks)
                .toList();
        List<AlbumEntity> albumEntities = ArtistMapper.mapAlbums(albums);
        artist.setAlbums(albumEntities);
        artist.setCreatedDate(LocalDateTime.now());
        artist.setFullLoaded(true);
        return artist;
    }

    private Pair<AlbumDto, List<TrackDto>> loadAlbumTracks(AlbumDto album) {
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

    private List<TrackDto> loadArtistTopTracks(ArtistEntity artist) {
        Long artistId = artist.getId();
        artist.setTopLoaded(true);
        return deezerClient.loadTopTracks(artistId, DEFAULT_PAGE_SIZE).data();
    }


}
