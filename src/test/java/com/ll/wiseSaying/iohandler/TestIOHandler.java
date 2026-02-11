package com.ll.wiseSaying.iohandler;

import lombok.Getter;

import java.util.*;

public class TestIOHandler implements IOHandler {
    private final Queue<String> inputs;
    @Getter
    private final List<String> outputs = new ArrayList<>();

    public TestIOHandler(String... inputs) {
        this.inputs = new LinkedList<>(Arrays.asList(inputs));
    }

    @Override
    public String readLine() {
        return inputs.poll();
    }

    @Override
    public void print(String text) {
        outputs.add(text);
    }

    @Override
    public void println(String text) {
        outputs.add(text);
    }

}