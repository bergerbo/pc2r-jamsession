package pc2r.upmc.jamsession.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

import pc2r.upmc.jamsession.sound.SoundMixer;

public class AudioConnection {

	private ArrayBlockingQueue<byte[]> chunks;

	private Thread reception;
	private Thread sending;
	private boolean running;

	private SoundMixer mixer;

	private Socket s;
	private int port;
	private int tick;
	private PrintWriter out;
	private BufferedReader in;
	private int bufferSize;
	private Client client;

	public AudioConnection(Client client, SoundMixer mixer, int port,
			SessionInfo info, int tick) {
		chunks = new ArrayBlockingQueue<byte[]>(10);
		this.client = client;
		this.mixer = mixer;
		this.port = port;
		this.tick = tick;
	}

	public void setTick(int tick) {
		this.tick = tick;
	}

	public boolean connect() {
		try {
			s = new Socket("localhost", port);
			out = new PrintWriter(s.getOutputStream());
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void start(int tempo) {
		bufferSize = 44100 * 60 * 4 / tempo;
		running = true;
		reception = new Thread(new MixReceiver(bufferSize));
		sending = new Thread(new ChunkSender(bufferSize));
		reception.start();
		sending.start();
	}

	public void stop() {
		running = false;
	}

	public void pushChunk(byte[] buf) {
		try {
			chunks.put(buf);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ChunkSender implements Runnable {
		private byte[] bytes;
		private Message msg;

		public ChunkSender(int bufferSize) {
			bytes = new byte[bufferSize];
		}

		@Override
		public void run() {
			boolean sent;
			while (running) {
				sent = false;
				try {
					while (!sent) {
						bytes = chunks.take();
						msg = new Message(Command.AUDIO_CHUNK);
						msg.addArg("" + tick);
						msg.addArg(new String(bytes));
						out.println(MessageBuilder.build(msg));
						out.flush();
						msg = client
								.waitFor(Command.AUDIO_KO, Command.AUDIO_OK);
						if (msg.getCmd().equals(Command.AUDIO_OK))
							sent = true;
					}
					tick++;

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private class MixReceiver implements Runnable {
		private char[] buf;
		private int bufferSize;
		private Message msg;

		public MixReceiver(int bufferSize) {
			this.bufferSize = bufferSize + "AUDIO_MIX//".length();
			buf = new char[this.bufferSize];
		}

		@Override
		public void run() {

			try {
				while (running) {
					in.read(buf, 0, bufferSize);
					msg = MessageBuilder.parse(new String(buf));
					if (msg.getCmd() == Command.AUDIO_MIX) {
						mixer.pushIncomming(msg.getArgs().get(0).getBytes());
						msg = new Message(Command.AUDIO_ACK);
						client.send(msg);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnkownCommandException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
