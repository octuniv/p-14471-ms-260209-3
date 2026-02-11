package com.ll.wiseSaying.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class WiseSaying {
    private final int id;
    private String body;
    private String author;
    @Setter
    private static int nextId = 1;

    public WiseSaying(String body, String author) {
        this.body = body;
        this.author = author;
        this.id = nextId++;
    }

    private WiseSaying(int id,
                       String body,
                       String author) {
        this.id = id;
        this.body = body;
        this.author = author;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> ret = new HashMap<>();
        ret.put("id", id);
        ret.put("body", body);
        ret.put("author", author);
        return ret;
    }

    public static WiseSaying fromMap(Map<String, Object> jsonParam) throws IllegalArgumentException{
        final String[] fieldKeys = {"id", "body", "author"};
        for (String key: fieldKeys) {
            if (!jsonParam.containsKey(key)) throw new IllegalArgumentException("does not have %s key in mapToWise".formatted(key));
        }
        return new WiseSaying(
                (int)jsonParam.get("id"),
                String.valueOf(jsonParam.get("body")),
                String.valueOf(jsonParam.get("author"))
        );
    }
}
