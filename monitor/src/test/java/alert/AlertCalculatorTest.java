package alert;

import eventbus.HttpMonitorEventBus;
import model.AlertEvent;
import model.SectionStatistic;
import model.TrafficStatisticEvent;
import org.junit.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

public class AlertCalculatorTest {

    @Test
    public void validate_recovering_of_alert() {
        //Given
        HttpMonitorEventBus eventBus = Mockito.mock(HttpMonitorEventBus.class);
        Duration totalTrafficInterval = Duration.ofSeconds(120);
        Duration ticInterval = Duration.ofSeconds(10);
        int alertRpsThreshold = 10;
        AlertCalculator calculator = new AlertCalculator(eventBus, totalTrafficInterval, ticInterval, alertRpsThreshold);

        TrafficStatisticEvent trafficStatistic = new TrafficStatisticEvent(
                List.of(new SectionStatistic("/test", 10)),
                1,
                10,
                10);
        //When every 10s calculator receive 10hits
        for (int i = 0; i < 12; i++) {
            calculator.handleEvent(trafficStatistic);
        }
        //Then average of hits should be 1, 12 * 10 / 120s = 1
        Mockito.verify(eventBus).publishEvent(new AlertEvent(BigDecimal.ONE.setScale(2), AlertEvent.Type.RECOVERED));
    }

    @Test
    public void validate_activation_of_alert() {
        //Given
        HttpMonitorEventBus eventBus = Mockito.mock(HttpMonitorEventBus.class);
        Duration totalTrafficInterval = Duration.ofSeconds(120);
        Duration ticInterval = Duration.ofSeconds(10);
        int alertRpsThreshold = 10;
        AlertCalculator calculator = new AlertCalculator(eventBus, totalTrafficInterval, ticInterval, alertRpsThreshold);


        //When every 10s calculator receive 10hits
        int prev = 10;
        for (int i = 0; i < 12; i++) {
            TrafficStatisticEvent trafficStatisticEvent = new TrafficStatisticEvent(
                    List.of(new SectionStatistic("/test", prev)),
                    1,
                    prev,
                    prev);
            prev = prev * 2;
            calculator.handleEvent(trafficStatisticEvent);
        }
        Mockito.verify(eventBus).publishEvent(new AlertEvent(BigDecimal.valueOf(341.25), AlertEvent.Type.ACTIVE));
    }
}