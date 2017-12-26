package com.p2p.shake;

import android.util.Log;
import java.nio.ByteBuffer;

public class ShakeData {
    public static final int DEFAULT_LEFT_LENGTH = 28;
    public static final int DEFAULT_RIGHT_COUNT = 0;
    public static final int DEFAULT_STRING_PARAMETER_LENGTH = 64;
    public static final int iCustomerID = 0;
    private int cmd;
    private String devModel;
    private String devP2pID;
    private int error_code;
    private int flag;
    private int id;
    private int leftlength;
    private String name;
    private int rightCount;
    private int type;

    public static class Cmd {
        public static final int GET_DEVICE_LIST = 1;
        public static final int NO_ASSOCIATED_DATA_PACKET = 9999;
        public static final int RECEIVE_DEVICE_LIST = 2;

        public static int[] getCmds() {
            return new int[]{1, 2};
        }
    }

    public static byte[] getBytes(ShakeData data) {
        if (data.getLeftlength() == 0) {
            data.setLeftlength(28);
        }
        if (data.getRightCount() == 0) {
            data.setRightCount(0);
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(data.getCmd());
        buffer.putInt(data.getError_code());
        buffer.putInt(data.getLeftlength());
        buffer.putInt(data.getRightCount());
        buffer.putInt(data.getId());
        buffer.putInt(data.getType());
        buffer.putInt(data.getFlag());
        byte[] bytes = buffer.array();
        buffer.clear();
        return bytes;
    }

    public static ShakeData getShakeData(ByteBuffer buffer) {
        ShakeData data = new ShakeData();
        boolean isTrue = false;
        try {
            for (int valueOf : Cmd.getCmds()) {
                if (buffer.getInt(0) == Integer.valueOf(valueOf).intValue()) {
                    isTrue = true;
                    break;
                }
            }
            if (isTrue) {
                data.setCmd(buffer.getInt(0));
                data.setError_code(buffer.getInt(4));
                data.setLeftlength(buffer.getInt(8));
                data.setRightCount(buffer.getInt(12));
                data.setId(buffer.getInt(16));
                data.setType(buffer.getInt(20));
                data.setFlag(buffer.getInt(24));
                if (data.getLeftlength() == 68 && data.getRightCount() == 0 && data.getId() == 0) {
                    int j;
                    byte[] devModel = new byte[16];
                    for (j = 0; j < devModel.length; j++) {
                        devModel[j] = buffer.get(j + 28);
                    }
                    data.setDevModel(new String(devModel).trim());
                    byte[] devP2pID = new byte[24];
                    for (j = 0; j < devP2pID.length; j++) {
                        devP2pID[j] = buffer.get(j + 44);
                    }
                    data.setDevP2pID(new String(devP2pID).trim());
                    return data;
                }
                data.setDevModel("");
                data.setDevP2pID("");
                return data;
            }
            throw new Exception();
        } catch (Exception e) {
            data.setCmd(9999);
            Log.e("my", "no associated data packet. ");
        }
    }

    public static int putString(ByteBuffer buffer, String data, int index) {
        int position = (index * 64) + 512;
        int i = 0;
        byte[] bytes = data.getBytes();
        while (i < bytes.length && i < 63) {
            buffer.put(position + i, bytes[i]);
            i++;
        }
        buffer.put(index + 64, (byte) 0);
        return index + 64;
    }

    public static String getString(ByteBuffer buffer, int index) {
        String data = "";
        byte[] bytes = buffer.array();
        int position = (index * 64) + 512;
        for (int i = 0; i < 64; i++) {
            if (bytes[position + i] == (byte) 0) {
                System.out.println(position + ":" + (position + i));
                return new String(bytes, position, i);
            }
        }
        return data;
    }

    public int getCmd() {
        return this.cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public int getLeftlength() {
        return this.leftlength;
    }

    public void setLeftlength(int leftlength) {
        this.leftlength = leftlength;
    }

    public int getRightCount() {
        return this.rightCount;
    }

    public void setRightCount(int rightCount) {
        this.rightCount = rightCount;
    }

    public int getError_code() {
        return this.error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDevModel() {
        return this.devModel;
    }

    public void setDevModel(String devModel) {
        this.devModel = devModel;
    }

    public String getDevP2pID() {
        return this.devP2pID;
    }

    public void setDevP2pID(String devP2pID) {
        this.devP2pID = devP2pID;
    }
}
