package pc2r.upmc.jamsession.gui;

import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.SwingWorker;

import pc2r.upmc.jamsession.network.Client;

public class ConnectionWorker extends SwingWorker<Boolean, Boolean> {

	private Client client;
	private JButton button;
	
	public ConnectionWorker(Client client, JButton button){
		this.client = client;
		this.button = button;
	}
	
	
	@Override
	protected Boolean doInBackground() throws Exception {
		
		return client.connect();
	}
	
	@Override
	protected void done() {
		try {
			if(get()){
				button.setText("Connected!");
			}
			else {
				button.setText("Connect");
				button.setEnabled(true);
			}
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
