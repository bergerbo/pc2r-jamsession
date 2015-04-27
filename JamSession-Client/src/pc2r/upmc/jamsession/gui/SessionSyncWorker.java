package pc2r.upmc.jamsession.gui;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.network.SessionInfo;

public class SessionSyncWorker extends SwingWorker<SessionInfo, Boolean> {

	private Client client;
	private JButton button;
	
	public SessionSyncWorker(Client client, JButton button){
		this.client = client;
		this.button = button;
	}
	
	@Override
	protected SessionInfo doInBackground() throws Exception {
		button.setText("Syncing");
		return client.waitForSyncInfo();
	}

}
