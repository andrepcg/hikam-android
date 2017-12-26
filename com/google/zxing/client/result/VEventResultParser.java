package com.google.zxing.client.result;

import com.google.zxing.Result;

final class VEventResultParser extends ResultParser {
    private VEventResultParser() {
    }

    public static CalendarParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        if (rawText.indexOf("BEGIN:VEVENT") < 0) {
            return null;
        }
        try {
            return new CalendarParsedResult(VCardResultParser.matchSingleVCardPrefixedField("SUMMARY", rawText, true), VCardResultParser.matchSingleVCardPrefixedField("DTSTART", rawText, true), VCardResultParser.matchSingleVCardPrefixedField("DTEND", rawText, true), VCardResultParser.matchSingleVCardPrefixedField("LOCATION", rawText, true), null, VCardResultParser.matchSingleVCardPrefixedField("DESCRIPTION", rawText, true));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
