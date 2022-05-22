package com.github.vendigo.grabdeezer.repository;

import com.github.vendigo.grabdeezer.entity.TrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {

    @Query("SELECT track FROM TrackEntity track WHERE size(track.contributorsIds) > 1")
    Page<TrackEntity> findTracksForGraphLoading(Pageable pageable);
}
