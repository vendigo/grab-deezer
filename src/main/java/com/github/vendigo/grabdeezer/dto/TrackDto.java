package com.github.vendigo.grabdeezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TrackDto(Long id, String title, Integer duration, Integer rank, String preview,
                       @JsonProperty("release_date") String releaseDate,
                       List<ArtistDto> contributors, ErrorDto error) implements ErrorAware {
}
