package de.fu_berlin.inf.dpp.serviceProviders;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.expressions.Expression;
import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.jivesoftware.smack.Connection;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.net.ConnectionState;
import de.fu_berlin.inf.dpp.net.IConnectionListener;
import de.fu_berlin.inf.dpp.project.AbstractSarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;

/**
 * Adds variables to Eclipse's Core {@link Expression}s in order to be used in
 * plugin.xml.
 * 
 * @author bkahlert
 */
public class SarosSourceProvider extends AbstractSourceProvider {

    /**
     * Corresponds to a serviceProvider variable as defined in Extension
     * org.eclipse.ui.services.
     */
    public static final String SAROS = "de.fu_berlin.inf.dpp.Saros";

    /**
     * Corresponds to a serviceProvider variable as defined in Extension
     * org.eclipse.ui.services.
     */
    public static final String SAROS_SESSION = "de.fu_berlin.inf.dpp.SarosSession";

    /**
     * Contains the IDs of commands which change their UI elements on the base
     * of the {@link #SAROS} variable.
     */
    public static final String[] SAROS_DEPENDENT_COMMANDS = {};

    /**
     * Contains the IDs of commands which change their UI elements on the base
     * of the {@link #SAROS_SESSION} variable.
     */
    public static final String[] SAROS_SESSION_DEPENDENT_COMMANDS = {};
    @Inject
    protected Saros saros;
    @Inject
    protected ISarosSessionManager sarosSessionManager;

    protected IConnectionListener connectionListener = new IConnectionListener() {
        @Override
        public void connectionStateChanged(Connection connection,
            ConnectionState newState) {
            connectionChanged();
        }
    };

    protected ISarosSessionListener sarosSessionListener = new AbstractSarosSessionListener() {

        @Override
        public void sessionStarted(ISarosSession newSarosSession) {
            sessionChanged(newSarosSession);
        }

        @Override
        public void sessionEnding(ISarosSession oldSarosSession) {
            sessionChanged(new NullSarosSession());
        }

    };

    public SarosSourceProvider() {
        SarosPluginContext.initComponent(this);
        saros.getSarosNet().addListener(this.connectionListener);
        this.sarosSessionManager
            .addSarosSessionListener(this.sarosSessionListener);

        this.fireSourceChanged(ISources.WORKBENCH, SAROS, this.saros);
        this.fireSourceChanged(ISources.WORKBENCH, SAROS_SESSION,
            sarosSessionManager.getSarosSession());
    }

    @Override
    public void dispose() {
        this.sarosSessionManager
            .removeSarosSessionListener(this.sarosSessionListener);
        saros.getSarosNet().removeListener(this.connectionListener);
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] { SAROS, SAROS_SESSION };
    }

    @Override
    public Map<Object, Object> getCurrentState() {
        ISarosSession sarosSession = this.sarosSessionManager.getSarosSession();
        if (sarosSession == null)
            sarosSession = new NullSarosSession();

        Map<Object, Object> map = new HashMap<Object, Object>(2);
        map.put(SAROS, this.saros);
        map.put(SAROS_SESSION, sarosSession);
        return map;
    }

    private final void connectionChanged() {
        SWTUtils.runSafeSWTAsync(null, new Runnable() {
            @Override
            public void run() {
                fireSourceChanged(ISources.WORKBENCH, SAROS, saros);
                refreshUIElements(SAROS_DEPENDENT_COMMANDS);
            }
        });
    }

    private final void sessionChanged(final ISarosSession sarosSession) {
        SWTUtils.runSafeSWTAsync(null, new Runnable() {
            @Override
            public void run() {
                fireSourceChanged(ISources.WORKBENCH, SAROS_SESSION,
                    sarosSession);

                refreshUIElements(SAROS_SESSION_DEPENDENT_COMMANDS);
            }
        });
    }

    /**
     * Refreshes all UI elements that display the given {@link Command}s.
     * 
     * @param commandIDs
     */
    private void refreshUIElements(final String[] commandIDs) {
        ICommandService commandService = (ICommandService) PlatformUI
            .getWorkbench().getActiveWorkbenchWindow()
            .getService(ICommandService.class);

        if (commandService == null)
            return;

        for (String commandID : commandIDs)
            commandService.refreshElements(commandID, null);
    }
}
