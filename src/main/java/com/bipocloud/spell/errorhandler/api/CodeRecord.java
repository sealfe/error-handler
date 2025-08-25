package com.bipocloud.spell.errorhandler.api;

import java.util.List;

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
}
