package com.github.vendigo.grabdeezer.repository;

import com.github.vendigo.grabdeezer.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
}
