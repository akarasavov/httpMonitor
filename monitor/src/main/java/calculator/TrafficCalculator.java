package calculator;

import com.google.common.eventbus.Subscribe;
import eventbus.HttpMonitorEventBus;
import model.AccessLogEvent;
import model.SectionStatistic;
import model.TrafficStatisticEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TrafficCalculator {

    private Logger logger = LoggerFactory.getLogger(TrafficCalculator.class);

    private final HttpMonitorEventBus eventBus;
    private final Duration ticInterval;
    private final Duration lagInterval = Duration.ofMillis(200);
    private static final LinkedBlockingQueue<AccessLogEvent> queue = new LinkedBlockingQueue<>();

    public TrafficCalculator(HttpMonitorEventBus eventBus, Duration ticInterval) {
        eventBus.register(this);
        this.eventBus = eventBus;
        this.ticInterval = ticInterval;
    }

    public void start() {
        Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(this::calculateTrafficStatistics,
                        ticInterval.toMillis(),
                        ticInterval.toMillis(),
                        TimeUnit.MILLISECONDS);
    }

    void calculateTrafficStatistics() {
        long transferredBytes = 0;
        long successfulRequests = 0;
        long totalRequests = 0;
        Map<String, Integer> sectionHitsMap = new HashMap<>();
        LocalDateTime end = LocalDateTime.now(ZoneId.systemDefault());
        LocalDateTime start = end.minus(ticInterval).minus(lagInterval);

        for (AccessLogEvent accessLogEvent = queue.poll();
             accessLogEvent != null && validateAccessLogDate(accessLogEvent, start, end);
             accessLogEvent = queue.poll()) {
            totalRequests++;
            if (accessLogEvent.isSuccessfulRequest()) {
                successfulRequests++;
            }
            transferredBytes += accessLogEvent.bytesSize;

            Integer hits = sectionHitsMap.getOrDefault(accessLogEvent.section, 0);
            sectionHitsMap.put(accessLogEvent.section, hits + 1);
        }

        List<SectionStatistic> mostPopularSections = sectionHitsMap.entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(6)
                .map(entry -> new SectionStatistic(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        TrafficStatisticEvent statistic = new TrafficStatisticEvent(
                mostPopularSections,
                transferredBytes,
                successfulRequests,
                totalRequests
        );
        eventBus.publishEvent(statistic);
    }

    private boolean validateAccessLogDate(AccessLogEvent accessLogEvent, LocalDateTime start, LocalDateTime end) {
        return accessLogEvent.dateTime.isAfter(start) && accessLogEvent.dateTime.isBefore(end);
    }

    @Subscribe
    public void handleEvent(AccessLogEvent accessLogEvent) {
        try {
            logger.debug("Receive {}", accessLogEvent);
            queue.put(accessLogEvent);
        } catch (InterruptedException e) {
            logger.error("Error in handling accessLog={} ", accessLogEvent, e);
        }
    }
}
