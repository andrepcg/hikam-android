package com.google.gson;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class DefaultDateTypeAdapter extends TypeAdapter<Date> {
    private static final String SIMPLE_NAME = "DefaultDateTypeAdapter";
    private final Class<? extends Date> dateType;
    private final DateFormat enUsFormat;
    private final DateFormat localFormat;

    DefaultDateTypeAdapter(Class<? extends Date> dateType) {
        this((Class) dateType, DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, String datePattern) {
        this((Class) dateType, new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, int style) {
        this((Class) dateType, DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
    }

    public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
        this(Date.class, DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    public DefaultDateTypeAdapter(Class<? extends Date> dateType, int dateStyle, int timeStyle) {
        this((Class) dateType, DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
    }

    DefaultDateTypeAdapter(Class<? extends Date> dateType, DateFormat enUsFormat, DateFormat localFormat) {
        if (dateType == Date.class || dateType == java.sql.Date.class || dateType == Timestamp.class) {
            this.dateType = dateType;
            this.enUsFormat = enUsFormat;
            this.localFormat = localFormat;
            return;
        }
        throw new IllegalArgumentException("Date type must be one of " + Date.class + ", " + Timestamp.class + ", or " + java.sql.Date.class + " but was " + dateType);
    }

    public void write(JsonWriter out, Date value) throws IOException {
        synchronized (this.localFormat) {
            out.value(this.enUsFormat.format(value));
        }
    }

    public Date read(JsonReader in) throws IOException {
        if (in.peek() != JsonToken.STRING) {
            throw new JsonParseException("The date should be a string value");
        }
        Date date = deserializeToDate(in.nextString());
        if (this.dateType == Date.class) {
            return date;
        }
        if (this.dateType == Timestamp.class) {
            return new Timestamp(date.getTime());
        }
        if (this.dateType == java.sql.Date.class) {
            return new java.sql.Date(date.getTime());
        }
        throw new AssertionError();
    }

    private Date deserializeToDate(String s) {
        Date parse;
        synchronized (this.localFormat) {
            try {
                parse = this.localFormat.parse(s);
            } catch (ParseException e) {
                try {
                    parse = this.enUsFormat.parse(s);
                } catch (ParseException e2) {
                    try {
                        parse = ISO8601Utils.parse(s, new ParsePosition(0));
                    } catch (ParseException e3) {
                        throw new JsonSyntaxException(s, e3);
                    }
                }
            }
        }
        return parse;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(SIMPLE_NAME);
        sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
        return sb.toString();
    }
}
