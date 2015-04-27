package pc2r.upmc.jamsession.gui;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;

public class AudioConnectionWorker extends SwingWorker<Boolean, Boolean> {

	private Client client;
	private JButton button;
	
	public AudioConnectionWorker(Client client, JButton button){
		this.client = client;
		this.button = button;
	}
	
	@Override
	protected Boolean doInBackground() throws Exception {
		client.setupAudioConnection();
		return true;
	}
	
	@Override
	protected void done() {
		button.setText("Connected!");
	}

}
