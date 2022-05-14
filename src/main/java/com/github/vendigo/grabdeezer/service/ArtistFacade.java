package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.dto.ArtistDto;
import com.github.vendigo.grabdeezer.dto.TrackDto;
import com.github.vendigo.grabdeezer.entity.AlbumEntity;
import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import com.github.vendigo.grabdeezer.entity.TrackEntity;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class ArtistFacade {

    public static final int PRELOAD_CHUNK_SIZE = 45;
    private static final int TOP_LOAD_CHUNK_SIZE = 20;
    private static final int FULL_LOAD_CHUNK_SIZE = 10;
    private final ArtistDeezerService artistDeezerService;
    private final ArtistDbService artistDbService;

    @Transactional
    public boolean fullLoadArtists() {
        Set<Long> artistIds = artistDbService.getArtistIdsFromQueue(FULL_LOAD_CHUNK_SIZE);

        if (artistIds.isEmpty()) {
            log.info("No more artists to load");
            return false;
        }

        Set<Long> artistIdsToLoad = artistDbService.filterLoadedArtistIds(artistIds);
        log.info("Got {} artistId to load", artistIdsToLoad.size());

        artistIdsToLoad.forEach(this::loadArtist);
        artistDbService.removeFromQueue(artistIds);

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

    private void loadArtist(Long artistId) {
        ArtistEntity artist = artistDeezerService.loadArtist(artistId);
        artistDbService.saveFullArtist(artist);
        Set<Long> artistIdsToLoad = collectNextArtistsToLoad(artist);
        artistDbService.addToQueue(artistIdsToLoad);
    }

    private Set<Long> collectNextArtistsToLoad(ArtistEntity artist) {
        Set<Long> collabs = artist.getAlbums().stream()
                .map(AlbumEntity::getTracks)
                .flatMap(Collection::stream)
                .map(TrackEntity::getContributorsIds)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
        Set<Long> relatedArtists = artistDeezerService.getRelatedArtistIds(artist.getId());
        return Sets.union(collabs, relatedArtists);
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
}
