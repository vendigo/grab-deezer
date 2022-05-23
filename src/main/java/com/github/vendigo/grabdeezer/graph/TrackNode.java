package com.github.vendigo.grabdeezer.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node("Track")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackNode {
    @Id
    private Long id;
    @Property
    private String title;
    @Property
    private Integer duration;
    @Property
    private String preview;
    @Property
    private LocalDate releaseDate;
    @Relationship(type = "contribute", direction = Relationship.Direction.INCOMING)
    private List<ArtistNode> contributors;
}
