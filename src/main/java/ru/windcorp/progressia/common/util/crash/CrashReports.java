package ru.windcorp.progressia.common.util.crash;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

import ru.windcorp.jputil.chars.StringUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class CrashReports {

    private CrashReports() {
    }

    private static final Path CRASH_REPORTS_PATH = Paths.get("crash-reports");

    private static final Collection<ContextProvider> PROVIDERS = Collections.synchronizedCollection(new ArrayList<>());

    private static final Collection<Analyzer> ANALYZERS = Collections.synchronizedCollection(new ArrayList<>());

    private static final Logger LOGGER = LogManager.getLogger("crash");
    
    public static ReportedException report(Throwable throwable, String messageFormat, Object... args) {
        if (throwable instanceof ReportedException) return  (ReportedException) throwable;

        return new ReportedException(throwable, messageFormat, args);
    }

    public static RuntimeException crash(Throwable throwable, String messageFormat, Object... args) {
        if (throwable instanceof ReportedException) {
            throw crash((ReportedException) throwable);
        } else {
            throw crash(report(throwable, messageFormat, args));
        }
    }

    /**
     * <em>This method never returns.</em>
     * <p>
     * TODO document
     *
     * @param reportException
     */
    public static RuntimeException crash(ReportedException reportException) {
        Throwable throwable = reportException.getCause();
        String messageFormat = reportException.getMessageFormat();
        Object[] args = reportException.getArgs();

        StringBuilder output = new StringBuilder();

        try {
            String.format(messageFormat, args);
        } catch (IllegalFormatException e) {
            messageFormat = StringUtil.replaceAll(messageFormat, "%", "%%");

            if (args.length != 0) {
                messageFormat += "\nArgs:";
                for (Object arg : args) {
                    try {
                        messageFormat += " \"" + arg.toString() + "\"";
                    } catch (Throwable t) {
                        messageFormat += " exc: \"" + t.getClass().toString() + "\"";
                    }
                }
                args = new Object[0]; // clear args
            }

            messageFormat += "\nCould not format provided description";
        }

        appendContextProviders(output);
        addSeparator(output);
        if (appendAnalyzers(output, throwable, messageFormat, args)) {
            addSeparator(output);
        }

        appendMessageFormat(output, messageFormat, args);

        appendStackTrace(output, throwable);

        export(output.toString());

        System.exit(0);
        return reportException;
    }

    private static void appendContextProviders(StringBuilder output) {

        // Do a local copy to avoid deadlocks -OLEGSHA
        ContextProvider[] localProvidersCopy = PROVIDERS.toArray(new ContextProvider[PROVIDERS.size()]);

        for (ContextProvider provider : localProvidersCopy) {
            if (provider == null)
                continue;

            addSeparator(output);

            try {
                Map<String, String> buf = new HashMap<>();
                provider.provideContext(buf);

                if (!buf.isEmpty()) {
                    output.append(StringUtilsTemp.center(provider.getName(), 80)).append("\n");
                    for (Map.Entry<String, String> entry : buf.entrySet()) {
                        output.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                    }
                }
            } catch (Throwable t) {
                String providerName;

                try {
                    providerName = provider.getName();
                } catch (Throwable t1) {
                    providerName = provider.getClass().getName();
                }

                output.append(providerName).append(" is broken").append("\n");
                // ContextProvider is broken
            }
        }
    }

    private static boolean appendAnalyzers(StringBuilder output, Throwable throwable, String messageFormat,
                                           Object[] args) {
        boolean analyzerResponsesExist = false;

        // Do a local copy to avoid deadlocks -OLEGSHA
        Analyzer[] localAnalyzersCopy = ANALYZERS.toArray(new Analyzer[ANALYZERS.size()]);

        if (localAnalyzersCopy.length > 0) {
            output.append(StringUtilsTemp.center("Analyzers", 80)).append("\n");
        }

        for (Analyzer analyzer : localAnalyzersCopy) {
            if (analyzer == null)
                continue;

            String answer;
            try {
                answer = analyzer.analyze(throwable, messageFormat, args);

                if (answer != null && !answer.isEmpty()) {
                    analyzerResponsesExist = true;
                    output.append(analyzer.getName()).append(": ").append(answer).append("\n");
                }
            } catch (Throwable t) {
                analyzerResponsesExist = true;

                output.append("\n");

                String analyzerName;

                try {
                    analyzerName = analyzer.getName();
                } catch (Throwable t1) {
                    analyzerName = analyzer.getClass().getName();
                }

                output.append(analyzerName).append(" is broken").append("\n");
                // Analyzer is broken
            }
        }

        return analyzerResponsesExist;
    }

    private static void appendMessageFormat(StringBuilder output, String messageFormat, Object... arg) {
        output.append("Provided description: \n");
        if (messageFormat.isEmpty())
            output.append("none").append("\n");
        else
            output.append(String.format(messageFormat, arg)).append("\n");

        addSeparator(output);
    }

    private static void appendStackTrace(StringBuilder output, Throwable throwable) {
        output.append("Stacktrace: \n");

        if (throwable == null) {
            output.append("no Throwable provided").append("\n");
            return;
        }

        // Formatting to a human-readable string
        Writer sink = new StringBuilderWriter(output);
        try {
            throwable.printStackTrace(new PrintWriter(sink));
        } catch (Exception e) {
            // PLAK
        }
        output.append("\n");
    }

    private static void export(String report) {
        try {
            LOGGER.fatal("\n" + report);
        } catch (Exception e) {
            // PLAK
        }

        System.err.println(report);

        generateCrashReportFiles(report);
    }

    private static void generateCrashReportFiles(String output) {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");

        try {
            if (!Files.exists(CRASH_REPORTS_PATH))
                Files.createDirectory(CRASH_REPORTS_PATH);

            createFileForCrashReport(output, CRASH_REPORTS_PATH.toString() + "/latest.log");
            createFileForCrashReport(output,
                    CRASH_REPORTS_PATH.toString() + "/crash-" + dateFormat.format(date) + ".log");
        } catch (Throwable t) {
            // Crash Report not created
        }
    }

    private static void createFileForCrashReport(String buffer, String filename) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filename), StandardCharsets.UTF_8)) {
            writer.write(buffer);
        } catch (IOException ex) {
            // Crash Report not created
        }
    }

    public static void registerProvider(ContextProvider provider) {
        PROVIDERS.add(provider);
    }

    public static void registerAnalyzer(Analyzer analyzer) {
        ANALYZERS.add(analyzer);
    }

    private static void addSeparator(StringBuilder sb) {
        sb.append(StringUtil.sequence('-', 80)).append("\n");
    }

    public static class ReportedException extends RuntimeException {

        private String messageFormat;
        private Object[] args;

        public ReportedException(Throwable throwable, String messageFormat, Object... args){
            super(throwable);
            this.messageFormat = messageFormat;
            this.args = args;
        }

        public String getMessageFormat() {
            return messageFormat;
        }

        public Object[] getArgs() {
            return args;
        }
    }

}

class StringUtilsTemp {
    public static String center(String s, int size) {
        return center(s, size, ' ');
    }

    public static String center(String s, int size, char pad) {
        if (s == null || size <= s.length())
            return s;

        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < (size - s.length()) / 2; i++) {
            sb.append(pad);
        }
        sb.append(s);
        while (sb.length() < size) {
            sb.append(pad);
        }
        return sb.toString();
    }
}