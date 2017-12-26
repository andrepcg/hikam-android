package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.Hashtable;
import java.util.Vector;

final class SMSMMSResultParser extends ResultParser {
    private SMSMMSResultParser() {
    }

    public static SMSParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null) {
            return null;
        }
        if (!rawText.startsWith("sms:") && !rawText.startsWith("SMS:") && !rawText.startsWith("mms:") && !rawText.startsWith("MMS:")) {
            return null;
        }
        String smsURIWithoutQuery;
        Hashtable nameValuePairs = ResultParser.parseNameValuePairs(rawText);
        String subject = null;
        String body = null;
        boolean querySyntax = false;
        if (!(nameValuePairs == null || nameValuePairs.isEmpty())) {
            subject = (String) nameValuePairs.get("subject");
            body = (String) nameValuePairs.get("body");
            querySyntax = true;
        }
        int queryStart = rawText.indexOf(63, 4);
        if (queryStart < 0 || !querySyntax) {
            smsURIWithoutQuery = rawText.substring(4);
        } else {
            smsURIWithoutQuery = rawText.substring(4, queryStart);
        }
        int lastComma = -1;
        Vector numbers = new Vector(1);
        Vector vias = new Vector(1);
        while (true) {
            int comma = smsURIWithoutQuery.indexOf(44, lastComma + 1);
            if (comma > lastComma) {
                addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1, comma));
                lastComma = comma;
            } else {
                addNumberVia(numbers, vias, smsURIWithoutQuery.substring(lastComma + 1));
                return new SMSParsedResult(ResultParser.toStringArray(numbers), ResultParser.toStringArray(vias), subject, body);
            }
        }
    }

    private static void addNumberVia(Vector numbers, Vector vias, String numberPart) {
        int numberEnd = numberPart.indexOf(59);
        if (numberEnd < 0) {
            numbers.addElement(numberPart);
            vias.addElement(null);
            return;
        }
        String via;
        numbers.addElement(numberPart.substring(0, numberEnd));
        String maybeVia = numberPart.substring(numberEnd + 1);
        if (maybeVia.startsWith("via=")) {
            via = maybeVia.substring(4);
        } else {
            via = null;
        }
        vias.addElement(via);
    }
}
