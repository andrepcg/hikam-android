package org.jboss.netty.util;

import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class ThreadRenamingRunnable implements Runnable {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(ThreadRenamingRunnable.class);
    private static volatile ThreadNameDeterminer threadNameDeterminer = ThreadNameDeterminer.PROPOSED;
    private final ThreadNameDeterminer determiner;
    private final String proposedThreadName;
    private final Runnable runnable;

    public static ThreadNameDeterminer getThreadNameDeterminer() {
        return threadNameDeterminer;
    }

    public static void setThreadNameDeterminer(ThreadNameDeterminer threadNameDeterminer) {
        if (threadNameDeterminer == null) {
            throw new NullPointerException("threadNameDeterminer");
        }
        threadNameDeterminer = threadNameDeterminer;
    }

    public ThreadRenamingRunnable(Runnable runnable, String proposedThreadName, ThreadNameDeterminer determiner) {
        if (runnable == null) {
            throw new NullPointerException("runnable");
        } else if (proposedThreadName == null) {
            throw new NullPointerException("proposedThreadName");
        } else {
            this.runnable = runnable;
            this.determiner = determiner;
            this.proposedThreadName = proposedThreadName;
        }
    }

    public ThreadRenamingRunnable(Runnable runnable, String proposedThreadName) {
        this(runnable, proposedThreadName, null);
    }

    public void run() {
        Thread currentThread = Thread.currentThread();
        String oldThreadName = currentThread.getName();
        String newThreadName = getNewThreadName(oldThreadName);
        boolean renamed = false;
        if (!oldThreadName.equals(newThreadName)) {
            try {
                currentThread.setName(newThreadName);
                renamed = true;
            } catch (SecurityException e) {
                logger.debug("Failed to rename a thread due to security restriction.", e);
            }
        }
        try {
            this.runnable.run();
        } finally {
            if (renamed) {
                currentThread.setName(oldThreadName);
            }
        }
    }

    private String getNewThreadName(String currentThreadName) {
        String newThreadName = null;
        try {
            ThreadNameDeterminer nameDeterminer = this.determiner;
            if (nameDeterminer == null) {
                nameDeterminer = getThreadNameDeterminer();
            }
            newThreadName = nameDeterminer.determineThreadName(currentThreadName, this.proposedThreadName);
        } catch (Throwable t) {
            logger.warn("Failed to determine the thread name", t);
        }
        if (newThreadName == null) {
            return currentThreadName;
        }
        return newThreadName;
    }
}
