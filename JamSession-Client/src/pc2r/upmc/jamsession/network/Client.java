package pc2r.upmc.jamsession.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import pc2r.upmc.jamsession.sound.SoundMixer;

public class Client {

	private ArrayBlockingQueue<Message> inQueue;

	private Socket s;
	private PrintWriter out;
	private BufferedReader in;

	private SoundMixer mixer;
	private AudioConnection ac;
	private Thread receiver;
	private Thread sessionManager;
	boolean running;

	private int port;
	private int audioPort;
	private String user;
	private SessionInfo info;
	
	private int tick;

	public Client(int port, String user) {
		this.port = port;
		this.user = user;
		info = new SessionInfo();
		inQueue = new ArrayBlockingQueue<>(10);
	}
	
	public SessionInfo getInfo() {
		return info;
	}

	public boolean connect() throws InterruptedException {
		try {
			s = new Socket("localhost", port);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			running = true;

			receiver = new Thread(new MessageReceiver());
			sessionManager = new Thread(new SessionManager());
			receiver.start();
			sessionManager.start();

			// Handshake
			Message msg = new Message(Command.CONNECT);
			msg.addArg(user);
			send(msg);

			msg = waitFor(Command.WELCOME);

			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public SessionInfo waitForSyncInfo() throws InterruptedException,
			UnexpectedMessageException {
		Message msg;

		// Get audio port
		msg = waitFor(Command.AUDIO_PORT);

		audioPort = Integer.parseInt(msg.getArgs().get(0));

		// Get session infos & tick sync if needed
		msg = waitFor(Command.CURRENT_SESSION, Command.EMPTY_SESSION,
				Command.FULL_SESSION);
		String cmd = msg.getCmd();

		if (cmd.equals(Command.CURRENT_SESSION)) {
			info.updateInfos(msg.getArgs());

			msg = waitFor(Command.AUDIO_SYNC);

			tick = Integer.parseInt(msg.getArgs().get(0));

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

	public boolean sendSessionInfo(SessionInfo info)
			throws InterruptedException {
		// Send user created session infos
		Message msg = new Message(Command.SET_OPTIONS);
		msg.addArg(info.style);
		msg.addArg("" + info.tempo);
		send(msg);

		msg = receive();
		if (!msg.getCmd().equals(Command.ACK_OPTS))
			return false;

		tick = 0;

		return true;
	}

	public void setupAudioConnection() throws InterruptedException {

		ac = new AudioConnection(this, mixer, audioPort, info, tick);
		mixer = new SoundMixer(info.tempo,ac);

		if (!ac.connect()) {
			close();
			return;
		}

		waitFor(Command.AUDIO_OK);

		// Start recording and playing in Mixer
		// Start reception and sending in AudioConnection
		mixer.start();
		ac.start();
		
	}

	public void send(Message msg) {
		out.println(MessageBuilder.build(msg));
		out.flush();
	}

	public Message receive() throws InterruptedException {
		return inQueue.take();
	}

	public Message waitFor(String... cmds) throws InterruptedException {
		List<String> commands = Arrays.asList(cmds);
		while (true) {
			synchronized (inQueue) {
				Message head = inQueue.peek();
				if (head != null && commands.contains(head.getCmd())) {
					inQueue.poll();
					inQueue.notifyAll();
					return head;
				} else
					inQueue.wait();
			}
		}
	}

	public Message waitFor(String command) throws InterruptedException {
		while (true) {
			synchronized (inQueue) {
				Message head = inQueue.peek();
				if (head != null && head.getCmd().equals(command)) {
					inQueue.poll();
					inQueue.notifyAll();
					return head;
				} else
					inQueue.wait();
			}
		}
	}

	public void close() {
		if (s != null) {
			try {
				s.close();
				out.close();
				in.close();
				running = false;
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

	private class MessageReceiver implements Runnable {

		@Override
		public void run() {

			try {
				while (running) {
					String resp;
					resp = in.readLine();
					synchronized (inQueue) {

						try {
							inQueue.put(MessageBuilder.parse(resp));
						} catch (UnkownCommandException e) {
							e.printStackTrace();
						}
						inQueue.notifyAll();
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SessionManager implements Runnable {
		private Message msg;

		@Override
		public void run() {
			while (running) {
				try {
					msg = waitFor(Command.CONNECTED, Command.EXITED);
					synchronized (info) {
						if (msg.getCmd().equals(Command.CONNECTED))
							info.nb_mus++;
						else
							info.nb_mus--;
						
						info.notifyAll();
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

}
