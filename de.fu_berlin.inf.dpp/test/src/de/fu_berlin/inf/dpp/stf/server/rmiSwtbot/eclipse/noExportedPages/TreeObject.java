package de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.noExportedPages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.conditions.SarosConditions;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.EclipseObject;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.RmiSWTWorkbenchBot;

public class TreeObject extends EclipseObject {

    public TreeObject(RmiSWTWorkbenchBot rmiBot) {
        super(rmiBot);
    }

    /**
     * test, if a tree node exist. This method ist very helpful, if you are not
     * sure, how exactly is the tree node's name.
     */
    public boolean isTreeItemWithMatchTextExist(SWTBotTree tree,
        String... regexs) {
        SWTBotTreeItem item = null;
        for (String regex : regexs) {
            boolean exist = false;
            if (item == null) {
                for (int i = 0; i < tree.getAllItems().length; i++) {
                    log.info("treeItem'name: "
                        + tree.getAllItems()[i].getText());
                    if (tree.getAllItems()[i].getText().matches(regex)) {
                        item = tree.getAllItems()[i].expand();
                        exist = true;
                    }
                }
            } else {
                for (String nodeName : item.getNodes()) {
                    log.info("node'name: " + nodeName);
                    if (nodeName.matches(regex)) {
                        item = item.getNode(nodeName).expand();
                        exist = true;
                    }
                }
            }
            if (!exist) {
                return false;
            }
        }
        return item != null;
    }

    public SWTBotTreeItem getTreeItemWithMatchText(SWTBotTree tree,
        String... regexs) {
        try {
            SWTBotTreeItem item = null;
            for (String regex : regexs) {
                if (item == null) {
                    for (int i = 0; i < tree.getAllItems().length; i++) {
                        log.info("treeItem'name: "
                            + tree.getAllItems()[i].getText());
                        if (tree.getAllItems()[i].getText().matches(regex)) {
                            item = tree.getAllItems()[i].expand();
                        }
                    }
                } else {
                    for (String nodeName : item.getNodes()) {
                        log.info("node'name: " + nodeName);
                        if (nodeName.matches(regex)) {
                            item = item.getNode(nodeName).expand();
                        }
                    }
                }
            }
            return item;
        } catch (WidgetNotFoundException e) {
            log.error("gematched Context menu can't be found!", e);
            return null;
        }
    }

    public SWTBotTreeItem selectTreeWithLabelsWithWaitungExpand(
        SWTBotTree tree, String... labels) {
        SWTBotTreeItem selectedTreeItem = null;
        for (String label : labels) {
            try {
                if (selectedTreeItem == null) {
                    // treeObject.waitUntilTreeExisted(tree, label);
                    selectedTreeItem = tree.expandNode(label);
                    log.info("treeItem name: " + selectedTreeItem.getText());
                } else {
                    // treeObject
                    // .waitUntilTreeNodeExisted(selectedTreeItem, label);
                    selectedTreeItem = selectedTreeItem.expandNode(label);
                    log.info("treeItem name: " + selectedTreeItem.getText());
                }
            } catch (WidgetNotFoundException e) {
                log.error("treeitem \"" + label + "\" not found");
            }
        }
        if (selectedTreeItem != null) {
            log.info("treeItem name: " + selectedTreeItem.getText());
            selectedTreeItem.select();
            return selectedTreeItem;
        }
        return null;
    }

    public SWTBotTreeItem selectTreeWithLabels(SWTBotTree tree,
        String... labels) {
        try {
            return tree.expandNode(labels);
        } catch (WidgetNotFoundException e) {
            log.warn("table item not found.", e);
            return null;
        }

    }

    public List<String> getAllItemsOfTreeItem(String... paths) {
        SWTBotTree tree = bot.tree();
        SWTBotTreeItem item = selectTreeWithLabels(tree, paths);
        List<String> list = new ArrayList<String>();

        for (int i = 0; i < item.getItems().length; i++) {
            list.add(item.getItems()[i].getText());
            log.info("existed item In TreeItem:  " + list.get(i));
        }
        return list;
    }

    public List<String> getAllItemsOftree() {
        SWTBotTree tree = bot.tree();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < tree.getAllItems().length; i++) {
            list.add(tree.getAllItems()[i].getText());
            log.info("existed TreeItem in Tree:  " + list.get(i));
        }
        return list;
    }

    public boolean isTreeItemOfTreeExisted(SWTBotTree tree, String label) {
        return getAllItemsOftree().contains(label);
        // try {
        // tree.getTreeItem(label);
        // return true;
        // } catch (WidgetNotFoundException e) {
        // return false;
        // }

    }

    public void waitUntilTreeItemExisted(SWTBotTreeItem treeItem,
        String nodeName) {
        waitUntil(SarosConditions.existTreeItem(treeItem, nodeName));
    }

    public void waitUntilTreeExisted(SWTBotTree tree, String nodeName) {
        waitUntil(SarosConditions.existTree(tree, nodeName));
    }

    public void waitUntilTreeNodeExisted(SWTBotTreeItem treeItem,
        String nodeName) {
        waitUntil(SarosConditions.existTreeItem(treeItem, nodeName));
    }
}