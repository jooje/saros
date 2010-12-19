package de.fu_berlin.inf.dpp.stf.client;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.XMPPException;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.stf.client.testProject.helpers.MakeOperationConcurrently;
import de.fu_berlin.inf.dpp.stf.client.testProject.helpers.STFTest;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.noGUI.SarosState;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.ChatViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.RSViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.RosterViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.SarosMainMenuComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.SarosPEViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.SarosWorkbenchComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench.SessionViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.workbench.BasicComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.workbench.EditorComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.workbench.ProgressViewComponent;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.workbench.ShellComponent;

/**
 * Tester encapsulates a test instance of Saros. It takes use of all RMI
 * interfaces to help testwriters to write their STF tests nicely. STF is short
 * for Sandor's Test Framework.
 */
public class Tester extends STFTest {
    private static final Logger log = Logger.getLogger(Tester.class);

    public SarosPEViewComponent pEV;
    public ProgressViewComponent progressV;
    public RosterViewComponent rosterV;
    public SessionViewComponent sessionV;
    public RSViewComponent rSV;
    public ChatViewComponent chatV;
    public BasicComponent basic;
    public SarosState state;
    public EditorComponent editor;
    public ShellComponent shell;
    public SarosMainMenuComponent mainMenu;
    public SarosWorkbenchComponent workbench;

    public JID jid;
    public String password;
    public String host;
    public int port;

    public Tester(JID jid, String password, String host, int port) {
        super();
        this.jid = jid;
        this.password = password;
        this.host = host;
        this.port = port;
    }

    /*************** init Methods ******************/

    public void initBot() throws AccessException, RemoteException,
        NotBoundException {
        log.trace("initBot enter, initRmi");
        initRmi();
    }

    private void initRmi() throws RemoteException, NotBoundException,
        AccessException {
        Registry registry = LocateRegistry.getRegistry(host, port);
        try {

            chatV = (ChatViewComponent) registry.lookup("chatView");
            rosterV = (RosterViewComponent) registry.lookup("rosterView");
            sessionV = (SessionViewComponent) registry.lookup("sessionView");
            /*
             * TODO i am not sure, if i can pass the local value to remote
             * object. It worked for the local tests, but i don't know if it
             * work for the remote tests too.
             */
            sessionV.setJID(jid);
            rSV = (RSViewComponent) registry.lookup("remoteScreenView");
            pEV = (SarosPEViewComponent) registry.lookup("packageExplorerView");
            progressV = (ProgressViewComponent) registry.lookup("progressView");
            state = (SarosState) registry.lookup("state");
            workbench = (SarosWorkbenchComponent) registry.lookup("workbench");
            shell = (ShellComponent) registry.lookup("shell");
            editor = (EditorComponent) registry.lookup("eclipseEditor");
            mainMenu = (SarosMainMenuComponent) registry
                .lookup("sarosMainMenu");
            basic = (BasicComponent) registry.lookup("basicObject");

        } catch (java.rmi.ConnectException e) {
            throw new RuntimeException("Could not connect to RMI of bot " + jid
                + ", did you start the Eclipse instance?");
        }

    }

    // ********** Component, which consist of other simple functions ***********

    public void buildSessionDoneSequentially(String projectName,
        TypeOfShareProject howToShareProject,
        TypeOfCreateProject usingWhichProject, Tester... invitees)
        throws RemoteException {
        String[] baseJIDOfInvitees = getPeersBaseJID(invitees);

        pEV.shareProjectWith(projectName, howToShareProject, baseJIDOfInvitees);
        for (Tester invitee : invitees) {
            invitee.pEV.confirmWizardSessionInvitationUsingWhichProject(
                projectName, usingWhichProject);
        }
    }

