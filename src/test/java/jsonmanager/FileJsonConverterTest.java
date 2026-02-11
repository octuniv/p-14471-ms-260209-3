package jsonmanager;

import com.ll.wiseSaying.App;
import com.ll.wiseSaying.iohandler.TestIOHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import standard.util.jsonmanager.FileJSONConverter;
import standard.util.jsonmanager.JSONConverter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class FileJsonConverterTest {
    private static final String testPath = "db/test";

    @AfterEach
    void cleanup() {
        try {
            Files.deleteIfExists(Paths.get(testPath + "/data.json"));
        } catch (IOException e) {
            // 무시
        }
    }

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
                JSONConverter converter= new FileJSONConverter(true, testPath);
                App app = new App(io, converter);) {
            app.run();
            List<Map<String, Object>> result = converter.readFile();

            assertThat(result)
                    .hasSize(1) // 1개의 명언만 등록되었는지 확인
                    .first()
                    .satisfies(map -> {
                        assertThat(map).containsEntry("id", 1); // id는 정수 타입 주의
                        assertThat(map).containsEntry("body", "현재를 사랑하라."); // 입력값 그대로(마침표 포함)
                        assertThat(map).containsEntry("author", "작자미상");
                        assertThat(map.keySet()).containsExactlyInAnyOrder("id", "body", "author");
                    });
        }
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
                JSONConverter converter= new FileJSONConverter(true, testPath);
                App app = new App(io, converter);) {
            app.run();

            List<Map<String, Object>> result = converter.readFile();
            assertThat(result)
                    .hasSize(0);
        }
    }

    @Test
    @DisplayName("명령 : 수정")
    void modifyTest() throws Exception {
        TestIOHandler io = new TestIOHandler(
                "등록",
                "현재를 사랑하라.",
                "작자미상",
                "등록",
                "그래도 지구는 돈다.",
                "갈릴레이",
                "목록",
                "수정?id=5",
                "수정?id=1",
                "수정된 명언의 내용",
                "수정된 명언의 저자",
                "목록",
                "종료"
        );
        try (
                JSONConverter converter= new FileJSONConverter(true, testPath);
                App app = new App(io, converter);) {
            app.run();
            List<Map<String, Object>> result = converter.readFile();

            assertThat(result).hasSize(2);

            assertThat(result)
                    .filteredOn(map -> map.get("id").equals(1))
                    .hasSize(1)
                    .first()
                    .satisfies(map -> {
                        assertThat(map)
                                .containsEntry("id", 1)
                                .containsEntry("body", "수정된 명언의 내용")
                                .containsEntry("author", "수정된 명언의 저자");
                        assertThat(map.keySet())
                                .containsExactlyInAnyOrder("id", "body", "author");
                    });

            assertThat(result)
                    .filteredOn(map -> map.get("id").equals(2))
                    .hasSize(1)
                    .first()
                    .satisfies(map -> {
                        assertThat(map)
                                .containsEntry("id", 2)
                                .containsEntry("body", "그래도 지구는 돈다.")
                                .containsEntry("author", "갈릴레이");
                        assertThat(map.keySet())
                                .containsExactlyInAnyOrder("id", "body", "author");
                    });
        }
    }

    @Test
    @DisplayName("명령 : 목록, 파일로 저장된 목록 표기")
    void listTest() throws Exception {
        TestIOHandler io = new TestIOHandler("목록", "종료");

        try (JSONConverter converter = new FileJSONConverter(true, testPath)) {
            // 1. 사전 데이터 저장 (동일 인스턴스 사용)
            converter.writeFile(List.of(
                    Map.of("id", 1, "author", "작자미상", "body", "현재를 사랑하라."),
                    Map.of("id", 2, "author", "작자미상", "body", "과거에 집착하지 마라.")
            ));

            // 2. 앱 실행 (동일 인스턴스 재사용)
            App app = new App(io, converter);
            app.run();
        } // → close()는 테스트 종료 시점에 한 번만 호출 → 디렉터리 삭제

        // 3. 출력 검증 (순서까지 정확히 확인)
        List<String> outputs = io.getOutputs();
        assertThat(outputs)
                .containsSequence(
                        "번호 / 작가 / 명언",
                        "----------------------",  // ID 오름차순 정렬 가정
                        "2 / 작자미상 / 과거에 집착하지 마라.",
                        "1 / 작자미상 / 현재를 사랑하라."
                        );
    }

}
