package com.hp.hpl.sparta;

import com.hp.hpl.sparta.xpath.AttrEqualsExpr;
import com.hp.hpl.sparta.xpath.AttrExistsExpr;
import com.hp.hpl.sparta.xpath.AttrGreaterExpr;
import com.hp.hpl.sparta.xpath.AttrLessExpr;
import com.hp.hpl.sparta.xpath.AttrNotEqualsExpr;
import com.hp.hpl.sparta.xpath.BooleanExprVisitor;
import com.hp.hpl.sparta.xpath.ElementTest;
import com.hp.hpl.sparta.xpath.NodeTest;
import com.hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.hp.hpl.sparta.xpath.Step;
import com.hp.hpl.sparta.xpath.TextEqualsExpr;
import com.hp.hpl.sparta.xpath.TextExistsExpr;
import com.hp.hpl.sparta.xpath.TextNotEqualsExpr;
import com.hp.hpl.sparta.xpath.TrueExpr;
import com.hp.hpl.sparta.xpath.XPath;
import com.hp.hpl.sparta.xpath.XPathException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

public abstract class Node {
    private Object annotation_ = null;
    private Document doc_ = null;
    private int hash_ = 0;
    private Node nextSibling_ = null;
    private Element parentNode_ = null;
    private Node previousSibling_ = null;

    class C10611 implements BooleanExprVisitor {
        private final Node this$0;
        private final String val$msgContext;
        private final Element val$newChild;
        private final Element val$parent;
        private final String val$tagName;

        C10611(Node node, Element element, Element element2, String str, String str2) throws XPathException {
            this.this$0 = node;
            this.val$newChild = element;
            this.val$parent = element2;
            this.val$msgContext = str;
            this.val$tagName = str2;
        }

        public void visit(AttrEqualsExpr attrEqualsExpr) throws XPathException {
            this.val$newChild.setAttribute(attrEqualsExpr.getAttrName(), attrEqualsExpr.getAttrValue());
        }

        public void visit(AttrExistsExpr attrExistsExpr) throws XPathException {
            this.val$newChild.setAttribute(attrExistsExpr.getAttrName(), "something");
        }

        public void visit(AttrGreaterExpr attrGreaterExpr) throws XPathException {
            this.val$newChild.setAttribute(attrGreaterExpr.getAttrName(), Long.toString(Long.MAX_VALUE));
        }

        public void visit(AttrLessExpr attrLessExpr) throws XPathException {
            this.val$newChild.setAttribute(attrLessExpr.getAttrName(), Long.toString(Long.MIN_VALUE));
        }

        public void visit(AttrNotEqualsExpr attrNotEqualsExpr) throws XPathException {
            this.val$newChild.setAttribute(attrNotEqualsExpr.getAttrName(), new StringBuffer().append("not ").append(attrNotEqualsExpr.getAttrValue()).toString());
        }

        public void visit(PositionEqualsExpr positionEqualsExpr) throws XPathException {
            int i = 1;
            int position = positionEqualsExpr.getPosition();
            if (this.val$parent != null || position == 1) {
                while (i < position) {
                    this.val$parent.appendChild(new Element(this.val$tagName));
                    i++;
                }
                return;
            }
            throw new XPathException(XPath.get(this.val$msgContext), "Position of root node must be 1");
        }

        public void visit(TextEqualsExpr textEqualsExpr) throws XPathException {
            this.val$newChild.appendChild(new Text(textEqualsExpr.getValue()));
        }

        public void visit(TextExistsExpr textExistsExpr) throws XPathException {
            this.val$newChild.appendChild(new Text("something"));
        }

        public void visit(TextNotEqualsExpr textNotEqualsExpr) throws XPathException {
            this.val$newChild.appendChild(new Text(new StringBuffer().append("not ").append(textNotEqualsExpr.getValue()).toString()));
        }

        public void visit(TrueExpr trueExpr) {
        }
    }

    protected static void htmlEncode(Writer writer, String str) throws IOException {
        int i = 0;
        int length = str.length();
        for (int i2 = 0; i2 < length; i2++) {
            String str2;
            char charAt = str.charAt(i2);
            if (charAt < '') {
                switch (charAt) {
                    case '\"':
                        str2 = "&quot;";
                        break;
                    case '&':
                        str2 = "&amp;";
                        break;
                    case '\'':
                        str2 = "&#39;";
                        break;
                    case '<':
                        str2 = "&lt;";
                        break;
                    case '>':
                        str2 = "&gt;";
                        break;
                    default:
                        str2 = null;
                        break;
                }
            }
            str2 = new StringBuffer().append("&#").append(charAt).append(";").toString();
            if (str2 != null) {
                writer.write(str, i, i2 - i);
                writer.write(str2);
                i = i2 + 1;
            }
        }
        if (i < length) {
            writer.write(str, i, length - i);
        }
    }

    public abstract Object clone();

    protected abstract int computeHashCode();

    public Object getAnnotation() {
        return this.annotation_;
    }

    public Node getNextSibling() {
        return this.nextSibling_;
    }

    public Document getOwnerDocument() {
        return this.doc_;
    }

    public Element getParentNode() {
        return this.parentNode_;
    }

    public Node getPreviousSibling() {
        return this.previousSibling_;
    }

    public int hashCode() {
        if (this.hash_ == 0) {
            this.hash_ = computeHashCode();
        }
        return this.hash_;
    }

