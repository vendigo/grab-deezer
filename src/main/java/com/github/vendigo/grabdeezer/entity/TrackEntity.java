package com.github.vendigo.grabdeezer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static com.github.vendigo.grabdeezer.service.ArtistFacade.TRACKS_GRAPH_LOAD_CHUNK_SIZE;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Table(name = "dez_track")
public class TrackEntity {
    @Id
    private Long id;
    @Column
    private String title;
    @Column
    private Integer duration;
    @Column
    private String preview;
    @Column(name = "release_date")
    private LocalDate releaseDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = TRACKS_GRAPH_LOAD_CHUNK_SIZE)
    @CollectionTable(
            name = "dez_contributor",
            joinColumns = @JoinColumn(name = "track_id")
    )
    @Column(name = "artist_id")
    private List<Long> contributorsIds;
}
