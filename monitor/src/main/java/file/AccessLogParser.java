package file;

import model.AccessLogEvent;
import model.AccessLogLineEvent;

public interface AccessLogParser {

    AccessLogEvent parse(AccessLogLineEvent line);
}
