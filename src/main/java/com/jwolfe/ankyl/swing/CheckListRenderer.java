package com.jwolfe.ankyl.swing;

import com.jwolfe.ankyl.swing.CheckListItem;

import javax.swing.*;
import java.awt.*;

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
