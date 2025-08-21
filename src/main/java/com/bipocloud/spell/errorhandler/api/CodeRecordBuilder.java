package com.bipocloud.spell.errorhandler.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CodeRecordBuilder {
    public CodeRecord build(StackTraceRootCause cause, String appName) throws IOException, InterruptedException {
        Path project = Path.of(System.getProperty("user.home"), "IdeaProjects", appName);
        Map<String, CodeFrame> frames = new LinkedHashMap<>();
        for (StackTraceElement call : cause.getCalls()) {
            String file = findFile(project, call.getClassName());
            int line = call.getLineNumber();
            String key = file + ":" + line;
            if (frames.containsKey(key)) {
                continue;
            }
            String author = author(project, file, line);
            List<String> code = snippet(project, file, line, 20);
            frames.put(key, new CodeFrame(file, line, author, code));
        }
        StackTraceElement origin = cause.getOrigin();
        String originFile = findFile(project, origin.getClassName());
        int originLine = origin.getLineNumber();
        String code = line(project, originFile, originLine);
        return new CodeRecord(cause.getType(), code, new ArrayList<>(frames.values()));
    }

    private String findFile(Path project, String className) throws IOException, InterruptedException {
        String name = className.contains("$") ? className.substring(0, className.indexOf('$')) : className;
        String path = name.replace('.', '/') + ".java";
        ProcessBuilder pb = new ProcessBuilder("bash", "-lc", "find . -path '*/src/main/java/" + path + "' -print -quit");
        pb.directory(project.toFile());
        Process p = pb.start();
        byte[] out = p.getInputStream().readAllBytes();
        p.waitFor();
        String result = new String(out, StandardCharsets.UTF_8).trim();
        if (result.startsWith("./")) {
            result = result.substring(2);
        }
        return result.isEmpty() ? "src/main/java/" + path : result;
    }

    private String author(Path project, String file, int line) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("git", "blame", "-p", "-L", line + "," + line, "--", file);
        pb.directory(project.toFile());
        Process p = pb.start();
        byte[] out = p.getInputStream().readAllBytes();
        p.waitFor();
        String[] lines = new String(out, StandardCharsets.UTF_8).split("\\R");
        for (String l : lines) {
            if (l.startsWith("author ")) {
                return l.substring(7).trim();
            }
        }
        return "";
    }

    private List<String> snippet(Path project, String file, int line, int radius) throws IOException {
        Path path = project.resolve(file);
        List<String> all = Files.readAllLines(path);
        int start = Math.max(0, line - radius - 1);
        int end = Math.min(all.size(), line + radius);
        return new ArrayList<>(all.subList(start, end));
    }

    private String line(Path project, String file, int line) throws IOException {
        Path path = project.resolve(file);
        List<String> all = Files.readAllLines(path);
        if (line > 0 && line <= all.size()) {
            return all.get(line - 1);
        }
        return "";
    }
}
