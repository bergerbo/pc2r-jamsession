package pc2r.upmc.jamsession.sound;

import java.util.Arrays;
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
	
	private ArrayBlockingQueue<byte[]> playbackQueue = new ArrayBlockingQueue<>(
			100);

	private AudioConnection ac;
	private SourceDataLine mixedout;
	private SourceDataLine playback;
	private TargetDataLine in;
	private AudioFormat format;
	private int tempo;

	private Thread audioRecorder;
	private Thread audioPlayer;
	private Thread audioPlayback;
	private boolean running;

	public SoundMixer(AudioConnection ac) {
		this.ac = ac;
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
			mixedout = (SourceDataLine) AudioSystem.getLine(info);
			playback = (SourceDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			audioRecorder = new Thread(new Record(tempo));
			audioPlayback = new Thread(new Playback());
			audioPlayer = new Thread(new MixPlayer());
			in.open(format);
			in.start();
			mixedout.open(format);
			mixedout.start();
			playback.open(format);
			playback.start();
			audioRecorder.start();
			audioPlayback.start();
			audioPlayer.start();
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void stop() {
		running = false;
	}

	
	private class Playback implements Runnable {
		byte buffer[];

		@Override
		public void run() {
			while (running) {
				try {
					buffer = playbackQueue.take();
					playback.write(buffer, 0, buffer.length);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			playback.close();
		}
	}
	
	private class Record implements Runnable {
		byte buffer[];
		int timeSize;

		public Record(int tempo) {
			timeSize = 44100 * 60 * 4 / tempo;
			buffer = new byte[timeSize];
		}

		@Override
		public void run() {
			int offset = 0;
			while (running) {
				int count = in.read(buffer, offset, timeSize);
				if (count > 0) {
					
					playbackQueue.offer(Arrays.copyOfRange(buffer, offset, offset+count));
					
					offset+=count;
					
					if(offset == timeSize){
						ac.pushChunk(buffer);
						offset = 0;
					}
				}
			}
			in.close();
		}
	}

	private class MixPlayer implements Runnable {
		byte[] buffer;

		@Override
		public void run() {
			while (running) {
				try {
					buffer = incomingMix.take();
					mixedout.write(buffer, 0, buffer.length);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mixedout.close();
		}
	}

}
