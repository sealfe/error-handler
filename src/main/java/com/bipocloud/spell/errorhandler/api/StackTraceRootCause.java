package com.bipocloud.spell.errorhandler.api;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StackTraceRootCause {
    private String type;
    private StackTraceElement origin;
    private List<StackTraceElement> calls;
}
