package com.bipocloud.spell.errorhandler.api;

import java.util.ArrayList;
import java.util.List;

public class StackTraceLocator {
    public StackTraceRootCause findRootCause(String stack) {
        List<Cause> causes = parse(stack);
        int root = causes.size() - 1;
        for (int i = root; i >= 0; i--) {
            if (containsBipo(causes.get(i).frames)) {
                root = i;
                break;
            }
        }
        List<StackTraceElement> calls = new ArrayList<>();
        for (int i = root; i < causes.size(); i++) {
            for (StackTraceElement el : causes.get(i).frames) {
                if (el.getClassName().startsWith("com.bipo")) {
                    calls.add(el);
                }
            }
        }
        StackTraceElement origin = calls.isEmpty() ? null : calls.get(0);
        return new StackTraceRootCause(causes.get(root).type, origin, calls);
    }

    private boolean containsBipo(List<StackTraceElement> frames) {
        for (StackTraceElement el : frames) {
            if (el.getClassName().startsWith("com.bipo")) {
                return true;
            }
        }
        return false;
    }

    private List<Cause> parse(String stack) {
        String[] lines = stack.split("\n");
        List<Cause> causes = new ArrayList<>();
        Cause current = null;
        for (String line : lines) {
            if (line.startsWith("Caused by: ")) {
                String type = line.substring(11).split(":")[0];
                current = new Cause(type);
                causes.add(current);
            } else if (!line.startsWith("\tat ")) {
                if (current == null) {
                    String type = line.split(":")[0];
                    current = new Cause(type);
                    causes.add(current);
                }
            } else {
                if (current != null) {
                    current.frames.add(parseFrame(line.substring(4)));
                }
            }
        }
        return causes;
    }

    private StackTraceElement parseFrame(String text) {
        int paren = text.indexOf('(');
        String classMethod = text.substring(0, paren);
        int lastDot = classMethod.lastIndexOf('.');
        String className = classMethod.substring(0, lastDot);
        String method = classMethod.substring(lastDot + 1);
        String fileLine = text.substring(paren + 1, text.length() - 1);
        int colon = fileLine.indexOf(':');
        String file = colon >= 0 ? fileLine.substring(0, colon) : fileLine;
        int line = colon >= 0 ? Integer.parseInt(fileLine.substring(colon + 1)) : -1;
        return new StackTraceElement(className, method, file, line);
    }

    private static class Cause {
        private String type;
        private List<StackTraceElement> frames = new ArrayList<>();
        private Cause(String type) {
            this.type = type;
        }
    }
}
