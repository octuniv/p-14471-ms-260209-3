package com.ll.wiseSaying;

import com.ll.wiseSaying.utils.WiseSayingRequester;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RequestTest {
    @Test
    @DisplayName("getActionName(\"삭제?id=1\") : 삭제")
    void t1() {

        WiseSayingRequester rq = new WiseSayingRequester("삭제?id=1");

        String actionName = rq.getActionName(); // 삭제

        assertThat(actionName).isEqualTo("삭제");
    }

    @Test
    @DisplayName("getActionName(\"수정?id=1\") : 수정")
    void t2() {

        WiseSayingRequester rq = new WiseSayingRequester("수정?id=1");

        String actionName = rq.getActionName(); // 수정

        assertThat(actionName).isEqualTo("수정");
    }

    @Test
    @DisplayName("입력값 : \"등록?이름=홍길동\" : getParam(\"이름\"): 홍길동")
    void t3() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?이름=홍길동");

        String paramValue = rq.getParam("이름", ""); // 홍길동

        assertThat(paramValue).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울\" : getParam(\"고향\"): 서울")
    void t4() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울");

        String paramValue = rq.getParam("고향", ""); // 서울

        assertThat(paramValue).isEqualTo("서울");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울\" : getParam(\"고향\"): 서울")
    void t5() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울");

        String paramValue = rq.getParam("이름", ""); //

        assertThat(paramValue).isEqualTo("");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울&이름=홍길동\" : getParam(\"고향\"): 서울")
    void t6() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울&이름=홍길동");

        String paramValue = rq.getParam("고향", ""); // 서울

        assertThat(paramValue).isEqualTo("서울");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울&이름=홍길동\" : getParam(\"이름\"): 홍길동")
    void t7() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울&이름=홍길동");

        String paramValue = rq.getParam("이름", ""); // 홍길동

        assertThat(paramValue).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울&이름=홍길동&성별=남자\" : getParam(\"이름\"): 홍길동")
    void t8() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울&이름=홍길동&성별=남자");

        String paramValue = rq.getParam("이름", ""); // 홍길동

        assertThat(paramValue).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("입력값 : \"등록?고향=서울&이름=홍길동&성별=남자\" : getParam(\"성별\"): 남자")
    void t9() {

        WiseSayingRequester rq = new WiseSayingRequester("등록?고향=서울&이름=홍길동&성별=남자");

        String paramValue = rq.getParam("성별", ""); // 남자

        assertThat(paramValue).isEqualTo("남자");
    }

    @Test
    @DisplayName("입력값 : \"목록?page=1\" : getParam(\"page\"): 1")
    void t10() {

        WiseSayingRequester rq = new WiseSayingRequester("목록?page=1");

        int paramValue = rq.getParamAsInt("page", -1); // 1

        assertThat(paramValue).isEqualTo(1);
    }

    @Test
    @DisplayName("입력값 : \"목록?page=2번\" : getParam(\"page\"): -1")
    void t11() {

        WiseSayingRequester rq = new WiseSayingRequester("목록?page=2번");

        int paramValue = rq.getParamAsInt("page", -1); // -1

        assertThat(paramValue).isEqualTo(-1);
    }

}

