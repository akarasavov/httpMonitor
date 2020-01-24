package model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TrafficStatisticEvent {

    public final List<SectionStatistic> mostPopularSections;
    public final long transferredBytes;
    public final long successfulRequests;
    public final long totalRequests;

    public TrafficStatisticEvent(List<SectionStatistic> mostPopularSections,
                                 long transferredBytes,
                                 long successfulRequests,
                                 long totalRequests) {
        this.mostPopularSections = Collections.unmodifiableList(mostPopularSections);
        this.transferredBytes = transferredBytes;
        this.successfulRequests = successfulRequests;
        this.totalRequests = totalRequests;
    }

    public boolean isEmpty() {
        return totalRequests == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrafficStatisticEvent that = (TrafficStatisticEvent) o;
        return transferredBytes == that.transferredBytes &&
                successfulRequests == that.successfulRequests &&
                totalRequests == that.totalRequests &&
                Objects.equals(mostPopularSections, that.mostPopularSections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mostPopularSections, transferredBytes, successfulRequests, totalRequests);
    }

    @Override
    public String toString() {
        return "AccessLogStatistic{" +
                "mostPopularSections=" + mostPopularSections +
                ", transferredBytes=" + transferredBytes +
                ", successfulRequests=" + successfulRequests +
                ", totalRequests=" + totalRequests +
                '}';
    }
}
