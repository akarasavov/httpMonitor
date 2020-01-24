package application;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

import java.io.File;
import java.time.Duration;

public class ConsoleParameters {

    @Parameter(names = "-f",
            description = "file path to access log",
            validateWith = FileValidator.class)
    String filePath = "/tmp/access.log";

    public static class FileValidator implements IParameterValidator {

        @Override
        public void validate(String name, String value) throws ParameterException {
            boolean exists = new File(value).exists();
            if (!exists) {
                throw new ParameterException("File=" + value + " don't exists");
            }
        }
    }

    @Parameter(names = "-t", description = "alert threshold - hits per second")
    Integer maximTrafficThreshold = 10;

    @Parameter(names = {"-h", "--help"},
            description = "Help/Usage",
            help = true)
    boolean help;

    @Parameter(names = "-ai", description = "alert interval in seconds")
    Integer alertInterval = 120;

    @Parameter(names = "-ti", description = "tic interval in seconds")
    Integer ticInterval = 10;
}
