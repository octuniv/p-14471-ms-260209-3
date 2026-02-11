package com.ll.wiseSaying.service;

import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.repository.WiseSayingRepository;
import com.ll.wiseSaying.utils.WiseSayingRequester;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WiseSayingService {
    private final WiseSayingRepository repository;

    public WiseSaying write(String body, String author) {
        return repository.create(body, author);
    }

    public List<WiseSaying> findListDesc(WiseSayingRequester rq) {
        String keyType = rq.getParam("keywordType", "");
        if (keyType.isEmpty()) return repository.findByInstancesDesc();
        return WiseSayingRepository.filteredInstancesWithSearchParam(repository.findByInstancesDesc(), rq);
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
