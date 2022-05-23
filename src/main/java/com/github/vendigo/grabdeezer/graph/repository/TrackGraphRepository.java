package com.github.vendigo.grabdeezer.graph.repository;

import com.github.vendigo.grabdeezer.graph.TrackNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface TrackGraphRepository extends Neo4jRepository<TrackNode, Long> {
}
