package com.github.vendigo.grabdeezer.service;

import com.github.vendigo.grabdeezer.dto.AlbumDto;
import com.github.vendigo.grabdeezer.dto.ArtistDto;
import com.github.vendigo.grabdeezer.dto.TrackDto;
import com.github.vendigo.grabdeezer.entity.AlbumEntity;
import com.github.vendigo.grabdeezer.entity.ArtistEntity;
import com.github.vendigo.grabdeezer.entity.TrackEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArtistMapper {

    public static ArtistEntity mapPreloadArtist(ArtistDto dto) {
        return new ArtistEntity(dto.id(), dto.name(), dto.picture(), dto.fans(), null,
                false, false, 0, LocalDateTime.now(), dto.albums(), false);
    }

    public static List<TrackEntity> mapTracks(List<TrackDto> tracks) {
        return tracks.stream()
                .map(ArtistMapper::mapTrack)
                .collect(Collectors.toList());
    }

    public static List<AlbumEntity> mapAlbums(List<Pair<AlbumDto, List<TrackDto>>> albums) {
        return albums.stream()
                .map(pair -> mapAlbum(pair.getFirst(), pair.getSecond()))
                .collect(Collectors.toList());
    }

    private static AlbumEntity mapAlbum(AlbumDto dto, List<TrackDto> tracks) {
        List<TrackEntity> mappedTracks = tracks.stream()
                .map(ArtistMapper::mapTrack)
                .collect(Collectors.toList());
        return new AlbumEntity(dto.id(), dto.title(), dto.releaseDate(), mappedTracks);
    }

    private static TrackEntity mapTrack(TrackDto dto) {
        return new TrackEntity(dto.id(), dto.title(), dto.duration(), dto.preview(),
                parseDate(dto.releaseDate()), mapContributorsId(dto.contributors()));
    }

    @Nullable
    private static LocalDate parseDate(String rawDate) {
        try {
            return LocalDate.parse(rawDate);
        } catch (Exception ex) {
            return null;
        }
    }

    private static List<Long> mapContributorsId(List<ArtistDto> contributors) {
        return contributors.stream().map(ArtistDto::id).collect(Collectors.toList());
    }
}
