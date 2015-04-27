package pc2r.upmc.jamsession.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.network.SessionInfo;

public class Window extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton button;
	private Client client;
	private JFrame frame;

	public Window(final Client client, final JFrame frame) {
		this.client = client;
		this.frame = frame;
		JPanel topButtons = new JPanel();
		SessionDisplay si = new SessionDisplay(client.getInfo());
		// Connect Button
		button = new JButton("Connect");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					button.setText("Connecting");
					button.setEnabled(false);
					
					ConnectionWorker connection;
					connection = new ConnectionWorker(client, button);
					connection.execute();
					if (!connection.get()) {
						reset();
						return;
					}

					SessionSyncWorker sync = new SessionSyncWorker(client, button);
					sync.execute();
					SessionInfo info = sync.get();
					if (info == null) {
						info = sessionInfoDialog();
						SessionOptionWorker opt = new SessionOptionWorker(
								client, info);
						opt.execute();

						if (!opt.get()) {
							reset();
							return;
						}

					} else if (info.full) {
						JOptionPane
								.showMessageDialog(frame, "Session is full.");
						reset();
						return;
					}
					AudioConnectionWorker ac = new AudioConnectionWorker(client);
					ac.execute();
					ac.get();

				} catch (InterruptedException | ExecutionException e) {
					reset();
				}

			}
		});
		topButtons.add(button);

		this.add(topButtons);
		this.add(si);
	}

	public void reset() {
		client.close();
		button.setText("Connect");
		button.setEnabled(true);
	}

	public SessionInfo sessionInfoDialog() {
		JTextField styleField = new JTextField(20);
		JTextField tempoField = new JTextField(5);

		JPanel sessionPanel = new JPanel();
		sessionPanel.add(new JLabel("Style :"));
		sessionPanel.add(styleField);
		sessionPanel.add(Box.createVerticalStrut(15)); // a spacer
		sessionPanel.add(new JLabel("Tempo :"));
		sessionPanel.add(tempoField);

		int result = JOptionPane.showConfirmDialog(null, sessionPanel,
				"New Session !", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			SessionInfo info = new SessionInfo();
			info.full = false;
			info.nb_mus = 1;
			info.style = styleField.getText();
			info.tempo = Integer.parseInt(tempoField.getText());
			return info;
		}

		return null;
	}

}
