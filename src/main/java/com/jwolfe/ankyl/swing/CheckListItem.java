package com.jwolfe.ankyl.swing;

public class CheckListItem {
    private String label;
    private String description;
    private Object tag;
    private boolean isSelected = false;

    public CheckListItem(final String label) {
        this.label = label;
    }

    public CheckListItem(final String label, final boolean isSelected) {
        this(label);
        this.isSelected = isSelected;
    }

    public CheckListItem(final String label, final Object tag, final boolean isSelected) {
        this(label, isSelected);
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(final Object tag) {
        this.tag = tag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(final boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }
}
