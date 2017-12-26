package com.hp.hpl.sparta;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

class NodeListWithPosition {
    private static final Integer EIGHT = new Integer(8);
    private static final Integer FIVE = new Integer(5);
    private static final Integer FOUR = new Integer(4);
    private static final Integer NINE = new Integer(9);
    private static final Integer ONE = new Integer(1);
    private static final Integer SEVEN = new Integer(7);
    private static final Integer SIX = new Integer(6);
    private static final Integer TEN = new Integer(10);
    private static final Integer THREE = new Integer(3);
    private static final Integer TWO = new Integer(2);
    private Hashtable positions_ = new Hashtable();
    private final Vector vector_ = new Vector();

    NodeListWithPosition() {
    }

    private static Integer identity(Node node) {
        return new Integer(System.identityHashCode(node));
    }

    void add(Node node, int i) {
        Object obj;
        this.vector_.addElement(node);
        switch (i) {
            case 1:
                obj = ONE;
                break;
            case 2:
                obj = TWO;
                break;
            case 3:
                obj = THREE;
                break;
            case 4:
                obj = FOUR;
                break;
            case 5:
                obj = FIVE;
                break;
            case 6:
                obj = SIX;
                break;
            case 7:
                obj = SEVEN;
                break;
            case 8:
                obj = EIGHT;
                break;
            case 9:
                obj = NINE;
                break;
            case 10:
                obj = TEN;
                break;
            default:
                obj = new Integer(i);
                break;
        }
        this.positions_.put(identity(node), obj);
    }

    void add(String str) {
        this.vector_.addElement(str);
    }

    Enumeration iterator() {
        return this.vector_.elements();
    }

    int position(Node node) {
        return ((Integer) this.positions_.get(identity(node))).intValue();
    }

    void removeAllElements() {
        this.vector_.removeAllElements();
        this.positions_.clear();
    }

    public String toString() {
        try {
            StringBuffer stringBuffer = new StringBuffer("{ ");
            Enumeration elements = this.vector_.elements();
            while (elements.hasMoreElements()) {
                Object nextElement = elements.nextElement();
                if (nextElement instanceof String) {
                    stringBuffer.append(new StringBuffer().append("String(").append(nextElement).append(") ").toString());
                } else {
                    Node node = (Node) nextElement;
                    stringBuffer.append(new StringBuffer().append("Node(").append(node.toXml()).append(")[").append(this.positions_.get(identity(node))).append("] ").toString());
                }
            }
            stringBuffer.append("}");
            return stringBuffer.toString();
        } catch (Throwable e) {
            return e.toString();
        }
    }
}
