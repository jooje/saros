package de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.workbench;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import de.fu_berlin.inf.dpp.editor.EditorManager;
import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.project.SessionManager;
import de.fu_berlin.inf.dpp.stf.client.Musician;
import de.fu_berlin.inf.dpp.stf.client.test.helpers.TestPattern;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.conditions.SarosConditions;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.noExportedObjects.TablePart;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.noExportedObjects.ViewPart;
import de.fu_berlin.inf.dpp.stf.server.rmiSwtbot.eclipse.saros.noGUI.SarosState;

/**
 * This interface contains convenience API to perform a action using widgets in
 * session view. then you can start off as follows:
 * <ol>
 * <li>
 * At first you need to create a {@link Musician} object in your junit-test.
 * (How to do it please look at the javadoc in class {@link TestPattern} or read
 * the user guide in TWiki https://www.inf.fu-berlin.de/w/SE/SarosSTFTests).</li>
 * <li>
 * then you can use the object sessionV initialized in {@link Musician} to
 * access the API :), e.g.
 * 
 * <pre>
 * alice.sessionV.openSharedSessionView();
 * </pre>
 * 
 * </li>
 * 
 * @author Lin
 */
public interface SessionViewComponent extends Remote {

    /**
     * Test if you are now in a session. <br>
     * This function check if the tool bar button "Leave the session" in the
     * session view is enabled. You can also use another function
     * {@link SarosState#isInSession()}, which test the session state without
     * GUI.
     * 
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * <li>Try to use the {@link SessionViewComponent#isInSession()} and
     * {@link SarosState#isInSession()} together in your junit tests.</li>
     * </ol>
     * 
     * @return <tt>true</tt> if the tool bar button "Leave the session" is
     *         enabled.
     * 
     * @throws RemoteException
     */
    public boolean isInSession() throws RemoteException;

    /**
     * @throws RemoteException
     * @see ViewPart#openViewById(String)
     */
    public void openSessionView() throws RemoteException;

    /**
     * 
     * @return <tt>true</tt> if all the opened views contains the session view.
     * 
     * @throws RemoteException
     * @see ViewPart#isViewOpen(String)
     */
    public boolean isSessionViewOpen() throws RemoteException;

    /**
     * waits until the session is open.
     * 
     * @throws RemoteException
     * @see SarosConditions#isInSession(SarosState)
     */
    public void waitUntilSessionOpen() throws RemoteException;

    /**
     * waits until the session by the defined peer is open.
     * 
     * @param stateOfPeer
     *            the {@link SarosState} of the defined peer.
     * @throws RemoteException
     */
    public void waitUntilSessionOpenBy(SarosState stateOfPeer)
        throws RemoteException;

    /**
     * @see ViewPart#setFocusOnViewByTitle(String)
     * @throws RemoteException
     */
    public void setFocusOnSessionView() throws RemoteException;

    /**
     * @return <tt>true</tt> if session view is active.
     * 
     * @throws RemoteException
     * @see ViewPart#isViewActive(String)
     */
    public boolean isSessionViewActive() throws RemoteException;

    /**
     * @throws RemoteException
     * @see ViewPart#closeViewById(String)
     */
    public void closeSessionView() throws RemoteException;

    /**
     * Waits until the {@link SessionManager#getSarosSession()} is null.
     * <p>
     * <b>Attention</b>:<br/>
     * After a action is performed, you immediately try to assert a condition is
     * true/false or perform a following action which based on that the current
     * performed action is completely finished, e.g. alice.state.isInSession is
     * false after alice leave the session by running the
     * {@link SessionViewComponentImp#leaveTheSession()} and confirming the
     * appeared pop up window without this waitUntil. In this case, you may get
     * the AssertException, because alice should not really leave the session
     * yet during asserting the condition or performing a following action. So
     * it is recommended that you wait until the session is completely closed
     * before you run the assertion or perform a following action.
     * 
     * @throws RemoteException
     */
    public void waitUntilSessionClosed() throws RemoteException;

    /**
     * Waits until the {@link SessionManager#getSarosSession()} is null.
     * <p>
     * <b>Attention</b>:<br/>
     * After a action is performed, you immediately try to assert a condition is
     * true/false or perform a following action which based on that the current
     * performed action is completely finished, e.g.
     * assertFalse(alice.state.isDriver(bob.jid)) after bob leave the session by
     * running the {@link SessionViewComponentImp#leaveTheSession()} and
     * confirming the appeared pop up window without this waitUntil. In this
     * case, you may get the AssertException, because bob should not really
     * leave the session yet during asserting a condition or performing a
     * following action. So it is recommended that you wait until the session by
     * the defined peer is completely closed before you run a assertion or
     * perform a following action.
     * 
     * @param stateOfPeer
     *            the {@link SarosState} of the user, whose session should be
     *            closed.
     * @throws RemoteException
     */
    public void waitUntilSessionClosedBy(SarosState stateOfPeer)
        throws RemoteException;

