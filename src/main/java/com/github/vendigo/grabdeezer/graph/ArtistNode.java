package com.github.vendigo.grabdeezer.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("Artist")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistNode {
    @Id
    private Long id;
    @Property
    private String name;
    @Property
    private String picture;
    @Property
    private Integer fans;
}
