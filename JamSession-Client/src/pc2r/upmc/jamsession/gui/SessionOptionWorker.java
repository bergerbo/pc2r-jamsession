package pc2r.upmc.jamsession.gui;

import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.network.SessionInfo;

public class SessionOptionWorker extends SwingWorker<Boolean, Boolean> {

	private Client client;
	private SessionInfo info;

	public SessionOptionWorker(Client client, SessionInfo info) {
		this.client = client;
		this.info = info;
	}

	@Override
	protected Boolean doInBackground() throws Exception {
		return client.sendSessionInfo(info);
	}

}
