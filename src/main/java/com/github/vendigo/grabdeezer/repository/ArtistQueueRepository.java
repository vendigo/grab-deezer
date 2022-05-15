package com.github.vendigo.grabdeezer.repository;

import com.github.vendigo.grabdeezer.entity.ArtistQueueEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ArtistQueueRepository extends JpaRepository<ArtistQueueEntity, Long> {
    @Query("SELECT queue.artistId FROM ArtistQueueEntity queue")
    Page<Long> findArtistIds(Pageable pageable);
}
