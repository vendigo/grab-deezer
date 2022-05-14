package com.github.vendigo.grabdeezer.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dez_artist_queue")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ArtistQueueEntity {
    @Id
    @Column(name = "artist_id")
    private Long artistId;
}
