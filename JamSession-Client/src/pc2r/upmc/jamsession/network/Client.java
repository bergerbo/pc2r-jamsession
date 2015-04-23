package pc2r.upmc.jamsession.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import pc2r.upmc.jamsession.sound.SoundMixer;

public class Client {

	private Socket s;
	private PrintWriter out;
	private BufferedReader in;

	private SoundMixer mixer;
	private AudioConnection ac;

	private int port;
	private String user;

	public Client(SoundMixer mixer, int port, String user) {
		this.mixer = mixer;
		this.port = port;
		this.user = user;
	}

	public boolean connect() {
		try {
			s = new Socket("localhost", port);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			// Handshake
			Message msg = new Message(Command.CONNECT);
			msg.addArg(user);
			out.println(MessageBuilder.build(msg));
			out.flush();


			msg = receive();
			if (!msg.getCmd().equals(Command.WELCOME))
				return false;

			// Get audio port
			msg = receive();
			if (!msg.getCmd().equals(Command.AUDIO_PORT))
				return false;

			int audioport = Integer.parseInt(msg.getArgs().get(0));
			ac = new AudioConnection(mixer, audioport);

			if (!ac.connect())
				return false;

			msg = receive();
			if (!msg.getCmd().equals(Command.AUDIO_OK))
				return false;

			// Start recording and playing in Mixer
			// Start reception and sending in AudioConnection
			mixer.setAudioConnection(ac);
			mixer.start();
			ac.start();

			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnkownCommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean waitForSyncInfo() {
		Message msg;
		
		msg = receive();
		
		return false;
	}

	public void send(Message msg) {
		out.println(MessageBuilder.build(msg));
		out.flush();
	}

	public Message receive() throws IOException, UnkownCommandException {
		String resp;
		resp = in.readLine();
		return MessageBuilder.parse(resp);
	}

	public void close() {
		if (s != null) {
			try {
				s.close();
				out.close();
				in.close();
				s = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (ac != null) {
			ac.stop();
			mixer.stop();
			ac = null;
		}
	}

}