    /**
     * Test if a contact exists in the contact list in the session view.
     * 
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param contactName
     *            the name, which listed in the session view. e.g. "You" or
     *            "alice1_fu@jabber.ccc.de (Driver)" or "Bob1_fu@jabber.ccc.de".
     * @return <tt>true</tt> if the passed contactName is in the contact list in
     *         the session view.
     * @throws RemoteException
     */
    public boolean isContactInSessionView(String contactName)
        throws RemoteException;

    /**
     * Perform the action "Give driver Role" which should be activated by
     * clicking the context menu "Give driver Role" of a tableItem with itemText
     * e.g. "bob1_fu@jabber.ccc.de" in the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>Waits until the shell "Progress Information" is closed. It guarantee
     * that the "Give driver Role" action is completely done.</li>
     * </ol>
     * 
     * @param stateOfInvitee
     *            the {@link SarosState} of the user whom you want to give
     *            drive role.
     * @throws RemoteException
     */
    public void giveDriverRole(SarosState stateOfInvitee)
        throws RemoteException;

    /**
     * Using this function host can perform the action
     * "Give exclusive driver Role" which should be activated by clicking the
     * context menu "Give exclusive driver Role" of the tableItem with itemText
     * e.g. "bob1_fu@jabber.ccc.de" in the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * <li>Waits until the shell "Progress Information" is closed. It guarantee
     * that the "Give exclusive driver Role" action is completely done.</li>
     * </ol>
     * 
     * @param stateOfInvitee
     *            the {@link SarosState} of the user whom you want to give
     *            exclusive drive role.
     * @throws RemoteException
     */
    public void giveExclusiveDriverRole(SarosState stateOfInvitee)
        throws RemoteException;

    /**
     * performs the action "Remove driver Role" which should be activated by
     * clicking the context menu "Remove driver Role" of the tableItem with
     * itemText e.g. "bob1_fu@jabber.ccc.de (Driver)" in the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * <li>Waits until the shell "Progress Information" is closed. It guarantee
     * that the "Remove driver Role" action is completely done.</li>
     * </ol>
     * 
     * @param stateOfInvitee
     *            the {@link SarosState} of the user whose drive role you
     *            want to remove.
     * @throws RemoteException
     */
    public void removeDriverRole(SarosState stateOfInvitee)
        throws RemoteException;

    /**
     * Performs the action "Follow this user" which should be activated by
     * clicking the context menu "Follow this user" of the tableItem with
     * itemText e.g. "alice1_fu@jabber.ccc.de (Driver)" in the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param stateOfFollowedUser
     *            the {@link SarosState} of the user whom you want to follow.
     * @throws RemoteException
     */
    public void followThisUser(SarosState stateOfFollowedUser)
        throws RemoteException;

    /**
     * Test if you are in follow mode. <br>
     * This function check if the context menu "Stop following this user" of
     * every contact listed in the session view is existed and enabled. You can
     * also use another function {@link SarosState#isInFollowMode()}, which
     * test the following state without GUI.
     * 
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * <li>Try to use only the function{@link SarosState#isInSession()} for
     * your junittests, because the method
     * {@link TablePart#existsContextOfTableItem(String, String)} isn't really
     * optimal implemented.</li>
     * </ol>
     * 
     * @return <tt>true</tt> if the tool bar button "Leave the session" is
     *         enabled.
     * 
     * @throws RemoteException
     */
    public boolean isInFollowMode() throws RemoteException;

    /**
     * This function do same as the
     * {@link SessionViewComponent#stopFollowingThisUser(SarosState)} except
     * you don't need to pass the {@link SarosState} of the followed user to
     * the function. It is very useful, if you don't exactly know whom you are
     * now following. Instead, we get the followed user JID from the method
     * {@link SarosState#getFollowedUserJID()}.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void stopFollowing() throws RemoteException;

    /**
     * Performs the action "Stop following this user" which should be activated
     * by clicking the context menu "Stop following this user" of a tableItem
     * with the itemText e.g. "alice1_fu@jabber.ccc.de (Driver)" in the session
     * view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param stateOfFollowedUser
     *            the {@link SarosState} of the user whom you want to stop
     *            following.
     * @throws RemoteException
     */
    public void stopFollowingThisUser(SarosState stateOfFollowedUser)
        throws RemoteException;

