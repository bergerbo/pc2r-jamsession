package pc2r.upmc.jamsession.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import pc2r.upmc.jamsession.network.Client;

public class Window extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton connect;
	
	public Window(final Client client) {
		JPanel topButtons = new JPanel();

		//Connect Button
		connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				connect.setEnabled(false);
				connect.setText("Connecting");
				new ConnectionWorker(client, connect).execute();
			}
		});
		topButtons.add(connect);
		
		
		this.add(topButtons);
	}

}
