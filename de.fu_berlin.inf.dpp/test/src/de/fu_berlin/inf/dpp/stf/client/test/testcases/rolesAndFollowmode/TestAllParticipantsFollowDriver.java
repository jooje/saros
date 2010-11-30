package de.fu_berlin.inf.dpp.stf.client.test.testcases.rolesAndFollowmode;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.Musician;
import de.fu_berlin.inf.dpp.stf.client.MusicianConfigurationInfos;
import de.fu_berlin.inf.dpp.stf.client.test.helpers.InitMusician;
import de.fu_berlin.inf.dpp.stf.client.test.helpers.STFTest;

public class TestAllParticipantsFollowDriver extends STFTest {

    /**
     * Preconditions:
     * <ol>
     * <li>Alice (Host, Driver)</li>
     * <li>Bob (Observer)</li>
     * <li>Carl (Observer)</li>
     * <li>Dave (Observer)</li>
     * <li>All observers enable followmode</li>
     * </ol>
     * 
     * @throws AccessException
     * @throws RemoteException
     * @throws InterruptedException
     */
    @BeforeClass
    public static void initMusican() throws AccessException, RemoteException,
        InterruptedException {
        /*
         * initialize the musicians simultaneously
         */
        List<Musician> musicians = InitMusician.initMusiciansConcurrently(
            MusicianConfigurationInfos.PORT_ALICE,
            MusicianConfigurationInfos.PORT_BOB,
            MusicianConfigurationInfos.PORT_CARL,
            MusicianConfigurationInfos.PORT_DAVE);
        alice = musicians.get(0);
        bob = musicians.get(1);
        carl = musicians.get(2);
        dave = musicians.get(3);
        alice.pEV.newJavaProjectWithClass(PROJECT1, PKG1, CLS1);
        alice.editor.closeJavaEditorWithSave(CLS1);

        /*
         * build session with bob, carl and dave simultaneously
         */
        alice.buildSessionConcurrently(PROJECT1, CONTEXT_MENU_SHARE_PROJECT,
            bob, carl, dave);
        // alice.bot.waitUntilNoInvitationProgress();

    }

    /**
     * Closes all opened xmppConnects, popup windows and editor.<br/>
     * Delete all existed projects.
     * 
     * @throws RemoteException
     */
    @AfterClass
    public static void resetSaros() throws RemoteException {
        bob.workbench.resetSaros();
        carl.workbench.resetSaros();
        dave.workbench.resetSaros();
        alice.workbench.resetSaros();
    }

    /**
     * bob, carl and dave follow alice.
     */
    @Before
    public void setFollowMode() throws RemoteException, InterruptedException {
        alice.followedBy(bob, carl, dave);
    }

    /**
     * Closes all opened popup windows and editor.
     * 
     * @throws RemoteException
     */
    @After
    public void cleanUp() throws RemoteException {
        bob.workbench.resetWorkbench();
        carl.workbench.resetWorkbench();
        dave.workbench.resetWorkbench();
        alice.workbench.resetWorkbench();
    }

