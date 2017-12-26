package com.google.zxing.client.result;

public final class CalendarParsedResult extends ParsedResult {
    private final String attendee;
    private final String description;
    private final String end;
    private final String location;
    private final String start;
    private final String summary;

    public CalendarParsedResult(String summary, String start, String end, String location, String attendee, String description) {
        super(ParsedResultType.CALENDAR);
        if (start == null) {
            throw new IllegalArgumentException();
        }
        validateDate(start);
        if (end == null) {
            end = start;
        } else {
            validateDate(end);
        }
        this.summary = summary;
        this.start = start;
        this.end = end;
        this.location = location;
        this.attendee = attendee;
        this.description = description;
    }

    public String getSummary() {
        return this.summary;
    }

    public String getStart() {
        return this.start;
    }

    public String getEnd() {
        return this.end;
    }

    public String getLocation() {
        return this.location;
    }

    public String getAttendee() {
        return this.attendee;
    }

    public String getDescription() {
        return this.description;
    }

    public String getDisplayResult() {
        StringBuffer result = new StringBuffer(100);
        ParsedResult.maybeAppend(this.summary, result);
        ParsedResult.maybeAppend(this.start, result);
        ParsedResult.maybeAppend(this.end, result);
        ParsedResult.maybeAppend(this.location, result);
        ParsedResult.maybeAppend(this.attendee, result);
        ParsedResult.maybeAppend(this.description, result);
        return result.toString();
    }

    private static void validateDate(String date) {
        if (date != null) {
            int length = date.length();
            if (length == 8 || length == 15 || length == 16) {
                int i = 0;
                while (i < 8) {
                    if (Character.isDigit(date.charAt(i))) {
                        i++;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                if (length <= 8) {
                    return;
                }
                if (date.charAt(8) != 'T') {
                    throw new IllegalArgumentException();
                }
                i = 9;
                while (i < 15) {
                    if (Character.isDigit(date.charAt(i))) {
                        i++;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
                if (length == 16 && date.charAt(15) != 'Z') {
                    throw new IllegalArgumentException();
                }
                return;
            }
            throw new IllegalArgumentException();
        }
    }
}
