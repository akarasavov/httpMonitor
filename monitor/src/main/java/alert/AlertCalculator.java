package alert;

import com.google.common.eventbus.Subscribe;
import eventbus.HttpMonitorEventBus;
import model.AlertEvent;
import model.TrafficStatisticEvent;
import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

public class AlertCalculator {
    private final CircularFifoQueue<TrafficStatisticEvent> circularQueue;
    private final HttpMonitorEventBus eventBus;
    private final int maximumTraffic;
    private final long totalIntervalInSeconds;
    private long totalHist;

    public AlertCalculator(HttpMonitorEventBus eventBus,
                           Duration totalTrafficInterval,
                           Duration ticInterval,
                           int maximTrafficThreshold) {
        int queueSize = (int) (totalTrafficInterval.toSeconds() / ticInterval.toSeconds());
        this.totalIntervalInSeconds = totalTrafficInterval.toSeconds();
        this.circularQueue = new CircularFifoQueue<>(queueSize);
        this.eventBus = eventBus;
        this.maximumTraffic = maximTrafficThreshold;
        eventBus.register(this);
    }

    private void addStatisticToQueue(TrafficStatisticEvent statistic) {
        if (circularQueue.isAtFullCapacity()) {
            TrafficStatisticEvent first = circularQueue.remove();
            totalHist -= first.totalRequests;

            circularQueue.add(statistic);
        } else {
            circularQueue.add(statistic);
        }

        totalHist += statistic.totalRequests;
        validateAlertThreshold();
    }

    private void validateAlertThreshold() {
        if (circularQueue.isAtFullCapacity()) {
            BigDecimal average = BigDecimal.valueOf(totalHist)
                    .divide(BigDecimal.valueOf(totalIntervalInSeconds), 2, RoundingMode.UP);
            if (average.compareTo(BigDecimal.valueOf(maximumTraffic)) > 0) {
                eventBus.publishEvent(new AlertEvent(average, AlertEvent.Type.ACTIVE));
            } else {
                eventBus.publishEvent(new AlertEvent(average, AlertEvent.Type.RECOVERED));
            }
        }
    }

    @Subscribe
    public void handleEvent(TrafficStatisticEvent statistic) {
        addStatisticToQueue(statistic);

    }
}
