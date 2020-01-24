package model;

import file.HttpMethod;

import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class AccessLogEvent {

    public final String hostName;
    public final String userName;
    public final LocalDateTime dateTime;
    public final HttpMethod httpMethod;
    public final String resource;
    public final String section;
    public final String httpVersion;
    public final Integer responseCode;
    public final long bytesSize;

    public AccessLogEvent(String hostName,
                          String userName,
                          LocalDateTime dateTime,
                          HttpMethod httpMethod,
                          String resource,
                          String section,
                          String httpVersion,
                          Integer responseCode,
                          long bytesSize) {
        this.hostName = requireNonNull(hostName, "hostname");
        this.userName = requireNonNull(userName, "username");
        this.dateTime = requireNonNull(dateTime, "datetime");
        this.httpMethod = requireNonNull(httpMethod, "httpMethod");
        this.resource = requireNonNull(resource, "resource");
        this.section = requireNonNull(section, "section");
        this.httpVersion = requireNonNull(httpVersion);
        this.responseCode = requireNonNull(responseCode, "responseCode");
        this.bytesSize = bytesSize;
    }

    public boolean isSuccessfulRequest() {
        return responseCode >= 200 && responseCode < 300;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessLogEvent accessLogEvent = (AccessLogEvent) o;
        return bytesSize == accessLogEvent.bytesSize &&
                Objects.equals(hostName, accessLogEvent.hostName) &&
                Objects.equals(userName, accessLogEvent.userName) &&
                Objects.equals(dateTime, accessLogEvent.dateTime) &&
                httpMethod == accessLogEvent.httpMethod &&
                Objects.equals(resource, accessLogEvent.resource) &&
                Objects.equals(section, accessLogEvent.section) &&
                Objects.equals(responseCode, accessLogEvent.responseCode);
    }

    @Override
    public int hashCode() {
        return hash(hostName, userName, dateTime, httpMethod, resource, section, responseCode, bytesSize);
    }

    @Override
    public String toString() {
        return "AccessLog{" +
                "hostName='" + hostName + '\'' +
                ", userName='" + userName + '\'' +
                ", dateTime=" + dateTime +
                ", httpMethod=" + httpMethod +
                ", resource='" + resource + '\'' +
                ", section='" + section + '\'' +
                ", httpVersion='" + httpVersion + '\'' +
                ", responseCode=" + responseCode +
                ", bytesSize=" + bytesSize +
                '}';
    }
}