    void insertAtEndOfLinkedList(Node node) {
        this.previousSibling_ = node;
        if (node != null) {
            node.nextSibling_ = this;
        }
    }

    Element makeMatching(Element element, Step step, String str) throws ParseException, XPathException {
        NodeTest nodeTest = step.getNodeTest();
        if (nodeTest instanceof ElementTest) {
            String tagName = ((ElementTest) nodeTest).getTagName();
            Element element2 = new Element(tagName);
            step.getPredicate().accept(new C10611(this, element2, element, str, tagName));
            return element2;
        }
        throw new ParseException(new StringBuffer().append("\"").append(nodeTest).append("\" in \"").append(str).append("\" is not an element test").toString());
    }

    void notifyObservers() {
        this.hash_ = 0;
        if (this.doc_ != null) {
            this.doc_.notifyObservers();
        }
    }

    void removeFromLinkedList() {
        if (this.previousSibling_ != null) {
            this.previousSibling_.nextSibling_ = this.nextSibling_;
        }
        if (this.nextSibling_ != null) {
            this.nextSibling_.previousSibling_ = this.previousSibling_;
        }
        this.nextSibling_ = null;
        this.previousSibling_ = null;
    }

    void replaceInLinkedList(Node node) {
        if (this.previousSibling_ != null) {
            this.previousSibling_.nextSibling_ = node;
        }
        if (this.nextSibling_ != null) {
            this.nextSibling_.previousSibling_ = node;
        }
        node.nextSibling_ = this.nextSibling_;
        node.previousSibling_ = this.previousSibling_;
        this.nextSibling_ = null;
        this.previousSibling_ = null;
    }

    public void setAnnotation(Object obj) {
        this.annotation_ = obj;
    }

    void setOwnerDocument(Document document) {
        this.doc_ = document;
    }

    void setParentNode(Element element) {
        this.parentNode_ = element;
    }

    public String toString() {
        try {
            OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Writer outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
            toString(outputStreamWriter);
            outputStreamWriter.flush();
            return new String(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            return super.toString();
        }
    }

    abstract void toString(Writer writer) throws IOException;

    public String toXml() throws IOException {
        OutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Writer outputStreamWriter = new OutputStreamWriter(byteArrayOutputStream);
        toXml(outputStreamWriter);
        outputStreamWriter.flush();
        return new String(byteArrayOutputStream.toByteArray());
    }

    abstract void toXml(Writer writer) throws IOException;

    public abstract Element xpathSelectElement(String str) throws ParseException;

    public abstract Enumeration xpathSelectElements(String str) throws ParseException;

    public abstract String xpathSelectString(String str) throws ParseException;

    public abstract Enumeration xpathSelectStrings(String str) throws ParseException;

    public boolean xpathSetStrings(String str, String str2) throws ParseException {
        try {
            int lastIndexOf = str.lastIndexOf(47);
            if (str.substring(lastIndexOf + 1).equals("text()") || str.charAt(lastIndexOf + 1) == '@') {
                boolean z;
                String substring = str.substring(0, lastIndexOf);
                Element element;
                if (str.charAt(lastIndexOf + 1) == '@') {
                    String substring2 = str.substring(lastIndexOf + 2);
                    if (substring2.length() == 0) {
                        throw new ParseException(new StringBuffer().append("Xpath expression \"").append(str).append("\" specifies zero-length attribute name\"").toString());
                    }
                    Enumeration xpathSelectElements = xpathSelectElements(substring);
                    z = false;
                    while (xpathSelectElements.hasMoreElements()) {
                        element = (Element) xpathSelectElements.nextElement();
                        if (!str2.equals(element.getAttribute(substring2))) {
                            element.setAttribute(substring2, str2);
                            z = true;
                        }
                    }
                } else {
                    Enumeration xpathSelectElements2 = xpathSelectElements(substring);
                    z = xpathSelectElements2.hasMoreElements();
                    while (xpathSelectElements2.hasMoreElements()) {
                        element = (Element) xpathSelectElements2.nextElement();
                        Vector vector = new Vector();
                        for (Node firstChild = element.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
                            if (firstChild instanceof Text) {
                                vector.addElement((Text) firstChild);
                            }
                        }
                        if (vector.size() == 0) {
                            Node text = new Text(str2);
                            if (text.getData().length() > 0) {
                                element.appendChild(text);
                                z = true;
                            }
                        } else {
                            boolean z2;
                            Text text2 = (Text) vector.elementAt(0);
                            if (text2.getData().equals(str2)) {
                                z2 = z;
                            } else {
                                vector.removeElementAt(0);
                                text2.setData(str2);
                                z2 = true;
                            }
                            int i = 0;
                            while (i < vector.size()) {
                                element.removeChild((Text) vector.elementAt(i));
                                i++;
                                z2 = true;
                            }
                            z = z2;
                        }
                    }
                }
                return z;
            }
            throw new ParseException(new StringBuffer().append("Last step of Xpath expression \"").append(str).append("\" is not \"text()\" and does not start with a '@'. It starts with a '").append(str.charAt(lastIndexOf + 1)).append("'").toString());
        } catch (DOMException e) {
            throw new Error(new StringBuffer().append("Assertion failed ").append(e).toString());
        } catch (IndexOutOfBoundsException e2) {
            throw new ParseException(new StringBuffer().append("Xpath expression \"").append(str).append("\" is not in the form \"xpathExpression/@attributeName\"").toString());
        }
    }
}
