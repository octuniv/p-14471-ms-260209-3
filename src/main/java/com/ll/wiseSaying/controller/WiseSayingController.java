package com.ll.wiseSaying.controller;

import com.ll.wiseSaying.entity.WiseSayingPageDto;
import com.ll.wiseSaying.iohandler.IOHandler;
import com.ll.wiseSaying.entity.WiseSaying;
import com.ll.wiseSaying.service.WiseSayingService;
import com.ll.wiseSaying.utils.WiseSayingRequester;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public void list(WiseSayingRequester rq) {
        this.searchParamView(rq);
        WiseSayingPageDto wiseDto = service.findListDesc(rq);
        List<WiseSaying> wiseList = wiseDto.getPagedWises();
        this.wiseListView(wiseList);
        this.pageView(wiseDto);
    }

    private void wiseListView(List<WiseSaying> wiseList) {
        io.println("번호 / 작가 / 명언");
        io.println("----------------------");
        wiseList.forEach(wise -> io.println(
                "%d / %s / %s".formatted(
                        wise.getId(),
                        wise.getAuthor(),
                        wise.getBody()
                )
        ));
    }

    private void searchParamView(WiseSayingRequester rq) {
        String keyType = rq.getParam("keywordType", "");
        if (!keyType.isEmpty()) {
            String key = rq.getParam("keyword", "");
            io.println("----------------------");
            io.println("검색타입 : %s".formatted(keyType));
            io.println("검색어 : %s".formatted(key));
            io.println("----------------------");
        }
    }

    private void pageView(WiseSayingPageDto wiseDto) {
        int page = wiseDto.getPresentPage();
        int lastPage = wiseDto.getLastPage();
        if (lastPage == 0) return;
        io.println("----------------------");
        io.println(IntStream.rangeClosed(1, lastPage)
                .mapToObj(i -> {
                    StringBuilder ret = new StringBuilder();
                    if (i == 1) ret.append("페이지 : ");
                    if (i == page) ret.append("[%d]".formatted(i));
                    else ret.append("%d".formatted(i));
                    if (i != lastPage) ret.append(" / ");
                    return ret.toString();
                }).collect(Collectors.joining()));
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
