package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.Vector;

final class AddressBookAUResultParser extends ResultParser {
    AddressBookAUResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || rawText.indexOf("MEMORY") < 0 || rawText.indexOf("\r\n") < 0) {
            return null;
        }
        return new AddressBookParsedResult(ResultParser.maybeWrap(ResultParser.matchSinglePrefixedField("NAME1:", rawText, '\r', true)), ResultParser.matchSinglePrefixedField("NAME2:", rawText, '\r', true), matchMultipleValuePrefix("TEL", 3, rawText, true), matchMultipleValuePrefix("MAIL", 3, rawText, true), ResultParser.matchSinglePrefixedField("MEMORY:", rawText, '\r', false), ResultParser.matchSinglePrefixedField("ADD:", rawText, '\r', true) == null ? null : new String[]{ResultParser.matchSinglePrefixedField("ADD:", rawText, '\r', true)}, null, null, null, null);
    }

    private static String[] matchMultipleValuePrefix(String prefix, int max, String rawText, boolean trim) {
        Vector values = null;
        for (int i = 1; i <= max; i++) {
            String value = ResultParser.matchSinglePrefixedField(new StringBuffer().append(prefix).append(i).append(':').toString(), rawText, '\r', trim);
            if (value == null) {
                break;
            }
            if (values == null) {
                values = new Vector(max);
            }
            values.addElement(value);
        }
        if (values == null) {
            return null;
        }
        return ResultParser.toStringArray(values);
    }
}
