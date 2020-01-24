package simulator;

import com.beust.jcommander.Parameter;

public class ConsoleParameters {

    @Parameter(names = "-f",
            description = "File to append values in common log format")
    String filePath = "/tmp/access.log";

    @Parameter(names = "-l", description = "Amount of log lines")
    Integer logLines = 105;

    @Parameter(names = "-d", description = "Delay in seconds between writing a new batch of log lines. Default values is 10s")
    Integer delayInSeconds = 10;

    @Parameter(names = {"-h", "--help"},
            description = "Help/Usage",
            help = true)
    boolean help;

}
