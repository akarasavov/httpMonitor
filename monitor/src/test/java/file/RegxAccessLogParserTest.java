package file;

import eventbus.HttpMonitorEventBus;
import model.AccessLogEvent;
import model.AccessLogLineEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static file.HttpMethod.GET;
import static file.HttpMethod.POST;

public class RegxAccessLogParserTest {

    AccessLogParser logParser = new RegxAccessLogParser(Mockito.mock(HttpMonitorEventBus.class));

    @Test
    public void parse_access_log_line_in_common_log_format() {
        AccessLogLineEvent line1 = new AccessLogLineEvent("127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET /report HTTP/1.0\" 200 123");
        AccessLogLineEvent line2 = new AccessLogLineEvent("255.0.0.1 - frank [09/May/2018:16:00:42 +0000] \"POST /api/user/test HTTP/1.0\" 200 34");

        AccessLogEvent accessLogEvent1 = new AccessLogEvent("127.0.0.1",
                "james",
                LocalDateTime.of(2018, 5, 9, 16, 0, 39),
                GET,
                "/report",
                "/report",
                "HTTP/1.0", 200,
                123);

        AccessLogEvent accessLogEvent2 = new AccessLogEvent("255.0.0.1",
                "frank",
                LocalDateTime.of(2018, 5, 9, 16, 0, 42),
                POST,
                "/api/user/test",
                "/api",
                "HTTP/1.0", 200,
                34);

        Assert.assertEquals(accessLogEvent1, logParser.parse(line1));
        Assert.assertEquals(accessLogEvent2, logParser.parse(line2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_parse_line_with_illegal_http_method() {
        AccessLogLineEvent line = new AccessLogLineEvent("127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"OFFER /report HTTP/1.0\" 200 123");
        logParser.parse(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_parse_line_without_date() {
        AccessLogLineEvent line = new AccessLogLineEvent("127.0.0.1 - james test \"GET /report HTTP/1.0\" 200 123");
        logParser.parse(line);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_parse_line_without_resource() {
        AccessLogLineEvent line = new AccessLogLineEvent("127.0.0.1 - james [09/May/2018:16:00:39 +0000] \"GET HTTP/1.0\" 200 123");
        logParser.parse(line);
    }

}