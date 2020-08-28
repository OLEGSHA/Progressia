package ru.windcorp.progressia.client.localization;

import ru.windcorp.jputil.chars.EscapeException;
import ru.windcorp.jputil.chars.Escaper;
import ru.windcorp.progressia.common.resource.ResourceManager;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class Parser {

    public Parser(String filePath) {
        this.filePath = filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Map<String, String> parse() {
        Map<String, String> parsedData = new HashMap<>();
        try (Reader rawData = ResourceManager
                .getResource(filePath)
                .getReader()
        ) {
            int code;
            char c;
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                code = rawData.read();
                if (code == -1) {
                    break;
                }
                c = (char)code;
                if (c == '#') {
                    while (c != '\n') {
                        code = rawData.read();
                        if (code == -1) {
                            break;
                        }
                        c = (char)code;
                    }
                } else if (c == ' ') {
                    code = rawData.read();
                    if (code == -1) {
                        break;
                    }
                    c = (char) code;
                    if (c == '=') {
                        String key = escaper.escape(stringBuilder.toString());
                        stringBuilder.setLength(0);
                        rawData.read(); //skip a char
                        while (true) {
                            code = rawData.read();
                            if (code == -1) {
                                break;
                            }

                            c = (char) code;
                            if (code == '\n') {
                                break;
                            } else {
                                stringBuilder.append(c);
                            }
                        }
                        parsedData.put(escaper.unescape(key),
                                escaper.unescape(stringBuilder.toString()));
                        stringBuilder.setLength(0);
                    }
                } else if (c == '\n') {
                    stringBuilder.setLength(0);
                } else {
                    stringBuilder.append(c);
                }
            }

        } catch (IOException | EscapeException e) {
            throw new RuntimeException(e);
        }
        return parsedData;
    }

    public String getFilePath() {
        return filePath;
    }


    private String filePath;
    static private final Escaper escaper = new Escaper.EscaperBuilder()
            .withChars("n", "\n").build();
}
