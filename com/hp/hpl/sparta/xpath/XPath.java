package com.hp.hpl.sparta.xpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

public class XPath {
    private static final int ASSERTION = 0;
    private static Hashtable cache_ = new Hashtable();
    private boolean absolute_;
    private Stack steps_;
    private String string_;

    private XPath(String str) throws XPathException {
        this(str, new InputStreamReader(new ByteArrayInputStream(str.getBytes())));
    }

    private XPath(String str, Reader reader) throws XPathException {
        this.steps_ = new Stack();
        try {
            boolean z;
            this.string_ = str;
            SimpleStreamTokenizer simpleStreamTokenizer = new SimpleStreamTokenizer(reader);
            simpleStreamTokenizer.ordinaryChar('/');
            simpleStreamTokenizer.ordinaryChar('.');
            simpleStreamTokenizer.wordChars(':', ':');
            simpleStreamTokenizer.wordChars('_', '_');
            if (simpleStreamTokenizer.nextToken() == 47) {
                this.absolute_ = true;
                if (simpleStreamTokenizer.nextToken() == 47) {
                    simpleStreamTokenizer.nextToken();
                    z = true;
                } else {
                    z = false;
                }
            } else {
                this.absolute_ = false;
                z = false;
            }
            this.steps_.push(new Step(this, z, simpleStreamTokenizer));
            while (simpleStreamTokenizer.ttype == 47) {
                if (simpleStreamTokenizer.nextToken() == 47) {
                    simpleStreamTokenizer.nextToken();
                    z = true;
                } else {
                    z = false;
                }
                this.steps_.push(new Step(this, z, simpleStreamTokenizer));
            }
            if (simpleStreamTokenizer.ttype != -1) {
                throw new XPathException(this, "at end of XPATH expression", simpleStreamTokenizer, "end of expression");
            }
        } catch (Exception e) {
            throw new XPathException(this, e);
        }
    }

    private XPath(boolean z, Step[] stepArr) {
        this.steps_ = new Stack();
        for (Object addElement : stepArr) {
            this.steps_.addElement(addElement);
        }
        this.absolute_ = z;
        this.string_ = null;
    }

    private String generateString() {
        StringBuffer stringBuffer = new StringBuffer();
        Enumeration elements = this.steps_.elements();
        Object obj = 1;
        while (elements.hasMoreElements()) {
            Step step = (Step) elements.nextElement();
            if (obj == null || this.absolute_) {
                stringBuffer.append('/');
                if (step.isMultiLevel()) {
                    stringBuffer.append('/');
                }
            }
            stringBuffer.append(step.toString());
            obj = null;
        }
        return stringBuffer.toString();
    }

    public static XPath get(String str) throws XPathException {
        XPath xPath;
        synchronized (cache_) {
            xPath = (XPath) cache_.get(str);
            if (xPath == null) {
                xPath = new XPath(str);
                cache_.put(str, xPath);
            }
        }
        return xPath;
    }

    public static XPath get(boolean z, Step[] stepArr) {
        XPath xPath = new XPath(z, stepArr);
        String xPath2 = xPath.toString();
        synchronized (cache_) {
            XPath xPath3 = (XPath) cache_.get(xPath2);
            if (xPath3 == null) {
                cache_.put(xPath2, xPath);
                return xPath;
            }
            return xPath3;
        }
    }

    public static boolean isStringValue(String str) throws XPathException, IOException {
        return get(str).isStringValue();
    }

    public Object clone() {
        Step[] stepArr = new Step[this.steps_.size()];
        Enumeration elements = this.steps_.elements();
        for (int i = 0; i < stepArr.length; i++) {
            stepArr[i] = (Step) elements.nextElement();
        }
        return new XPath(this.absolute_, stepArr);
    }

    public String getIndexingAttrName() throws XPathException {
        BooleanExpr predicate = ((Step) this.steps_.peek()).getPredicate();
        if (predicate instanceof AttrExistsExpr) {
            return ((AttrExistsExpr) predicate).getAttrName();
        }
        throw new XPathException(this, "has no indexing attribute name (must end with predicate of the form [@attrName]");
    }

    public String getIndexingAttrNameOfEquals() throws XPathException {
        BooleanExpr predicate = ((Step) this.steps_.peek()).getPredicate();
        return predicate instanceof AttrEqualsExpr ? ((AttrEqualsExpr) predicate).getAttrName() : null;
    }

    public Enumeration getSteps() {
        return this.steps_.elements();
    }

    public boolean isAbsolute() {
        return this.absolute_;
    }

    public boolean isStringValue() {
        return ((Step) this.steps_.peek()).isStringValue();
    }

    public String toString() {
        if (this.string_ == null) {
            this.string_ = generateString();
        }
        return this.string_;
    }
}
