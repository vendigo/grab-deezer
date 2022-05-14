package com.github.vendigo.grabdeezer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public record AlbumDto(Long id, String title,
                       @JsonProperty("release_date") LocalDate releaseDate, ErrorDto error) implements ErrorAware {
}
