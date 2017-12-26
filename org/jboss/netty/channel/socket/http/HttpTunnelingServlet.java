package org.jboss.netty.channel.socket.http;

import java.io.IOException;
import java.io.PushbackInputStream;
import java.net.SocketAddress;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.local.DefaultLocalClientChannelFactory;
import org.jboss.netty.channel.local.LocalAddress;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public class HttpTunnelingServlet extends HttpServlet {
    static final /* synthetic */ boolean $assertionsDisabled = (!HttpTunnelingServlet.class.desiredAssertionStatus());
    private static final String CONNECT_ATTEMPTS = "connectAttempts";
    private static final String ENDPOINT = "endpoint";
    private static final String RETRY_DELAY = "retryDelay";
    static final InternalLogger logger = InternalLoggerFactory.getInstance(HttpTunnelingServlet.class);
    private static final long serialVersionUID = 4259910275899756070L;
    private volatile ChannelFactory channelFactory;
    private volatile long connectAttempts = 1;
    private volatile SocketAddress remoteAddress;
    private volatile long retryDelay;

    private static final class OutboundConnectionHandler extends SimpleChannelUpstreamHandler {
        private final ServletOutputStream out;

        public OutboundConnectionHandler(ServletOutputStream out) {
            this.out = out;
        }

        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            ChannelBuffer buffer = (ChannelBuffer) e.getMessage();
            synchronized (this) {
                buffer.readBytes(this.out, buffer.readableBytes());
                this.out.flush();
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            if (HttpTunnelingServlet.logger.isWarnEnabled()) {
                HttpTunnelingServlet.logger.warn("Unexpected exception while HTTP tunneling", e.getCause());
            }
            e.getChannel().close();
        }
    }

    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        String endpoint = config.getInitParameter(ENDPOINT);
        if (endpoint == null) {
            throw new ServletException("init-param 'endpoint' must be specified.");
        }
        try {
            this.remoteAddress = parseEndpoint(endpoint.trim());
            try {
                this.channelFactory = createChannelFactory(this.remoteAddress);
                String temp = config.getInitParameter(CONNECT_ATTEMPTS);
                if (temp != null) {
                    try {
                        this.connectAttempts = Long.parseLong(temp);
                        if (this.connectAttempts < 1) {
                            throw new ServletException("init-param 'connectAttempts' must be >= 1. Actual value: " + this.connectAttempts);
                        }
                    } catch (NumberFormatException e) {
                        throw new ServletException("init-param 'connectAttempts' is not a valid number. Actual value: " + temp);
                    }
                }
                temp = config.getInitParameter(RETRY_DELAY);
                if (temp != null) {
                    try {
                        this.retryDelay = Long.parseLong(temp);
                        if (this.retryDelay < 0) {
                            throw new ServletException("init-param 'retryDelay' must be >= 0. Actual value: " + this.retryDelay);
                        }
                    } catch (NumberFormatException e2) {
                        throw new ServletException("init-param 'retryDelay' is not a valid number. Actual value: " + temp);
                    }
                }
            } catch (ServletException e3) {
                throw e3;
            } catch (Exception e4) {
                throw new ServletException("Failed to create a channel factory.", e4);
            }
        } catch (ServletException e32) {
            throw e32;
        } catch (Exception e42) {
            throw new ServletException("Failed to parse an endpoint.", e42);
        }
    }

    protected SocketAddress parseEndpoint(String endpoint) throws Exception {
        if (endpoint.startsWith("local:")) {
            return new LocalAddress(endpoint.substring(6).trim());
        }
        throw new ServletException("Invalid or unknown endpoint: " + endpoint);
    }

    protected ChannelFactory createChannelFactory(SocketAddress remoteAddress) throws Exception {
        if (remoteAddress instanceof LocalAddress) {
            return new DefaultLocalClientChannelFactory();
        }
        throw new ServletException("Unsupported remote address type: " + remoteAddress.getClass().getName());
    }

    public void destroy() {
        try {
            destroyChannelFactory(this.channelFactory);
        } catch (Exception e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to destroy a channel factory.", e);
            }
        }
    }

    protected void destroyChannelFactory(ChannelFactory factory) throws Exception {
        factory.releaseExternalResources();
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    protected void service(javax.servlet.http.HttpServletRequest r19, javax.servlet.http.HttpServletResponse r20) throws javax.servlet.ServletException, java.io.IOException {
        /*
        r18 = this;
        r13 = "POST";
        r14 = r19.getMethod();
        r13 = r13.equalsIgnoreCase(r14);
        if (r13 != 0) goto L_0x0038;
    L_0x000c:
        r13 = logger;
        r13 = r13.isWarnEnabled();
        if (r13 == 0) goto L_0x0030;
    L_0x0014:
        r13 = logger;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "Unallowed method: ";
        r14 = r14.append(r15);
        r15 = r19.getMethod();
        r14 = r14.append(r15);
        r14 = r14.toString();
        r13.warn(r14);
    L_0x0030:
        r13 = 405; // 0x195 float:5.68E-43 double:2.0E-321;
        r0 = r20;
        r0.sendError(r13);
    L_0x0037:
        return;
    L_0x0038:
        r11 = org.jboss.netty.channel.Channels.pipeline();
        r10 = r20.getOutputStream();
        r7 = new org.jboss.netty.channel.socket.http.HttpTunnelingServlet$OutboundConnectionHandler;
        r7.<init>(r10);
        r13 = "handler";
        r11.addLast(r13, r7);
        r0 = r18;
        r13 = r0.channelFactory;
        r4 = r13.newChannel(r11);
        r12 = 0;
        r6 = 0;
    L_0x0054:
        r14 = (long) r12;
        r0 = r18;
        r0 = r0.connectAttempts;
        r16 = r0;
        r13 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1));
        if (r13 >= 0) goto L_0x007d;
    L_0x005f:
        r0 = r18;
        r13 = r0.remoteAddress;
        r13 = r4.connect(r13);
        r6 = r13.awaitUninterruptibly();
        r13 = r6.isSuccess();
        if (r13 != 0) goto L_0x007d;
    L_0x0071:
        r12 = r12 + 1;
        r0 = r18;
        r14 = r0.retryDelay;	 Catch:{ InterruptedException -> 0x007b }
        java.lang.Thread.sleep(r14);	 Catch:{ InterruptedException -> 0x007b }
        goto L_0x0054;
    L_0x007b:
        r13 = move-exception;
        goto L_0x0054;
    L_0x007d:
        r13 = r6.isSuccess();
        if (r13 != 0) goto L_0x00b3;
    L_0x0083:
        r13 = logger;
        r13 = r13.isWarnEnabled();
        if (r13 == 0) goto L_0x00ab;
    L_0x008b:
        r3 = r6.getCause();
        r13 = logger;
        r14 = new java.lang.StringBuilder;
        r14.<init>();
        r15 = "Endpoint unavailable: ";
        r14 = r14.append(r15);
        r15 = r3.getMessage();
        r14 = r14.append(r15);
        r14 = r14.toString();
        r13.warn(r14, r3);
    L_0x00ab:
        r13 = 503; // 0x1f7 float:7.05E-43 double:2.485E-321;
        r0 = r20;
        r0.sendError(r13);
        goto L_0x0037;
    L_0x00b3:
        r9 = 0;
        r13 = 200; // 0xc8 float:2.8E-43 double:9.9E-322;
        r0 = r20;
        r0.setStatus(r13);	 Catch:{ all -> 0x00f3 }
        r13 = "Content-Type";
        r14 = "application/octet-stream";
        r0 = r20;
        r0.setHeader(r13, r14);	 Catch:{ all -> 0x00f3 }
        r13 = "Content-Transfer-Encoding";
        r14 = "binary";
        r0 = r20;
        r0.setHeader(r13, r14);	 Catch:{ all -> 0x00f3 }
        r10.flush();	 Catch:{ all -> 0x00f3 }
        r8 = new java.io.PushbackInputStream;	 Catch:{ all -> 0x00f3 }
        r13 = r19.getInputStream();	 Catch:{ all -> 0x00f3 }
        r8.<init>(r13);	 Catch:{ all -> 0x00f3 }
    L_0x00d9:
        r13 = r4.isConnected();	 Catch:{ all -> 0x00f3 }
        if (r13 == 0) goto L_0x00e5;
    L_0x00df:
        r2 = read(r8);	 Catch:{ EOFException -> 0x00ec }
        if (r2 != 0) goto L_0x00ee;
    L_0x00e5:
        if (r9 != 0) goto L_0x0100;
    L_0x00e7:
        r4.close();
        goto L_0x0037;
    L_0x00ec:
        r5 = move-exception;
        goto L_0x00e5;
    L_0x00ee:
        r9 = r4.write(r2);	 Catch:{ all -> 0x00f3 }
        goto L_0x00d9;
    L_0x00f3:
        r13 = move-exception;
        if (r9 != 0) goto L_0x00fa;
    L_0x00f6:
        r4.close();
    L_0x00f9:
        throw r13;
    L_0x00fa:
        r14 = org.jboss.netty.channel.ChannelFutureListener.CLOSE;
        r9.addListener(r14);
        goto L_0x00f9;
    L_0x0100:
        r13 = org.jboss.netty.channel.ChannelFutureListener.CLOSE;
        r9.addListener(r13);
        goto L_0x0037;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.jboss.netty.channel.socket.http.HttpTunnelingServlet.service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse):void");
    }

    private static ChannelBuffer read(PushbackInputStream in) throws IOException {
        byte[] buf;
        int readBytes;
        int bytesToRead = in.available();
        if (bytesToRead > 0) {
            buf = new byte[bytesToRead];
            readBytes = in.read(buf);
        } else if (bytesToRead != 0) {
            return null;
        } else {
            int b = in.read();
            if (b < 0 || in.available() < 0) {
                return null;
            }
            in.unread(b);
            buf = new byte[in.available()];
            readBytes = in.read(buf);
        }
        if (!$assertionsDisabled && readBytes <= 0) {
            throw new AssertionError();
        } else if (readBytes == buf.length) {
            return ChannelBuffers.wrappedBuffer(buf);
        } else {
            return ChannelBuffers.wrappedBuffer(buf, 0, readBytes);
        }
    }
}
