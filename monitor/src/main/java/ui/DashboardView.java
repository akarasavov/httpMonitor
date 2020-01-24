package ui;

import model.AlertEvent;
import model.SectionStatistic;
import model.TrafficStatisticEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DashboardView {

    private final static String TIC_TIMER = "Time from the start:%ss";
    private final static String BYTES_TRANSFERRED = "Bytes transferred: %db";
    private final static String TOTAL_REQUESTS = "Total requests: %d";
    private final static String SUCCESSFUL_REQUESTS = "2xx requests: %d";
    private final static String OTHER_REQUESTS = "Other requests: %d";
    private final static String ALERT_ACTIVATED = "High traffic generated an alert - hits = {%s}, triggered at {%s}\n";
    private final static String ALERT_DISABLED = "Traffic is recovered - hits = {%s}, triggered at {%s}\n";
    private final static DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private JLabel timerLabel;
    private JList<String> mostPopularList;
    private JList<String> trafficParameters;
    private JTextArea alertsMessages;
    private boolean alertActive;
    private long secondsFromTheStart = 0;

    public static void main(String[] args) {
        //Demo of DashboardView
        DashboardView dashboardView = new DashboardView();
        dashboardView.start();
        Executors.newScheduledThreadPool(1)
                .scheduleWithFixedDelay(() -> {
                    dashboardView.updateTime();
                    TrafficStatisticEvent trafficStatistic = new TrafficStatisticEvent(Arrays.asList(new SectionStatistic("aa", 2)), 10, 20, 30);
                    dashboardView.updateStatistics(trafficStatistic);
                    int i = new Random().nextInt(10);
                    dashboardView.updateAlertField(new AlertEvent(BigDecimal.ONE, AlertEvent.Type.ACTIVE));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dashboardView.updateAlertField(new AlertEvent(BigDecimal.ONE, AlertEvent.Type.RECOVERED));

                }, 2, 1, TimeUnit.SECONDS);
    }

    public void start() {
        SwingUtilities.invokeLater(this::createMainFrame);
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(() -> SwingUtilities.invokeLater(this::updateTime), 1, 1, TimeUnit.SECONDS);
    }

    public void updateStatistics(TrafficStatisticEvent statistic) {
        updateMostPopularList(statistic);
        updateTrafficParameters(statistic);
    }

    public void updateAlertField(AlertEvent alertEvent) {
        String formattedTime = alertEvent.time.toLocalTime().format(TIME_FORMATTER);
        double averageHits = alertEvent.averageHitsPerSecond.doubleValue();
        if (alertEvent.isActive() && !alertActive) {
            String text = String.format(ALERT_ACTIVATED, averageHits, formattedTime);
            alertsMessages.append(text);
            alertActive = true;
        } else if (!alertEvent.isActive() && alertActive) {
            String text = String.format(ALERT_DISABLED, averageHits, formattedTime);
            alertsMessages.append(text);
            alertActive = false;
        }
    }

    private void updateTrafficParameters(TrafficStatisticEvent statistic) {
        List<String> trafficValues = Arrays.asList(formatString(BYTES_TRANSFERRED, statistic.transferredBytes),
                formatString(TOTAL_REQUESTS, statistic.totalRequests),
                formatString(SUCCESSFUL_REQUESTS, statistic.successfulRequests),
                formatString(OTHER_REQUESTS, statistic.totalRequests - statistic.successfulRequests));
        Vector<String> values = new Vector<>(trafficValues);
        trafficParameters.setListData(values);
    }

    private void updateMostPopularList(TrafficStatisticEvent statistic) {
        List<SectionStatistic> mostPopularSections = statistic.mostPopularSections;
        Vector<String> values = new Vector<>();
        mostPopularSections.forEach(v -> values.addElement(v.toString()));

        mostPopularList.setListData(values);
    }

    private void updateTime() {
        secondsFromTheStart += 1;
        timerLabel.setText(String.format(TIC_TIMER, secondsFromTheStart));
    }

    private void createMainFrame() {
        JFrame mainFrame = new JFrame("Http Monitor");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new GridLayout(4, 0));

        Color background = mainFrame.getBackground();
        mainFrame.setSize(450, 400);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.setVisible(true);
        mainFrame.setResizable(false);

        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.add(alertsPanel());
        mainPanel.add(trafficInfoPanel(background));
        mainPanel.add(statisticPanel(background));
        mainPanel.add(timePanel());

        mainFrame.add(mainPanel);
    }

    private JPanel timePanel() {
        timerLabel = new JLabel(String.format(TIC_TIMER, 0));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(timerLabel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel trafficInfoPanel(Color parentBackgroundColor) {
        JPanel trafficInfoPanel = new JPanel(new BorderLayout());
        trafficInfoPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel trafficStatistics = new JLabel("Traffic statistics:");
        trafficStatistics.setFont(new Font("Dialog", Font.BOLD, 14));

        trafficInfoPanel.add(trafficStatistics, BorderLayout.NORTH);
        this.trafficParameters = new JList<>(new String[]{
                formatString(BYTES_TRANSFERRED, 0),
                formatString(TOTAL_REQUESTS, 0),
                formatString(SUCCESSFUL_REQUESTS, 0),
                formatString(OTHER_REQUESTS, 0),
        });
        trafficParameters.setSelectionModel(new DisabledItemSelectionModel());

        trafficParameters.setBackground(parentBackgroundColor);
        trafficInfoPanel.add(trafficParameters, BorderLayout.CENTER);
        return trafficInfoPanel;
    }

    private JPanel statisticPanel(Color background) {
        JPanel statisticPanel = new JPanel(new GridLayout(0, 2));
        statisticPanel.setBorder(new EmptyBorder(5, 0, 5, 0));
        statisticPanel.setSize(300, 200);

        JPanel mostPopularPanel = new JPanel(new BorderLayout());
        statisticPanel.add(mostPopularPanel);

        JLabel mostPopular = new JLabel("Six most popular sections:");
        mostPopular.setFont(new Font("Dialog", Font.BOLD, 14));

        mostPopularPanel.add(mostPopular, BorderLayout.NORTH);
        this.mostPopularList = new JList<>();

        mostPopularList.setSelectionModel(new DisabledItemSelectionModel());
        mostPopularList.setBackground(background);

        mostPopularPanel.add(new JScrollPane(mostPopularList), BorderLayout.CENTER);
        return statisticPanel;
    }

    private String formatString(String format, long value) {
        return String.format(format, value);
    }

    private JPanel alertsPanel() {
        JPanel alertsPanel = new JPanel(new BorderLayout());
        JLabel alertLabel = new JLabel("Alerts:");
        alertLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        alertsPanel.add(alertLabel, BorderLayout.NORTH);

        this.alertsMessages = new JTextArea(3, 20);
        alertsMessages.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(alertsMessages);
        alertsPanel.add(scrollPane, BorderLayout.CENTER);
        return alertsPanel;
    }

    private static class DisabledItemSelectionModel extends DefaultListSelectionModel {
        @Override
        public void setSelectionInterval(int index0, int index1) {
            super.setSelectionInterval(-1, -1);
        }
    }
}