    public void buildSessionDoneConcurrently(final String projectName,
        TypeOfShareProject howToShareProject,
        final TypeOfCreateProject usingWhichProject, Tester... invitees)
        throws RemoteException, InterruptedException {

        log.trace("alice.shareProjectParallel");
        this.pEV.shareProjectWith(projectName, howToShareProject,
            getPeersBaseJID(invitees));

        List<Callable<Void>> joinSessionTasks = new ArrayList<Callable<Void>>();
        for (final Tester invitee : invitees) {
            joinSessionTasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                    invitee.pEV
                        .confirmWizardSessionInvitationUsingWhichProject(
                            projectName, usingWhichProject);
                    invitee.sessionV.waitUntilSessionOpen();
                    return null;
                }
            });
        }
        log.trace("workAll(joinSessionTasks)");
        MakeOperationConcurrently.workAll(joinSessionTasks);
    }

    /**
     * Define the leave session with the following steps.
     * <ol>
     * <li>The host(alice) leave session first.</li>
     * <li>Then other invitees confirm the windonws "Closing the Session"
     * concurrently</li>
     * </ol>
     * 
     * @param peers
     *            the invitees
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void leaveSessionHostFirstDone(Tester... peers)
        throws RemoteException, InterruptedException {
        List<Callable<Void>> closeSessionTasks = new ArrayList<Callable<Void>>();
        for (final Tester tester : peers) {
            if (tester.sessionV.isInSession()) {
                closeSessionTasks.add(new Callable<Void>() {
                    public Void call() throws Exception {
                        // Need to check for isDriver before leaving.
                        tester.sessionV.confirmClosingTheSessionWindow();
                        return null;
                    }
                });
            }
        }
        sessionV.leaveTheSessionByHost();
        MakeOperationConcurrently.workAll(closeSessionTasks);
    }

    /**
     * Define the leave session with the following steps.
     * <ol>
     * <li>The musicians bob and carl leave the session first.(concurrently)</li>
     * <li>wait until bob and carl are really not in the session using
     * "waitUntilAllPeersLeaveSession", then leave the host alice.</li>
     * </ol>
     * make sure,
     * 
     * @param testers
     *            bob and carl
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void leaveSessionPeersFirstDone(Tester... testers)
        throws RemoteException, InterruptedException {
        List<Callable<Void>> leaveTasks = new ArrayList<Callable<Void>>();
        for (int i = 0; i < testers.length; i++) {
            final Tester tester = testers[i];
            leaveTasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                    tester.sessionV.leaveTheSessionByPeer();
                    return null;
                }
            });
        }
        List<JID> peerJIDs = new ArrayList<JID>();
        for (Tester tester : testers) {
            peerJIDs.add(tester.jid);
        }
        MakeOperationConcurrently.workAll(leaveTasks);
        sessionV.waitUntilAllPeersLeaveSession(peerJIDs);
        sessionV.clickTBleaveTheSession();
        sessionV.waitUntilSessionClosed();
    }

    private String[] getPeersBaseJID(Tester... peers) {
        String[] peerBaseJIDs = new String[peers.length];
        for (int i = 0; i < peers.length; i++) {
            peerBaseJIDs[i] = peers[i].getBaseJid();
        }
        return peerBaseJIDs;
    }

    /**
     * the local user can be concurrently followed by many other users.
     * 
     * @param testers
     *            the list of the remote Users who want to follow the local
     *            user.
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void followedBy(Tester... testers) throws RemoteException,
        InterruptedException {
        List<Callable<Void>> followTasks = new ArrayList<Callable<Void>>();
        for (int i = 0; i < testers.length; i++) {
            final Tester tester = testers[i];
            followTasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                    tester.sessionV.followThisUserGUI(jid);
                    return null;
                }
            });
        }
        MakeOperationConcurrently.workAll(followTasks);
    }

    /**
     * stop the follow-mode of the remote users who are following the local
     * user.
     * 
     * @param testers
     *            the list of the remote Users who are following the local user.
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void stopFollowedBy(Tester... testers) throws RemoteException,
        InterruptedException {
        List<Callable<Void>> stopFollowTasks = new ArrayList<Callable<Void>>();
        for (int i = 0; i < testers.length; i++) {
            final Tester tester = testers[i];
            stopFollowTasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                    tester.sessionV.stopFollowingThisUserGUI(jid);
                    return null;
                }
            });
        }
        MakeOperationConcurrently.workAll(stopFollowTasks);
    }

    /**
     * @Return the name segment of {@link JID}.
     */
    public String getName() {
        return jid.getName();
    }

    /**
     * @Return the Jabber ID without resource qualifier.
     */
    public String getBaseJid() {
        return jid.getBase();
    }

    /**
     * @Return the resource qualified {@link JID}.
     */
    public String getRQjid() {
        return jid.toString();
    }

    public String getXmppServer() {
        return jid.getDomain();
    }

    /**
     * add buddy with GUI, which should be performed by both the users.
     * 
     * @param peer
     *            the user, which should be added in your contact list
     * @throws RemoteException
     * @throws XMPPException
     * 
     */
    public void addBuddyDone(Tester peer) throws RemoteException, XMPPException {
        if (!rosterV.hasBuddy(peer.jid)) {
            rosterV.addANewContact(peer.jid);
            peer.rosterV.confirmRequestOfSubscriptionReceivedWindow();
            rosterV.confirmRequestOfSubscriptionReceivedWindow();
        }
    }

    /**
     * add buddy with GUI, which should be performed by both the users.
     * 
     * @param peer
     *            the user, which should be added in your contact list
     * @throws RemoteException
     */
    public void addBuddyGUIDone(Tester peer) throws RemoteException {
        if (!rosterV.hasBuddy(peer.jid)) {
            rosterV.addANewContactGUI(peer.jid);
            peer.rosterV.confirmRequestOfSubscriptionReceivedWindow();
            rosterV.confirmRequestOfSubscriptionReceivedWindow();
        }
    }

    /**
     * Remove given contact from Roster with GUI, if contact was added before.
     */
    public void deleteBuddyGUIDone(Tester peer) throws RemoteException {
        if (!rosterV.hasBuddy(peer.jid))
            return;
        rosterV.deleteBuddyGUI(peer.jid);
        peer.rosterV.confirmRemovelOfSubscriptionWindow();
    }

    /**
     * Remove given contact from Roster without GUI, if contact was added before
     * 
     * @throws XMPPException
     */
    public void deleteBuddyDone(Tester peer) throws RemoteException,
        XMPPException {
        if (!rosterV.hasBuddy(peer.jid))
            return;
        rosterV.deleteBuddy(peer.jid);
        peer.rosterV.confirmRemovelOfSubscriptionWindow();
    }

    public void shareYourScreenWithSelectedUserDone(Tester peer)
        throws RemoteException {
        sessionV.shareYourScreenWithSelectedUserGUI(peer.jid);
        peer.sessionV.confirmIncomingScreensharingSesionWindow();
    }

    /**
     * This method is same as
     * {@link Tester#buildSessionDoneConcurrently(String, String, Tester...)} .
     * The difference to buildSessionConcurrently is that the invitation process
     * is activated by clicking the toolbarbutton "open invitation interface" in
     * the roster view.
     * 
     * @param projectName
     *            the name of the project which is in a session now.
     * @param peers
     *            the user whom you want to invite to your session.
     * @throws RemoteException
     * @throws InterruptedException
     */
    public void inviteUsersInYourSessionDone(final String projectName,
        Tester... peers) throws RemoteException, InterruptedException {
        sessionV.openInvitationInterface(getPeersBaseJID(peers));
        List<Callable<Void>> joinSessionTasks = new ArrayList<Callable<Void>>();
        for (final Tester tester : peers) {
            joinSessionTasks.add(new Callable<Void>() {
                public Void call() throws Exception {
                    tester.pEV
                        .confirmWirzardSessionInvitationWithNewProject(projectName);
                    return null;
                }
            });
        }
        log.trace("workAll(joinSessionTasks)");
        MakeOperationConcurrently.workAll(joinSessionTasks);

    }
}