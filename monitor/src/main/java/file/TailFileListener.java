package file;

import eventbus.HttpMonitorEventBus;
import model.AccessLogLineEvent;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;

public class TailFileListener extends TailerListenerAdapter implements FileListener {

    private final Logger logger = LoggerFactory.getLogger(TailFileListener.class);
    private final Tailer tailer;
    private final HttpMonitorEventBus eventBus;
    private final File target;

    public TailFileListener(File target, Duration delay, HttpMonitorEventBus eventBus) {
        this.tailer = new Tailer(target, this, delay.toMillis(), true);
        this.target = target;
        this.eventBus = eventBus;
    }

    @Override
    public void start() {
        new Thread(tailer).start();
    }

    @Override
    public void close() {
        tailer.stop();
    }

    @Override
    public void handle(String line) {
        if (!line.trim().isEmpty()) {
            eventBus.publishEvent(new AccessLogLineEvent(line));
        }
    }

    @Override
    public void fileNotFound() {
        logger.error("There is not file={}", target.getAbsolutePath());
        System.exit(1);
    }

    @Override
    public void handle(Exception ex) {
        logger.error("File channel error", ex);
        System.exit(1);
    }
}
