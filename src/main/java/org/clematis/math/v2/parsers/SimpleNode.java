/* Generated By:JJTree: Do not edit this line. SimpleNode.java */
package org.clematis.math.v2.parsers;

import java.util.ArrayList;

import org.clematis.math.v2.algorithm.AlgorithmException;
import org.clematis.math.v2.algorithm.iParameterProvider;
import org.jdom2.Element;

/**
 * Node class derived from stub, generated by JavaCC. This is a basic node for maths
 * expression tree. It holds reference to parent and children nodes.
 */
public class SimpleNode implements Node {
    /**
     * Parent node, it is also a calculable expression item
     */
    private Node parent = null;
    /**
     * Node's children, it is also calculable expression items
     */
    private Node[] children = null;
    /**
     * Name of this node from javacc token
     */
    protected String token = null;
    /**
     * Modifier for this node
     */
    protected int kind = -1;
    /**
     * Ident from parser grammar
     */
    protected int id = 0;

    /**
     * Empty public constructor
     */
    public SimpleNode() {
    }

    /**
     * Constructor with ident, used by javacc parser
     *
     * @param i ident, used by javacc parser
     */
    public SimpleNode(int i) {
        id = i;
    }

    /**
     * Shallow copy constructor. This does not copy parent and children nodes.
     *
     * @param node to copy
     */
    public SimpleNode(SimpleNode node) {
        this.id = node.id;
        this.kind = node.kind;
        this.token = node.token;
    }

    public String getTokenName() {
        return token != null ? token : "";
    }

    public void setTokenName(String tokenName) {
        token = tokenName;
    }

    public int getTokenKind() {
        return kind;
    }

    public void setTokenKind(int kind) {
        this.kind = kind;
    }

    public int getId() {
        return id;
    }

    public static Node jjtCreate(Object parser, int id) {
        return NodeFactory.createNode(id, parser);
    }

    public void jjtOpen() {
        // does nothing for now
    }

    public void jjtClose() {
        // does nothing for now
    }

    public void jjtSetParent(Node n) {
        parent = n;
    }

    public Node jjtGetParent() {
        return parent;
    }

    public Node[] getChildren() {
        return children;
    }

    public void jjtAddChild(Node n, int i) {
        if (getChildren() == null) {
            children = new Node[i + 1];
        } else if (i >= getChildren().length) {
            Node[] c = new Node[i + 1];
            System.arraycopy(getChildren(), 0, c, 0, getChildren().length);
            children = c;
        }
        getChildren()[i] = n;
    }

    public Node jjtGetChild(int i) {
        return getChildren()[i];
    }

    public int jjtGetNumChildren() {
        return (getChildren() == null) ? 0 : getChildren().length;
    }

    public String toString() {
        return getTokenName();
    }

    public String toString(String prefix) {
        return prefix + getClass() + " : " + this;
    }

    /**
     * Calculates the values of children of this node. It calls calculate() on every child.
     *
     * @return the calculated first argument or result of operation
     * @throws org.clematis.math.v2.algorithm.AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculateArguments(iParameterProvider parameterProvider, ArrayList<Node> calculated_nodes)
        throws AlgorithmException {
        if (jjtGetNumChildren() > 0) {
            /**
             * Calculate all arguments
             */
            for (int i = 0; i < jjtGetNumChildren(); i++) {
                // get next argument and calculate it
                Node calculated_node = jjtGetChild(i).calculate(parameterProvider);
                if (calculated_node != null) {
                    calculated_nodes.add(calculated_node);
                } else {
                    throw new AlgorithmException("Cannot calculate node: " + jjtGetChild(i));
                }
            }
            return calculated_nodes.get(0);
        }
        return null;
    }

    /**
     * Calculate the value of this node. It calls calculate() on every child and then makes
     * operation which this node was created for. If no operation is defined, method returns
     * the calculated first argument. Takes the values for parameters from parameter provider, if
     * they are exist.
     *
     * @param parameterProvider with precalculated values for some parameters
     * @return the calculated first argument or result of operation
     * @throws org.clematis.math.v2.algorithm.AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculate() throws AlgorithmException {
        return calculate(null);
    }

    /**
     * Calculate the value of this node. It calls calculate() on every child and then makes
     * operation which this node was created for. If no operation is defined, method returns
     * the calculated first argument. Takes the values for parameters from parameter provider, if
     * they are exist.
     *
     * @param parameterProvider with precalculated values for some parameters
     * @return the calculated first argument or result of operation
     * @throws org.clematis.math.v2.algorithm.AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node calculate(iParameterProvider parameterProvider) throws AlgorithmException {
        Node result = calculateArguments(parameterProvider, new ArrayList<Node>());
        if (result != null) {
            return result;
        } else {
            return new SimpleNode(this);
        }
    }

    /**
     * Simplification aid. This node should be able to add another node to itself.
     *
     * @param item to add to this node
     * @return a result of addition, the Node representing either constant values or addition operation.
     * @throws org.clematis.math.v2.algorithm.AlgorithmException is thrown if some error occurs while calculating or null result is yeilded.
     */
    public Node add(Node item) throws AlgorithmException {
        return null;
    }

    /**
     * Simplification aid. This node should be able to multiply another node by itself.
     *
     * @param item to multiply this node with
     * @return a result of multiplication, the Node representing either constant values or multiplication
     * operation.
     * @throws org.clematis.math.v2.algorithm.AlgorithmException
     */
    public Node multiply(Node item) throws AlgorithmException {
        return null;
    }

    /**
     * Simplification aid. Returns true, if given node is in some relationship with this node. For example,
     * it is also addition or multiplication, but with a different set of arguments. For more information
     * about similar items consult documentation.
     *
     * @param item to check similarity of
     * @return true if given node is in some relationship with this node
     */
    public boolean isSimilar(Node item) {
        if (item instanceof SimpleNode toCompare) {
            /**
             * Compare token names
             */
            boolean tokenNames = getTokenName() != null && toCompare.getTokenName() != null
                && getTokenName().equals(toCompare.getTokenName());
            if (tokenNames) {
                /**
                 * Compare token kinds
                 */
                return getTokenName() != null && toCompare.getTokenName() != null
                    && getTokenName().equals(toCompare.getTokenName());
            }
        }
        return false;
    }

    /**
     * Simplification aid. Returns true, if given node equals this node. For example,
     * it is also addition or multiplication, but with a disordered set of arguments. For more information
     * about similar items consult documentation.
     *
     * @param item to check similarity of
     * @return true if given node equals this node
     */
    public boolean equals(Node item) {
        /**
         * In addition to similarity we need the same quantity of children and they equality,
         * although they may be in different order.
         */
        if (isSimilar(item)) {
            if ((this.jjtGetNumChildren() > 0) && (this.jjtGetNumChildren() == item.jjtGetNumChildren())) {
                for (Node child : getChildren()) {
                    boolean found = false;
                    for (Node itemChild : item.getChildren()) {
                        if (child.equals(itemChild)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        /** cannot find - not equal */
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Dump this node along with its children to mathml
     *
     * @return mathml for this node along with its children
     */
    public Element toMathML() {
        return null;
    }

    /**
     * Dump this node along with its children to text tree to standard output stream
     */
    public void dump(String prefix) {
        System.out.println(toString(prefix));
        if (getChildren() != null) {
            for (int i = 0; i < getChildren().length; ++i) {
                Node n = getChildren()[i];
                if (n != null) {
                    n.dump(prefix + " ");
                }
            }
        }
    }

}
