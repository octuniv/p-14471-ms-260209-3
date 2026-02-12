package com.ll.wiseSaying;

import com.ll.wiseSaying.iohandler.TestIOHandler;
import jsonmanager.DummyJsonConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import standard.util.jsonmanager.FileJSONConverter;
import standard.util.jsonmanager.JSONConverter;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static java.util.Collections.frequency;
import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private static final String testPath = "db/test";
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
    @DisplayName("명령 : 목록, 기본 페이징")
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
                .contains("1 / 작자미상 / 현재를 사랑하라.")
                .contains("페이지 : [1]");
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

    @Test
    @DisplayName("목록, 검색 기능 테스트")
    void searchList() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "등록",
                "과거에 집착하지 마라.",
                "작자미상",
                "목록?keywordType=content&keyword=과거",
                "목록?keywordType=author&keyword=작자",
                "종료"
        );
        try (
                JSONConverter converter = new DummyJsonConverter();
                App app = new App(io, converter);
        ) {
            app.run();
        }

        List<String> outputs = io.getOutputs();

        // 등록 성공 메시지 검증
        assertThat(outputs).contains("1번 명언이 등록되었습니다.")
                .contains("2번 명언이 등록되었습니다.");

        // 첫 번째 검색 (content 기반) 결과 검증
        assertThat(outputs).contains("검색타입 : content")
                .contains("검색어 : 과거")
                .contains("2 / 작자미상 / 과거에 집착하지 마라."); // 첫 검색엔 포함되지 않아야 함

        // 두 번째 검색 (author 기반) 결과 검증 - 역순 정렬 확인
        assertThat(outputs).contains("검색타입 : author")
                .contains("검색어 : 작자")
                .containsSequence(
                        "2 / 작자미상 / 과거에 집착하지 마라.",
                        "1 / 작자미상 / 현재를 사랑하라."
                );

        // 첫 번째 명언이 첫 번째 검색 결과에는 없고, 두 번째 검색 결과에만 존재함을 확인
        int firstQuoteIndex = outputs.indexOf("1 / 작자미상 / 현재를 사랑하라.");
        int secondSearchStartIndex = outputs.indexOf("검색타입 : author");

        assertThat(firstQuoteIndex)
                .isGreaterThan(secondSearchStartIndex);
    }

    @Test
    @DisplayName("명령 : 목록, 페이징 리스트 출력")
    void pagedListTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "목록",
                "목록?page=2",
                "종료"
        );
        List<Map<String, Object>> wises = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> Map.<String, Object>of(
                        "id", i,
                        "author", "작자미상 %d".formatted(i),
                        "body", "명언 %d".formatted(i)))
                .toList();
        try (JSONConverter converter= new FileJSONConverter(true, testPath);) {
            converter.writeFile(wises);
            App app = new App(io, converter);
            app.run();
            app.close();
        }

        List<String> outputs = io.getOutputs();

        List<String> allWisdomLines = IntStream.rangeClosed(1, 10)
                .mapToObj(i -> "%d / 작자미상 %d / 명언 %d".formatted(i, i, i))
                .toList();

        allWisdomLines.forEach(line ->
                assertThat(frequency(outputs, line))
                        .as("명언 '%s'는 정확히 1회만 등장해야 함", line)
                        .isEqualTo(1)
        );

        assertThat(outputs).containsSequence(
                "번호 / 작가 / 명언",
                "----------------------",
                "10 / 작자미상 10 / 명언 10",
                "9 / 작자미상 9 / 명언 9",
                "8 / 작자미상 8 / 명언 8",
                "7 / 작자미상 7 / 명언 7",
                "6 / 작자미상 6 / 명언 6",
                "----------------------",
                "페이지 : [1] / 2"
        );

        assertThat(outputs).containsSequence(
                "번호 / 작가 / 명언",
                "----------------------",
                "5 / 작자미상 5 / 명언 5",
                "4 / 작자미상 4 / 명언 4",
                "3 / 작자미상 3 / 명언 3",
                "2 / 작자미상 2 / 명언 2",
                "1 / 작자미상 1 / 명언 1",
                "----------------------",
                "페이지 : 1 / [2]"
        );

        int idxPage1Last = outputs.indexOf("6 / 작자미상 6 / 명언 6");
        int idxPage2First = outputs.indexOf("5 / 작자미상 5 / 명언 5");
        assertThat(idxPage1Last)
                .as("페이지 1의 마지막 명언(6번)은 페이지 2의 첫 명언(5번)보다 앞에 있어야 함")
                .isLessThan(idxPage2First);
    }

    @Test
    @DisplayName("명령 : 목록, 빈 목록")
    void emptyList() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "목록",
                "종료"
        );

        try (
                JSONConverter converter= new DummyJsonConverter();
                App app = new App(io, converter);) {
            app.run();
        }

        List<String> outputs = io.getOutputs();
        assertThat(outputs)
                .noneMatch(output -> output.matches(".*페이지 : \\d+ / \\[\\d+\\].*"));
    }

}
