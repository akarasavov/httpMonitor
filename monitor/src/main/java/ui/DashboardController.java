package ui;

import com.google.common.eventbus.Subscribe;
import eventbus.HttpMonitorEventBus;
import model.AlertEvent;
import model.TrafficStatisticEvent;

import java.time.Duration;

public class DashboardController {

    private final DashboardView dashboardView;

    public DashboardController(HttpMonitorEventBus eventBus) {
        eventBus.register(this);
        this.dashboardView = new DashboardView();
        dashboardView.start();
    }

    @Subscribe
    public void handleAlertStatistics(AlertEvent statistic){
        dashboardView.updateAlertField(statistic);
    }

    @Subscribe
    public void handleTrafficStatistic(TrafficStatisticEvent statistic){
        dashboardView.updateStatistics(statistic);
    }


}
