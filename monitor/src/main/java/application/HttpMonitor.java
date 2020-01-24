package application;

import alert.AlertCalculator;
import calculator.TrafficCalculator;
import com.beust.jcommander.JCommander;
import eventbus.HttpMonitorEventBus;
import file.AccessLogParser;
import file.FileListener;
import file.RegxAccessLogParser;
import file.TailFileListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ui.DashboardController;

import java.io.File;
import java.time.Duration;

public class HttpMonitor {

    private static Logger logger = LoggerFactory.getLogger(HttpMonitor.class);

    public static void main(String[] args) throws InterruptedException {
        ConsoleParameters consoleParameters = new ConsoleParameters();
        JCommander commander = JCommander.newBuilder()
                .addObject(consoleParameters)
                .build();

        commander.parse(args);

        if (consoleParameters.help) {
            commander.usage();
        } else {
            startApplication(consoleParameters);
        }
    }

    private static void startApplication(ConsoleParameters parameters) throws InterruptedException {
        validateParameters(parameters);
        HttpMonitorEventBus eventBus = new HttpMonitorEventBus();

        AccessLogParser parser = new RegxAccessLogParser(eventBus);

        FileListener fileListener = new TailFileListener(new File(parameters.filePath), Duration.ofMillis(500), eventBus);
        fileListener.start();

        Duration ticInterval = Duration.ofSeconds(parameters.ticInterval);
        TrafficCalculator trafficCalculator = new TrafficCalculator(eventBus, ticInterval);
        trafficCalculator.start();

        AlertCalculator alertCalculator = new AlertCalculator(eventBus,
                Duration.ofSeconds(parameters.alertInterval),
                ticInterval,
                parameters.maximTrafficThreshold);

        DashboardController dashboardController = new DashboardController(eventBus);
    }

    private static void validateParameters(ConsoleParameters parameters) {
        if (parameters.ticInterval > parameters.alertInterval || parameters.alertInterval % parameters.ticInterval != 0) {
            throw new IllegalArgumentException("ticInterval should be less than alertInterval and alertInterval % ticInterval == 0");
        }
    }
}
