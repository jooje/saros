package de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.view.saros;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;

import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.stf.client.tester.AbstractTester;
import de.fu_berlin.inf.dpp.stf.server.StfRemoteObject;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.ISuperBot;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.IContextMenusInBuddiesArea;
import de.fu_berlin.inf.dpp.stf.server.rmi.superbot.component.contextmenu.sarosview.IContextMenusInSessionArea;

/**
 * This interface contains convenience API to perform a action using widgets in
 * the Saros Buddies view. then you can start off as follows:
 * <ol>
 * <li>
 * At first you need to create a {@link AbstractTester} object in your
 * junit-test. (How to do it please read the user guide in TWiki
 * https://www.inf.fu-berlin.de/w/SE/SarosSTFTests).</li>
 * <li>
 * after then you can use the object rosterV initialized in
 * {@link AbstractTester} to access the API :), e.g.
 * 
 * <pre>
 * alice.rosterV.openRosterView();
 * </pre>
 * 
 * </li>
 * 
 * @author lchen
 */
public interface ISarosView extends Remote {

    /**********************************************
     * 
     * Actions
     * 
     **********************************************/
    /**
     * Connects with the given JID and waits until the tester is connected to
     * the server.
     * 
     * @param jid
     *            see {@link JID}
     * @param password
     * @throws RemoteException
     */
    public void connectWith(JID jid, String password) throws RemoteException;

    /**
     * Connects with the current active account and waits until the tester is
     * connected to the server.
     * 
     * @throws RemoteException
     */
    public void connectWithActiveAccount() throws RemoteException;

    /**
     * Clicks the tool bar button "Disconnect" and waits until the tester is
     * disconnected from the server.
     * 
     * @throws RemoteException
     */
    public void disconnect() throws RemoteException;

    /**
     * Performs the action "add a new buddy" which should be activated by
     * clicking the tool bar button with the toolTip text "Add a new buddy".
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>The function should treat all the recursive following actions, which
     * are activated or indirectly activated by clicking the toolBar button.
     * i.e, after clicking the button the following popUp window would be
     * handled.</li>
     * </ol>
     * 
     * @param jid
     *            see {@link JID}.
     * @throws RemoteException
     */
    public void addNewBuddy(JID jid) throws RemoteException;

    /**
     * Select the buddy specified with the given baseJID which is located under
     * node "Buddies".
     * 
     * @param buddyJID
     *            see {@link JID}
     * @throws RemoteException
     */
    public IContextMenusInBuddiesArea selectBuddy(JID buddyJID)
        throws RemoteException;

    /**
     * Selects the tree node "Buddies"
     */
    public IContextMenusInBuddiesArea selectBuddies() throws RemoteException;

    /**
     * Selects the tree node "Session"
     */
    public IContextMenusInSessionArea selectSession() throws RemoteException;

    /**
     * Selects the tree node "No Session Running"
     */
    public IContextMenusInSessionArea selectNoSessionRunning()
        throws RemoteException;

    /**
     * Selects the participant with the given JID in the roster.
     * 
     * @param jid
     *            the JID of the participant
     * @throws IllegalStateException
     *             if the tester is not in a session
     * @throws WidgetNotFoundException
     *             if the participant could not be found
     * @throws RemoteException
     * 
     */
    public IContextMenusInSessionArea selectParticipant(final JID jid)
        throws RemoteException;

    /*
     * Chat stuff
     */

    /**
     * Selects the chat room with the given title.
     * 
     * @param name
     *            the name of the chat (as displayed in the chat tab)
     * @return an {@link IChatroom} interface
     * @throws RemoteException
     *             if the chat does not exist
     */
    public IChatroom selectChatroom(String name) throws RemoteException;

    /**
     * Selects the chat room with the given regular expression.
     * 
     * @param regex
     *            the regular expression to find the chat
     * @return an {@link IChatroom} interface for the first found match
     * @throws RemoteException
     *             if no matches where found
     */

    public IChatroom selectChatroomWithRegex(String regex)
        throws RemoteException;

    /**
     * Closes the chat room with the given title.
     * 
     * @param name
     *            the name of the chat (as displayed in the chat tab)
     * @throws RemoteException
     * 
     */

    public void closeChatroom(String name) throws RemoteException;

    /**
     * Closes all chat rooms that matches the given regular expression.
     * 
     * @param regex
     *            the regular expression to find the chat
     * @throws RemoteException
     * 
     */

    public void closeChatroomWithRegex(String regex) throws RemoteException;

    /**********************************************
     * 
     * States
     * 
     **********************************************/

    /**
     * 
     * @return<tt>true</tt>, if the toolBarbutton with the toolTip text
     *                       "Disconnect.*" is visible.
     * @throws RemoteException
     */
    public boolean isConnected() throws RemoteException;

