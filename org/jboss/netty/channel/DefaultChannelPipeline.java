package org.jboss.netty.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class DefaultChannelPipeline implements ChannelPipeline {
    static final ChannelSink discardingSink = new DiscardingChannelSink();
    static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
    private volatile Channel channel;
    private volatile DefaultChannelHandlerContext head;
    private final Map<String, DefaultChannelHandlerContext> name2ctx = new HashMap(4);
    private volatile ChannelSink sink;
    private volatile DefaultChannelHandlerContext tail;

    private final class DefaultChannelHandlerContext implements ChannelHandlerContext {
        private volatile Object attachment;
        private final boolean canHandleDownstream;
        private final boolean canHandleUpstream;
        private final ChannelHandler handler;
        private final String name;
        volatile DefaultChannelHandlerContext next;
        volatile DefaultChannelHandlerContext prev;

        DefaultChannelHandlerContext(DefaultChannelHandlerContext prev, DefaultChannelHandlerContext next, String name, ChannelHandler handler) {
            if (name == null) {
                throw new NullPointerException(HttpPostBodyUtil.NAME);
            } else if (handler == null) {
                throw new NullPointerException("handler");
            } else {
                this.canHandleUpstream = handler instanceof ChannelUpstreamHandler;
                this.canHandleDownstream = handler instanceof ChannelDownstreamHandler;
                if (this.canHandleUpstream || this.canHandleDownstream) {
                    this.prev = prev;
                    this.next = next;
                    this.name = name;
                    this.handler = handler;
                    return;
                }
                throw new IllegalArgumentException("handler must be either " + ChannelUpstreamHandler.class.getName() + " or " + ChannelDownstreamHandler.class.getName() + '.');
            }
        }

        public Channel getChannel() {
            return getPipeline().getChannel();
        }

        public ChannelPipeline getPipeline() {
            return DefaultChannelPipeline.this;
        }

        public boolean canHandleDownstream() {
            return this.canHandleDownstream;
        }

        public boolean canHandleUpstream() {
            return this.canHandleUpstream;
        }

        public ChannelHandler getHandler() {
            return this.handler;
        }

        public String getName() {
            return this.name;
        }

        public Object getAttachment() {
            return this.attachment;
        }

        public void setAttachment(Object attachment) {
            this.attachment = attachment;
        }

        public void sendDownstream(ChannelEvent e) {
            DefaultChannelHandlerContext prev = DefaultChannelPipeline.this.getActualDownstreamContext(this.prev);
            if (prev == null) {
                try {
                    DefaultChannelPipeline.this.getSink().eventSunk(DefaultChannelPipeline.this, e);
                    return;
                } catch (Throwable t) {
                    DefaultChannelPipeline.this.notifyHandlerException(e, t);
                    return;
                }
            }
            DefaultChannelPipeline.this.sendDownstream(prev, e);
        }

        public void sendUpstream(ChannelEvent e) {
            DefaultChannelHandlerContext next = DefaultChannelPipeline.this.getActualUpstreamContext(this.next);
            if (next != null) {
                DefaultChannelPipeline.this.sendUpstream(next, e);
            }
        }
    }

    private static final class DiscardingChannelSink implements ChannelSink {
        DiscardingChannelSink() {
        }

        public void eventSunk(ChannelPipeline pipeline, ChannelEvent e) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("Not attached yet; discarding: " + e);
            }
        }

        public void exceptionCaught(ChannelPipeline pipeline, ChannelEvent e, ChannelPipelineException cause) throws Exception {
            throw cause;
        }

        public ChannelFuture execute(ChannelPipeline pipeline, Runnable task) {
            if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                DefaultChannelPipeline.logger.warn("Not attached yet; rejecting: " + task);
            }
            return Channels.failedFuture(pipeline.getChannel(), new RejectedExecutionException("Not attached yet"));
        }
    }

    public Channel getChannel() {
        return this.channel;
    }

    public ChannelSink getSink() {
        ChannelSink sink = this.sink;
        if (sink == null) {
            return discardingSink;
        }
        return sink;
    }

    public void attach(Channel channel, ChannelSink sink) {
        if (channel == null) {
            throw new NullPointerException("channel");
        } else if (sink == null) {
            throw new NullPointerException("sink");
        } else if (this.channel == null && this.sink == null) {
            this.channel = channel;
            this.sink = sink;
        } else {
            throw new IllegalStateException("attached already");
        }
    }

    public boolean isAttached() {
        return this.sink != null;
    }

    public synchronized void addFirst(String name, ChannelHandler handler) {
        if (this.name2ctx.isEmpty()) {
            init(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultChannelHandlerContext oldHead = this.head;
            DefaultChannelHandlerContext newHead = new DefaultChannelHandlerContext(null, oldHead, name, handler);
            callBeforeAdd(newHead);
            oldHead.prev = newHead;
            this.head = newHead;
            this.name2ctx.put(name, newHead);
            callAfterAdd(newHead);
        }
    }

    public synchronized void addLast(String name, ChannelHandler handler) {
        if (this.name2ctx.isEmpty()) {
            init(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultChannelHandlerContext oldTail = this.tail;
            DefaultChannelHandlerContext newTail = new DefaultChannelHandlerContext(oldTail, null, name, handler);
            callBeforeAdd(newTail);
            oldTail.next = newTail;
            this.tail = newTail;
            this.name2ctx.put(name, newTail);
            callAfterAdd(newTail);
        }
    }

    public synchronized void addBefore(String baseName, String name, ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = getContextOrDie(baseName);
        if (ctx == this.head) {
            addFirst(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx.prev, ctx, name, handler);
            callBeforeAdd(newCtx);
            ctx.prev.next = newCtx;
            ctx.prev = newCtx;
            this.name2ctx.put(name, newCtx);
            callAfterAdd(newCtx);
        }
    }

    public synchronized void addAfter(String baseName, String name, ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = getContextOrDie(baseName);
        if (ctx == this.tail) {
            addLast(name, handler);
        } else {
            checkDuplicateName(name);
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(ctx, ctx.next, name, handler);
            callBeforeAdd(newCtx);
            ctx.next.prev = newCtx;
            ctx.next = newCtx;
            this.name2ctx.put(name, newCtx);
            callAfterAdd(newCtx);
        }
    }

    public synchronized void remove(ChannelHandler handler) {
        remove(getContextOrDie(handler));
    }

    public synchronized ChannelHandler remove(String name) {
        return remove(getContextOrDie(name)).getHandler();
    }

    public synchronized <T extends ChannelHandler> T remove(Class<T> handlerType) {
        return remove(getContextOrDie((Class) handlerType)).getHandler();
    }

    private DefaultChannelHandlerContext remove(DefaultChannelHandlerContext ctx) {
        if (this.head == this.tail) {
            callBeforeRemove(ctx);
            this.tail = null;
            this.head = null;
            this.name2ctx.clear();
            callAfterRemove(ctx);
        } else if (ctx == this.head) {
            removeFirst();
        } else if (ctx == this.tail) {
            removeLast();
        } else {
            callBeforeRemove(ctx);
            DefaultChannelHandlerContext prev = ctx.prev;
            DefaultChannelHandlerContext next = ctx.next;
            prev.next = next;
            next.prev = prev;
            this.name2ctx.remove(ctx.getName());
            callAfterRemove(ctx);
        }
        return ctx;
    }

    public synchronized ChannelHandler removeFirst() {
        DefaultChannelHandlerContext oldHead;
        if (this.name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }
        oldHead = this.head;
        if (oldHead == null) {
            throw new NoSuchElementException();
        }
        callBeforeRemove(oldHead);
        if (oldHead.next == null) {
            this.tail = null;
            this.head = null;
            this.name2ctx.clear();
        } else {
            oldHead.next.prev = null;
            this.head = oldHead.next;
            this.name2ctx.remove(oldHead.getName());
        }
        callAfterRemove(oldHead);
        return oldHead.getHandler();
    }

    public synchronized ChannelHandler removeLast() {
        DefaultChannelHandlerContext oldTail;
        if (this.name2ctx.isEmpty()) {
            throw new NoSuchElementException();
        }
        oldTail = this.tail;
        if (oldTail == null) {
            throw new NoSuchElementException();
        }
        callBeforeRemove(oldTail);
        if (oldTail.prev == null) {
            this.tail = null;
            this.head = null;
            this.name2ctx.clear();
        } else {
            oldTail.prev.next = null;
            this.tail = oldTail.prev;
            this.name2ctx.remove(oldTail.getName());
        }
        callAfterRemove(oldTail);
        return oldTail.getHandler();
    }

    public synchronized void replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
        replace(getContextOrDie(oldHandler), newName, newHandler);
    }

    public synchronized ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
        return replace(getContextOrDie(oldName), newName, newHandler);
    }

    public synchronized <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
        return replace(getContextOrDie((Class) oldHandlerType), newName, newHandler);
    }

    private ChannelHandler replace(DefaultChannelHandlerContext ctx, String newName, ChannelHandler newHandler) {
        if (ctx == this.head) {
            removeFirst();
            addFirst(newName, newHandler);
        } else if (ctx == this.tail) {
            removeLast();
            addLast(newName, newHandler);
        } else {
            boolean sameName = ctx.getName().equals(newName);
            if (!sameName) {
                checkDuplicateName(newName);
            }
            DefaultChannelHandlerContext prev = ctx.prev;
            DefaultChannelHandlerContext next = ctx.next;
            DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(prev, next, newName, newHandler);
            callBeforeRemove(ctx);
            callBeforeAdd(newCtx);
            prev.next = newCtx;
            next.prev = newCtx;
            if (!sameName) {
                this.name2ctx.remove(ctx.getName());
            }
            this.name2ctx.put(newName, newCtx);
            ChannelHandlerLifeCycleException removeException = null;
            ChannelHandlerLifeCycleException addException = null;
            boolean removed = false;
            try {
                callAfterRemove(ctx);
                removed = true;
            } catch (ChannelHandlerLifeCycleException e) {
                removeException = e;
            }
            boolean added = false;
            try {
                callAfterAdd(newCtx);
                added = true;
            } catch (ChannelHandlerLifeCycleException e2) {
                addException = e2;
            }
            if (!removed && !added) {
                logger.warn(removeException.getMessage(), removeException);
                logger.warn(addException.getMessage(), addException);
                throw new ChannelHandlerLifeCycleException("Both " + ctx.getHandler().getClass().getName() + ".afterRemove() and " + newCtx.getHandler().getClass().getName() + ".afterAdd() failed; see logs.");
            } else if (!removed) {
                throw removeException;
            } else if (!added) {
                throw addException;
            }
        }
        return ctx.getHandler();
    }

    private static void callBeforeAdd(ChannelHandlerContext ctx) {
        if (ctx.getHandler() instanceof LifeCycleAwareChannelHandler) {
            LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler) ctx.getHandler();
            try {
                h.beforeAdd(ctx);
            } catch (Throwable t) {
                ChannelHandlerLifeCycleException channelHandlerLifeCycleException = new ChannelHandlerLifeCycleException(h.getClass().getName() + ".beforeAdd() has thrown an exception; not adding.", t);
            }
        }
    }

    private void callAfterAdd(ChannelHandlerContext ctx) {
        LifeCycleAwareChannelHandler h;
        if (ctx.getHandler() instanceof LifeCycleAwareChannelHandler) {
            h = (LifeCycleAwareChannelHandler) ctx.getHandler();
            try {
                h.afterAdd(ctx);
                return;
            } catch (Throwable t2) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to remove a handler: " + ctx.getName(), t2);
                }
            }
        } else {
            return;
        }
        ChannelHandlerLifeCycleException channelHandlerLifeCycleException;
        if (removed) {
            channelHandlerLifeCycleException = new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterAdd() has thrown an exception; removed.", t);
        } else {
            channelHandlerLifeCycleException = new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterAdd() has thrown an exception; also failed to remove.", t);
        }
    }

    private static void callBeforeRemove(ChannelHandlerContext ctx) {
        if (ctx.getHandler() instanceof LifeCycleAwareChannelHandler) {
            LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler) ctx.getHandler();
            try {
                h.beforeRemove(ctx);
            } catch (Throwable t) {
                ChannelHandlerLifeCycleException channelHandlerLifeCycleException = new ChannelHandlerLifeCycleException(h.getClass().getName() + ".beforeRemove() has thrown an exception; not removing.", t);
            }
        }
    }

    private static void callAfterRemove(ChannelHandlerContext ctx) {
        if (ctx.getHandler() instanceof LifeCycleAwareChannelHandler) {
            LifeCycleAwareChannelHandler h = (LifeCycleAwareChannelHandler) ctx.getHandler();
            try {
                h.afterRemove(ctx);
            } catch (Throwable t) {
                ChannelHandlerLifeCycleException channelHandlerLifeCycleException = new ChannelHandlerLifeCycleException(h.getClass().getName() + ".afterRemove() has thrown an exception.", t);
            }
        }
    }

    public synchronized ChannelHandler getFirst() {
        ChannelHandler channelHandler;
        DefaultChannelHandlerContext head = this.head;
        if (head == null) {
            channelHandler = null;
        } else {
            channelHandler = head.getHandler();
        }
        return channelHandler;
    }

    public synchronized ChannelHandler getLast() {
        ChannelHandler channelHandler;
        DefaultChannelHandlerContext tail = this.tail;
        if (tail == null) {
            channelHandler = null;
        } else {
            channelHandler = tail.getHandler();
        }
        return channelHandler;
    }

    public synchronized ChannelHandler get(String name) {
        ChannelHandler channelHandler;
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) this.name2ctx.get(name);
        if (ctx == null) {
            channelHandler = null;
        } else {
            channelHandler = ctx.getHandler();
        }
        return channelHandler;
    }

    public synchronized <T extends ChannelHandler> T get(Class<T> handlerType) {
        T t;
        ChannelHandlerContext ctx = getContext((Class) handlerType);
        if (ctx == null) {
            t = null;
        } else {
            t = ctx.getHandler();
        }
        return t;
    }

    public synchronized ChannelHandlerContext getContext(String name) {
        if (name == null) {
            throw new NullPointerException(HttpPostBodyUtil.NAME);
        }
        return (ChannelHandlerContext) this.name2ctx.get(name);
    }

    public synchronized ChannelHandlerContext getContext(ChannelHandler handler) {
        ChannelHandlerContext ctx;
        if (handler != null) {
            if (!this.name2ctx.isEmpty()) {
                ctx = this.head;
                while (ctx.getHandler() != handler) {
                    ctx = ctx.next;
                    if (ctx == null) {
                        ctx = null;
                        break;
                    }
                }
            }
            ctx = null;
        } else {
            throw new NullPointerException("handler");
        }
        return ctx;
    }

    public synchronized ChannelHandlerContext getContext(Class<? extends ChannelHandler> handlerType) {
        ChannelHandlerContext ctx;
        if (handlerType != null) {
            if (!this.name2ctx.isEmpty()) {
                ctx = this.head;
                while (!handlerType.isAssignableFrom(ctx.getHandler().getClass())) {
                    ctx = ctx.next;
                    if (ctx == null) {
                        ctx = null;
                        break;
                    }
                }
            }
            ctx = null;
        } else {
            throw new NullPointerException("handlerType");
        }
        return ctx;
    }

    public List<String> getNames() {
        List<String> list = new ArrayList();
        if (!this.name2ctx.isEmpty()) {
            DefaultChannelHandlerContext ctx = this.head;
            do {
                list.add(ctx.getName());
                ctx = ctx.next;
            } while (ctx != null);
        }
        return list;
    }

    public Map<String, ChannelHandler> toMap() {
        Map<String, ChannelHandler> map = new LinkedHashMap();
        if (!this.name2ctx.isEmpty()) {
            DefaultChannelHandlerContext ctx = this.head;
            do {
                map.put(ctx.getName(), ctx.getHandler());
                ctx = ctx.next;
            } while (ctx != null);
        }
        return map;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getSimpleName());
        buf.append('{');
        DefaultChannelHandlerContext ctx = this.head;
        if (ctx != null) {
            while (true) {
                buf.append('(');
                buf.append(ctx.getName());
                buf.append(" = ");
                buf.append(ctx.getHandler().getClass().getName());
                buf.append(')');
                ctx = ctx.next;
                if (ctx == null) {
                    break;
                }
                buf.append(", ");
            }
        }
        buf.append('}');
        return buf.toString();
    }

    public void sendUpstream(ChannelEvent e) {
        DefaultChannelHandlerContext head = getActualUpstreamContext(this.head);
        if (head != null) {
            sendUpstream(head, e);
        } else if (logger.isWarnEnabled()) {
            logger.warn("The pipeline contains no upstream handlers; discarding: " + e);
        }
    }

    void sendUpstream(DefaultChannelHandlerContext ctx, ChannelEvent e) {
        try {
            ((ChannelUpstreamHandler) ctx.getHandler()).handleUpstream(ctx, e);
        } catch (Throwable t) {
            notifyHandlerException(e, t);
        }
    }

    public void sendDownstream(ChannelEvent e) {
        DefaultChannelHandlerContext tail = getActualDownstreamContext(this.tail);
        if (tail == null) {
            try {
                getSink().eventSunk(this, e);
                return;
            } catch (Throwable t) {
                notifyHandlerException(e, t);
                return;
            }
        }
        sendDownstream(tail, e);
    }

    void sendDownstream(DefaultChannelHandlerContext ctx, ChannelEvent e) {
        if (e instanceof UpstreamMessageEvent) {
            throw new IllegalArgumentException("cannot send an upstream event to downstream");
        }
        try {
            ((ChannelDownstreamHandler) ctx.getHandler()).handleDownstream(ctx, e);
        } catch (Throwable t) {
            e.getFuture().setFailure(t);
            notifyHandlerException(e, t);
        }
    }

    private DefaultChannelHandlerContext getActualUpstreamContext(DefaultChannelHandlerContext ctx) {
        if (ctx == null) {
            return null;
        }
        DefaultChannelHandlerContext realCtx = ctx;
        while (!realCtx.canHandleUpstream()) {
            realCtx = realCtx.next;
            if (realCtx == null) {
                return null;
            }
        }
        return realCtx;
    }

    private DefaultChannelHandlerContext getActualDownstreamContext(DefaultChannelHandlerContext ctx) {
        if (ctx == null) {
            return null;
        }
        DefaultChannelHandlerContext realCtx = ctx;
        while (!realCtx.canHandleDownstream()) {
            realCtx = realCtx.prev;
            if (realCtx == null) {
                return null;
            }
        }
        return realCtx;
    }

    public ChannelFuture execute(Runnable task) {
        return getSink().execute(this, task);
    }

    protected void notifyHandlerException(ChannelEvent e, Throwable t) {
        if (!(e instanceof ExceptionEvent)) {
            ChannelPipelineException pe;
            if (t instanceof ChannelPipelineException) {
                pe = (ChannelPipelineException) t;
            } else {
                pe = new ChannelPipelineException(t);
            }
            try {
                this.sink.exceptionCaught(this, e, pe);
            } catch (Exception e1) {
                if (logger.isWarnEnabled()) {
                    logger.warn("An exception was thrown by an exception handler.", e1);
                }
            }
        } else if (logger.isWarnEnabled()) {
            logger.warn("An exception was thrown by a user handler while handling an exception event (" + e + ')', t);
        }
    }

    private void init(String name, ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = new DefaultChannelHandlerContext(null, null, name, handler);
        callBeforeAdd(ctx);
        this.tail = ctx;
        this.head = ctx;
        this.name2ctx.clear();
        this.name2ctx.put(name, ctx);
        callAfterAdd(ctx);
    }

    private void checkDuplicateName(String name) {
        if (this.name2ctx.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate handler name: " + name);
        }
    }

    private DefaultChannelHandlerContext getContextOrDie(String name) {
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) getContext(name);
        if (ctx != null) {
            return ctx;
        }
        throw new NoSuchElementException(name);
    }

    private DefaultChannelHandlerContext getContextOrDie(ChannelHandler handler) {
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) getContext(handler);
        if (ctx != null) {
            return ctx;
        }
        throw new NoSuchElementException(handler.getClass().getName());
    }

    private DefaultChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
        DefaultChannelHandlerContext ctx = (DefaultChannelHandlerContext) getContext((Class) handlerType);
        if (ctx != null) {
            return ctx;
        }
        throw new NoSuchElementException(handlerType.getName());
    }
}
