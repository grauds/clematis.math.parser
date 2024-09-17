// Created: 12.05.2004 T 17:58:28
package org.clematis.math.v1.algorithm;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Key for parameters and functions hashtable
 */
@Setter
@Getter
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

    /**
     * Create key to find in parameter provider. This method
     * alternates names with braces like ${a} to $a
     *
     * @param inputName of desired parameter
     * @return key to find
     */
    public static Key create(String inputName) {
        /* normalize parameter token */
        String name = inputName;
        if (SimpleParameter.isNameWithBraces(name)) {
            name = SimpleParameter.alternateParameterName(name);
        }
        return new Key(name);
    }
}
