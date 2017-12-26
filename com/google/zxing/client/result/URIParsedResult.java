package com.google.zxing.client.result;

public final class URIParsedResult extends ParsedResult {
    private final String title;
    private final String uri;

    public URIParsedResult(String uri, String title) {
        super(ParsedResultType.URI);
        this.uri = massageURI(uri);
        this.title = title;
    }

    public String getURI() {
        return this.uri;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isPossiblyMaliciousURI() {
        return containsUser();
    }

    private boolean containsUser() {
        int hostStart = this.uri.indexOf(58) + 1;
        int uriLength = this.uri.length();
        while (hostStart < uriLength && this.uri.charAt(hostStart) == '/') {
            hostStart++;
        }
        int hostEnd = this.uri.indexOf(47, hostStart);
        if (hostEnd < 0) {
            hostEnd = uriLength;
        }
        int at = this.uri.indexOf(64, hostStart);
        return at >= hostStart && at < hostEnd;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(30);
        ParsedResult.maybeAppend(this.title, result);
        ParsedResult.maybeAppend(this.uri, result);
        return result.toString();
    }

    private static String massageURI(String uri) {
        int protocolEnd = uri.indexOf(58);
        if (protocolEnd < 0) {
            return new StringBuffer().append("http://").append(uri).toString();
        }
        if (isColonFollowedByPortNumber(uri, protocolEnd)) {
            return new StringBuffer().append("http://").append(uri).toString();
        }
        return new StringBuffer().append(uri.substring(0, protocolEnd).toLowerCase()).append(uri.substring(protocolEnd)).toString();
    }

    private static boolean isColonFollowedByPortNumber(String uri, int protocolEnd) {
        int nextSlash = uri.indexOf(47, protocolEnd + 1);
        if (nextSlash < 0) {
            nextSlash = uri.length();
        }
        if (nextSlash <= protocolEnd + 1) {
            return false;
        }
        int x = protocolEnd + 1;
        while (x < nextSlash) {
            if (uri.charAt(x) < '0' || uri.charAt(x) > '9') {
                return false;
            }
            x++;
        }
        return true;
    }
}
