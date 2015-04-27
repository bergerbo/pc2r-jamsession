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
	private SessionInfo info;
	private int tick;

	public Client( int port, String user) {
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
			send(msg);

			msg = receive();
			if (!msg.getCmd().equals(Command.WELCOME))
				return false;

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

	public SessionInfo waitForSyncInfo() throws IOException, UnkownCommandException, UnexpectedMessageException {
		Message msg;
		msg = receive();
		String cmd = msg.getCmd();
		
		if (cmd.equals(Command.CURRENT_SESSION)) {
			info = new SessionInfo(msg.getArgs());
			
			msg = receive();
			if(!msg.getCmd().equals(Command.AUDIO_SYNC))
				throw new UnexpectedMessageException(msg.getCmd());
			
			tick = Integer.parseInt(msg.getArgs().get(0));

            // Start recording and playing in Mixer
            // Start reception and sending in AudioConnection
            mixer.setAudioConnection(ac);
            mixer.start(info.tempo);
            ac.start(info);
			
			return info;
		} else if (cmd.equals(Command.EMPTY_SESSION)) {
			return null;
		} else if (cmd.equals(Command.FULL_SESSION)) {
			info = new SessionInfo();
			return info;
		} else {
			throw new UnexpectedMessageException(cmd);
		}		
	}
	
	public boolean sendSessionInfo(SessionInfo info) throws IOException, UnkownCommandException{
		Message msg = new Message(Command.SET_OPTIONS);
		msg.addArg(info.style);
		msg.addArg(""+info.tempo);
		send(msg);
		
		msg = receive();
		if(!msg.getCmd().equals(Command.ACK_OPTS))
			return false;
		
		tick = 0;

		// Start recording and playing in Mixer
		// Start reception and sending in AudioConnection
		mixer.setAudioConnection(ac);
		mixer.start(info.tempo);
		ac.start(info);
		
		return true;
	}

	public void  setupAudioConnection() throws IOException, UnkownCommandException, UnexpectedMessageException {
		// Get audio port
		Message msg;
		msg = receive();
		if (!msg.getCmd().equals(Command.AUDIO_PORT))
			throw new UnexpectedMessageException(msg.getCmd());

		int audioport = Integer.parseInt(msg.getArgs().get(0));
		mixer = new SoundMixer();
		ac = new AudioConnection(mixer, audioport, tick);
		if (!ac.connect())
			throw new UnexpectedMessageException(msg.getCmd());

		msg = receive();
		if (!msg.getCmd().equals(Command.AUDIO_OK))
			throw new UnexpectedMessageException(msg.getCmd());
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
