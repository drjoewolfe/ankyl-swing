package com.jwolfe.ankyl.swing;

import javax.swing.tree.DefaultMutableTreeNode;

public class TaggedMutableTreeNode extends DefaultMutableTreeNode {
    private Object tag;
    private String description;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaggedMutableTreeNode() {
    }

    public TaggedMutableTreeNode(Object userObject) {
        super(userObject);
    }

    public TaggedMutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }
}
