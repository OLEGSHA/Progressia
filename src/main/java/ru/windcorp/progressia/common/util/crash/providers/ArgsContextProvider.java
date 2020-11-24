package ru.windcorp.progressia.common.util.crash.providers;

import ru.windcorp.progressia.Progressia;
import ru.windcorp.progressia.ProgressiaLauncher;
import ru.windcorp.progressia.common.util.crash.ContextProvider;

import java.util.Map;

public class ArgsContextProvider implements ContextProvider {

    @Override
    public void provideContext(Map<String, String> output) {
        output.put("Number of arguments", ProgressiaLauncher.arguments.length + " total");
        if (ProgressiaLauncher.arguments.length > 0) {
            StringBuilder buffer = new StringBuilder();
            for (String arg : ProgressiaLauncher.arguments) {
                buffer.append(arg).append(";");
            }
            output.put("Args", System.getProperty(buffer.toString()));
        }
    }

    @Override
    public String getName() {
        return "Arguments Context Provider";
    }
}
