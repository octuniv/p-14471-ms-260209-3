package util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import standard.util.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilJsonTest {

    @Test
    @DisplayName("Map을 Json으로 바꿀 수 있다.")
    void t1() {
        // given

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", 1);
        map.put("name", "홍길동");
        map.put("age", 20);

        // when
        String jsonStr = Util.json.toString(map);

        // then
        assertThat(jsonStr).isEqualTo(
                """
                        {
                            "id": 1,
                            "name": "홍길동",
                            "age": 20
                        }"""
        );

    }


    @Test
    @DisplayName("Json을 map으로 바꿀 수 있다.")
    void t2() {
        // given
        String jsonStr = """
                {
                    "id": 1,
                    "name": "홍길동",
                    "age": 20
                }""";

        // when
        Map<String, Object> map = Util.json.toMap(jsonStr);

        // then
        assertThat(map)
                .containsEntry("id", 1)
                .containsEntry("name", "홍길동")
                .containsEntry("age", 20);

    }

    @Test
    @DisplayName("List<Map>를 Json으로 바꿀 수 있다.")
    void t3() {
        List<Map<String, Object>> listedMap = new ArrayList<>();
        Map<String, Object> m1, m2;
        m1 = new LinkedHashMap<>();
        m1.put("id", 1);
        m1.put("name", "홍길동");
        m1.put("age", 20);
        m2 = new LinkedHashMap<>();
        m2.put("id", 2);
        m2.put("name", "김길동");
        m2.put("age", 40);
        listedMap.add(m1);
        listedMap.add(m2);

        String listedJsonStr = Util.json.toString(listedMap);

        assertThat(listedJsonStr).isEqualTo(
                """
                        [
                            {
                                "id": 1,
                                "name": "홍길동",
                                "age": 20
                            },
                            {
                                "id": 2,
                                "name": "김길동",
                                "age": 40
                            }
                        ]
                        """.stripIndent().stripTrailing()
        );
    }

    @Test
    @DisplayName("Json을 List<Map>로 바꿀 수 있다.")
    void t4() {
        String listedJsonStr =
                """
                        [
                            {
                                "id": 1,
                                "name": "홍길동",
                                "age": 20
                            },
                            {
                                "id": 2,
                                "name": "김길동",
                                "age": 40
                            }
                        ]
                        """;
        List<Map<String, Object>> listedMap = Util.json.toListedMap(listedJsonStr);

        assertThat(listedMap).hasSize(2);
        assertThat(listedMap.get(0))
                .containsEntry("id", 1)
                .containsEntry("name", "홍길동")
                .containsEntry("age", 20);
        assertThat(listedMap.get(1))
                .containsEntry("id", 2)
                .containsEntry("name", "김길동")
                .containsEntry("age", 40);
    }
}