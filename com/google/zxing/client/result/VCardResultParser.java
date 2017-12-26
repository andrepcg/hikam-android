package com.google.zxing.client.result;

import com.google.zxing.Result;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

final class VCardResultParser extends ResultParser {
    private VCardResultParser() {
    }

    public static AddressBookParsedResult parse(Result result) {
        String rawText = result.getText();
        if (rawText == null || !rawText.startsWith("BEGIN:VCARD")) {
            return null;
        }
        String[] names = matchVCardPrefixedField("FN", rawText, true);
        if (names == null) {
            names = matchVCardPrefixedField("N", rawText, true);
            formatNames(names);
        }
        String[] phoneNumbers = matchVCardPrefixedField("TEL", rawText, true);
        String[] emails = matchVCardPrefixedField("EMAIL", rawText, true);
        String note = matchSingleVCardPrefixedField("NOTE", rawText, false);
        String[] addresses = matchVCardPrefixedField("ADR", rawText, true);
        if (addresses != null) {
            for (int i = 0; i < addresses.length; i++) {
                addresses[i] = formatAddress(addresses[i]);
            }
        }
        String org = matchSingleVCardPrefixedField("ORG", rawText, true);
        String birthday = matchSingleVCardPrefixedField("BDAY", rawText, true);
        if (!isLikeVCardDate(birthday)) {
            birthday = null;
        }
        return new AddressBookParsedResult(names, null, phoneNumbers, emails, note, addresses, org, birthday, matchSingleVCardPrefixedField("TITLE", rawText, true), matchSingleVCardPrefixedField("URL", rawText, true));
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String[] matchVCardPrefixedField(java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
        r8 = 0;
        r4 = 0;
        r9 = r18.length();
    L_0x0006:
        if (r4 >= r9) goto L_0x0012;
    L_0x0008:
        r0 = r18;
        r1 = r17;
        r4 = r0.indexOf(r1, r4);
        if (r4 >= 0) goto L_0x001c;
    L_0x0012:
        if (r8 == 0) goto L_0x001a;
    L_0x0014:
        r15 = r8.isEmpty();
        if (r15 == 0) goto L_0x014c;
    L_0x001a:
        r15 = 0;
    L_0x001b:
        return r15;
    L_0x001c:
        if (r4 <= 0) goto L_0x002f;
    L_0x001e:
        r15 = r4 + -1;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 10;
        r0 = r16;
        if (r15 == r0) goto L_0x002f;
    L_0x002c:
        r4 = r4 + 1;
        goto L_0x0006;
    L_0x002f:
        r15 = r17.length();
        r4 = r4 + r15;
        r0 = r18;
        r15 = r0.charAt(r4);
        r16 = 58;
        r0 = r16;
        if (r15 == r0) goto L_0x004c;
    L_0x0040:
        r0 = r18;
        r15 = r0.charAt(r4);
        r16 = 59;
        r0 = r16;
        if (r15 != r0) goto L_0x0006;
    L_0x004c:
        r11 = r4;
    L_0x004d:
        r0 = r18;
        r15 = r0.charAt(r4);
        r16 = 58;
        r0 = r16;
        if (r15 == r0) goto L_0x005c;
    L_0x0059:
        r4 = r4 + 1;
        goto L_0x004d;
    L_0x005c:
        r12 = 0;
        r13 = 0;
        if (r4 <= r11) goto L_0x00b6;
    L_0x0060:
        r5 = r11 + 1;
    L_0x0062:
        if (r5 > r4) goto L_0x00b6;
    L_0x0064:
        r0 = r18;
        r15 = r0.charAt(r5);
        r16 = 59;
        r0 = r16;
        if (r15 == r0) goto L_0x007c;
    L_0x0070:
        r0 = r18;
        r15 = r0.charAt(r5);
        r16 = 58;
        r0 = r16;
        if (r15 != r0) goto L_0x00a9;
    L_0x007c:
        r15 = r11 + 1;
        r0 = r18;
        r10 = r0.substring(r15, r5);
        r15 = 61;
        r3 = r10.indexOf(r15);
        if (r3 < 0) goto L_0x00a8;
    L_0x008c:
        r15 = 0;
        r6 = r10.substring(r15, r3);
        r15 = r3 + 1;
        r14 = r10.substring(r15);
        r15 = "ENCODING";
        r15 = r6.equalsIgnoreCase(r15);
        if (r15 == 0) goto L_0x00ac;
    L_0x009f:
        r15 = "QUOTED-PRINTABLE";
        r15 = r14.equalsIgnoreCase(r15);
        if (r15 == 0) goto L_0x00a8;
    L_0x00a7:
        r12 = 1;
    L_0x00a8:
        r11 = r5;
    L_0x00a9:
        r5 = r5 + 1;
        goto L_0x0062;
    L_0x00ac:
        r15 = "CHARSET";
        r15 = r6.equalsIgnoreCase(r15);
        if (r15 == 0) goto L_0x00a8;
    L_0x00b4:
        r13 = r14;
        goto L_0x00a8;
    L_0x00b6:
        r4 = r4 + 1;
        r7 = r4;
    L_0x00b9:
        r15 = 10;
        r0 = r18;
        r4 = r0.indexOf(r15, r4);
        if (r4 < 0) goto L_0x010b;
    L_0x00c3:
        r15 = r18.length();
        r15 = r15 + -1;
        if (r4 >= r15) goto L_0x00ea;
    L_0x00cb:
        r15 = r4 + 1;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 32;
        r0 = r16;
        if (r15 == r0) goto L_0x00e7;
    L_0x00d9:
        r15 = r4 + 1;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 9;
        r0 = r16;
        if (r15 != r0) goto L_0x00ea;
    L_0x00e7:
        r4 = r4 + 2;
        goto L_0x00b9;
    L_0x00ea:
        if (r12 == 0) goto L_0x010b;
    L_0x00ec:
        r15 = r4 + -1;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 61;
        r0 = r16;
        if (r15 == r0) goto L_0x0108;
    L_0x00fa:
        r15 = r4 + -2;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 61;
        r0 = r16;
        if (r15 != r0) goto L_0x010b;
    L_0x0108:
        r4 = r4 + 1;
        goto L_0x00b9;
    L_0x010b:
        if (r4 >= 0) goto L_0x0110;
    L_0x010d:
        r4 = r9;
        goto L_0x0006;
    L_0x0110:
        if (r4 <= r7) goto L_0x0148;
    L_0x0112:
        if (r8 != 0) goto L_0x011a;
    L_0x0114:
        r8 = new java.util.Vector;
        r15 = 1;
        r8.<init>(r15);
    L_0x011a:
        r15 = r4 + -1;
        r0 = r18;
        r15 = r0.charAt(r15);
        r16 = 13;
        r0 = r16;
        if (r15 != r0) goto L_0x012a;
    L_0x0128:
        r4 = r4 + -1;
    L_0x012a:
        r0 = r18;
        r2 = r0.substring(r7, r4);
        if (r19 == 0) goto L_0x0136;
    L_0x0132:
        r2 = r2.trim();
    L_0x0136:
        if (r12 == 0) goto L_0x0143;
    L_0x0138:
        r2 = decodeQuotedPrintable(r2, r13);
    L_0x013c:
        r8.addElement(r2);
        r4 = r4 + 1;
        goto L_0x0006;
    L_0x0143:
        r2 = stripContinuationCRLF(r2);
        goto L_0x013c;
    L_0x0148:
        r4 = r4 + 1;
        goto L_0x0006;
    L_0x014c:
        r15 = com.google.zxing.client.result.ResultParser.toStringArray(r8);
        goto L_0x001b;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.zxing.client.result.VCardResultParser.matchVCardPrefixedField(java.lang.String, java.lang.String, boolean):java.lang.String[]");
    }

    private static String stripContinuationCRLF(String value) {
        int length = value.length();
        StringBuffer result = new StringBuffer(length);
        boolean lastWasLF = false;
        for (int i = 0; i < length; i++) {
            if (!lastWasLF) {
                char c = value.charAt(i);
                lastWasLF = false;
                switch (c) {
                    case '\n':
                        lastWasLF = true;
                        break;
                    case '\r':
                        break;
                    default:
                        result.append(c);
                        break;
                }
            }
            lastWasLF = false;
        }
        return result.toString();
    }

    private static String decodeQuotedPrintable(String value, String charset) {
        int length = value.length();
        StringBuffer result = new StringBuffer(length);
        ByteArrayOutputStream fragmentBuffer = new ByteArrayOutputStream();
        int i = 0;
        while (i < length) {
            char c = value.charAt(i);
            switch (c) {
                case '\n':
                case '\r':
                    break;
                case '=':
                    if (i >= length - 2) {
                        break;
                    }
                    char nextChar = value.charAt(i + 1);
                    if (!(nextChar == '\r' || nextChar == '\n')) {
                        try {
                            fragmentBuffer.write((toHexValue(nextChar) * 16) + toHexValue(value.charAt(i + 2)));
                        } catch (IllegalArgumentException e) {
                        }
                        i += 2;
                        break;
                    }
                default:
                    maybeAppendFragment(fragmentBuffer, charset, result);
                    result.append(c);
                    break;
            }
            i++;
        }
        maybeAppendFragment(fragmentBuffer, charset, result);
        return result.toString();
    }

    private static int toHexValue(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return (c - 65) + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 97) + 10;
        }
        throw new IllegalArgumentException();
    }

    private static void maybeAppendFragment(ByteArrayOutputStream fragmentBuffer, String charset, StringBuffer result) {
        if (fragmentBuffer.size() > 0) {
            String fragment;
            byte[] fragmentBytes = fragmentBuffer.toByteArray();
            if (charset == null) {
                fragment = new String(fragmentBytes);
            } else {
                try {
                    fragment = new String(fragmentBytes, charset);
                } catch (UnsupportedEncodingException e) {
                    fragment = new String(fragmentBytes);
                }
            }
            fragmentBuffer.reset();
            result.append(fragment);
        }
    }

    static String matchSingleVCardPrefixedField(String prefix, String rawText, boolean trim) {
        String[] values = matchVCardPrefixedField(prefix, rawText, trim);
        return values == null ? null : values[0];
    }

    private static boolean isLikeVCardDate(String value) {
        if (value == null || ResultParser.isStringOfDigits(value, 8)) {
            return true;
        }
        if (value.length() == 10 && value.charAt(4) == '-' && value.charAt(7) == '-' && ResultParser.isSubstringOfDigits(value, 0, 4) && ResultParser.isSubstringOfDigits(value, 5, 2) && ResultParser.isSubstringOfDigits(value, 8, 2)) {
            return true;
        }
        return false;
    }

    private static String formatAddress(String address) {
        if (address == null) {
            return null;
        }
        int length = address.length();
        StringBuffer newAddress = new StringBuffer(length);
        for (int j = 0; j < length; j++) {
            char c = address.charAt(j);
            if (c == ';') {
                newAddress.append(' ');
            } else {
                newAddress.append(c);
            }
        }
        return newAddress.toString().trim();
    }

    private static void formatNames(String[] names) {
        if (names != null) {
            for (int i = 0; i < names.length; i++) {
                String name = names[i];
                String[] components = new String[5];
                int start = 0;
                int componentIndex = 0;
                while (true) {
                    int end = name.indexOf(59, start);
                    if (end <= 0) {
                        break;
                    }
                    components[componentIndex] = name.substring(start, end);
                    componentIndex++;
                    start = end + 1;
                }
                components[componentIndex] = name.substring(start);
                StringBuffer newName = new StringBuffer(100);
                maybeAppendComponent(components, 3, newName);
                maybeAppendComponent(components, 1, newName);
                maybeAppendComponent(components, 2, newName);
                maybeAppendComponent(components, 0, newName);
                maybeAppendComponent(components, 4, newName);
                names[i] = newName.toString().trim();
            }
        }
    }

    private static void maybeAppendComponent(String[] components, int i, StringBuffer newName) {
        if (components[i] != null) {
            newName.append(' ');
            newName.append(components[i]);
        }
    }
}
