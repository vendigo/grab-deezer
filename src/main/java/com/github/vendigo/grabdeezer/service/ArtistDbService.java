package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import com.github.vendigo.grabdeezer.entity.TrackEntity;
import com.github.vendigo.grabdeezer.repository.ArtistQueueRepository;
import com.github.vendigo.grabdeezer.repository.ArtistRepository;
import com.github.vendigo.grabdeezer.repository.TrackRepository;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
public class ArtistDbService {
    private final ArtistRepository artistRepository;
    private final ArtistQueueRepository artistQueueRepository;
    private final TrackRepository trackRepository;

    public void saveArtists(List<ArtistEntity> artists) {
        artistRepository.saveAll(artists);
    }

    public Set<Long> getArtistIdsFromQueue(int chunkSize) {
        return artistQueueRepository.findArtistIds(Pageable.ofSize(chunkSize)).toSet();
    }

    public Set<Long> filterLoadedArtistIds(Set<Long> artistIds) {
        Set<Long> alreadyLoaded = artistRepository.findPresentIds(artistIds);
        return Sets.difference(artistIds, alreadyLoaded);
    }

    public void removeFromQueue(Collection<Long> artistIds) {
        artistQueueRepository.deleteAllById(artistIds);
    }

    public long getQueueSize() {
        return artistQueueRepository.count();
    }

    public long countArtistsToTopLoad() {
        return artistRepository.countArtists(false, false);
    }

    public List<ArtistEntity> getArtistsToTopLoad(int topLoadChunkSize) {
        return artistRepository.findArtistsToLoad(false, false,
                Pageable.ofSize(topLoadChunkSize)).toList();
    }

    public List<ArtistEntity> getArtistsToFullLoad(int chunkSize) {
        return artistRepository.findArtistsToLoad(false, true,
                Pageable.ofSize(chunkSize)).toList();
    }

    public void saveTracks(List<TrackEntity> tracks) {
        trackRepository.saveAll(tracks);
    }

    public void flushArtists() {
        artistRepository.flush();
    }

    public List<ArtistEntity> getArtistsForEnriching(int chunkSize) {
        return artistRepository.findArtistsForEnriching(Pageable.ofSize(chunkSize)).toList();
    }
}
