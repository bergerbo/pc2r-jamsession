package pc2r.upmc.jamsession.sound;

import java.util.concurrent.ArrayBlockingQueue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import pc2r.upmc.jamsession.network.AudioConnection;

public class SoundMixer {

	private ArrayBlockingQueue<byte[]> incomingMix = new ArrayBlockingQueue<>(
			10);
	
	private ArrayBlockingQueue<byte[]> repeater = new ArrayBlockingQueue<>(10);

	private AudioConnection ac;
	private SourceDataLine out;
	private TargetDataLine in;
	private AudioFormat format;
	private int tempo;
	
	private Thread audioRecorder;
	private Thread audioPlayer;
	private boolean running;

	public SoundMixer(AudioConnection ac) {

		float sampleRate = 44100;
		int sampleSizeInBits = 32;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		format = new AudioFormat(sampleRate, sampleSizeInBits, channels,
				signed, bigEndian);

		try {
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
			    System.err.println("Unsupported Line");
			}
			in = (TargetDataLine) AudioSystem.getLine(info);
			info = new DataLine.Info(SourceDataLine.class, format);
			out = (SourceDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setAudioConnection(AudioConnection ac) {
		this.ac = ac;
	}

	public void pushIncomming(byte[] buf) {
		try {
			incomingMix.put(buf);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start(int tempo) {

		try {
			this.tempo = tempo;
			running = true;
			audioRecorder = new Thread(new AudioRecorder(tempo));
			audioPlayer = new Thread(new AudioPlayer());
			in.open(format);
			out.open(format);
			audioRecorder.start();
			audioPlayer.start();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stop() {
		running = false;
	}

	private class AudioRecorder implements Runnable {
		byte buffer[];

		public AudioRecorder(int tempo){
			int samples = 44100 * 60 / tempo;
			buffer = new byte[samples * 4];
		}
		@Override
		public void run() {
			while (running) {
				int count = in.read(buffer, 0, buffer.length);
				if (count > 0) {
					ac.pushChunk(buffer);
					try {
						repeater.put(buffer);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			in.close();
		}
	}

	private class AudioPlayer implements Runnable {
		byte[] buffer;
		byte[] repeat;
		@Override
		public void run() {
			while (running) {
				try {
					buffer = incomingMix.take();
					repeat = repeater.poll();
					
					for(int i = 0; i< buffer.length; i++){
						buffer[i] = (byte) ((buffer[i] + repeat[i])/2);
					}
					
					out.write(buffer, 0, buffer.length);
					out.flush();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			out.close();
		}
	}

}
