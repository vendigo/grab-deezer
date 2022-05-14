package com.github.vendigo.grabdeezer.dto;

import java.util.List;

public record ResultDto<T>(List<T> data, Integer total, String next, ErrorDto error) implements ErrorAware {
}
