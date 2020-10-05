package com.file.south.model;

import lombok.Data;

@Data
public abstract class File {
    private String type;

    protected abstract boolean validation(String[] args);

}
