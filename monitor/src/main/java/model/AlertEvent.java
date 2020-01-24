package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

public class AlertEvent {

    public enum Type {
        ACTIVE,
        RECOVERED
    }

    public final Type type;
    public final BigDecimal averageHitsPerSecond;
    public final LocalDateTime time;

    public AlertEvent(BigDecimal averageHitsPerSecond, Type type) {
        this.averageHitsPerSecond = averageHitsPerSecond;
        this.type = type;
        this.time = LocalDateTime.now(ZoneId.systemDefault());
    }

    public boolean isActive() {
        return type == Type.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlertEvent that = (AlertEvent) o;
        return type == that.type &&
                Objects.equals(averageHitsPerSecond, that.averageHitsPerSecond);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, averageHitsPerSecond);
    }

    @Override
    public String toString() {
        return "AlertStatistic{" +
                "type=" + type +
                ", averageHitsPerSecond=" + averageHitsPerSecond +
                '}';
    }
}