    /**
     * Steps:
     * <ol>
     * <li>Alice open a editor</li>
     * </ol>
     * 
     * Result:
     * <ol>
     * <li>bob, carl and dave's editor would be opened too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    @Test
    public void followingUserOpenClassWhenFollowedUserOpenClass()
        throws RemoteException {
        assertFalse(bob.editor.isJavaEditorOpen(CLS1));
        assertFalse(carl.editor.isJavaEditorOpen(CLS1));
        assertFalse(dave.editor.isJavaEditorOpen(CLS1));

        alice.pEV.openClass(PROJECT1, PKG1, CLS1);

        bob.editor.waitUntilJavaEditorOpen(CLS1);
        carl.editor.waitUntilJavaEditorOpen(CLS1);
        dave.editor.waitUntilJavaEditorOpen(CLS1);

        assertTrue(bob.editor.isJavaEditorOpen(CLS1));
        assertTrue(carl.editor.isJavaEditorOpen(CLS1));
        assertTrue(dave.editor.isJavaEditorOpen(CLS1));

    }

    /**
     * Steps:
     * <ol>
     * <li>Alice open a editor and set text in the opened java editor without
     * save.</li>
     * </ol>
     * 
     * Result:
     * <ol>
     * <li>bob, carl and dave have the same dirty content as alice and their
     * editor would be opened and activated too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    @Test
    public void testFollowModeByEditingClassByAlice() throws RemoteException {
        alice.editor.setTextInJavaEditorWithoutSave(CP1, PROJECT1, PKG1, CLS1);
        String dirtyClsContentOfAlice = alice.editor.getTextOfJavaEditor(
            PROJECT1, PKG1, CLS1);

        bob.editor.waitUntilJavaEditorContentSame(dirtyClsContentOfAlice,
            PROJECT1, PKG1, CLS1);
        assertTrue(bob.editor.isJavaEditorActive(CLS1));
        assertTrue(bob.editor
            .isClassDirty(PROJECT1, PKG1, CLS1, ID_JAVA_EDITOR));
        assertTrue(bob.editor.getTextOfJavaEditor(PROJECT1, PKG1, CLS1).equals(
            dirtyClsContentOfAlice));

        carl.editor.waitUntilJavaEditorContentSame(dirtyClsContentOfAlice,
            PROJECT1, PKG1, CLS1);
        assertTrue(carl.editor.isJavaEditorActive(CLS1));
        assertTrue(carl.editor.isClassDirty(PROJECT1, PKG1, CLS1,
            ID_JAVA_EDITOR));
        assertTrue(carl.editor.getTextOfJavaEditor(PROJECT1, PKG1, CLS1)
            .equals(dirtyClsContentOfAlice));

        dave.editor.waitUntilJavaEditorContentSame(dirtyClsContentOfAlice,
            PROJECT1, PKG1, CLS1);
        assertTrue(dave.editor.isJavaEditorActive(CLS1));
        assertTrue(dave.editor.isClassDirty(PROJECT1, PKG1, CLS1,
            ID_JAVA_EDITOR));
        assertTrue(dave.editor.getTextOfJavaEditor(PROJECT1, PKG1, CLS1)
            .equals(dirtyClsContentOfAlice));

        bob.editor.closeJavaEditorWithSave(CLS1);
        carl.editor.closeJavaEditorWithSave(CLS1);
        dave.editor.closeJavaEditorWithSave(CLS1);
        alice.editor.closeJavaEditorWithSave(CLS1);
    }

    /**
     * Steps:
     * <ol>
     * <li>Alice open a class.</li>
     * <li>Alice set text in the opened java editor and then close it with save.
     * </li>
     * </ol>
     * 
     * Result:
     * <ol>
     * <li>bob, carl and dave open the class too</li>
     * <li>bob, carl and dave have the same class content as alice and their
     * opened editor are closed too.</li>
     * </ol>
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public void testFollowModeByClosingEditorByAlice() throws IOException,
        CoreException {
        alice.pEV.openClass(PROJECT1, PKG1, CLS1);
        bob.editor.waitUntilJavaEditorOpen(CLS1);
        carl.editor.waitUntilJavaEditorOpen(CLS1);
        dave.editor.waitUntilJavaEditorOpen(CLS1);
        assertTrue(bob.editor.isJavaEditorOpen(CLS1));
        assertTrue(carl.editor.isJavaEditorOpen(CLS1));
        assertTrue(dave.editor.isJavaEditorOpen(CLS1));

        alice.editor.setTextInJavaEditorWithoutSave(CP1_CHANGE, PROJECT1, PKG1,
            CLS1);
        alice.editor.closeJavaEditorWithSave(CLS1);
        String clsContentOfAlice = alice.state.getClassContent(PROJECT1, PKG1,
            CLS1);

        bob.editor.waitUntilJavaEditorClosed(CLS1);
        carl.editor.waitUntilJavaEditorClosed(CLS1);
        dave.editor.waitUntilJavaEditorClosed(CLS1);
        assertFalse(bob.editor.isJavaEditorOpen(CLS1));
        assertFalse(carl.editor.isJavaEditorOpen(CLS1));
        assertFalse(dave.editor.isJavaEditorOpen(CLS1));

        assertTrue(bob.state.getClassContent(PROJECT1, PKG1, CLS1).equals(
            clsContentOfAlice));
        assertTrue(carl.state.getClassContent(PROJECT1, PKG1, CLS1).equals(
            clsContentOfAlice));
        assertTrue(bob.state.getClassContent(PROJECT1, PKG1, CLS1).equals(
            clsContentOfAlice));
    }
}
