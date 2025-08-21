package com.bipocloud.spell.errorhandler.api;

import java.util.List;

public class StackTraceRootCause {
    private final String type;
    private final StackTraceElement origin;
    private final List<StackTraceElement> calls;

    public StackTraceRootCause(String type, StackTraceElement origin, List<StackTraceElement> calls) {
        this.type = type;
        this.origin = origin;
        this.calls = calls;
    }

    public String getType() {
        return type;
    }

    public StackTraceElement getOrigin() {
        return origin;
    }

    public List<StackTraceElement> getCalls() {
        return calls;
    }
}
