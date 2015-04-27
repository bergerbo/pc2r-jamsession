package pc2r.upmc.jamsession.gui;

import javax.swing.JPanel;
import javax.swing.JTextField;

import pc2r.upmc.jamsession.network.SessionInfo;

@SuppressWarnings("serial")
public class SessionDisplay extends JPanel {

	
	private JTextField style;
	private JTextField tempo;
	private JTextField nb_mus;
	private SessionInfo info;
	
	public SessionDisplay(SessionInfo info){
		this.info = info;
		style = new JTextField(info.style);
		tempo = new JTextField(""+info.tempo);
		nb_mus = new JTextField(""+info.nb_mus);
		
		this.add(style);
		this.add(tempo);
		this.add(nb_mus);
		
		new Thread(new Updater()).start();
	}
	
	public void updateInfo(){
		style.setText(info.style);
		tempo.setText(""+info.tempo);
		nb_mus.setText(""+info.nb_mus);
		updateUI();
	}
	
	public class Updater implements Runnable {

		@Override
		public void run() {
			while(true){
				try {
					synchronized (info) {
						info.wait();
						updateInfo();	
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
}
