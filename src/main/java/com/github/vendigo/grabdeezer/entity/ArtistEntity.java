package com.github.vendigo.grabdeezer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dez_artist")
public class ArtistEntity {
    @Id
    @Column
    private Long id;
    @Column
    private String name;
    @Column
    private String picture;
    @Column(name = "nb_fans")
    private Integer fansCount;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private List<AlbumEntity> albums;
    @Column(name = "full_loaded")
    private boolean fullLoaded;
    @Column(name = "top_loaded")
    private boolean topLoaded;
    @Column
    private int priority;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "nb_albums")
    private Integer albumsCount;
    @Column(name = "loaded_to_graph")
    private boolean graphLoaded;
}
