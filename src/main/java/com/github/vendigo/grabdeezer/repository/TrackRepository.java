package com.github.vendigo.grabdeezer.repository;

import com.github.vendigo.grabdeezer.entity.TrackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {

    @Query(value = """
            SELECT DISTINCT dt.id from dez_track dt
            JOIN dez_contributor dc1 ON dt.id = dc1.track_id
            JOIN dez_contributor dc2 ON dt.id = dc2.track_id AND dc1.artist_id <> dc2.artist_id
            ORDER BY dt.id
            """
            , nativeQuery = true, countName = "countTracksForGraphLoading")
    Page<BigInteger> findTrackIdsForGraphLoading(Pageable pageable);

    @Query(value = """
            SELECT count(DISTINCT dt.id) from dez_track dt
            JOIN dez_contributor dc1 ON dt.id = dc1.track_id
            JOIN dez_contributor dc2 ON dt.id = dc2.track_id AND dc1.artist_id <> dc2.artist_id
            """
            , nativeQuery = true)
    long countTracksForGraphLoading();
}
