package de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.sarosFinder.remoteComponents.contextMenu;

import java.rmi.RemoteException;
import java.util.List;

import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.finder.remoteWidgets.STFBotShell;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.finder.remoteWidgets.STFBotTableItem;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.finder.remoteWidgets.STFBotTree;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.finder.remoteWidgets.STFBotTreeItem;
import de.fu_berlin.inf.dpp.stf.server.rmiSarosSWTBot.sarosFinder.remoteComponents.EclipseComponentImp;

public class ContextMenuWrapperImp extends EclipseComponentImp implements
    ContextMenuWrapper {

    protected static TeamCImp teamC;
    protected static NewCImp newC;
    protected static RefactorCImp reafactorC;

    protected STFBotTreeItem treeItem;
    protected STFBotTree tree;
    protected TreeItemType type;

    protected STFBotTableItem tableItem;

    public void setTreeItem(STFBotTreeItem treeItem) {
        this.treeItem = treeItem;
    }

    public void setTableItem(STFBotTableItem tableItem) {
        this.tableItem = tableItem;
    }

    public void setTreeItemType(TreeItemType type) {
        this.type = type;
    }

    public void setTree(STFBotTree tree) {
        this.tree = tree;
    }

    public void open() throws RemoteException {
        treeItem.contextMenu(CM_OPEN).click();
    }

    public void copy() throws RemoteException {
        treeItem.contextMenu(MENU_COPY).click();
    }

    public void paste(String target) throws RemoteException {
        if (treeItem == null) {
            tree.contextMenu(MENU_PASTE).click();
            STFBotShell shell = bot().shell(SHELL_COPY_PROJECT);
            shell.activate();
            shell.bot().textWithLabel("Project name:").setText(target);
            shell.bot().button(OK).click();
            bot().waitsUntilShellIsClosed(SHELL_COPY_PROJECT);
            bot().sleep(1000);
        }
        // switch (type) {
        // case PROJECT:

        // break;
        // default:
        // break;
        // }
    }

    public void openWith(String editorType) throws RemoteException {
        treeItem.contextMenu(CM_OPEN_WITH, CM_OTHER).click();
        bot().waitUntilShellIsOpen(SHELL_EDITOR_SELECTION);
        STFBotShell shell_bob = bot().shell(SHELL_EDITOR_SELECTION);
        shell_bob.activate();
        shell_bob.bot().table().getTableItem(editorType).select();
        shell_bob.bot().button(OK).waitUntilIsEnabled();
        shell_bob.confirm(OK);
    }

    public void delete() throws RemoteException {
        treeItem.contextMenu(CM_DELETE).click();
        switch (type) {
        case PROJECT:
            bot().shell(SHELL_DELETE_RESOURCE).confirmWithCheckBox(OK, true);
            bot().waitsUntilShellIsClosed(SHELL_DELETE_RESOURCE);
            break;
        case JAVA_PROJECT:
            bot().shell(SHELL_DELETE_RESOURCE).confirmWithCheckBox(OK, true);
            bot().waitsUntilShellIsClosed(SHELL_DELETE_RESOURCE);
            break;
        default:
            bot().waitUntilShellIsOpen(CONFIRM_DELETE);
            bot().shell(CONFIRM_DELETE).activate();
            bot().shell(CONFIRM_DELETE).bot().button(OK).click();
            bot().sleep(300);
            break;
        }
        tree.waitUntilItemNotExists(treeItem.getText());
    }

    public NewC newC() throws RemoteException {
        newC.setTree(tree);
        newC.setTreeItem(treeItem);
        return newC;
    }

    public TeamC team() throws RemoteException {
        teamC.setTreeItem(treeItem);
        return teamC;
    }

    public RefactorC refactor() throws RemoteException {
        reafactorC.setTreeItem(treeItem);
        reafactorC.setTreeItemType(type);
        return reafactorC;
    }

    public boolean existsWithRegex(String name) throws RemoteException {
        if (treeItem == null) {
            for (String item : tree.getTextOfItems()) {
                if (item.matches(name + ".*"))
                    return true;
            }
            return false;
        } else {
            for (String item : treeItem.getTextOfItems()) {
                if (item.matches(name + ".*"))
                    return true;
            }
            return false;
        }
    }

    public boolean exists(String name) throws RemoteException {
        if (treeItem == null) {
            return tree.getTextOfItems().contains(name);
        } else {
            return treeItem.getTextOfItems().contains(name);
        }
    }

    public void deleteAllProjects() throws RemoteException {
        List<String> allTreeItems = tree.getTextOfItems();
        if (allTreeItems != null) {
            for (String item : allTreeItems) {
                tree.selectTreeItem(item).contextMenu(MENU_DELETE).click();
                STFBotShell shell = bot().shell(SHELL_DELETE_RESOURCE);

                shell.confirmWithCheckBox(OK, true);
                bot().waitsUntilShellIsClosed(SHELL_DELETE_RESOURCE);
            }
        }
    }

    public void deleteAllItems() throws RemoteException {

        for (String text : treeItem.getTextOfItems()) {
            treeItem.expandNode(text).contextMenu(CM_DELETE).click();
            bot().waitUntilShellIsOpen(CONFIRM_DELETE);
            bot().shell(CONFIRM_DELETE).activate();
            bot().shell(CONFIRM_DELETE).bot().button(OK).click();
        }
    }
}