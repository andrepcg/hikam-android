package com.jwkj.utils;

import android.os.Handler;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPHelper {
    public static final int HANDLER_MESSAGE_BIND_ERROR = 1;
    public static final int HANDLER_MESSAGE_RECEIVE_HIKAM_MSG = 3;
    public static final int HANDLER_MESSAGE_RECEIVE_MSG = 2;
    public Boolean IsThreadDisable = Boolean.valueOf(false);
    DatagramSocket datagramSocket = null;
    public Handler mHandler;
    InetAddress mInetAddress;
    public int port;

    class C05691 implements Runnable {
        public void run() {
            /* JADX: method processing error */
/*
Error: java.util.NoSuchElementException
	at java.util.HashMap$HashIterator.nextNode(HashMap.java:1431)
	at java.util.HashMap$KeyIterator.next(HashMap.java:1453)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.applyRemove(BlockFinallyExtract.java:535)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.extractFinally(BlockFinallyExtract.java:175)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.processExceptionHandler(BlockFinallyExtract.java:79)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:51)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r13 = this;
            r10 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
            r8 = new byte[r10];
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11 = new java.net.DatagramSocket;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r12 = com.jwkj.utils.UDPHelper.this;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r12 = r12.port;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11.<init>(r12);	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r10.datagramSocket = r11;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r10 = "port";	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11.<init>();	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r12 = "port=";	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11 = r11.append(r12);	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r12 = com.jwkj.utils.UDPHelper.this;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r12 = r12.port;	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11 = r11.append(r12);	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            r11 = r11.toString();	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
            android.util.Log.e(r10, r11);	 Catch:{ Exception -> 0x0110, SocketException -> 0x0143 }
        L_0x002d:
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.datagramSocket;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.setBroadcast(r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r4 = new java.net.DatagramPacket;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r8.length;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r4.<init>(r8, r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x003b:
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.IsThreadDisable;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.booleanValue();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 != 0) goto L_0x00fd;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x0045:
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.datagramSocket;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.receive(r4);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r4.getAddress();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.mInetAddress = r11;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "ip_address";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "mInetAddress=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = r12.mInetAddress;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.toString();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            android.util.Log.e(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r3 = r4.getData();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "setwifi";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = java.util.Arrays.toString(r3);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            android.util.Log.e(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 0;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r3[r10];	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 != r11) goto L_0x0157;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x0083:
            r10 = 16;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r1 = com.jwkj.utils.UDPHelper.bytesToInt(r3, r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 24;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r6 = com.jwkj.utils.UDPHelper.bytesToInt(r3, r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "setwifi";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "contactId=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r1);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "--frag=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r6);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.toString();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            android.util.Log.e(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 == 0) goto L_0x0157;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x00b7:
            r9 = new android.os.Message;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 2;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.what = r10;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0 = new android.os.Bundle;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "contactId";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = java.lang.String.valueOf(r1);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "frag";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = java.lang.String.valueOf(r6);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mInetAddress;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r7 = java.lang.String.valueOf(r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "ipFlag";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = ".";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r7.lastIndexOf(r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11 + 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = r7.length();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r7.substring(r11, r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.setData(r0);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.sendMessage(r9);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x00fd:
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            if (r10 == 0) goto L_0x010f;
        L_0x0103:
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            r10.close();
            r10 = com.jwkj.utils.UDPHelper.this;
            r11 = 0;
            r10.datagramSocket = r11;
        L_0x010f:
            return;
        L_0x0110:
            r5 = move-exception;
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 57521; // 0xe0b1 float:8.0604E-41 double:2.8419E-319;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.port = r11;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = new java.net.DatagramSocket;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = r12.port;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11.<init>(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.datagramSocket = r11;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "port";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "port=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = r12.port;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.toString();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            android.util.Log.e(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            goto L_0x002d;
        L_0x0143:
            r10 = move-exception;
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            if (r10 == 0) goto L_0x010f;
        L_0x014a:
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            r10.close();
            r10 = com.jwkj.utils.UDPHelper.this;
            r11 = 0;
            r10.datagramSocket = r11;
            goto L_0x010f;
        L_0x0157:
            r10 = 0;
            r10 = r3[r10];	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 2;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 != r11) goto L_0x003b;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x015d:
            r10 = 24;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r6 = com.jwkj.utils.UDPHelper.bytesToInt(r3, r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 28;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 16;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r2 = com.jwkj.utils.UDPHelper.bytesToString(r3, r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 44;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 24;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r1 = com.jwkj.utils.UDPHelper.bytesToString(r3, r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "setwifi";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = new java.lang.StringBuilder;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "contactModel=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r2);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = "--contactId=";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.append(r1);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11.toString();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            android.util.Log.e(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 == 0) goto L_0x003b;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x019b:
            r9 = new android.os.Message;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = 3;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.what = r10;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0 = new android.os.Bundle;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.<init>();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "contactModel";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r2);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "contactId";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r1);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "frag";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = java.lang.String.valueOf(r6);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mInetAddress;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r7 = java.lang.String.valueOf(r10);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = "ipFlag";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = ".";	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r7.lastIndexOf(r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r11 + 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r12 = r7.length();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = r7.substring(r11, r12);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r0.putString(r10, r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r9.setData(r0);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.sendMessage(r9);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            goto L_0x00fd;
        L_0x01e4:
            r5 = move-exception;
            r5.printStackTrace();	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = java.lang.Boolean.valueOf(r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.IsThreadDisable = r11;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            if (r10 == 0) goto L_0x01ff;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x01f7:
            r10 = com.jwkj.utils.UDPHelper.this;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10 = r10.mHandler;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r11 = 1;	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
            r10.sendEmptyMessage(r11);	 Catch:{ SocketException -> 0x0143, Exception -> 0x01e4, all -> 0x0213 }
        L_0x01ff:
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            if (r10 == 0) goto L_0x010f;
        L_0x0205:
            r10 = com.jwkj.utils.UDPHelper.this;
            r10 = r10.datagramSocket;
            r10.close();
            r10 = com.jwkj.utils.UDPHelper.this;
            r11 = 0;
            r10.datagramSocket = r11;
            goto L_0x010f;
        L_0x0213:
            r10 = move-exception;
            r11 = com.jwkj.utils.UDPHelper.this;
            r11 = r11.datagramSocket;
            if (r11 == 0) goto L_0x0226;
        L_0x021a:
            r11 = com.jwkj.utils.UDPHelper.this;
            r11 = r11.datagramSocket;
            r11.close();
            r11 = com.jwkj.utils.UDPHelper.this;
            r12 = 0;
            r11.datagramSocket = r12;
        L_0x0226:
            throw r10;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.jwkj.utils.UDPHelper.1.run():void");
        }

        C05691() {
        }
    }

    public UDPHelper(int port) {
        this.port = port;
    }

    public void setCallBack(Handler handler) {
        this.mHandler = handler;
    }

    public void StartListen() {
        new Thread(new C05691()).start();
    }

    public static int bytesToInt(byte[] src, int offset) {
        return (((src[offset] & 255) | ((src[offset + 1] & 255) << 8)) | ((src[offset + 2] & 255) << 16)) | ((src[offset + 3] & 255) << 24);
    }

    public static String bytesToString(byte[] src, int offset, int length) {
        byte[] tmp = new byte[length];
        for (int i = 0; i < length; i++) {
            tmp[i] = src[offset + i];
        }
        return new String(tmp).trim();
    }

    public void StopListen() {
        this.IsThreadDisable = Boolean.valueOf(true);
        if (this.datagramSocket != null) {
            this.datagramSocket.close();
            this.datagramSocket = null;
        }
    }
}
