package pc2r.upmc.jamsession.gui;

import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;

public class AudioConnectionWorker extends SwingWorker<Boolean, Boolean> {

	private Client client;
	
	public AudioConnectionWorker(Client client){
		this.client = client;
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		client.setupAudioConnection();
		return true;
	}

}
