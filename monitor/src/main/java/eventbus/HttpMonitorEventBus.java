package eventbus;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpMonitorEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus;
    private Logger logger = LoggerFactory.getLogger(HttpMonitorEventBus.class);

    public HttpMonitorEventBus() {
        this.eventBus = new EventBus(this);
    }

    public void register(Object subscriber) {
        eventBus.register(subscriber);
    }

    public void unregister(Object subscriber) {
        eventBus.unregister(subscriber);
    }

    public void publishEvent(Object event) {
        eventBus.post(event);
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        logger.warn("Error in subscriber={}", context.getSubscriber(), exception);
    }
}
