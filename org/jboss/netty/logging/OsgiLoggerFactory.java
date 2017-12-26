package org.jboss.netty.logging;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiLoggerFactory extends InternalLoggerFactory {
    private final InternalLoggerFactory fallback;
    volatile LogService logService;
    private final ServiceTracker logServiceTracker;

    public OsgiLoggerFactory(BundleContext ctx) {
        this(ctx, null);
    }

    public OsgiLoggerFactory(BundleContext ctx, InternalLoggerFactory fallback) {
        if (ctx == null) {
            throw new NullPointerException("ctx");
        }
        if (fallback == null) {
            fallback = InternalLoggerFactory.getDefaultFactory();
            if (fallback instanceof OsgiLoggerFactory) {
                fallback = new JdkLoggerFactory();
            }
        }
        this.fallback = fallback;
        this.logServiceTracker = new ServiceTracker(ctx, "org.osgi.service.log.LogService", null) {
            public Object addingService(ServiceReference reference) {
                LogService service = (LogService) super.addingService(reference);
                OsgiLoggerFactory.this.logService = service;
                return service;
            }

            public void removedService(ServiceReference reference, Object service) {
                OsgiLoggerFactory.this.logService = null;
            }
        };
        this.logServiceTracker.open();
    }

    public InternalLoggerFactory getFallback() {
        return this.fallback;
    }

    public LogService getLogService() {
        return this.logService;
    }

    public void destroy() {
        this.logService = null;
        this.logServiceTracker.close();
    }

    public InternalLogger newInstance(String name) {
        return new OsgiLogger(this, name, this.fallback.newInstance(name));
    }
}
