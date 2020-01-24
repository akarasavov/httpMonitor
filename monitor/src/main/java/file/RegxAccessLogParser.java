package file;

import com.google.common.eventbus.Subscribe;
import eventbus.HttpMonitorEventBus;
import model.AccessLogEvent;
import model.AccessLogLineEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxAccessLogParser implements AccessLogParser {
    private final static Logger logger = LoggerFactory.getLogger(RegxAccessLogParser.class);
    private static final Pattern PATTERN =
            Pattern.compile("^(\\S+) (\\S+) (\\S+) \\[([^]]+)] \"([A-Z]+) ([^ \"]+) ?([^\"]+)?\" ([0-9]{3}) ([0-9]+|-)$");

    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss");
    private final HttpMonitorEventBus evenBus;

    public RegxAccessLogParser(HttpMonitorEventBus eventBus) {
        eventBus.register(this);
        this.evenBus = eventBus;
    }

    @Subscribe
    public void handleEvent(AccessLogLineEvent line) {
        try {
            evenBus.publishEvent(parse(line));
        } catch (IllegalArgumentException ex) {
            logger.error("Input {} is in not in common log format", line, ex);
        }
    }

    @Override
    public AccessLogEvent parse(AccessLogLineEvent line) {
        Matcher matcher = PATTERN.matcher(line.value);
        if (matcher.matches() && matcher.groupCount() == 9) {
            try {
                String hostName = matcher.group(1);
                String userName = matcher.group(3);
                LocalDateTime dateTime = dateTimeFormat.parse(matcher.group(4))
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                HttpMethod httpMethod = HttpMethod.resolve(matcher.group(5));
                String resource = matcher.group(6);
                String httpVersion = matcher.group(7);
                Integer responseCode = Integer.valueOf(matcher.group(8));
                long bytesSize = Long.parseLong(matcher.group(9));
                String section = getSection(resource);
                return new AccessLogEvent(hostName,
                        userName,
                        dateTime,
                        httpMethod,
                        resource,
                        section,
                        httpVersion,
                        responseCode,
                        bytesSize);
            } catch (Exception ex) {
                throw new IllegalArgumentException("Argument=" + line + " is not in common log format");
            }
        }
        throw new IllegalArgumentException("Argument=" + line + " is not in common log format");
    }

    private String getSection(String resource) {
        int secondIndex = resource.indexOf("/", 1);
        if (secondIndex < 0) {
            return resource;
        }
        return resource.substring(0, secondIndex);
    }
}
