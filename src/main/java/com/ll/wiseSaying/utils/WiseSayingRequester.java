package com.ll.wiseSaying.utils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class WiseSayingRequester {
    private final String action;
    private final Map<String, String> paramMap;

    public WiseSayingRequester(String cmd) {
        String[] cmdBits = cmd.split("\\?");
        action = cmdBits[0];
        String params = cmdBits.length > 1 ? cmdBits[1] : "";
        paramMap = Arrays.stream(params.split("&")).map(param -> param.split("="))
                .filter(paramBits -> paramBits.length == 2 && paramBits[0] != null && paramBits[1] != null)
                .collect(Collectors.toMap(bits -> bits[0], bits -> bits[1]));
    }

    public String getActionName() {
        return action;
    }

    public String getParam(String key, String defaultValue) {

        if(paramMap.containsKey(key)) {
            return paramMap.get(key);
        }
        return defaultValue;
    }

    public int getParamAsInt(String key, int defaultValue) {

        try {
            return Integer.parseInt(paramMap.get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}