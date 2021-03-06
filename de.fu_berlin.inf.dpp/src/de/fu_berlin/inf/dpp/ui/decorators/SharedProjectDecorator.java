/*
 * DPP - Serious Distributed Pair Programming
 * (c) Freie Universität Berlin - Fachbereich Mathematik und Informatik - 2006
 * (c) Riad Djemili - 2006
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.fu_berlin.inf.dpp.ui.decorators;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.SarosPluginContext;
import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.filesystem.ResourceAdapterFactory;
import de.fu_berlin.inf.dpp.project.AbstractSarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSessionListener;
import de.fu_berlin.inf.dpp.project.ISarosSessionManager;
import de.fu_berlin.inf.dpp.session.ISarosSession;
import de.fu_berlin.inf.dpp.ui.ImageManager;
import de.fu_berlin.inf.dpp.ui.Messages;
import de.fu_berlin.inf.dpp.ui.util.SWTUtils;

/**
 * Decorates shared projects and their files.
 * 
 * @see ILightweightLabelDecorator
 * 
 * @author oezbek
 */
/*
 * http://help.eclipse.org/helios/index.jsp?topic=/org.eclipse.platform.doc.isv/
 * guide/workbench_advext_decorators.htm
 * 
 * If your plug-in needs to manipulate the label text in addition to the icon,
 * or if the type of icon is determined dynamically, you can use a
 * non-declarative lightweight decorator. In this case, an implementation class
 * that implements ILightweightLabelDecorator must be defined. The designated
 * class is responsible for supplying a prefix, suffix, and overlay image at
 * runtime which are applied to the label. The mechanics of concatenating the
 * prefix and suffix with the label text and performing the overlay are handled
 * by the workbench code in a background thread. Thus, any work performed by
 * your plug-in in its ILightweightLabelDecorator implementation must be
 * UI-thread safe.
 */
@Component(module = "eclipse")
public final class SharedProjectDecorator implements ILightweightLabelDecorator {

    private static final Logger LOG = Logger
        .getLogger(SharedProjectDecorator.class);

    private static final ImageDescriptor PROJECT_DESCRIPTOR = ImageManager
        .getImageDescriptor("icons/ovr16/shared.png"); // NON-NLS-1

    private final List<ILabelProviderListener> listeners = new CopyOnWriteArrayList<ILabelProviderListener>();

    @Inject
    private ISarosSessionManager sessionManager;

    private volatile ISarosSession session;

    private final ISarosSessionListener sessionListener = new AbstractSarosSessionListener() {

        @Override
        public void sessionStarted(ISarosSession session) {
            SharedProjectDecorator.this.session = session;
        }

        @Override
        public void sessionEnded(ISarosSession session) {
            SharedProjectDecorator.this.session = null;
            LOG.debug("clearing project decoration for all shared projects");
            updateDecoratorsAsync(null); // update all labels
        }

        @Override
        public void projectAdded(String projectID) {
            LOG.debug("updating project decoration for all shared projects");
            updateDecoratorsAsync(null); // update all labels
        }
    };

    public SharedProjectDecorator() {
        SarosPluginContext.initComponent(this);
        session = sessionManager.getSarosSession();
        sessionManager.addSarosSessionListener(sessionListener);
    }

    @Override
    public void dispose() {
        sessionManager.removeSarosSessionListener(sessionListener);
    }

    @Override
    public void decorate(Object element, IDecoration decoration) {
        // make a copy as this value might change while decorating
        ISarosSession currentSession = session;

        if (currentSession == null)
            return;

        IResource resource = (IResource) element;

        if (!currentSession.isShared(ResourceAdapterFactory.create(resource)))
            return;

        decoration.addOverlay(SharedProjectDecorator.PROJECT_DESCRIPTOR,
            IDecoration.TOP_LEFT);

        if (resource.getType() == IResource.PROJECT) {
            boolean isCompletelyShared = currentSession
                .isCompletelyShared(ResourceAdapterFactory.create(resource
                    .getProject()));

            decoration
                .addSuffix(isCompletelyShared ? Messages.SharedProjectDecorator_shared
                    : Messages.SharedProjectDecorator_shared_partial);
        }
    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void addListener(ILabelProviderListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
        listeners.remove(listener);
    }

    private void updateDecoratorsAsync(final IResource[] resources) {
        SWTUtils.runSafeSWTAsync(LOG, new Runnable() {
            @Override
            public void run() {
                LabelProviderChangedEvent event = new LabelProviderChangedEvent(
                    SharedProjectDecorator.this, resources);

                for (ILabelProviderListener listener : listeners) {
                    listener.labelProviderChanged(event);
                }
            }
        });
    }
}