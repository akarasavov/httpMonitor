package simulator;

import com.beust.jcommander.JCommander;

import java.io.File;
import java.io.FileWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.*;

public class TrafficSimulator {

    private final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss.Z");

    public static void main(String[] args) {
        ConsoleParameters consoleParameters = new ConsoleParameters();
        JCommander commander = JCommander.newBuilder()
                .addObject(consoleParameters)
                .build();

        commander.parse(args);

        if (consoleParameters.help) {
            commander.usage();
        } else {
            ExecutorService executorService = startApplication(consoleParameters);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    System.out.println("Stop traffic simulator");
                    executorService.shutdown();
                }
            });
        }
    }

    private static ExecutorService startApplication(ConsoleParameters consoleParameters) {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService
                .scheduleWithFixedDelay(() -> appendLogs(consoleParameters),
                        0,
                        consoleParameters.delayInSeconds,
                        TimeUnit.SECONDS);
        return executorService;
    }

    private static void appendLogs(ConsoleParameters consoleParameters) {
        System.out.println("Start appending logs " + ZonedDateTime.now());
        List<String> resources = List.of("/home", "/home/abc", "/google/temp", "/yandex/car", "/yandex/food", "/facebook/alex");
        List<Integer> responseCodes = List.of(200, 300);
        try (FileWriter fileWriter = new FileWriter(new File(consoleParameters.filePath), true)) {
            Random random = new Random();
            ZonedDateTime now = ZonedDateTime.now();
            for (int i = 0; i < consoleParameters.logLines; i++) {
                String resource = resources.get(random.nextInt(resources.size()));
                int messageSize = random.nextInt(1000);
                int responseCode = responseCodes.get(random.nextInt(responseCodes.size()));

                fileWriter.append(createCommonLogLine(now, resource, responseCode, messageSize));
                fileWriter.flush();

                now = now.plus(1, ChronoUnit.MICROS);
            }
            System.out.println("Finish appending logs " + ZonedDateTime.now());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static String createCommonLogLine(ZonedDateTime dateTime, String resource, int httpCode, int messageSize) {
        StringJoiner stringBuilder = new StringJoiner(" ");
        stringBuilder.add("127.0.0.1");
        stringBuilder.add("-");
        stringBuilder.add("james");
        stringBuilder.add("[" + dateTimeFormatter.format(dateTime) + "]");
        stringBuilder.add("\"GET " + resource + " HTTP/1.0\"");
        stringBuilder.add("" + httpCode);
        stringBuilder.add("" + messageSize);

        return stringBuilder.toString() + "\n";
    }
}
