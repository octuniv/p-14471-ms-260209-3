package com.ll.wiseSaying.repository;

import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.entity.WiseSayingPageDto;
import com.ll.wiseSaying.utils.WiseSayingRequester;
import standard.util.jsonmanager.JSONConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WiseSayingRepository {
    private final List<WiseSaying> wiseSayings;
    private final JSONConverter converter;

    public WiseSayingRepository(JSONConverter converter) {
        this.converter = converter;
        wiseSayings = new ArrayList<>(converter.readFile().stream()
                .map(WiseSaying::fromMap).toList());
        WiseSaying.setNextId(
                wiseSayings.stream()
                        .mapToInt(WiseSaying::getId)
                        .max().orElse(0) + 1);
    }

    private void writeFile() {
        converter.writeFile(wiseSayings.stream().map(WiseSaying::toMap).toList());
    }

    public WiseSaying create(String body, String author) {
        WiseSaying addItem = new WiseSaying(body, author);
        wiseSayings.add(addItem);
        this.writeFile();
        return addItem;
    }

    public List<WiseSaying> findByInstancesDesc() {
        return wiseSayings.reversed();
    }

    public static List<WiseSaying> filteredInstancesWithSearchParam(List<WiseSaying> original, WiseSayingRequester rq) {
        String keyType = rq.getParam("keywordType", "");
        if (keyType.isEmpty()) return original;
        String key = rq.getParam("keyword", "");
        return original.stream().filter(w -> w.contain(keyType, key)).toList();
    }

    public static WiseSayingPageDto getWiseSayingDto(List<WiseSaying> original, WiseSayingRequester rq) {
        if (original.isEmpty()) return new WiseSayingPageDto(0, 0, original);
        int page = rq.getParamAsInt("page", 1);
        int lastPage = (int) Math.ceil((double) original.size() / 5);
        if (page > lastPage) page = lastPage;
        return new WiseSayingPageDto(
                page, lastPage, original.stream().skip(5L * (page - 1)).limit(5).toList()
        );
    }

    public Optional<WiseSaying> getInstanceById(int id) {
        return wiseSayings.stream().filter(w -> w.getId() == id).findFirst();
    }

    public boolean remove(int id) {
        boolean ret = wiseSayings.removeIf(w -> w.getId() == id);
        this.writeFile();
        return ret;
    }

    public void save(int id, String body, String author) {
        this.getInstanceById(id).ifPresent(w -> {
            w.setBody(body);
            w.setAuthor(author);
        });
        this.writeFile();
    }
}
