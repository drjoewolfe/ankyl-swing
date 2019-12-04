package com.jwolfe.ankyl.swing;


import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import java.awt.Component;

public class CheckListRenderer extends JCheckBox implements ListCellRenderer {
    public Component getListCellRendererComponent(final JList list, final Object value,
                                                  final int index, final boolean isSelected, final boolean hasFocus) {
        setEnabled(list.isEnabled());
        setSelected(((CheckListItem) value).isSelected());
        setFont(list.getFont());
        setBackground(list.getBackground());
        setForeground(list.getForeground());
        setText(value.toString());
        return this;
    }
}
