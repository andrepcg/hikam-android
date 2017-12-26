package com.hp.hpl.sparta;

import com.hp.hpl.sparta.xpath.Step;
import com.hp.hpl.sparta.xpath.XPath;
import com.hp.hpl.sparta.xpath.XPathException;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class Element extends Node {
    private static final boolean DEBUG = false;
    private Vector attributeNames_ = null;
    private Hashtable attributes_ = null;
    private Node firstChild_ = null;
    private Node lastChild_ = null;
    private String tagName_ = null;

    Element() {
    }

    public Element(String str) {
        this.tagName_ = Sparta.intern(str);
    }

    private void checkInvariant() {
    }

    private boolean removeChildNoChecking(Node node) {
        int i = 0;
        for (Node node2 = this.firstChild_; node2 != null; node2 = node2.getNextSibling()) {
            if (node2.equals(node)) {
                if (this.firstChild_ == node2) {
                    this.firstChild_ = node2.getNextSibling();
                }
                if (this.lastChild_ == node2) {
                    this.lastChild_ = node2.getPreviousSibling();
                }
                node2.removeFromLinkedList();
                node2.setParentNode(null);
                node2.setOwnerDocument(null);
                return true;
            }
            i++;
        }
        return false;
    }

    private void replaceChild_(Node node, Node node2) throws DOMException {
        int i = 0;
        for (Node node3 = this.firstChild_; node3 != null; node3 = node3.getNextSibling()) {
            if (node3 == node2) {
                if (this.firstChild_ == node2) {
                    this.firstChild_ = node;
                }
                if (this.lastChild_ == node2) {
                    this.lastChild_ = node;
                }
                node2.replaceInLinkedList(node);
                node.setParentNode(this);
                node2.setParentNode(null);
                return;
            }
            i++;
        }
        throw new DOMException((short) 8, new StringBuffer().append("Cannot find ").append(node2).append(" in ").append(this).toString());
    }

    private XPathVisitor visitor(String str, boolean z) throws XPathException {
        XPath xPath = XPath.get(str);
        if (xPath.isStringValue() == z) {
            return new XPathVisitor(this, xPath);
        }
        throw new XPathException(xPath, new StringBuffer().append("\"").append(xPath).append("\" evaluates to ").append(z ? "evaluates to element not string" : "evaluates to string not element").toString());
    }

    public void appendChild(Node node) {
        appendChildNoChecking(!canHaveAsDescendent(node) ? (Element) node.clone() : node);
        notifyObservers();
    }

    void appendChildNoChecking(Node node) {
        Element parentNode = node.getParentNode();
        if (parentNode != null) {
            parentNode.removeChildNoChecking(node);
        }
        node.insertAtEndOfLinkedList(this.lastChild_);
        if (this.firstChild_ == null) {
            this.firstChild_ = node;
        }
        node.setParentNode(this);
        this.lastChild_ = node;
        node.setOwnerDocument(getOwnerDocument());
    }

    boolean canHaveAsDescendent(Node node) {
        if (node == this) {
            return false;
        }
        Element parentNode = getParentNode();
        return parentNode == null ? true : parentNode.canHaveAsDescendent(node);
    }

    public Object clone() {
        return cloneElement(true);
    }

    public Element cloneElement(boolean z) {
        Element element = new Element(this.tagName_);
        if (this.attributeNames_ != null) {
            Enumeration elements = this.attributeNames_.elements();
            while (elements.hasMoreElements()) {
                String str = (String) elements.nextElement();
                element.setAttribute(str, (String) this.attributes_.get(str));
            }
        }
        if (z) {
            for (Node node = this.firstChild_; node != null; node = node.getNextSibling()) {
                element.appendChild((Node) node.clone());
            }
        }
        return element;
    }

    public Element cloneShallow() {
        return cloneElement(false);
    }

    protected int computeHashCode() {
        int i;
        int hashCode = this.tagName_.hashCode();
        if (this.attributes_ != null) {
            Enumeration keys = this.attributes_.keys();
            i = hashCode;
            while (keys.hasMoreElements()) {
                String str = (String) keys.nextElement();
                i = ((String) this.attributes_.get(str)).hashCode() + (((i * 31) + str.hashCode()) * 31);
            }
        } else {
            i = hashCode;
        }
        for (Node node = this.firstChild_; node != null; node = node.getNextSibling()) {
            i = (i * 31) + node.hashCode();
        }
        return i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Element)) {
            return false;
        }
        Element element = (Element) obj;
        if (!this.tagName_.equals(element.tagName_)) {
            return false;
        }
        if ((this.attributes_ == null ? 0 : this.attributes_.size()) != (element.attributes_ == null ? 0 : element.attributes_.size())) {
            return false;
        }
        if (this.attributes_ != null) {
            Enumeration keys = this.attributes_.keys();
            while (keys.hasMoreElements()) {
                String str = (String) keys.nextElement();
                if (!((String) this.attributes_.get(str)).equals((String) element.attributes_.get(str))) {
                    return false;
                }
            }
        }
        Node node = this.firstChild_;
        Node node2 = element.firstChild_;
        while (node != null) {
            if (!node.equals(node2)) {
                return false;
            }
            node = node.getNextSibling();
            node2 = node2.getNextSibling();
        }
        return true;
    }

    public String getAttribute(String str) {
        return this.attributes_ == null ? null : (String) this.attributes_.get(str);
    }

    public Enumeration getAttributeNames() {
        return this.attributeNames_ == null ? Document.EMPTY : this.attributeNames_.elements();
    }

    public Node getFirstChild() {
        return this.firstChild_;
    }

    public Node getLastChild() {
        return this.lastChild_;
    }

    public String getTagName() {
        return this.tagName_;
    }

    public void removeAttribute(String str) {
        if (this.attributes_ != null) {
            this.attributes_.remove(str);
            this.attributeNames_.removeElement(str);
            notifyObservers();
        }
    }

    public void removeChild(Node node) throws DOMException {
        if (removeChildNoChecking(node)) {
            notifyObservers();
            return;
        }
        throw new DOMException((short) 8, new StringBuffer().append("Cannot find ").append(node).append(" in ").append(this).toString());
    }

    public void replaceChild(Element element, Node node) throws DOMException {
        replaceChild_(element, node);
        notifyObservers();
    }

    public void replaceChild(Text text, Node node) throws DOMException {
        replaceChild_(text, node);
        notifyObservers();
    }

    public void setAttribute(String str, String str2) {
        if (this.attributes_ == null) {
            this.attributes_ = new Hashtable();
            this.attributeNames_ = new Vector();
        }
        if (this.attributes_.get(str) == null) {
            this.attributeNames_.addElement(str);
        }
        this.attributes_.put(str, str2);
        notifyObservers();
    }

    public void setTagName(String str) {
        this.tagName_ = Sparta.intern(str);
        notifyObservers();
    }

    void toString(Writer writer) throws IOException {
        for (Node node = this.firstChild_; node != null; node = node.getNextSibling()) {
            node.toString(writer);
        }
    }

    public void toXml(Writer writer) throws IOException {
        writer.write(new StringBuffer().append("<").append(this.tagName_).toString());
        if (this.attributeNames_ != null) {
            Enumeration elements = this.attributeNames_.elements();
            while (elements.hasMoreElements()) {
                String str = (String) elements.nextElement();
                String str2 = (String) this.attributes_.get(str);
                writer.write(new StringBuffer().append(" ").append(str).append("=\"").toString());
                Node.htmlEncode(writer, str2);
                writer.write("\"");
            }
        }
        if (this.firstChild_ == null) {
            writer.write("/>");
            return;
        }
        writer.write(">");
        for (Node node = this.firstChild_; node != null; node = node.getNextSibling()) {
            node.toXml(writer);
        }
        writer.write(new StringBuffer().append("</").append(this.tagName_).append(">").toString());
    }

    public boolean xpathEnsure(String str) throws ParseException {
        try {
            if (xpathSelectElement(str) != null) {
                return false;
            }
            Element element;
            XPath xPath = XPath.get(str);
            Enumeration steps = xPath.getSteps();
            int i = 0;
            while (steps.hasMoreElements()) {
                steps.nextElement();
                i++;
            }
            Step[] stepArr = new Step[(i - 1)];
            Enumeration steps2 = xPath.getSteps();
            for (i = 0; i < stepArr.length; i++) {
                stepArr[i] = (Step) steps2.nextElement();
            }
            Step step = (Step) steps2.nextElement();
            if (stepArr.length == 0) {
                element = this;
            } else {
                String xPath2 = XPath.get(xPath.isAbsolute(), stepArr).toString();
                xpathEnsure(xPath2.toString());
                element = xpathSelectElement(xPath2);
            }
            element.appendChildNoChecking(makeMatching(element, step, str));
            return true;
        } catch (Throwable e) {
            throw new ParseException(str, e);
        }
    }

    public Element xpathSelectElement(String str) throws ParseException {
        try {
            return visitor(str, false).getFirstResultElement();
        } catch (Throwable e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public Enumeration xpathSelectElements(String str) throws ParseException {
        try {
            return visitor(str, false).getResultEnumeration();
        } catch (Throwable e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public String xpathSelectString(String str) throws ParseException {
        try {
            return visitor(str, true).getFirstResultString();
        } catch (Throwable e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public Enumeration xpathSelectStrings(String str) throws ParseException {
        try {
            return visitor(str, true).getResultEnumeration();
        } catch (Throwable e) {
            throw new ParseException("XPath problem", e);
        }
    }
}
