package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.util.Vector;

final class BizcardResultParser extends AbstractDoCoMoResultParser {
    BizcardResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText != null) {
            if (rawText.startsWith("BIZCARD:")) {
                String fullName = buildName(AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("N:", rawText, true), AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("X:", rawText, true));
                String title = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("T:", rawText, true);
                String org = AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("C:", rawText, true);
                return new AddressBookParsedResult(ResultParser.maybeWrap(fullName), null, buildPhoneNumbers(AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("B:", rawText, true), AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("M:", rawText, true), AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("F:", rawText, true)), ResultParser.maybeWrap(AbstractDoCoMoResultParser.matchSingleDoCoMoPrefixedField("E:", rawText, true)), null, AbstractDoCoMoResultParser.matchDoCoMoPrefixedField("A:", rawText, true), org, null, title, null);
            }
        }
        return null;
    }

    private static String[] buildPhoneNumbers(String number1, String number2, String number3) {
        Vector numbers = new Vector(3);
        if (number1 != null) {
            numbers.addElement(number1);
        }
        if (number2 != null) {
            numbers.addElement(number2);
        }
        if (number3 != null) {
            numbers.addElement(number3);
        }
        int size = numbers.size();
        if (size == 0) {
            return null;
        }
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = (String) numbers.elementAt(i);
        }
        return result;
    }

    private static String buildName(String firstName, String lastName) {
        if (firstName == null) {
            return lastName;
        }
        if (lastName != null) {
            firstName = new StringBuffer().append(firstName).append(' ').append(lastName).toString();
        }
        return firstName;
    }
}
