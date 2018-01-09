import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

class EndGame extends JPanel implements ActionListener {
	boolean endScreen = true;
	public EndGame() {
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
	
	public static JLabel background;

	private void makeGUI() {
		setLayout(new BorderLayout());
		
		background = new JLabel(new ImageIcon("gameover.jpg"));
		add(background);
		
		background.setLayout(new FlowLayout());

		JButton button2 = new JButton("High Scores");
		button2.setActionCommand("Scores");
		button2.addActionListener(this);
		background.add(button2);

		JButton button3 = new JButton("Try Again");
		button3.setActionCommand("Restart");
		button3.setBackground(new Color(154, 145, 129));
        button3.setFocusPainted(false);
		button3.addActionListener(this);
		background.add(button3);

	}

	public void actionPerformed(ActionEvent ae) {
		if(ae.getActionCommand().equalsIgnoreCase("Restart")){
			background.setVisible(false);
			this.setVisible(false);
			endScreen = false;
		}
		else if(ae.getActionCommand().equalsIgnoreCase("Scores")){
			JOptionPane.showMessageDialog(null, "These are the high scores....");
		}
	}

}
