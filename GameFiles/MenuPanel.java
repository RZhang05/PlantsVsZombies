
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class MenuPanel extends JPanel implements ActionListener {
	boolean menuVisible = true;
	public MenuPanel() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					makeGUI();
				}
			});
		} catch (Exception exc) {
			System.out.println("Failed to run due to: " + exc);
		}
	}

	private void makeGUI() {
		setLayout(new FlowLayout());

		JButton button1 = new JButton("Instructions");
		button1.setActionCommand("Instructions");
		button1.addActionListener(this);
		add(button1);

		JButton button2 = new JButton("High Scores");
		button2.setActionCommand("Scores");
		button2.addActionListener(this);
		add(button2);

		JButton button3 = new JButton("Play Game");
		button3.setActionCommand("Play");
		button3.addActionListener(this);
		add(button3);

	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equalsIgnoreCase("Play")){
			this.setVisible(false);
			menuVisible = false;
		}
		else if(ae.getActionCommand().equalsIgnoreCase("Instructions")){
			JOptionPane.showMessageDialog(null, "This is how to play....");
		} 
		else if(ae.getActionCommand().equalsIgnoreCase("Scores")){
			JOptionPane.showMessageDialog(null, "These are the high scores....");
		}
	}
}
