package com.bipocloud.api;

import org.springframework.stereotype.Component;

@Component
public class StackTraceLocator {
    public StackTraceElement locateBusinessFrame(String stack) {
        if (stack == null) {
            return null;
        }
        String[] lines = stack.split("\r?\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("at ")) {
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
                int colon = filePart.lastIndexOf(':');
                String fileName;
                int number;
                if (colon >= 0) {
                    fileName = filePart.substring(0, colon);
                    try {
                        number = Integer.parseInt(filePart.substring(colon + 1));
                    } catch (NumberFormatException e) {
                        number = -1;
                    }
                } else {
                    fileName = filePart;
                    number = -1;
                }
                return new StackTraceElement(className, methodName, fileName, number);
            }
        }
        return null;
    }
}
