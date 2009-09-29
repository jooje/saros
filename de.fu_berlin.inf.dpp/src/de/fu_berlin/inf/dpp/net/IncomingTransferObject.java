package de.fu_berlin.inf.dpp.net;

import java.io.IOException;

import org.eclipse.core.runtime.SubMonitor;

import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.exceptions.UserCancellationException;
import de.fu_berlin.inf.dpp.net.business.DispatchThreadContext;
import de.fu_berlin.inf.dpp.net.internal.TransferDescription;
import de.fu_berlin.inf.dpp.net.internal.XStreamExtensionProvider;
import de.fu_berlin.inf.dpp.net.internal.DataTransferManager.NetTransferMode;

@Component(module = "net")
public interface IncomingTransferObject {

    /**
     * @throws UserCancellationException
     *             If an user (remote or local) has canceled.
     * @throws IOException
     *             If there was a technical problem.
     * 
     * @blocking This is a long running operation (an archive might be received
     *           for instance). So do not call this from the
     *           {@link DispatchThreadContext} or from the SWT Thread.
     */
    public byte[] accept(SubMonitor progress) throws UserCancellationException,
        IOException;

    /**
     * Returns the transfer description of this transfer object (which you can
     * get by calling accept).
     */
    public TransferDescription getTransferDescription();

    /**
     * Returns the NetTransferMode using which this Transfer is going to be/has
     * been received.
     */
    public NetTransferMode getTransferMode();

    /**
     * Rejects the incoming transfer.
     * 
     * @throws IllegalStateException
     *             if accept or reject has been called previously.
     * 
     * @throws IOException
     *             If the rejection could not be sent. A client reasonably safe
     *             to discard.
     */
    public void reject() throws IOException;

    public static class IncomingTransferObjectExtensionProvider extends
        XStreamExtensionProvider<IncomingTransferObject> {
        public IncomingTransferObjectExtensionProvider() {
            super("incomingTransferObject");
        }
    }

}