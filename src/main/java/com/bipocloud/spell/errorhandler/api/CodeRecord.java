package com.bipocloud.spell.errorhandler.api;

import java.util.List;

public class CodeRecord {
    private final String type;
    private final String code;
    private final List<CodeFrame> stack;

    public CodeRecord(String type, String code, List<CodeFrame> stack) {
        this.type = type;
        this.code = code;
        this.stack = stack;
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
}
