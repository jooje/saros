package de.fu_berlin.inf.dpp.stf.test.stf.contextmenu;

import static de.fu_berlin.inf.dpp.stf.client.tester.SarosTester.ALICE;
import static de.fu_berlin.inf.dpp.stf.client.tester.SarosTester.BOB;

import java.rmi.RemoteException;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.StfTestCase;
import de.fu_berlin.inf.dpp.stf.shared.Constants.TypeOfCreateProject;
import de.fu_berlin.inf.dpp.stf.test.Constants;

public class ContextMenuShareWithTest extends StfTestCase {

    @BeforeClass
    public static void runBeforeClass() throws RemoteException {
        initTesters(ALICE, BOB);
        setUpWorkbench();
        setUpSaros();
    }

    @Override
    @After
    public void after() throws RemoteException {
        announceTestCaseEnd();
        leaveSessionPeersFirst(ALICE);
    }

    @Test
    public void testShareWithMultipleBuddies() throws RemoteException {
        ALICE
            .superBot()
            .views()
            .packageExplorerView()
            .tree()
            .newC()
            .javaProjectWithClasses(Constants.PROJECT1, Constants.PKG1,
                Constants.CLS1);
        ALICE.superBot().views().packageExplorerView()
            .selectJavaProject(Constants.PROJECT1).shareWith()
            .buddy(BOB.getJID());
        BOB.superBot().confirmShellSessionInvitationAndShellAddProject(
            Constants.PROJECT1, TypeOfCreateProject.NEW_PROJECT);

        BOB.superBot().views().sarosView().waitUntilIsInSession();

        // TODO remove this line
        BOB.remoteBot().sleep(10000);
    }
}
