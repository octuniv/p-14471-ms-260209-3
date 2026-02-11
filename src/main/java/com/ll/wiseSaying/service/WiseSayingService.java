package com.ll.wiseSaying.service;

import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.repository.WiseSayingRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WiseSayingService {
    private final WiseSayingRepository repository;

    public WiseSaying write(String body, String author) {
        return repository.create(body, author);
    }

    public List<WiseSaying> findListDesc() {
        return repository.findByInstancesDesc();
    }

    public boolean remove(int id) {
        return repository.remove(id);
    }

    public Optional<WiseSaying> getInstanceById(int modifyId) {
        return repository.getInstanceById(modifyId);
    }

    public void modify(int id, String body, String author) {
        repository.save(id, body, author);
    }
}
