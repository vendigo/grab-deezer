package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.entity.TrackEntity;
import com.github.vendigo.grabdeezer.graph.ArtistNode;
import com.github.vendigo.grabdeezer.graph.TrackNode;
import com.github.vendigo.grabdeezer.graph.repository.ArtistGraphRepository;
import com.github.vendigo.grabdeezer.graph.repository.TrackGraphRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TrackGraphService {

    private final ArtistGraphRepository artistGraphRepository;
    private final TrackGraphRepository trackGraphRepository;

    void saveTracksToGraph(List<TrackEntity> tracks) {
        List<Long> artistIds = tracks.stream()
                .map(TrackEntity::getContributorsIds)
                .flatMap(Collection::stream)
                .toList();
        Map<Long, ArtistNode> artistsById = artistGraphRepository.findAllById(artistIds).stream()
                .collect(Collectors.toMap(ArtistNode::getId, Function.identity()));
        List<TrackNode> trackNodes = tracks.stream()
                .map(track -> mapTrack(track, artistsById))
                .collect(Collectors.toList());
        trackGraphRepository.saveAll(trackNodes);
    }

    private TrackNode mapTrack(TrackEntity track, Map<Long, ArtistNode> artistIds) {
        List<ArtistNode> contributors = track.getContributorsIds().stream()
                .map(artistIds::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new TrackNode(track.getId(), track.getTitle(), track.getDuration(), track.getPreview(), track.getReleaseDate(),
                contributors);
    }
}
