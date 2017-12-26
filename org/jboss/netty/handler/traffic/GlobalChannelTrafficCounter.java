package org.jboss.netty.handler.traffic;

import java.util.concurrent.TimeUnit;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

public class GlobalChannelTrafficCounter extends TrafficCounter {

    private static final class MixedTrafficMonitoringTask implements TimerTask {
        private final TrafficCounter counter;
        private final GlobalChannelTrafficShapingHandler trafficShapingHandler1;

        MixedTrafficMonitoringTask(GlobalChannelTrafficShapingHandler trafficShapingHandler, TrafficCounter counter) {
            this.trafficShapingHandler1 = trafficShapingHandler;
            this.counter = counter;
        }

        public void run(Timeout timeout) throws Exception {
            if (this.counter.monitorActive) {
                long newLastTime = TrafficCounter.milliSecondFromNano();
                this.counter.resetAccounting(newLastTime);
                for (PerChannel perChannel : this.trafficShapingHandler1.channelQueues.values()) {
                    perChannel.channelTrafficCounter.resetAccounting(newLastTime);
                }
                this.trafficShapingHandler1.doAccounting(this.counter);
                this.counter.timer.newTimeout(this, this.counter.checkInterval.get(), TimeUnit.MILLISECONDS);
            }
        }
    }

    public GlobalChannelTrafficCounter(GlobalChannelTrafficShapingHandler trafficShapingHandler, Timer timer, String name, long checkInterval) {
        super(trafficShapingHandler, timer, name, checkInterval);
        if (timer == null) {
            throw new IllegalArgumentException("Timer must not be null");
        }
    }

    public synchronized void start() {
        if (!this.monitorActive) {
            this.lastTime.set(TrafficCounter.milliSecondFromNano());
            if (this.checkInterval.get() > 0) {
                this.monitorActive = true;
                this.timerTask = new MixedTrafficMonitoringTask((GlobalChannelTrafficShapingHandler) this.trafficShapingHandler, this);
                this.timeout = this.timer.newTimeout(this.timerTask, this.checkInterval.get(), TimeUnit.MILLISECONDS);
            }
        }
    }

    public synchronized void stop() {
        if (this.monitorActive) {
            this.monitorActive = false;
            resetAccounting(TrafficCounter.milliSecondFromNano());
            this.trafficShapingHandler.doAccounting(this);
            if (this.timeout != null) {
                this.timeout.cancel();
            }
        }
    }

    public void resetCumulativeTime() {
        for (PerChannel perChannel : ((GlobalChannelTrafficShapingHandler) this.trafficShapingHandler).channelQueues.values()) {
            perChannel.channelTrafficCounter.resetCumulativeTime();
        }
        super.resetCumulativeTime();
    }
}
