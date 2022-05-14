package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.entity.AlbumEntity;
import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import com.github.vendigo.grabdeezer.entity.ArtistQueueEntity;
import com.github.vendigo.grabdeezer.entity.TrackEntity;
import com.github.vendigo.grabdeezer.repository.ArtistQueueRepository;
import com.github.vendigo.grabdeezer.repository.ArtistRepository;
import com.github.vendigo.grabdeezer.repository.TrackRepository;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ArtistDbService {
    private final ArtistRepository artistRepository;
    private final ArtistQueueRepository artistQueueRepository;
    private final TrackRepository trackRepository;

    public void saveFullArtist(ArtistEntity artist) {
        if (artist.getAlbums().isEmpty()) {
            log.warn("Empty artist: {}, skipping", artist.getName());
            return;
        }

        artistRepository.save(artist);
        printStats(artist);
    }

    public void saveArtists(List<ArtistEntity> artists) {
        artistRepository.saveAll(artists);
    }

    public void addToQueue(Set<Long> artistId) {
        Set<Long> notPresentIds = filterLoadedAndQueuedArtistIds(artistId);
        log.info("Adding {} ids to the queue", notPresentIds.size());
        List<ArtistQueueEntity> queueItems = notPresentIds.stream()
                .map(ArtistQueueEntity::new)
                .toList();
        artistQueueRepository.saveAll(queueItems);
    }

    public Set<Long> getArtistIdsFromQueue(int chunkSize) {
        return artistQueueRepository.findArtistIds(Pageable.ofSize(chunkSize)).toSet();
    }

    public Set<Long> filterLoadedArtistIds(Set<Long> artistIds) {
        Set<Long> alreadyLoaded = artistRepository.findPresentIds(artistIds);
        return Sets.difference(artistIds, alreadyLoaded);
    }

    private Set<Long> filterLoadedAndQueuedArtistIds(Set<Long> artistIds) {
        Set<Long> remaining = filterLoadedArtistIds(artistIds);
        Set<Long> alreadyQueued = artistQueueRepository.findPresentIds(remaining);
        return Sets.difference(remaining, alreadyQueued);
    }

    public void removeFromQueue(Collection<Long> artistIds) {
        artistQueueRepository.deleteAllById(artistIds);
    }

    public long getQueueSize() {
        return artistQueueRepository.count();
    }

    public long countArtistsToTopLoad() {
        return artistRepository.countArtistIdsToTopLoad();
    }

    private void printStats(ArtistEntity artist) {
        List<AlbumEntity> albums = Optional.ofNullable(artist.getAlbums())
                .orElseGet(List::of);
        int totalTracks = albums.stream()
                .map(AlbumEntity::getTracks)
                .filter(Objects::nonNull)
                .mapToInt(List::size)
                .sum();
        log.info("Saved {}, {} albums, {} tracks", artist.getName(), albums.size(), totalTracks);
    }

    public List<ArtistEntity> getArtistsToTopLoad(int topLoadChunkSize) {
        return artistRepository.findArtistIdsToTopLoad(Pageable.ofSize(topLoadChunkSize)).toList();
    }

    public void saveTracks(List<TrackEntity> tracks) {
        trackRepository.saveAll(tracks);
    }

    public void flushArtists() {
        artistRepository.flush();
    }
}