    /**
     * checks if the context menu "Stop following this user" of a contact listed
     * in the session view is enabled. It would be used by
     * {@link SessionViewComponentImp#isInFollowMode()}.
     * 
     * @param baseJIDOfFollowedUser
     *            the name, which listed in the session view. e.g. "You" or
     *            "alice1_fu@jabber.ccc.de (Driver)" or "Bob1_fu@jabber.ccc.de".
     * @return <tt>true</tt> if the context menu "Following this user" of the
     *         passed contactName listed in the session view is enabled.
     * @throws RemoteException
     */
    public boolean isStopFollowingThisUserEnabled(String baseJIDOfFollowedUser)
        throws RemoteException;

    /**
     * check if the context menu "Stop following this user" of a contact listed
     * in the session view is visible.
     * 
     * @param baseJIDOfFollowedUser
     *            the name, which listed in the session view. e.g. "You" or
     *            "alice1_fu@jabber.ccc.de (Driver)" or "Bob1_fu@jabber.ccc.de".
     * @return <tt>true</tt> if the context menu "Following this user" of the
     *         passed contactName listed in the session view is visible.
     * @throws RemoteException
     */
    public boolean isStopFollowingThisUserVisible(String baseJIDOfFollowedUser)
        throws RemoteException;

    /**
     * Waits until the {@link EditorManager#getFollowedUser()} is same as the
     * given user.
     * <p>
     * <b>Attention</b>:<br/>
     * After a action is performed, you immediately try to assert a condition is
     * true/false or perform a following action which based on that the current
     * performed action is completely finished, e.g. assert bob's workbench
     * state after bob follow the given user by running the
     * {@link SessionViewComponentImp#followThisUser(SarosState)}without this
     * waitUntil. In this case, you may get the AssertException, because bob
     * should not really in the follow mode yet during asserting a condition or
     * performing a following action. So it is recommended that you wait until
     * the session is completely closed before you run the assertion or perform
     * a following action.
     * 
     * @param baseJIDOfFollowedUser
     * @throws RemoteException
     */
    public void waitUntilIsFollowingUser(String baseJIDOfFollowedUser)
        throws RemoteException;

    /**
     * performs the action "Share your screen with selected user" which should
     * be activated by clicking the tool bar button with the tooltip text
     * "Share your screen with selected user" on the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param stateOfPeer
     *            the {@link SarosState} of the user with whom you want to
     *            share your screen.
     * @throws RemoteException
     */
    public void shareYourScreenWithSelectedUser(SarosState stateOfPeer)
        throws RemoteException;

    /**
     * performs the action "Stop share session with user" which should be
     * activated by clicking the tool bar button with the tooltip text
     * "Stop share session with user" on the session view. This toolbar button
     * is only visible after clicking the
     * {@link SessionViewComponent#shareYourScreenWithSelectedUser(SarosState)}
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param stateOfselectedUser
     *            the {@link SarosState} of the user with whom you want to
     *            stop the screen session.
     * @throws RemoteException
     */
    public void stopSessionWithUser(SarosState stateOfselectedUser)
        throws RemoteException;

    /**
     * performs the action "Send a file to selected user" which should be
     * activated by clicking the tool bar button with the tooltip text
     * "Send a file to selected user" on the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Send a file to selected user", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet. SWTBot don't support native
     * dialog, So the action on the "Select the file to send" dialog can be
     * performed.
     * 
     * @param stateOfselectedUser
     *            the {@link SarosState} of the user with whom you want to
     *            share your screen.
     * @throws RemoteException
     */
    public void sendAFileToSelectedUser(SarosState stateOfselectedUser)
        throws RemoteException;

    /**
     * performs the action "Start a VoIP session" which should be activated by
     * clicking the tool bar button with the tooltip text "Start a VoIP session"
     * on the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Start a VoIP session", e.g. a popup window. In this case, e.g. a popup
     * window. In this case, the method should handle the popup window too.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet.
     * 
     * 
     * @param stateOfselectedUser
     *            the {@link SarosState} of the user with whom you want to
     *            share your screen.
     * @throws RemoteException
     */
    public void startAVoIPSession(SarosState stateOfselectedUser)
        throws RemoteException;

