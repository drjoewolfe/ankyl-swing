package com.jwolfe.ankyl.swing;

import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class CheckBoxWithIconListCellRenderer extends JPanel implements ListCellRenderer {
    JCheckBox checkBox;
    JPanel panel;
    JLabel label;

    private Function<CheckListItem, ImageIcon> iconProvider;

    public void setIconProvider(Function<CheckListItem, ImageIcon> iconProvider) {
        this.iconProvider = iconProvider;
    }

    public CheckBoxWithIconListCellRenderer() {
        super();
//        this.setLayout(new GridLayout());

        checkBox = new JCheckBox();
        label = new JLabel();

//        panel = new JPanel();
//        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
//        panel.add(checkBox);
//        panel.add(label);
        add(checkBox);
        add(label);
        checkBox.setBackground(UIManager.getColor("Tree.textBackground"));
        //panel.setBackground(UIManager.getColor("Tree.textBackground"));

        //add(panel, BorderLayout.CENTER);
        setOpaque(false);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean hasFocus) {

        CheckListItem item = (CheckListItem) value;

        checkBox.setEnabled(list.isEnabled());
        checkBox.setSelected(item.isSelected());
        checkBox.setFont(list.getFont());
        checkBox.setBackground(list.getBackground());
        checkBox.setForeground(list.getForeground());

        if (iconProvider != null) {
            var icon = iconProvider.apply(item);
            if (icon != null) {
                label.setIcon(icon);
            } else {
                label.setIcon(null);
            }
        }

        label.setText(item.toString());
        label.setFont(list.getFont());
        label.setBackground(list.getBackground());
        label.setForeground(list.getForeground());

        this.setToolTipText(item.getDescription());
        return this;
    }
}
