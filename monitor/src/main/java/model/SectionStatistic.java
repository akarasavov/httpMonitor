package model;

import java.util.Objects;

public class SectionStatistic {

    public final String section;
    public final Integer hits;

    public SectionStatistic(String section, Integer hits) {
        this.section = section;
        this.hits = hits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionStatistic that = (SectionStatistic) o;
        return Objects.equals(section, that.section) &&
                Objects.equals(hits, that.hits);
    }

    @Override
    public int hashCode() {
        return Objects.hash(section, hits);
    }

    @Override
    public String toString() {
        return section + ":" + hits;
    }
}