    /**
     * 
     * @param buddyJID
     *            see {@link JID}
     * @return <tt>true</tt>, if the buddy specified with the given buddyJID
     *         exists.
     * @throws RemoteException
     */
    public boolean hasBuddy(JID buddyJID) throws RemoteException;

    /**
     * 
     * @param buddyJID
     *            see {@link JID}
     * @return the nickname of the given user, if he has one.
     * @throws RemoteException
     */
    public String getNickname(JID buddyJID) throws RemoteException;

    /**
     * 
     * @param buddyJID
     *            see {@link JID}
     * @return <tt>true</tt>, if the given user has a nickname.
     * @throws RemoteException
     */
    public boolean hasNickName(JID buddyJID) throws RemoteException;

    /**
     * 
     * @return all buddies name
     * @throws RemoteException
     */
    public List<String> getAllBuddies() throws RemoteException;

    /**********************************************
     * 
     * Wait until
     * 
     **********************************************/

    /**
     * Waits until the tester is connected to the XMPP server.
     * 
     * @throws RemoteException
     */
    public void waitUntilIsConnected() throws RemoteException;

    /**
     * Wait until the tester is disconnected from the XMPP server.
     * 
     * @throws RemoteException
     */
    public void waitUntilIsDisconnected() throws RemoteException;

    /**********************************************
     * 
     * Actions
     * 
     **********************************************/
    /**
     * performs the action "Share your screen with selected buddy" which should
     * be activated by clicking the tool bar button with the tooltip text
     * {@link StfRemoteObject#TB_SHARE_SCREEN_WITH_BUDDY} on the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>All iterative triggered events by the action should be handled in the
     * method(exclude remote triggered events).</li>
     * </ol>
     * 
     * @param jidOfPeer
     *            the {@link JID} of the user with whom you want to share your
     *            screen.
     * @throws RemoteException
     */

    public void shareYourScreenWithSelectedBuddy(JID jidOfPeer)
        throws RemoteException;

    /**
     * performs the action "Send a file to selected buddy" which should be
     * activated by clicking the tool bar button with the tooltip text
     * {@link StfRemoteObject#TB_SEND_A_FILE_TO_SELECTED_BUDDY} on the session
     * view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>All iterative triggered events by the action should be handled in the
     * method(exclude remote triggered events). E.g. a popup window.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet. SWTBot don't support native
     * dialog, So the action on the "Select the file to send" dialog can be
     * performed.
     * 
     * @param selectedBuddyJID
     *            the {@link JID} of the user with whom you want to share your
     *            screen.
     * @throws RemoteException
     */
    public void sendFileToSelectedBuddy(JID selectedBuddyJID)
        throws RemoteException;

    /**
     * performs the action "Start a VoIP session" which should be activated by
     * clicking the tool bar button with the tooltip text
     * {@link StfRemoteObject#TB_SEND_A_FILE_TO_SELECTED_BUDDY} on the session
     * view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>All iterative triggered events by the action should be handled in the
     * method(exclude remote triggered events). E.g. a popup window.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet.
     * 
     * 
     * @param selectedBuddyJID
     *            the {@link JID} of the user with whom you want to share your
     *            screen.
     * @throws RemoteException
     */
    public void startAVoIPSessionWithSelectedBuddy(JID selectedBuddyJID)
        throws RemoteException;

    /**
     * performs the action "Leave the session" which should be activated by
     * clicking the tool bar button with the toolTip text
     * {@link StfRemoteObject#TB_LEAVE_SESSION} on the session view.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>All iterative triggered events by the action should be handled in the
     * method(exclude remote triggered events). E.g. a popup window.</li>
     * </ol>
     * 
     * @throws RemoteException
     */
    public void leaveSession() throws RemoteException;

    // /**
    // * performs the action "Open invitation interface" which should be
    // activated
    // * by clicking the tool bar button with the tooltip text
    // * {@link STF#TB_ADD_A_NEW_BUDDY} on the session view. The button is only
    // * enabled, if you are host.
    // * <p>
    // * <b>Attention:</b>
    // * <ol>
    // * <li>Makes sure, the session view is open and active.</li>
    // * <li>All iterative triggered events by the action should be handled in
    // the
    // * method(exclude remote triggered events). E.g. a popup window.</li>
    // * </ol>
    // *
    // * @param jidOfInvitees
    // * the buddy whom you want to invite to your session.
    // * @throws RemoteException
    // */
    // public void addBuddyToSession(String... jidOfInvitees)
    // throws RemoteException;

