package com.ll.wiseSaying.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class WiseSayingPageDto {
    private final int presentPage;
    private final int lastPage;
    private final List<WiseSaying> pagedWises;
}
