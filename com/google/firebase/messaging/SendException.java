package com.google.firebase.messaging;

import java.util.Locale;

public final class SendException extends Exception {
    public static final int ERROR_INVALID_PARAMETERS = 1;
    public static final int ERROR_SIZE = 2;
    public static final int ERROR_TOO_MANY_MESSAGES = 4;
    public static final int ERROR_TTL_EXCEEDED = 3;
    public static final int ERROR_UNKNOWN = 0;
    private final int zzcdb;

    SendException(String str) {
        super(str);
        this.zzcdb = zzub(str);
    }

    private int zzub(String str) {
        if (str == null) {
            return 0;
        }
        String toLowerCase = str.toLowerCase(Locale.US);
        int i = -1;
        switch (toLowerCase.hashCode()) {
            case -1743242157:
                if (toLowerCase.equals("service_not_available")) {
                    i = 3;
                    break;
                }
                break;
            case -1290953729:
                if (toLowerCase.equals("toomanymessages")) {
                    i = 4;
                    break;
                }
                break;
            case -920906446:
                if (toLowerCase.equals("invalid_parameters")) {
                    i = 0;
                    break;
                }
                break;
            case -617027085:
                if (toLowerCase.equals("messagetoobig")) {
                    i = 2;
                    break;
                }
                break;
            case -95047692:
                if (toLowerCase.equals("missing_to")) {
                    i = 1;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            default:
                return 0;
        }
    }

    public int getErrorCode() {
        return this.zzcdb;
    }
}
