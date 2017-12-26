package org.jboss.netty.handler.codec.http.cookie;

import java.nio.CharBuffer;
import org.jboss.netty.logging.InternalLogger;
import org.jboss.netty.logging.InternalLoggerFactory;

public abstract class CookieDecoder {
    private final InternalLogger logger = InternalLoggerFactory.getInstance(getClass());
    private final boolean strict;

    protected CookieDecoder(boolean strict) {
        this.strict = strict;
    }

    protected DefaultCookie initCookie(String header, int nameBegin, int nameEnd, int valueBegin, int valueEnd) {
        if (nameBegin == -1 || nameBegin == nameEnd) {
            this.logger.debug("Skipping cookie with null name");
            return null;
        } else if (valueBegin == -1) {
            this.logger.debug("Skipping cookie with null value");
            return null;
        } else {
            CharSequence wrappedValue = CharBuffer.wrap(header, valueBegin, valueEnd);
            CharSequence unwrappedValue = CookieUtil.unwrapValue(wrappedValue);
            if (unwrappedValue != null) {
                int invalidOctetPos;
                String name = header.substring(nameBegin, nameEnd);
                if (this.strict) {
                    invalidOctetPos = CookieUtil.firstInvalidCookieNameOctet(name);
                    if (invalidOctetPos >= 0) {
                        if (!this.logger.isDebugEnabled()) {
                            return null;
                        }
                        this.logger.debug("Skipping cookie because name '" + name + "' contains invalid char '" + name.charAt(invalidOctetPos) + "'");
                        return null;
                    }
                }
                boolean wrap = unwrappedValue.length() != valueEnd - valueBegin;
                if (this.strict) {
                    invalidOctetPos = CookieUtil.firstInvalidCookieValueOctet(unwrappedValue);
                    if (invalidOctetPos >= 0) {
                        if (!this.logger.isDebugEnabled()) {
                            return null;
                        }
                        this.logger.debug("Skipping cookie because value '" + unwrappedValue + "' contains invalid char '" + unwrappedValue.charAt(invalidOctetPos) + "'");
                        return null;
                    }
                }
                DefaultCookie cookie = new DefaultCookie(name, unwrappedValue.toString());
                cookie.setWrap(wrap);
                return cookie;
            } else if (!this.logger.isDebugEnabled()) {
                return null;
            } else {
                this.logger.debug("Skipping cookie because starting quotes are not properly balanced in '" + wrappedValue + "'");
                return null;
            }
        }
    }
}
