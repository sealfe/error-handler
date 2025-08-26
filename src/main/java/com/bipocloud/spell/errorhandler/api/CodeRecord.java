package com.bipocloud.spell.errorhandler.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeRecord {
    private final String type;
    private final String code;
    private final List<CodeFrame> stack;
    private final List<String> trace;

    public CodeRecord(String type, String code, List<CodeFrame> stack, List<String> trace) {
        this.type = type;
        this.code = code;
        this.stack = stack;
        this.trace = trace;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }

    public List<CodeFrame> getStack() {
        return stack;
    }

    public List<String> getTrace() {
        return trace;
    }

    public Map<String, Object> merge(String result) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("code", code);
        map.put("stack", stack);
        map.put("trace", trace);
        map.put("result", result);
        return map;
    }

    public String description(String result) {
        String collect = String.join("\n", trace);
        return collect + "\n" + result;
    }
}
