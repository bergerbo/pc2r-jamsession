package pc2r.upmc.jamsession.gui;

import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;

public class ConnectionWorker extends SwingWorker<Boolean, Boolean> {

	private Client client;
	
	public ConnectionWorker(Client client){
		this.client = client;
	}
	
	
	@Override
	protected Boolean doInBackground() throws Exception {
		
		return client.connect();
	}
	

}
