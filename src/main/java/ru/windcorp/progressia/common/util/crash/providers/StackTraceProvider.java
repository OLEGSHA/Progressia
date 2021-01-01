package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class StackTraceProvider implements ContextProvider  {
    @Override
    public void provideContext(Map<String, String> output) {
        StackTraceElement[] stackTraceBuffer = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (int i = 4; i < stackTraceBuffer.length; i++) {
            sb.append('\t').append(stackTraceBuffer[i].toString()).append("\n");
        }

        output.put("Reported from " + Thread.currentThread().getName(), sb.toString());
    }

    @Override
    public String getName() {
        return "Stack Trace Context Provider";
    }
}