    /**
     * performs the action "inconsistency detected in ..." which should be
     * activated by clicking the tool bar button with the tooltip text
     * "inconsistency detected in ..." on the session view. The button is only
     * enabled, if there are inconsistency detected in a file.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "inconsistency detected in ...", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet.
     * 
     * @throws RemoteException
     */
    public void inconsistencyDetected() throws RemoteException;

    /**
     * performs the action "Remove all river roles" which should be activated by
     * clicking the tool bar button with the tooltip text
     * "Remove all river roles" on the session view. The button is only enabled,
     * if there are Driver existed in the session.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "inconsistency detected in ...", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void removeAllRriverRoles() throws RemoteException;

    /**
     * performs the action "Enable/disable follow mode" which should be
     * activated by clicking the tool bar button with the tooltip text
     * "REnable/disable follow mode" on the session view. The button is only
     * enabled, if there are participant who is in follow mode.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Enable/disable follow mode", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void enableDisableFollowMode() throws RemoteException;

    /**
     * Waits until all the given users are not in the session.
     * <p>
     * <b>Attention</b>:<br/>
     * After a action is performed, you immediately try to assert a condition is
     * true/false or perform a following action which based on that the current
     * performed action is completely finished, e.g. alice(host)leave the
     * session immediately after bob and carl leave the session without this
     * waitUntil. In this case, you may get a popup window what you did not
     * expected, because bob and carl should not really leave the session yet
     * during performing a following action. So it is recommended that you wait
     * until all the peers completely leave the session before you run a
     * assertion or perform a following action.
     * 
     * @param jids
     * @throws RemoteException
     * @see Musician#leaveSessionFirstByPeers(Musician...)
     */
    public void waitUntilAllPeersLeaveSession(List<JID> jids)
        throws RemoteException;

    /**
     * Performs the action "Jump to position of selected user" which should be
     * activated by clicking the context menu
     * "SJump to position of selected user" of a tableItem with the itemText
     * e.g. "alice1_fu@jabber.ccc.de (Driver)" in the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Make sure, the session view is open and active.</li>
     * </ol>
     * 
     * @param stateOfselectedUser
     *            the {@link SarosState} of the user whom you want to stop
     *            following.
     * @throws RemoteException
     */
    public void jumpToPositionOfSelectedUser(SarosState stateOfselectedUser)
        throws RemoteException;

    /**
     * 
     * @return <tt>true</tt>, if the toolbar button is existed and enabled.
     * @throws RemoteException
     */
    public boolean isInconsistencyDetectedEnabled() throws RemoteException;

    /**
     * performs the action "Open invitation interface" which should be activated
     * by clicking the tool bar button with the tooltip text
     * "Open invitation interface" on the session view. The button is only
     * enabled, if you are host.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Open invitation interface", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * @param jidOfInvitee
     * @throws RemoteException
     */
    public void openInvitationInterface(String jidOfInvitee)
        throws RemoteException;

    /**
     * After perform the
     * {@link SessionViewComponent#openInvitationInterface(String)} you should
     * get this popup window.
     * 
     * @throws RemoteException
     */
    public void comfirmInvitationWindow(String jidOfinvitee)
        throws RemoteException;

    /**
     * performs the action "Leave the session" which should be activated by
     * clicking the tool bar button with the tooltip text "Leave the session" on
     * the session view. After clicking the button you will get different popup
     * window depending on whether you are host or not. So if you are host,
     * please use this one, otherwise use
     * {@link SessionViewComponent#leaveTheSessionByPeer()}
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Open invitation interface", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void leaveTheSessionByHost() throws RemoteException;

    /**
     * performs the action "Leave the session" which should be activated by
     * clicking the tool bar button with the tooltip text "Leave the session" on
     * the session view. After clicking the button you will get different popup
     * window depending on whether you are host or not. So if you are peer,
     * please use this one, otherwise use
     * {@link SessionViewComponent#leaveTheSessionByHost()}
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>If there are some following actions which are activated by the action
     * "Open invitation interface", e.g. a popup window. In this case, the
     * method should handle the popup window too.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void leaveTheSessionByPeer() throws RemoteException;

    /**
     * After perform the
     * {@link SessionViewComponent#shareYourScreenWithSelectedUser(SarosState)}
     * the selected user should get this popup window.
     * 
     * @throws RemoteException
     * @see Musician#shareYourScreenWithSelectedUserDone(Musician)
     */
    public void confirmIncomingScreensharingSesionWindow()
        throws RemoteException;

    public void confirmClosingTheSessionWindow() throws RemoteException;

}