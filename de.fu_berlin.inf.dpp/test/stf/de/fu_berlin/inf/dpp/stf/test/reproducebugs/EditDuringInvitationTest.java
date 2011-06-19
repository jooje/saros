package de.fu_berlin.inf.dpp.stf.test.reproducebugs;

import static de.fu_berlin.inf.dpp.stf.client.tester.SarosTester.ALICE;
import static de.fu_berlin.inf.dpp.stf.client.tester.SarosTester.BOB;
import static de.fu_berlin.inf.dpp.stf.client.tester.SarosTester.CARL;
import static de.fu_berlin.inf.dpp.stf.shared.Constants.ACCEPT;
import static de.fu_berlin.inf.dpp.stf.shared.Constants.SHELL_SESSION_INVITATION;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.rmi.RemoteException;

import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.StfTestCase;
import de.fu_berlin.inf.dpp.stf.client.util.Util;
import de.fu_berlin.inf.dpp.stf.shared.Constants.TypeOfCreateProject;
import de.fu_berlin.inf.dpp.stf.test.Constants;

public class EditDuringInvitationTest extends StfTestCase {

    /**
     * Preconditions:
     * <ol>
     * <li>ALICE (Host, Write Access), ALICE share a java project with BOB and
     * CARL.</li>
     * <li>BOB (Read-Only Access)</li>
     * <li>CARL (Read-Only Access)</li>
     * </ol>
     * 
     * @throws RemoteException
     * 
     */
    @BeforeClass
    public static void runBeforeClass() throws RemoteException {
        initTesters(ALICE, BOB, CARL);
        setUpWorkbench();
        setUpSaros();
        ALICE
            .superBot()
            .views()
            .packageExplorerView()
            .tree()
            .newC()
            .javaProjectWithClasses(Constants.PROJECT1, Constants.PKG1,
                Constants.CLS1);
    }

    /**
     * 
     * Steps:
     * <ol>
     * <li>Alice invites Bob.</li>
     * <li>Bob accepts the invitation</li>
     * <li>Alice invites Carl</li>
     * <li>Bob changes data during the running invtiation of Carl.</li>
     * </ol>
     * 
     * 
     * Expected Results:
     * <ol>
     * <li>All changes that Bob has done should be on Carl's side. There should
     * not be an inconsistency.</li>.
     * </ol>
     * 
     * @throws RemoteException
     */
    @Test
    public void testEditDuringInvitation() throws RemoteException {
        Util.buildSessionSequentially(Constants.PROJECT1,
            TypeOfCreateProject.NEW_PROJECT, ALICE, BOB);

        assertTrue(ALICE.superBot().views().sarosView()
            .selectParticipant(BOB.getJID()).hasWriteAccess());

        ALICE.superBot().views().sarosView().selectBuddy(CARL.getJID())
            .addToSarosSession();
        CARL.remoteBot().shell(SHELL_SESSION_INVITATION).confirm(ACCEPT);

        CARL.superBot()
            .confirmShellAddProjectWithNewProject(Constants.PROJECT1);

        BOB.superBot().views().packageExplorerView()
            .selectClass(Constants.PROJECT1, Constants.PKG1, Constants.CLS1)
            .open();

        BOB.remoteBot().editor(Constants.CLS1_SUFFIX)
            .setTextFromFile(Constants.CP1);

        for (int i = 0; i < 20; i++)
            BOB.remoteBot().editor(Constants.CLS1_SUFFIX).typeText("FooBar");

        String textByBob = BOB.remoteBot().editor(Constants.CLS1_SUFFIX)
            .getText();

        CARL.superBot()
            .views()
            .packageExplorerView()
            .waitUntilClassExists(Constants.PROJECT1, Constants.PKG1,
                Constants.CLS1);

        CARL.superBot().views().packageExplorerView()
            .selectClass(Constants.PROJECT1, Constants.PKG1, Constants.CLS1)
            .open();

        ALICE.remoteBot().editor(Constants.CLS1_SUFFIX)
            .waitUntilIsTextSame(textByBob);
        String textByAlice = ALICE.remoteBot().editor(Constants.CLS1_SUFFIX)
            .getText();

        // There are bugs here, CARL get completely different content as BOB.
        try {
            CARL.remoteBot().editor(Constants.CLS1_SUFFIX)
                .waitUntilIsTextSame(textByBob);
        } catch (TimeoutException e) {
            //
        }
        String textByCarl = CARL.remoteBot().editor(Constants.CLS1_SUFFIX)
            .getText();

        assertEquals(textByBob, textByAlice);
        assertEquals(textByBob, textByCarl);
    }
}
