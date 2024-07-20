// Created: 12.05.2004 T 17:58:28
package org.clematis.math.v1.algorithm;

import java.io.Serializable;

/**
 * Key for parameters and functions hashtable
 */
public class Key implements Serializable {
    private String name = "";
    private int no = 0;
    private boolean function = false;
    private String partId = "";
    private int line = 0;

    public Key(Key key) {
        this.setName(key.getName());
        this.setNo(key.getNo());
        this.setPartId(key.getPartId());
        this.setFunction(key.isFunction());
        this.setLine(key.getLine());
    }

    public Key(String name) {
        this.setName(name);
    }

    public int hashCode() {
        return getPartId().hashCode() + getName().hashCode() * 2 + getNo() + (isFunction() ? 1 : 0);
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public boolean equals(Object key) {
        if (this == key) {
            return true;
        }
        if (key instanceof Key anotherKey) {
            return anotherKey.getName() != null
                && anotherKey.getName().equals(this.getName())
                && anotherKey.getPartId().equals(this.partId)
                && anotherKey.getNo() == this.getNo();
        }
        return false;
    }

    public String getName() {
        return name != null ? name.trim() : "";
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public boolean isFunction() {
        return function;
    }

    public void setFunction(boolean function) {
        this.function = function;
    }

    public String getPartId() {
        return partId != null ? partId.trim() : "";
    }

    public void setPartId(String partId) {
        this.partId = partId;
    }
}
