package com.github.vendigo.grabdeezer.dto;

import javax.annotation.Nullable;

public interface ErrorAware {

    @Nullable
    ErrorDto error();
}
