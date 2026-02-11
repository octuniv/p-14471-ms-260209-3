package com.ll.wiseSaying;

import jsonmanager.DummyJsonConverter;
import com.ll.wiseSaying.iohandler.TestIOHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import standard.util.jsonmanager.JSONConverter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    @Test
    @DisplayName("명령 : 등록")
    void registerTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "종료"
        );
        try (
                JSONConverter converter= new DummyJsonConverter();
                App app = new App(io, converter);) {
            app.run();
        }

        List<String> outputs = io.getOutputs();
        assertThat(outputs).contains("명언 :")
                .contains("작가 :")
                .contains("1번 명언이 등록되었습니다.");
    }

    @Test
    @DisplayName("명령 : 목록")
    void listTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "등록",
                "과거에 집착하지 마라.",
                "작자미상",
                "목록",
                "종료"
        );
        try (
                JSONConverter converter= new DummyJsonConverter();
                App app = new App(io, converter);) {
            app.run();
        }

        List<String> outputs = io.getOutputs();
        assertThat(outputs).contains("번호 / 작가 / 명언")
                .contains("----------------------")
                .contains("2 / 작자미상 / 과거에 집착하지 마라.")
                .contains("1 / 작자미상 / 현재를 사랑하라.");
    }

    @Test
    @DisplayName("명령 : 삭제")
    void deleteTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "목록",
                "삭제?id=1",
                "삭제?id=1",
                "목록",
                "종료"
        );
        try (
                JSONConverter converter= new DummyJsonConverter();
                App app = new App(io, converter);) {
            app.run();
        }

        List<String> outputs = io.getOutputs();
        long count = outputs.stream()
                .filter("1 / 작자미상 / 현재를 사랑하라."::equals)
                .count();
        assertThat(count).isEqualTo(1);
        assertThat(outputs).contains("1번 명언이 삭제되었습니다.")
                .contains("1번 명언은 존재하지 않습니다.");
    }

    @Test
    @DisplayName("명령 : 수정")
    void modifyTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "목록",
                "수정?id=5",
                "수정?id=1",
                "수정된 명언의 내용",
                "수정된 명언의 저자",
                "목록",
                "종료"
        );
        try (
                JSONConverter converter= new DummyJsonConverter();
                App app = new App(io, converter);) {
            app.run();
        }

        List<String> outputs = io.getOutputs();
        assertThat(outputs).contains("1 / 작자미상 / 현재를 사랑하라.")
                .contains("5번 명언은 존재하지 않습니다.")
                .contains("명언(기존) : 현재를 사랑하라.")
                .contains("작가(기존) : 작자미상")
                .contains("1 / 수정된 명언의 저자 / 수정된 명언의 내용");

    }
}
