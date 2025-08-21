package com.bipocloud.api;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StackTraceLocator {
    public StackTraceRootCause findRootCause(String stack) {
        if (stack == null) {
            return null;
        }
        String[] lines = stack.split("\r?\n");
        List<Integer> causes = new ArrayList<>();
        causes.add(0);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.startsWith("Caused by")) {
                causes.add(i);
            }
        }
        int root = causes.get(causes.size() - 1);
        for (int j = causes.size() - 1; j >= 0; j--) {
            int start = causes.get(j);
            int end = j + 1 < causes.size() ? causes.get(j + 1) : lines.length;
            boolean hasBipo = false;
            for (int k = start + 1; k < end; k++) {
                String line = lines[k].trim();
                if (!line.startsWith("at ")) {
                    continue;
                }
                String content = line.substring(3);
                int paren = content.indexOf('(');
                if (paren < 0) {
                    continue;
                }
                String methodPart = content.substring(0, paren);
                int lastDot = methodPart.lastIndexOf('.');
                if (lastDot < 0) {
                    continue;
                }
                String className = methodPart.substring(0, lastDot);
                if (className.startsWith("com.bipo")) {
                    hasBipo = true;
                    break;
                }
            }
            if (hasBipo) {
                root = start;
                break;
            }
        }
        String header = lines[root].trim();
        if (header.startsWith("Caused by:")) {
            header = header.substring("Caused by:".length()).trim();
        }
        int colon = header.indexOf(':');
        String type;
        if (colon >= 0) {
            type = header.substring(0, colon).trim();
        } else {
            type = header;
        }
        int pos = causes.indexOf(root);
        int end = pos + 1 < causes.size() ? causes.get(pos + 1) : lines.length;
        List<StackTraceElement> calls = new ArrayList<>();
        for (int i = root + 1; i < end; i++) {
            String line = lines[i].trim();
            if (!line.startsWith("at ")) {
                continue;
            }
            String content = line.substring(3);
            int paren = content.indexOf('(');
            if (paren < 0) {
                continue;
            }
            String methodPart = content.substring(0, paren);
            int lastDot = methodPart.lastIndexOf('.');
            if (lastDot < 0) {
                continue;
            }
            String className = methodPart.substring(0, lastDot);
            if (!className.startsWith("com.bipo")) {
                continue;
            }
            String methodName = methodPart.substring(lastDot + 1);
            String filePart = content.substring(paren + 1, content.length() - 1);
            int colon2 = filePart.lastIndexOf(':');
            String fileName;
            int number;
            if (colon2 >= 0) {
                fileName = filePart.substring(0, colon2);
                try {
                    number = Integer.parseInt(filePart.substring(colon2 + 1));
                } catch (NumberFormatException e) {
                    number = -1;
                }
            } else {
                fileName = filePart;
                number = -1;
            }
            calls.add(new StackTraceElement(className, methodName, fileName, number));
        }
        StackTraceElement origin = calls.isEmpty() ? null : calls.get(0);
        return new StackTraceRootCause(type, origin, calls);
    }
}
