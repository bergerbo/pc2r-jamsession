package pc2r.upmc.jamsession.gui;

import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.network.SessionInfo;

public class SessionSyncWorker extends SwingWorker<SessionInfo, Boolean> {

	private Client client;
	
	public SessionSyncWorker(Client client){
		this.client = client;
	}
	
	@Override
	protected SessionInfo doInBackground() throws Exception {
		return client.waitForSyncInfo();
	}

}