    /**
     * performs the action "inconsistency detected in ..." which should be
     * activated by clicking the tool bar button with the toolTip text
     * {@link StfRemoteObject#TB_INCONSISTENCY_DETECTED} on the session view.
     * The button is only enabled, if there are inconsistency detected in a
     * file.
     * <p>
     * <b>Attention:</b>
     * <ol>
     * <li>Makes sure, the session view is open and active.</li>
     * <li>All iterative triggered events by the action should be handled in the
     * method(exclude remote triggered events). E.g. a popup window.</li>
     * </ol>
     * 
     * TODO: this function isn't complete yet.
     * 
     * @throws RemoteException
     */
    public void resolveInconsistency() throws RemoteException;

    /**********************************************
     * 
     * States
     * 
     **********************************************/

    /**
     * @param contactJID
     *            the JID of a user, who is sharing a session with you.
     * @return the status of the given participant (the text of the table item
     *         listed in the session view)
     * @throws RemoteException
     */
    public String getParticipantLabel(JID contactJID) throws RemoteException;

    /**
     * Test if the tester is in a session.
     * 
     * @return <tt>true</tt> if the tool bar button "Leave the session" is
     *         enabled.
     * 
     * @throws RemoteException
     */
    public boolean isInSession() throws RemoteException;

    /**
     * Check if the given user is existed in the session list.
     * 
     * @param participantJID
     *            {@link JID}
     * @return <tt>true</tt> if the passed JID is in the session list
     * @throws RemoteException
     */
    public boolean existsParticipant(JID participantJID) throws RemoteException;

    /**
     * Check whether the local user is host.
     * 
     * @return<tt>true</tt>, if the name of the first tableItem of the session
     *                       list is same as the local user.
     * @throws RemoteException
     */
    public boolean isHost() throws RemoteException;

    /**
     * check whether the local host is following
     * 
     * @return<tt>true</tt>, if there are participant in the session list
     *                       existed and his contextMenu
     *                       {@link StfRemoteObject#CM_STOP_FOLLOWING} is
     *                       enabled.
     * @throws RemoteException
     */
    public boolean isFollowing() throws RemoteException;

    /**
     * 
     * Gets all participants listed in the session view
     * 
     * @return list, which contain all the participants' status label
     * @throws RemoteException
     */
    public List<String> getAllParticipants() throws RemoteException;

    /**
     * 
     * @return the JID of the local user
     * @throws RemoteException
     */
    public JID getJID() throws RemoteException;

    /**
     * @return the JID of the followed user or null if currently no user is
     *         followed.
     */
    public JID getFollowedBuddy() throws RemoteException;

    /**********************************************
     * 
     * Wait until
     * 
     **********************************************/

    /**
     * Wait until the toolbarButton
     * {@link StfRemoteObject#TB_INCONSISTENCY_DETECTED} is enabled.
     * 
     * @throws RemoteException
     */
    public void waitUntilIsInconsistencyDetected() throws RemoteException;

    /**
     * Wait until the condition {@link ISarosView#isInSession()} is true.
     * 
     * @throws RemoteException
     */
    public void waitUntilIsInSession() throws RemoteException;

    /**
     * wait until the condition {@link ISarosView#isInSession()} by given user
     * is true.
     * 
     * @param superBot
     *            {@link ISuperBot} of the invitee, whose session status you
     *            want to know.
     * @throws RemoteException
     */
    public void waitUntilIsInviteeInSession(ISuperBot superBot)
        throws RemoteException;

    /**
     * Wait until the condition {@link ISarosView#isInSession()} is false.
     * <p>
     * <b>Attention</b>:<br/>
     * Some actions need to long time to complete, so you will get assertError
     * if you immediately assert the after-state caused by such actions. E.g.
     * you will get assertError with the assertion
     * assertTrue(alice.views().sessionviews().isInsession()) if alice leave the
     * session without waitUntil the condition {@link ISarosView#isInSession()}
     * So it is recommended that you wait until the session is completely closed
     * before you run the assertion or perform a following action.
     * 
     * @throws RemoteException
     */
    public void waitUntilIsNotInSession() throws RemoteException;

    /**
     * Wait until the condition {@link ISarosView#isInSession()} by the given
     * user is false.
     * 
     * 
     * 
     * @param superBot
     *            {@link ISuperBot} of the invitee, whose session status you
     *            want to know.
     * @throws RemoteException
     */
    public void waitUntilIsInviteeNotInSession(ISuperBot superBot)
        throws RemoteException;

    /**
     * Wait until the condition {@link ISarosView#existsParticipant(JID)} for
     * all participants is false.
     * 
     * @param jidsOfAllParticipants
     * @throws RemoteException
     */
    public void waitUntilAllPeersLeaveSession(
        final List<JID> jidsOfAllParticipants) throws RemoteException;

}
