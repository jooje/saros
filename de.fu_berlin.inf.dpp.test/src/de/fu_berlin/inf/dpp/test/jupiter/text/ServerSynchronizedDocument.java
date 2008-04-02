package de.fu_berlin.inf.dpp.test.jupiter.text;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.eclipse.ui.internal.presentations.util.ProxyControl;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

import de.fu_berlin.inf.dpp.jupiter.Algorithm;
import de.fu_berlin.inf.dpp.jupiter.Operation;
import de.fu_berlin.inf.dpp.jupiter.Request;
import de.fu_berlin.inf.dpp.jupiter.SynchronizedQueue;
import de.fu_berlin.inf.dpp.jupiter.TransformationException;
import de.fu_berlin.inf.dpp.jupiter.internal.Jupiter;
import de.fu_berlin.inf.dpp.net.JID;
import de.fu_berlin.inf.dpp.test.jupiter.text.network.NetworkConnection;
import de.fu_berlin.inf.dpp.test.jupiter.text.network.NetworkEventHandler;


public class ServerSynchronizedDocument implements JupiterServer, SynchronizedQueue, NetworkEventHandler, DocumentTestChecker{
	
	private static Logger logger = Logger.getLogger(ServerSynchronizedDocument.class);
	
	private Document doc;
	/* sync algorithm with ack-operation list. */
	private Algorithm algorithm;
	
	private JID jid;
	private NetworkConnection connection;
	
	private HashMap<JID,SynchronizedQueue> proxyQueues;
	
	public ServerSynchronizedDocument(String content, NetworkConnection con){
		init(content,con);
	}

	public ServerSynchronizedDocument(String content, NetworkConnection con, JID jid){		
		this.jid = jid;
		init(content,con);
	}
	
	/* init proxy queue and all necessary objects. */
	private void init(String content, NetworkConnection con){
		this.doc = new Document(content);
		this.algorithm = new Jupiter(true);
		this.connection = con;
		this.proxyQueues = new HashMap<JID,SynchronizedQueue>();
	}
	
	public void setJID(JID jid){
		this.jid = jid;
	}
	
	public JID getJID() {
		return jid;
	}

	/**
	 * {@inheritDoc}
	 */
	public Operation receiveOperation(Request req) {
		Operation op = null;
		try {
			logger.debug("Operation before OT:"+req.getOperation().toString());
			/* 1. transform operation. */
			op = algorithm.receiveRequest(req);
			logger.debug("Operation after OT: "+op.toString());
			
			/* 2 sync with proxy queues. */
			for(JID jid : proxyQueues.keySet()){
				proxyQueues.get(jid).receiveOperation(req);
			}
			
			
			/* 3. execution on server document*/
			doc.execOperation(op);
		} catch (TransformationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return op;
	}

	/**
	 * {@inheritDoc}
	 */
	private Operation receiveOperation(Request req, JID jid) {
		Operation op = null;
		try {
			logger.debug("Incoming Request from : "+jid.toString());
			logger.debug("Operation before OT:"+req.getOperation().toString());
			/* 1. transform operation. */
			op = algorithm.receiveRequest(req);
			logger.debug("Operation after OT: "+op.toString());
			
			/* 2. execution on server document*/
			doc.execOperation(op);
			
			/* 3 sync with proxy queues. */
			for(JID j : proxyQueues.keySet()){
				SynchronizedQueue q =  proxyQueues.get(j);
				/* 3.1 create transformed operation. */
				op = q.receiveOperation(req);
				
				/* 3.2. send operation */
				if(!j.toString().equals(jid.toString())){
					q.sendTransformedOperation(op,j);
				}
			}
			
		} catch (TransformationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return op;
	}
	
	/* send to all proxy clients. */
	public void sendOperation(Operation op) {
		/* 1. execute locally*/
		doc.execOperation(op);
		/* 2. transfer proxy queues. */
		for(JID jid: proxyQueues.keySet()){
			proxyQueues.get(jid).sendOperation(op);
		}
	}
	
	/**
	 * send operation to special jid.
	 * @param jid
	 * @param op
	 */
	public void sendOperation(JID jid, Operation op){
		sendOperation(jid, op, 0);
	}
	
	public void sendOperation(JID jid, Operation op, int delay) {
		/* 1. execute locally*/
		doc.execOperation(op);
		/* 2. transform operation. */
		Request req = algorithm.generateRequest(op);
		/*sent to client*/
//		connection.sendOperation(jid, req,delay);
		connection.sendOperation(new NetworkRequest(this.jid, jid,req), delay);
		
	}

	public void receiveNetworkEvent(Request req) {
		logger.info("receive operation : "+req.getOperation().toString());
		receiveOperation(req);	

	}
	
	


	public String getDocument() {
		return doc.getDocument();
	}

	
	public void addProxyClient(JID jid) {
		SynchronizedQueue queue = new ProxySynchronizedQueue(jid, this.connection);
		proxyQueues.put(jid,queue);
	}

	public void removeProxyClient(JID jid) {
		proxyQueues.remove(jid);
	}

	public void sendTransformedOperation(Operation op, JID toJID) {
		// TODO Auto-generated method stub
		
	}

	
	public void receiveNetworkEvent(NetworkRequest req) {
		logger.debug("receive network event with networtrequest from "+req.getFrom());
		receiveOperation(req.getRequest(), req.getFrom());
	}





}
