package com.ll.wiseSaying.controller;

import com.ll.wiseSaying.iohandler.IOHandler;
import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.service.WiseSayingService;
import com.ll.wiseSaying.utils.WiseSayingRequester;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class WiseSayingController {
    private final WiseSayingService service;
    private final IOHandler io;

    public void register() {
        String body;
        String author;

        io.print("명언 :");
        body = io.readLine();
        io.print("작가 :");
        author = io.readLine();

        WiseSaying wise = service.write(body, author);
        io.println("%d번 명언이 등록되었습니다.".formatted(wise.getId()));
    }

    public void list() {
        List<WiseSaying> wises = service.findListDesc();
        io.println("번호 / 작가 / 명언");
        io.println("----------------------");
        wises.forEach(wise -> io.println(
                "%d / %s / %s".formatted(
                        wise.getId(),
                        wise.getAuthor(),
                        wise.getBody()
                )
        ));
    }

    public void remove(WiseSayingRequester rq) throws IllegalArgumentException {
        int removeId = rq.getParamAsInt("id", -1);
        if (removeId == -1) throw new IllegalArgumentException("Invalid removeId");
        boolean ret = this.service.remove(removeId);
        if (ret) io.println("%d번 명언이 삭제되었습니다.".formatted(removeId));
        else io.println("%d번 명언은 존재하지 않습니다.".formatted(removeId));
    }

    public void modify(WiseSayingRequester rq) throws IllegalArgumentException {
        int modifyId = rq.getParamAsInt("id", -1);
        if (modifyId == -1) throw new IllegalArgumentException("Invalid modifyId");
        Optional<WiseSaying> instance = this.service.getInstanceById(modifyId);
        if (instance.isEmpty()) {
            io.println("%d번 명언은 존재하지 않습니다.".formatted(modifyId));
            return;
        }

        String body, author;
        io.println("명언(기존) : %s".formatted(instance.get().getBody()));
        io.print("명언 :");
        body = io.readLine();
        io.println("작가(기존) : %s".formatted(instance.get().getAuthor()));
        io.print("작가 :");
        author = io.readLine();
        this.service.modify(modifyId, body, author);
    }


}
