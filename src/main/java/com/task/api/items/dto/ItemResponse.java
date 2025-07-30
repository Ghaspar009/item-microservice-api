package com.task.api.items.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ItemResponse {

    private UUID id;
    private String name;
}
