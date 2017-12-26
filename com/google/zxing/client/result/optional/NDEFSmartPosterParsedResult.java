package com.google.zxing.client.result.optional;

import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;

public final class NDEFSmartPosterParsedResult extends ParsedResult {
    public static final int ACTION_DO = 0;
    public static final int ACTION_OPEN = 2;
    public static final int ACTION_SAVE = 1;
    public static final int ACTION_UNSPECIFIED = -1;
    private final int action;
    private final String title;
    private final String uri;

    NDEFSmartPosterParsedResult(int action, String uri, String title) {
        super(ParsedResultType.NDEF_SMART_POSTER);
        this.action = action;
        this.uri = uri;
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public String getURI() {
        return this.uri;
    }

    public int getAction() {
        return this.action;
    }

    public String getDisplayResult() {
        if (this.title == null) {
            return this.uri;
        }
        return new StringBuffer().append(this.title).append('\n').append(this.uri).toString();
    }
}
