package com.github.vendigo.grabdeezer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(
            name = "dez_contributor",
            joinColumns = @JoinColumn(name = "track_id")
    )
    @Column(name = "artist_id")
    private List<Long> contributorsIds;
}
