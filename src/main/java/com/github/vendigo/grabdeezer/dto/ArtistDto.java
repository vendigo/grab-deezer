package com.github.vendigo.grabdeezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ArtistDto(Long id, String name,
                        @JsonProperty("picture_medium") String picture,
                        @JsonProperty("nb_fan") Integer fans, String type, String role,
                        @JsonProperty("nb_album") Integer albums,
                        Integer position,
                        ErrorDto error) implements ErrorAware {
}