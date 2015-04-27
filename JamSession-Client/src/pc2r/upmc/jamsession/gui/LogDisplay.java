package pc2r.upmc.jamsession.gui;

import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import pc2r.upmc.jamsession.network.Log;
import pc2r.upmc.jamsession.network.Logger;

@SuppressWarnings("serial")
public class LogDisplay extends JPanel {

	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private JTextArea log;
	private Logger logger;
	private int position;

	public LogDisplay(Logger logger) {
		this.logger = logger;
		position = 0;
		log = new JTextArea("", 10, 50);
		add(log);
		
		new Thread(new Updater()).start();
	}

	private void updateLog() {
		while (position < logger.log.size()) {
			Log l = logger.log.get(position);
			log.append("[" + sdf.format(l.t) + "]" + l.s+"\n");
			position++;
			updateUI();
		}

	}

	private class Updater implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					synchronized (logger) {
						logger.wait();
						updateLog();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

}
