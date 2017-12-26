package com.p2p.shake;

import android.os.Handler;
import java.net.DatagramSocket;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;

public class ShakeThread extends Thread {
    public static final int CLOSE_SERVER = 999;
    public static final int DEFAULT_PORT = 8899;
    public static final int RECEIVE_IPC_INFO = 0;
    public int SEND_TIMES = 10;
    private DatagramSocket broadcast;
    private DatagramChannel channel;
    private Handler handler;
    private boolean isRun;
    private int port = DEFAULT_PORT;
    private Selector selector;
    private DatagramSocket server;

    class C06891 extends Thread {
        C06891() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r8 = this;
            r3 = 0;
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0067 }
            r5 = new java.net.DatagramSocket;	 Catch:{ Exception -> 0x0067 }
            r5.<init>();	 Catch:{ Exception -> 0x0067 }
            r4.broadcast = r5;	 Catch:{ Exception -> 0x0067 }
        L_0x000b:
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0067 }
            r4 = r4.SEND_TIMES;	 Catch:{ Exception -> 0x0067 }
            if (r3 >= r4) goto L_0x007c;
        L_0x0011:
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0067 }
            r4 = r4.isRun;	 Catch:{ Exception -> 0x0067 }
            if (r4 != 0) goto L_0x002f;
        L_0x0019:
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x002a }
            r4 = r4.broadcast;	 Catch:{ Exception -> 0x002a }
            r4.close();	 Catch:{ Exception -> 0x002a }
        L_0x0022:
            r4 = com.p2p.shake.ShakeManager.getInstance();
            r4.stopShaking();
        L_0x0029:
            return;
        L_0x002a:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x0022;
        L_0x002f:
            r3 = r3 + 1;
            r4 = "my";
            r5 = "shake thread send broadcast.";
            android.util.Log.e(r4, r5);	 Catch:{ Exception -> 0x0067 }
            r0 = new com.p2p.shake.ShakeData;	 Catch:{ Exception -> 0x0067 }
            r0.<init>();	 Catch:{ Exception -> 0x0067 }
            r4 = 1;
            r0.setCmd(r4);	 Catch:{ Exception -> 0x0067 }
            r2 = new java.net.DatagramPacket;	 Catch:{ Exception -> 0x0067 }
            r4 = com.p2p.shake.ShakeData.getBytes(r0);	 Catch:{ Exception -> 0x0067 }
            r5 = 64;
            r6 = "255.255.255.255";
            r6 = java.net.InetAddress.getByName(r6);	 Catch:{ Exception -> 0x0067 }
            r7 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0067 }
            r7 = r7.port;	 Catch:{ Exception -> 0x0067 }
            r2.<init>(r4, r5, r6, r7);	 Catch:{ Exception -> 0x0067 }
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0067 }
            r4 = r4.broadcast;	 Catch:{ Exception -> 0x0067 }
            r4.send(r2);	 Catch:{ Exception -> 0x0067 }
            r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            java.lang.Thread.sleep(r4);	 Catch:{ Exception -> 0x0067 }
            goto L_0x000b;
        L_0x0067:
            r1 = move-exception;
            r1.printStackTrace();	 Catch:{ all -> 0x009e }
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0099 }
            r4 = r4.broadcast;	 Catch:{ Exception -> 0x0099 }
            r4.close();	 Catch:{ Exception -> 0x0099 }
        L_0x0074:
            r4 = com.p2p.shake.ShakeManager.getInstance();
            r4.stopShaking();
            goto L_0x0029;
        L_0x007c:
            r4 = "my";
            r5 = "shake thread broadcast end.";
            android.util.Log.e(r4, r5);	 Catch:{ Exception -> 0x0067 }
            r4 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x0094 }
            r4 = r4.broadcast;	 Catch:{ Exception -> 0x0094 }
            r4.close();	 Catch:{ Exception -> 0x0094 }
        L_0x008c:
            r4 = com.p2p.shake.ShakeManager.getInstance();
            r4.stopShaking();
            goto L_0x0029;
        L_0x0094:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x008c;
        L_0x0099:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x0074;
        L_0x009e:
            r4 = move-exception;
            r5 = com.p2p.shake.ShakeThread.this;	 Catch:{ Exception -> 0x00b0 }
            r5 = r5.broadcast;	 Catch:{ Exception -> 0x00b0 }
            r5.close();	 Catch:{ Exception -> 0x00b0 }
        L_0x00a8:
            r5 = com.p2p.shake.ShakeManager.getInstance();
            r5.stopShaking();
            throw r4;
        L_0x00b0:
            r1 = move-exception;
            r1.printStackTrace();
            goto L_0x00a8;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.p2p.shake.ShakeThread.1.run():void");
        }
    }

    public void run() {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find block by offset: 0x00ab in list []
	at jadx.core.utils.BlockUtils.getBlockByOffset(BlockUtils.java:42)
	at jadx.core.dex.instructions.IfNode.initBlocks(IfNode.java:60)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.initBlocksInIfNodes(BlockFinish.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r14 = this;
        r10 = 1;
        r14.isRun = r10;
        r10 = java.nio.channels.Selector.open();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r14.selector = r10;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = java.nio.channels.DatagramChannel.open();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r14.channel = r10;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r14.channel;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = 0;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10.configureBlocking(r11);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r14.channel;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r10.socket();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r14.server = r10;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r14.server;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = new java.net.InetSocketAddress;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r14.port;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11.<init>(r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10.bind(r11);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r14.channel;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = r14.selector;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = 1;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10.register(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r9 = java.nio.ByteBuffer.allocate(r10);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = new com.p2p.shake.ShakeThread$1;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10.start();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x003f:
        r10 = r14.isRun;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r10 == 0) goto L_0x01b2;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x0043:
        r10 = r14.selector;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = 100;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r8 = r10.select(r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r8 <= 0) goto L_0x003f;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x004d:
        r10 = r14.selector;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r6 = r10.selectedKeys();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = r6.iterator();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x0057:
        r11 = r10.hasNext();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r11 == 0) goto L_0x003f;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x005d:
        r5 = r10.next();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r5 = (java.nio.channels.SelectionKey) r5;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r6.remove(r5);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = r5.isReadable();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r11 == 0) goto L_0x0057;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x006c:
        r3 = r5.channel();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r3 = (java.nio.channels.DatagramChannel) r3;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r1 = r3.receive(r9);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r1 = (java.net.InetSocketAddress) r1;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = 1;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r5.interestOps(r11);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r9.flip();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r2 = com.p2p.shake.ShakeData.getShakeData(r9);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = r2.getCmd();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        switch(r11) {
            case 2: goto L_0x00bb;
            default: goto L_0x008a;
        };	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x008a:
        r9.clear();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        goto L_0x0057;
    L_0x008e:
        r4 = move-exception;
        r4.printStackTrace();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = com.p2p.shake.ShakeManager.getInstance();
        r10.stopShaking();
        r10 = r14.handler;
        if (r10 == 0) goto L_0x00ab;
    L_0x009d:
        r7 = new android.os.Message;
        r7.<init>();
        r10 = 17;
        r7.what = r10;
        r10 = r14.handler;
        r10.sendMessage(r7);
    L_0x00ab:
        r10 = r14.server;	 Catch:{ Exception -> 0x01f3 }
        r10.close();	 Catch:{ Exception -> 0x01f3 }
    L_0x00b0:
        r10 = r14.channel;	 Catch:{ Exception -> 0x01f9 }
        r10.close();	 Catch:{ Exception -> 0x01f9 }
    L_0x00b5:
        r10 = r14.selector;	 Catch:{ Exception -> 0x01ff }
        r10.close();	 Catch:{ Exception -> 0x01ff }
    L_0x00ba:
        return;
    L_0x00bb:
        r11 = r2.getError_code();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = 1;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r11 != r12) goto L_0x008a;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x00c2:
        r11 = r14.handler;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r11 == 0) goto L_0x008a;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x00c6:
        r7 = new android.os.Message;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r7.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = 18;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r7.what = r11;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0 = new android.os.Bundle;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "address";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r1.getAddress();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putSerializable(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "id";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = r2.getId();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = "";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.toString();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "name";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = r2.getName();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = "";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.toString();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "flag";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r2.getFlag();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putInt(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "type";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r2.getType();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putInt(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r2.getDevModel();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = r11.equals(r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        if (r11 != 0) goto L_0x018c;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x0133:
        r11 = "model";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r2.getDevModel();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "id";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = r2.getDevP2pID();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = "";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.toString();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x0158:
        r7.setData(r0);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = r14.handler;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11.sendMessage(r7);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        goto L_0x008a;
    L_0x0162:
        r10 = move-exception;
        r11 = com.p2p.shake.ShakeManager.getInstance();
        r11.stopShaking();
        r11 = r14.handler;
        if (r11 == 0) goto L_0x017c;
    L_0x016e:
        r7 = new android.os.Message;
        r7.<init>();
        r11 = 17;
        r7.what = r11;
        r11 = r14.handler;
        r11.sendMessage(r7);
    L_0x017c:
        r11 = r14.server;	 Catch:{ Exception -> 0x0205 }
        r11.close();	 Catch:{ Exception -> 0x0205 }
    L_0x0181:
        r11 = r14.channel;	 Catch:{ Exception -> 0x020b }
        r11.close();	 Catch:{ Exception -> 0x020b }
    L_0x0186:
        r11 = r14.selector;	 Catch:{ Exception -> 0x0211 }
        r11.close();	 Catch:{ Exception -> 0x0211 }
    L_0x018b:
        throw r10;
    L_0x018c:
        r11 = "model";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r2.getDevModel();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "id";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = new java.lang.StringBuilder;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12.<init>();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = r2.getId();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r13 = "";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.append(r13);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r12 = r12.toString();	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r0.putString(r11, r12);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        goto L_0x0158;	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
    L_0x01b2:
        r10 = "my";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r11 = "shake thread end.";	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        android.util.Log.e(r10, r11);	 Catch:{ IOException -> 0x008e, all -> 0x0162 }
        r10 = com.p2p.shake.ShakeManager.getInstance();
        r10.stopShaking();
        r10 = r14.handler;
        if (r10 == 0) goto L_0x01d2;
    L_0x01c4:
        r7 = new android.os.Message;
        r7.<init>();
        r10 = 17;
        r7.what = r10;
        r10 = r14.handler;
        r10.sendMessage(r7);
    L_0x01d2:
        r10 = r14.server;	 Catch:{ Exception -> 0x01e9 }
        r10.close();	 Catch:{ Exception -> 0x01e9 }
    L_0x01d7:
        r10 = r14.channel;	 Catch:{ Exception -> 0x01ee }
        r10.close();	 Catch:{ Exception -> 0x01ee }
    L_0x01dc:
        r10 = r14.selector;	 Catch:{ Exception -> 0x01e3 }
        r10.close();	 Catch:{ Exception -> 0x01e3 }
        goto L_0x00ba;
    L_0x01e3:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x00ba;
    L_0x01e9:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x01d7;
    L_0x01ee:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x01dc;
    L_0x01f3:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x00b0;
    L_0x01f9:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x00b5;
    L_0x01ff:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x00ba;
    L_0x0205:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x0181;
    L_0x020b:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x0186;
    L_0x0211:
        r4 = move-exception;
        r4.printStackTrace();
        goto L_0x018b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.p2p.shake.ShakeThread.run():void");
    }

    public ShakeThread(Handler handler) {
    }

    public void setSearchTime(long time) {
        this.SEND_TIMES = (int) (time / 1000);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void killThread() {
        if (this.isRun) {
            this.selector.wakeup();
            this.isRun = false;
        }
    }
}
