package de.fu_berlin.inf.dpp.stf.client.test.testcases.invitation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.fu_berlin.inf.dpp.stf.client.Musician;
import de.fu_berlin.inf.dpp.stf.client.test.helpers.InitMusician;
import de.fu_berlin.inf.dpp.stf.server.BotConfiguration;

public class TestParallelInvitationWithTerminationByHost {

    private static final String CLS = BotConfiguration.CLASSNAME;
    private static final String PKG = BotConfiguration.PACKAGENAME;
    private static final String PROJECT = BotConfiguration.PROJECTNAME;

    protected static Musician alice;
    protected static Musician bob;
    protected static Musician carl;

    protected static ArrayList<String> invitees = new ArrayList<String>();

    @BeforeClass
    public static void initMusicians() throws AccessException, RemoteException,
        InterruptedException {
        // initialize the musicians simultaneously
        List<Musician> musicians = InitMusician.initAliceBobCarlConcurrently();
        alice = musicians.get(0);
        bob = musicians.get(1);
        carl = musicians.get(2);
        alice.eclipseMainMenu.newJavaProjectWithClass(PROJECT, PKG, CLS);
        invitees.add(bob.getBaseJid());
        invitees.add(carl.getBaseJid());
    }

    /**
     * make sure, all opened xmppConnects, popup windows and editor should be
     * closed. make sure, all existed projects should be deleted. if you need
     * some more after class condition for your tests, please add it.
     * 
     * @throws RemoteException
     */
    @AfterClass
    public static void resetSaros() throws RemoteException {
        bob.bot.resetSaros();
        carl.bot.resetSaros();
        alice.bot.resetSaros();
    }

    /**
     * make sure,all opened popup windows and editor should be closed. if you
     * need some more after condition for your tests, please add it.
     * 
     * @throws RemoteException
     */
    @After
    public void cleanUp() throws RemoteException {
        bob.bot.resetWorkbench();
        carl.bot.resetWorkbench();
        alice.bot.resetWorkbench();
    }

    /**
     * Steps:
     * <ol>
     * <li>Alice invites Bob and Carl simultaneously.</li>
     * <li>Carl accepts the invitation but does not choose a target project.</li>
     * <li>Alice opens the Progress View and cancels Bob's invitation before Bob
     * accepts.</li>
     * <li>Alice opens the Progress View and cancels Carl's invitation before
     * Carl accepts.</li>
     * </ol>
     * 
     * Result:
     * <ol>
     * <li>Bob is notified of Alice's canceling the invitation.</li>
     * <li>Carl is notified of Alice's canceling the invitation.</li>
     * <li>Carl and Bob are not in session</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    @Test
    public void testInvitationWithTerminationByHost() throws RemoteException {
        alice.bot.shareProject(PROJECT, invitees);
        carl.popupWindow.confirmSessionInvitationWindowStep1();

        alice.bot.cancelInvitation();
        bob.eclipseWindow.waitUntilShellActive("Invitation Cancelled");
        assertTrue(bob.eclipseWindow.isShellActive("Invitation Cancelled"));
        bob.bot.confirmInvitationCancelledWindow();
        alice.bot.removeProgress();

        alice.bot.cancelInvitation();
        carl.eclipseWindow.waitUntilShellActive("Invitation Cancelled");
        assertTrue(carl.eclipseWindow.isShellActive("Invitation Cancelled"));
        carl.bot.confirmInvitationCancelledWindow();
        alice.bot.removeProgress();

        assertFalse(bob.sessionV.isInSession());
        assertFalse(carl.sessionV.isInSession());

    }
}