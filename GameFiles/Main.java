import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

class Plant {
	public final int iden;
	public int row;
	public int column;
	public int type;
	public int originRow;
	public int originCol;
	public Plant (int num, int xIn, int yIn, int typeOfPlant) {
		iden = num; 
		row = xIn; 
		column = yIn; 
		type  = typeOfPlant;
		originRow = xIn;
		originCol = yIn;

	}
	public int hp = 10;
	public int getRow()
	{
		return row;
	}
	public int getCol()
	{
		return column;
	}
}

public class Main implements ActionListener {
	public static Timer globalTime = new Timer();
	static ArrayList<Plant> shooterPlants = new ArrayList<Plant>();
	static ArrayList<Plant> sunflowers = new ArrayList<Plant>();
	public static ActionListener PEA_SHOOTER;
	public static ActionListener SUNFLOWER;
	public static ActionListener WALLNUT;
	public static ActionListener DOUBLE_PEA_SHOOTER;
	public static int plantSelected = 0;
	public static int plantCount = 0;
	static Board b;
	static int sunCount = 50;
	public static int sunflowerState = 1;
	static JLabel StoredEnergy;

	public static void main(String[] args) {

		TimerTask peashooter = new TimerTask() {
			@Override
			public void run() {
				animateShooterPlants(shooterPlants);
			}
		};

		TimerTask sunflowerAnim = new TimerTask() {
			@Override
			public void run() {
				animateSunflowers(sunflowers);
			}
		};

		JFrame mainGame = new JFrame("Photosynthesis");
		mainGame.setLayout(new FlowLayout());
		b = new Board(5,10);

		MenuPanel startScreen = new MenuPanel();

		mainGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainGame.add(startScreen);
		mainGame.setSize(1000,500);
		mainGame.setVisible(true);
		
		StoredEnergy = new JLabel();
		StoredEnergy.setText("Stored Energy: " + sunCount);

		JButton button1 = new JButton("Sunflower");
		button1.setActionCommand("Sunflower");
		button1.setVisible(true);
		button1.setSize(150,150);
		button1.setText("SUNFLOWER");

		button1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 1;
				b.displayMessage("Selecting Sunflower...");
			}
		});

		JButton button2 = new JButton("Pea Shooter");
		button2.setActionCommand("Pea Shooter");
		button2.setVisible(true);
		button2.setSize(150,150);
		button2.setText("PEA SHOOTER");

		button2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 2;
				b.displayMessage("Selecting Pea Shooter...");
			}
		});

		JButton button3 = new JButton("Wallnut");
		button3.setActionCommand("Wallnut");
		button3.setVisible(true);
		button3.setSize(150,150);
		button3.setText("WALLNUT");

		button3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 3;
				b.displayMessage("Selecting Wallnut...");
			}
		});

		JButton button4 = new JButton("Double Pea Shooter");
		button4.setActionCommand("Double Pea");
		button4.setVisible(true);
		button4.setSize(150,150);
		button4.setText("DOUBLE PEA SHOOTER");

		button4.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 4;
				b.displayMessage("Selecting Double Pea Shooter...");
			}
		});
		
		globalTime.scheduleAtFixedRate(peashooter, (long)1000, (long)500);
		globalTime.scheduleAtFixedRate(sunflowerAnim, (long)1000, (long)5000);

		for(int i = 0;; i++) {
			
			if(startScreen.menuVisible) {
				startScreen.setVisible(true);
				mainGame.repaint();
			}
			else {
				StoredEnergy.setText("Stored Energy: " + sunCount);
				b.setVisible(true);
				startScreen.setVisible(false);

				//adding the GUI
				mainGame.add(b);
				mainGame.add(button1);
				mainGame.add(button2);
				mainGame.add(button3);
				mainGame.add(button4);
				mainGame.add(StoredEnergy);

				Coordinate grow = b.getClick();

				if(plantSelected > 0) {
					if(grow.getCol() > 5) {
						b.displayMessage("You can only plant on grass!");
					} else {
						if(plantSelected == 2) {
							if(sunCount < 100) {
								b.displayMessage("Need 100 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 100;
								shooterPlants.add(p);
								b.putPeg("green", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Pea Shooter at " + grow.getRow() + " " + grow.getCol());
							}
						} else if(plantSelected == 1) {
							if(sunCount < 50) {
								b.displayMessage("Need 50 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 50;
								StoredEnergy.setText("Stored Energy: " + sunCount);
								sunflowers.add(p);
								b.putPeg("yellow", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a sunflower at " + grow.getRow() + " " + grow.getCol());
							}
						} else if(plantSelected == 4) {
							if(sunCount < 150) {
								b.displayMessage("Need 150 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 150;
								StoredEnergy.setText("Stored Energy: " + sunCount);
								shooterPlants.add(p);
								b.putPeg("green", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Double Pea Shooter at " + grow.getRow() + " " + grow.getCol());
							}
						} else if(plantSelected == 3) {
							if(sunCount < 50) {
								b.displayMessage("Need 50 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 50;
								StoredEnergy.setText("Stored Energy: " + sunCount);
							}
						}
					}
				}
			}
		}	
	}

	public static void animateShooterPlants(ArrayList<Plant> plants) {
		//if zombies are on screen
		for(int i=0;i<plants.size();i++) { 
			Plant tempPlant = plants.get(i);
			if(tempPlant.type == 2) { //PEA SHOOTER
				if(tempPlant.getCol() < 9) {
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					b.putPeg("green", tempPlant.originRow, tempPlant.originCol);
					b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
					tempPlant.row = tempPlant.row;
					tempPlant.column = tempPlant.column + 1;
				} else {
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					tempPlant.row = tempPlant.getRow();
					tempPlant.column = tempPlant.getCol();
				}
			} else { //DOUBLE PEA SHOOTER
				if(tempPlant.getCol() < 8) {
					b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					b.removePeg(tempPlant.getRow(),tempPlant.getCol() + 1);
					tempPlant.row = tempPlant.row;
					tempPlant.column = tempPlant.column + 1;
				} else if(tempPlant.getCol() < 9) {
					b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
					b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 2);
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					b.removePeg(tempPlant.getRow(),tempPlant.getCol() + 1);
					tempPlant.row = tempPlant.row;
					tempPlant.column = tempPlant.column + 1;
				} else {
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					tempPlant.row = tempPlant.originRow;
					tempPlant.column = tempPlant.originCol;
				}
			}
		}
	}

	public static void animateSunflowers(ArrayList<Plant> plants) {
		for(int i=0;i<plants.size();i++) {
			Plant tempPlant = plants.get(i);
			b.removePeg(tempPlant.getRow(), tempPlant.getCol());
			if(sunflowerState == 1) {
				sunflowerState = 2;
				sunCount += 25 * plants.size();
				StoredEnergy.setText("Stored Energy: " + sunCount);
				b.putPeg("black", tempPlant.getRow(),tempPlant.getCol());
			} else {
				sunflowerState = 1;
				b.putPeg("yellow", tempPlant.getRow(),tempPlant.getCol());
			}

		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}
