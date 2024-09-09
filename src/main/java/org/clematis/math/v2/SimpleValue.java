// Created: 14.07.2005 T 17:54:50
package org.clematis.math.v2;

import org.clematis.math.IValue;
import org.clematis.math.io.OutputFormatSettings;
import org.clematis.math.v2.parsers.Node;
import org.clematis.math.v2.parsers.SimpleNode;

/**
 * Simple algorithm value = Node + IValue
 */
public class SimpleValue extends SimpleNode implements IValue {
    /**
     * Empty public constructor
     */
    public SimpleValue() {
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    public SimpleValue(SimpleValue node) {
        super(node);
    }

    public SimpleValue(String value) {
        this.token = value;
    }

    public SimpleValue(int i) {
        super(i);
    }

    public String getValue() {
        return token;
    }

    public String getValue(OutputFormatSettings fs) {
        return token;
    }

    public void setValue(String value) {
        this.token = value;
    }

    public Node[] getChildren() {
        return null;
    }

    public void jjtAddChild(Node n, int i) {
        // this class does not accept children
    }

    public Node jjtGetChild(int i) {
        return null;
    }
}
