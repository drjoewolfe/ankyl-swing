package com.jwolfe.ankyl.swing;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import static java.awt.GridBagConstraints.*;

public class JCheckBoxTree extends JTree {

    private boolean showCounts;

    private Function<TaggedMutableTreeNode, ImageIcon> iconProvider;

    public boolean isShowCounts() {
        return showCounts;
    }

    public void setShowCounts(boolean showCounts) {
        this.showCounts = showCounts;
    }

    public void setIconProvider(Function<TaggedMutableTreeNode, ImageIcon> iconProvider) {
        this.iconProvider = iconProvider;
    }

    private static final long serialVersionUID = -4194122328392241790L;

    JCheckBoxTree selfPointer = this;

    // Defining data structure that will enable to fast check-indicate the state of each node
    // It totally replaces the "selection" mechanism of the JTree
    private class CheckedNode {
        boolean isSelected;
        boolean hasChildren;
        boolean allChildrenSelected;

        public CheckedNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_) {
            isSelected = isSelected_;
            hasChildren = hasChildren_;
            allChildrenSelected = allChildrenSelected_;
        }
    }

    HashMap<TreePath, CheckedNode> nodesCheckingState;
    HashSet<TreePath> checkedPaths = new HashSet<TreePath>();

    // Defining a new event type for the checking mechanism and preparing event-handling mechanism
    protected EventListenerList listenerList = new EventListenerList();

    public class CheckChangeEvent extends EventObject {
        private static final long serialVersionUID = -8100230309044193368L;

        public CheckChangeEvent(Object source) {
            super(source);
        }
    }

    public interface CheckChangeEventListener extends EventListener {
        public void checkStateChanged(CheckChangeEvent event);
    }

    public void addCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerList.add(CheckChangeEventListener.class, listener);
    }
    public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerList.remove(CheckChangeEventListener.class, listener);
    }

    void fireCheckChangeEvent(CheckChangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == CheckChangeEventListener.class) {
                ((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
            }
        }
    }

    // Override
    public void setModel(TreeModel newModel) {
        super.setModel(newModel);
        resetCheckingState();
    }

    // New method that returns only the checked paths (totally ignores original "selection" mechanism)
    public TreePath[] getCheckedPaths() {
        return checkedPaths.toArray(new TreePath[checkedPaths.size()]);
    }

    // Returns true in case that the node is selected, has children but not all of them are selected
    public boolean isSelectedPartially(TreePath path) {
        CheckedNode cn = nodesCheckingState.get(path);
        return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
    }

    private void resetCheckingState() {
        nodesCheckingState = new HashMap<TreePath, CheckedNode>();
        checkedPaths = new HashSet<TreePath>();
        if(! (getModel().getRoot() instanceof TaggedMutableTreeNode)) {
            return;
        }

        TaggedMutableTreeNode node = (TaggedMutableTreeNode) getModel().getRoot();
        if (node == null) {
            return;
        }

        addSubtreeToCheckingStateTracking(node);
    }

    // Creating data structure of the current model for the checking mechanism
    private void addSubtreeToCheckingStateTracking(TaggedMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        TreePath tp = new TreePath(path);
        CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
        nodesCheckingState.put(tp, cn);
        for (int i = 0 ; i < node.getChildCount() ; i++) {
            addSubtreeToCheckingStateTracking((TaggedMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
        }
    }

    // Overriding cell renderer by a class that ignores the original "selection" mechanism
    // It decides how to show the nodes due to the checking-mechanism
    public class CheckBoxTreeCellRenderer extends JPanel implements TreeCellRenderer {
        private static final long serialVersionUID = -7341833835878991719L;
        JCheckBox checkBox;
        JPanel panel;
        JLabel nameLabel;
        JLabel descriptionLabel;
        JLabel countLabel;

        public CheckBoxTreeCellRenderer() {
            super();

            initializeControls();
        }

        private void initializeControls() {
            this.removeAll();

            // this.setLayout(new BorderLayout());
            this.setLayout(new BorderLayout());

            checkBox = new JCheckBox();
            nameLabel = new JLabel();
            descriptionLabel = new JLabel();
            countLabel = new JLabel();

            panel = new JPanel();
            panel.setLayout(new GridBagLayout());
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = 0;
            constraints.anchor = LINE_START;
            constraints.insets.bottom = 2;
            panel.add(checkBox, constraints);

            constraints.gridx = 1;
            constraints.gridy = 0;
            constraints.anchor = LINE_START;
            constraints.insets.left = 2;
            constraints.insets.right = 2;
            constraints.insets.bottom = 2;
            panel.add(nameLabel, constraints);

            constraints.gridx = 2;
            constraints.gridy = 0;
            constraints.anchor = LINE_START;
            constraints.insets.left = 2;
            constraints.insets.right = 2;
            constraints.insets.bottom = 2;
            panel.add(countLabel, constraints);

            constraints.gridx = 1;
            constraints.gridy = 1;
            constraints.anchor = LINE_START;
            constraints.insets.left = 2;
            constraints.insets.right = 2;
            constraints.insets.bottom = 3;
            panel.add(descriptionLabel, constraints);

            checkBox.setBackground(UIManager.getColor("Tree.textBackground"));
            countLabel.setFont(countLabel.getFont().deriveFont(Font.PLAIN));
            countLabel.setForeground(Color.BLUE);

            var descriptionFont = descriptionLabel.getFont().deriveFont(Font.ITALIC);
            descriptionLabel.setFont(descriptionFont);

            panel.setBackground(UIManager.getColor("Tree.textBackground"));
            add(panel, BorderLayout.WEST);
            setBackground(UIManager.getColor("Tree.textBackground"));
            setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row,
                                                      boolean hasFocus) {
            if(!(value instanceof TaggedMutableTreeNode)) {
                return this;
            }

            TaggedMutableTreeNode node = (TaggedMutableTreeNode) value;
            Object obj = node.getUserObject();
            TreePath tp = new TreePath(node.getPath());
            CheckedNode cn = nodesCheckingState.get(tp);
            if (cn == null) {
                return this;
            }
            checkBox.setSelected(cn.isSelected);
            checkBox.setOpaque(cn.isSelected && cn.hasChildren && ! cn.allChildrenSelected);

            if(iconProvider != null) {
                var icon = iconProvider.apply(node);
                if(icon != null) {
                    nameLabel.setIcon(icon);
                }
                else {
                    nameLabel.setIcon(null);
                }
            }

            var label = obj.toString();
            nameLabel.setText(label);

            if(showCounts && !node.isLeaf()) {
                String countString = null;
                if(tree instanceof JCheckBoxTree) {
                    var checkBoxTree = (JCheckBoxTree) tree;
                    countString = "(" +
                            checkBoxTree.getCheckedLeafCount((node)) +
                            " / " +
                            node.getLeafCount() + ")";
                }
                else {
                    countString = "(" +
                            node.getLeafCount() + ")";
                }

                countLabel.setText(countString);
            }
            else {
                countLabel.setText("");
            }

            descriptionLabel.setText(node.getDescription());

            return this;
        }
    }

    public JCheckBoxTree() {
        super();
        // Disabling toggling by double-click
        this.setToggleClickCount(0);
        // Overriding cell renderer by new one defined above
        CheckBoxTreeCellRenderer cellRenderer = new CheckBoxTreeCellRenderer();
        this.setCellRenderer(cellRenderer);

        // Overriding selection model by an empty one
        DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
            private static final long serialVersionUID = -8190634240451667286L;
            // Totally disabling the selection mechanism
            public void setSelectionPath(TreePath path) {
            }
            public void addSelectionPath(TreePath path) {
            }
            public void removeSelectionPath(TreePath path) {
            }
            public void setSelectionPaths(TreePath[] pPaths) {
            }
        };
        // Calling checking mechanism on mouse click
        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                TreePath tp = selfPointer.getPathForLocation(arg0.getX(), arg0.getY());
                if (tp == null) {
                    return;
                }
                boolean checkMode = ! nodesCheckingState.get(tp).isSelected;
                checkSubTree(tp, checkMode);
                updatePredecessorsWithCheckMode(tp, checkMode);
                // Firing the check change event
                fireCheckChangeEvent(new CheckChangeEvent(new Object()));
                // Repainting tree after the data structures were updated
                selfPointer.repaint();
            }
            public void mouseEntered(MouseEvent arg0) {
            }
            public void mouseExited(MouseEvent arg0) {
            }
            public void mousePressed(MouseEvent arg0) {
            }
            public void mouseReleased(MouseEvent arg0) {
            }
        });
        this.setSelectionModel(dtsm);
    }

    // When a node is checked/unchecked, updating the states of the predecessors
    public void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
        TreePath parentPath = tp.getParentPath();
        // If it is the root, stop the recursive calls and return
        if (parentPath == null) {
            return;
        }
        CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
        TaggedMutableTreeNode parentNode = (TaggedMutableTreeNode) parentPath.getLastPathComponent();
        parentCheckedNode.allChildrenSelected = true;
        parentCheckedNode.isSelected = false;
        for (int i = 0 ; i < parentNode.getChildCount() ; i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
            // It is enough that even one subtree is not fully selected
            // to determine that the parent is not fully selected
            if (! childCheckedNode.allChildrenSelected) {
                parentCheckedNode.allChildrenSelected = false;
            }
            // If at least one child is selected, selecting also the parent
            if (childCheckedNode.isSelected) {
                parentCheckedNode.isSelected = true;
            }
        }
        if (parentCheckedNode.isSelected) {
            checkedPaths.add(parentPath);
        } else {
            checkedPaths.remove(parentPath);
        }
        // Go to upper predecessor
        updatePredecessorsWithCheckMode(parentPath, check);
    }

    // Recursively checks/unchecks a subtree
    public void checkSubTree(TreePath tp, boolean check) {
        CheckedNode cn = nodesCheckingState.get(tp);
        cn.isSelected = check;
        TaggedMutableTreeNode node = (TaggedMutableTreeNode) tp.getLastPathComponent();
        for (int i = 0 ; i < node.getChildCount() ; i++) {
            checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
        }
        cn.allChildrenSelected = check;
        if (check) {
            checkedPaths.add(tp);
        } else {
            checkedPaths.remove(tp);
        }
    }

    public int getCheckedLeafCount(TaggedMutableTreeNode root) {
        if(root.isLeaf()) {
            TreeNode[] path = root.getPath();
            TreePath tp = new TreePath(path);
            var checkState = nodesCheckingState.get(tp);

            if(checkState.isSelected) {
                return 1;
            }
            else {
                return 0;
            }
        }

        var checkedLeafCount = 0;
        var children = root.children();
        while(children.hasMoreElements()) {
            var childNode = children.nextElement();
            checkedLeafCount += getCheckedLeafCount((TaggedMutableTreeNode) childNode);
        }

        return checkedLeafCount;
    }
}
