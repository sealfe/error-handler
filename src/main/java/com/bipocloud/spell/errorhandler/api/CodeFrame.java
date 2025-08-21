package com.bipocloud.spell.errorhandler.api;

import java.util.List;

public class CodeFrame {
    private final String file;
    private final int line;
    private final String author;
    private final List<String> code;

    public CodeFrame(String file, int line, String author, List<String> code) {
        this.file = file;
        this.line = line;
        this.author = author;
        this.code = code;
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getCode() {
        return code;
    }
}
