package com.github.vendigo.grabdeezer.graph.repository;

import com.github.vendigo.grabdeezer.graph.ArtistNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ArtistGraphRepository extends Neo4jRepository<ArtistNode, Long> {
}
