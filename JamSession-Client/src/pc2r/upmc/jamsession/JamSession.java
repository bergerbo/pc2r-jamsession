package pc2r.upmc.jamsession;

import javax.swing.JFrame;

import pc2r.upmc.jamsession.gui.Window;
import pc2r.upmc.jamsession.network.Client;
import pc2r.upmc.jamsession.sound.SoundMixer;

public class JamSession {

	public static void main(String[] args) {
		int port = Integer.parseInt(args[0]);
		String user = args[1];
		SoundMixer mixer = new SoundMixer();
		Client client = new Client(mixer, port, user);
		JFrame frame = new JFrame("JamSession");
		Window window = new Window(client, frame);
		frame.setContentPane(window);
		frame.pack();
		frame.setVisible(true);
	}

}
