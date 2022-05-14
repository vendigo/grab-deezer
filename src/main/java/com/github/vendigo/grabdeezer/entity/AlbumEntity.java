package com.github.vendigo.grabdeezer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dez_album")
public class AlbumEntity {
    @Id
    private Long id;
    @Column
    private String title;
    @Column(name = "release_date")
    private LocalDate releaseDate;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "album_id")
    private List<TrackEntity> tracks;
}
