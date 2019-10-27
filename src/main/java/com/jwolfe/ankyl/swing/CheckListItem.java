package com.jwolfe.ankyl.swing;

public class CheckListItem {
    private String label;
    private String description;
    private String tag;
    private boolean isSelected = false;

    public CheckListItem(String label) {
        this.label = label;
    }

    public CheckListItem(String label, boolean isSelected) {
        this(label);
        this.isSelected = isSelected;
    }

    public CheckListItem(String label, String tag, boolean isSelected) {
        this(label, isSelected);
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public String toString() {
        return label;
    }
}
