package com.ll.wiseSaying;

import com.ll.wiseSaying.iohandler.IOHandler;
import com.ll.wiseSaying.controller.WiseSayingController;
import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.repository.WiseSayingRepository;
import com.ll.wiseSaying.service.WiseSayingService;
import com.ll.wiseSaying.utils.WiseSayingRequester;
import lombok.Getter;
import standard.util.jsonmanager.JSONConverter;

@Getter
public class App implements AutoCloseable{
    private final IOHandler io;
    private final JSONConverter converter;
    private final WiseSayingController controller;
    private final WiseSayingService service;
    private final WiseSayingRepository repository;

    public App(IOHandler io, JSONConverter converter) {
        this.io = io;
        this.converter = converter;
        this.repository = new WiseSayingRepository(converter);
        this.service = new WiseSayingService(repository);
        this.controller = new WiseSayingController(service, io);
    }

    public void run() {
        label:
        while (true) {
            io.print("명령) ");
            WiseSayingRequester rq = new WiseSayingRequester(io.readLine().trim());
            String cmd = rq.getActionName();

            switch (cmd) {
                case "종료":
                    break label;
                case "등록":
                    this.controller.register();
                    break;
                case "목록":
                    this.controller.list();
                    break;
                case "삭제":
                    this.controller.remove(rq);
                    break;
                case "수정":
                    this.controller.modify(rq);
                    break;
            }
        }
    }

    @Override
    public void close() throws Exception {
        WiseSaying.setNextId(1);
        this.converter.close();
    }
}
