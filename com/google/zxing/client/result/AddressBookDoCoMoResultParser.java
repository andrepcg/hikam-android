package com.google.zxing.client.result;

import com.google.zxing.Result;

final class AddressBookDoCoMoResultParser extends AbstractDoCoMoResultParser {
    AddressBookDoCoMoResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || !rawText.startsWith("MECARD:")) {
            return null;
        }
        String[] rawName = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("N:", rawText, true);
        if (rawName == null) {
            return null;
        }
        String name = parseName(rawName[0]);
        String pronunciation = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("SOUND:", rawText, true);
        String[] phoneNumbers = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("TEL:", rawText, true);
        String[] emails = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("EMAIL:", rawText, true);
        String note = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("NOTE:", rawText, false);
        String[] addresses = AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("ADR:", rawText, true);
        String birthday = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("BDAY:", rawText, true);
        if (!(birthday == null || ResultParser.isStringOfDigits(birthday, 8))) {
            birthday = null;
        }
        String url = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("URL:", rawText, true);
        return new AddressBookParsedResult(ResultParser.maybeWrap(name), pronunciation, phoneNumbers, emails, note, addresses, AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("ORG:", rawText, true), birthday, null, url);
    }

    private static String parseName(String name) {
        int comma = name.indexOf(44);
        if (comma >= 0) {
            return new StringBuffer().append(name.substring(comma + 1)).append(' ').append(name.substring(0, comma)).toString();
        }
        return name;
    }
}
