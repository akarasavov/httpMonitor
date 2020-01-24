package calculator;

import eventbus.HttpMonitorEventBus;
import file.HttpMethod;
import model.AccessLogEvent;
import model.SectionStatistic;
import model.TrafficStatisticEvent;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Arrays.asList;

public class TrafficCalculatorTest {


    @Test
    public void calculate_traffic_statistics_for_three_hits_in_access_log() {
        //given traffic calculator with tic interval 1s
        HttpMonitorEventBus eventBus = Mockito.mock(HttpMonitorEventBus.class);
        TrafficCalculator trafficCalculator = new TrafficCalculator(eventBus, Duration.ofSeconds(1));

        //given access logs that are generated now
        LocalDateTime accessLogDate = LocalDateTime.now();
        List<AccessLogEvent> accessLogEvents = asList(createAccessLog(10, "test", 200, accessLogDate),
                createAccessLog(20, "google", 200, accessLogDate),
                createAccessLog(20, "test", 300, accessLogDate)
        );
        //when traffic calculator receive 3 events
        accessLogEvents.forEach(trafficCalculator::handleEvent);
        trafficCalculator.calculateTrafficStatistics();

        //then traffic calculator publish expected statistic on event bus
        List<SectionStatistic> mostPopular = asList(new SectionStatistic("test", 2),
                new SectionStatistic("google", 1));

        TrafficStatisticEvent statistic = new TrafficStatisticEvent(mostPopular, 50, 2, 3);
        Mockito.verify(eventBus).publishEvent(statistic);
    }

    @Test
    public void traffic_calculator_should_publish_statistic_only_for_tic_interval() throws InterruptedException {
        //given traffic calculator with ticInterval = 1s
        HttpMonitorEventBus eventBus = Mockito.mock(HttpMonitorEventBus.class);
        TrafficCalculator trafficCalculator = new TrafficCalculator(eventBus, Duration.ofSeconds(1));

        //Given two access logs generated 1 millis ago and one two seconds in the future
        LocalDateTime now = LocalDateTime.now();
        List<AccessLogEvent> accessLogEvents = asList(createAccessLog(10, "test", 200, now),
                createAccessLog(20, "test", 300, now),
                createAccessLog(30, "google", 200, now.plusSeconds(2))
        );
        //when traffic calculator receive 3 events
        accessLogEvents.forEach(trafficCalculator::handleEvent);
        trafficCalculator.calculateTrafficStatistics();

        //then traffic calculator publish expected statistic on event bus
        List<SectionStatistic> mostPopular = asList(new SectionStatistic("test", 2));
        TrafficStatisticEvent statistic = new TrafficStatisticEvent(mostPopular, 30, 1, 2);
        Mockito.verify(eventBus).publishEvent(statistic);
    }


    private AccessLogEvent createAccessLog(long byteSize, String resource, int responseCode, LocalDateTime date) {
        return new AccessLogEvent("127.0.0.1",
                "alex",
                date,
                HttpMethod.GET,
                resource,
                resource,
                "HTTP/1.0",
                responseCode,
                byteSize);
    }

}