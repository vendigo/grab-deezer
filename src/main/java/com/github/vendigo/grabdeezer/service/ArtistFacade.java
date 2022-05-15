package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.dto.ArtistDto;
import com.github.vendigo.grabdeezer.dto.TrackDto;
import com.github.vendigo.grabdeezer.entity.AlbumEntity;
import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import com.github.vendigo.grabdeezer.entity.TrackEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ArtistFacade {

    public static final int PRELOAD_CHUNK_SIZE = 45;
    public static final int FULL_LOAD_CHUNK_SIZE = 1;
    public static final int ENRICH_FANS_CHUNK_SIZE = 50;
    private static final int TOP_LOAD_CHUNK_SIZE = 20;
    private final ArtistDeezerService artistDeezerService;
    private final ArtistDbService artistDbService;

    @Transactional
    public boolean fullLoadArtists() {
        List<ArtistEntity> artists = artistDbService.getArtistsToFullLoad(FULL_LOAD_CHUNK_SIZE);

        if (artists.isEmpty()) {
            log.info("No more artists to load");
            return false;
        }

        List<ArtistEntity> fullArtists = artists.stream()
                .map(artistDeezerService::loadArtist)
                .peek(ArtistFacade::printStats)
                .collect(Collectors.toList());
        artistDbService.saveArtists(fullArtists);

        return true;
    }

    @Transactional
    public long preloadArtists() {
        Set<Long> artistIds = artistDbService.getArtistIdsFromQueue(PRELOAD_CHUNK_SIZE);

        if (artistIds.isEmpty()) {
            log.info("No more artists to preload");
            return 0;
        }

        List<ArtistEntity> preloadedArtists = artistDeezerService.preloadArtists(artistIds);
        artistDbService.saveArtists(preloadedArtists);
        artistDbService.removeFromQueue(artistIds);
        return artistDbService.getQueueSize();
    }

    @Transactional
    public long topLoadArtists(boolean loadMissing) {
        List<ArtistEntity> artists = artistDbService.getArtistsToTopLoad(TOP_LOAD_CHUNK_SIZE);

        if (artists.isEmpty()) {
            log.info("No more artists to load");
            return 0;
        }

        List<TrackDto> topTracks = artistDeezerService.loadArtistsTopTracks(artists);
        List<TrackEntity> trackEntities = ArtistMapper.mapTracks(topTracks);
        artistDbService.saveTracks(trackEntities);
        log.info("Top loaded {} artists", TOP_LOAD_CHUNK_SIZE);
        int savedNew = loadMissing ? saveMissingAsPreloaded(topTracks) : 0;
        artistDbService.flushArtists();
        long artistsToLoad = artistDbService.countArtistsToTopLoad();
        log.info("{} new artists saved, {} to process", savedNew, artistsToLoad);
        return artistsToLoad;
    }

    @Transactional
    public boolean enrichArtists() {
        List<ArtistEntity> artists = artistDbService.getArtistsForEnriching(ENRICH_FANS_CHUNK_SIZE);

        if (artists.isEmpty()) {
            return false;
        }

        artists.forEach(artist -> {
            ArtistDto artistDto = artistDeezerService.preloadArtist(artist.getId());
            if (artistDto != null) {
                artist.setFansCount(artistDto.fans());
                artist.setAlbumsCount(artistDto.albums());
            }
        });

        return true;
    }

    private int saveMissingAsPreloaded(List<TrackDto> tracks) {
        Map<Long, ArtistDto> artistsById = tracks.stream()
                .map(TrackDto::contributors)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ArtistDto::id, Function.identity(), (l, r) -> r));
        Set<Long> missingArtistIds = artistDbService.filterLoadedArtistIds(artistsById.keySet());
        List<ArtistEntity> artistsToSave = artistsById.values().stream()
                .filter(artist -> missingArtistIds.contains(artist.id()))
                .map(ArtistMapper::mapPreloadArtist)
                .collect(Collectors.toList());
        artistDbService.saveArtists(artistsToSave);
        return artistsToSave.size();
    }

    private static void printStats(ArtistEntity artist) {
        List<AlbumEntity> albums = Optional.ofNullable(artist.getAlbums())
                .orElseGet(List::of);
        int totalTracks = albums.stream()
                .map(AlbumEntity::getTracks)
                .filter(Objects::nonNull)
                .mapToInt(List::size)
                .sum();
        log.info("Saved {}, {} albums, {} tracks", artist.getName(), albums.size(), totalTracks);
    }
}
