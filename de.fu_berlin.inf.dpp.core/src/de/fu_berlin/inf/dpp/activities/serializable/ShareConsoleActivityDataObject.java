package de.fu_berlin.inf.dpp.activities.serializable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import de.fu_berlin.inf.dpp.activities.business.IActivity;
import de.fu_berlin.inf.dpp.activities.business.ShareConsoleActivity;
import de.fu_berlin.inf.dpp.filesystem.IPathFactory;
import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.session.ISarosSession;

@XStreamAlias("shareConsoleActivity")
public class ShareConsoleActivityDataObject extends AbstractActivityDataObject {

    private String consoleContent;

    public ShareConsoleActivityDataObject(JID source, String content) {
        super(source);

        this.consoleContent = content;
    }

    @Override
    public IActivity getActivity(ISarosSession sarosSession,
        IPathFactory pathFactory) {
        return new ShareConsoleActivity(sarosSession.getUser(source),
            consoleContent);
    }
}
