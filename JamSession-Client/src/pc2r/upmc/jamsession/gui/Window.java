package pc2r.upmc.jamsession.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.network.SessionInfo;

public class Window extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton button;
	private Client client;
	private JFrame frame;

	public Window(final Client client,final JFrame frame ) {
		this.client = client;
		this.frame = frame;
		JPanel topButtons = new JPanel();

		// Connect Button
		button = new JButton("Connect");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					ConnectionWorker connection;
					button.setEnabled(false);
					button.setText("Connecting");
					connection = new ConnectionWorker(client);
					connection.execute();
					if (!connection.get()) {
						reset();
						return;
					}

					button.setText("Syncing");
					updateUI();
					
					SessionSyncWorker sync = new SessionSyncWorker(client);
					sync.execute();
					SessionInfo info = sync.get();
					if (info == null) {
						// prompt for infos
					} else if (info.full) {
						JOptionPane.showMessageDialog(frame, "Session is full.");
						reset();
						return;
					} else {
						AudioConnectionWorker ac = new AudioConnectionWorker(client);
						ac.execute();
						ac.get();
					}

				} catch (InterruptedException | ExecutionException e) {
					reset();
				}

			}
		});
		topButtons.add(button);

		this.add(topButtons);
	}

	public void reset() {
		client.close();
		button.setText("Connect");
		button.setEnabled(true);
	}

}
