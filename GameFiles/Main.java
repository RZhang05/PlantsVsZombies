//imports
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

//Plant class
class Plant {
	//basic points
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
	//the basic attributes of a plant
	public int hp = 10;
	public boolean shotInterrupted = false;
	public int plantState = 1; //used solely for sunflower
	public int getRow()
	{
		return row;
	}
	public int getCol()
	{
		return column;
	}

}

//Zombie class
class Zombie {
	
}

public class Main {

	//global variables
	public static Timer globalTime = new Timer();

	//the plants currently in play
	static ArrayList<Plant> shooterPlants = new ArrayList<Plant>();
	static ArrayList<Plant> sunflowers = new ArrayList<Plant>();
	static ArrayList<Plant> wallnuts = new ArrayList<Plant>();

	//plant selected and 
	public static int plantSelected = 0;
	public static int plantCount = 0; //identity number

	//The Board
	static Board b;

	//Currency
	static int sunCount = 50;
	static JLabel StoredEnergy;

	public static void main(String[] args) {

		//Timer Animation Tasks
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

		TimerTask wallnutChange = new TimerTask() {
			@Override
			public void run() {
				animateWallnut(wallnuts);
			}
		};
		
		//the main layout
		JFrame mainGame = new JFrame("Photosynthesis");
		mainGame.setLayout(new FlowLayout());
		b = new Board(5,10);

		//create a new menu panel/ start screen
		MenuPanel startScreen = new MenuPanel();
		
		//set the basic parameters
		mainGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainGame.add(startScreen);
		mainGame.setSize(650,500);
		mainGame.setVisible(true);

		//the amount of currency you have
		StoredEnergy = new JLabel();
		StoredEnergy.setText("Stored Energy: " + sunCount);
		
		//new buttons and their action listeners
		JButton button1 = new JButton("Sunflower");
		button1.setActionCommand("Sunflower");
		button1.setVisible(true);
		button1.setSize(150,150);
		button1.setText("SUNFLOWER");

		button1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 1; //what type of plant is it
				b.displayMessage("Selecting Sunflower...");
			}
		});
		
		//another button
		JButton button2 = new JButton("Pea Shooter");
		button2.setActionCommand("Pea Shooter");
		button2.setVisible(true);
		button2.setSize(150,150);
		button2.setText("PEA SHOOTER");

		button2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 2; //type of plant
				b.displayMessage("Selecting Pea Shooter...");
			}
		});
		
		//another button
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

		//another button
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

		//timer animation events
		globalTime.scheduleAtFixedRate(peashooter, (long)1000, (long)500);
		globalTime.scheduleAtFixedRate(sunflowerAnim, (long)1000, (long)2500);

		for(;;) { //run this infinitely
			if(startScreen.menuVisible) {
				startScreen.setVisible(true);
				mainGame.repaint();
			}
			else {
				StoredEnergy.setText("Stored Energy: " + sunCount);
				b.setVisible(true);
				startScreen.setVisible(false);
				mainGame.repaint();

				//adding the GUI
				mainGame.add(b);
				mainGame.add(button1);
				mainGame.add(button2);
				mainGame.add(button3);
				mainGame.add(button4);
				mainGame.add(StoredEnergy);
				
				//if you click anywhere on the board
				Coordinate grow = b.getClick();

				if(plantSelected > 0) {
					if(grow.getCol() > 5) { //checking if you're planting on grass
						b.displayMessage("You can only plant on grass!");
					} else {
						if(plantSelected == 2) {
							if(sunCount < 100) {
								//not enough currency
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
								wallnuts.add(p);
								b.putPeg("orange", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Wallnut at " + grow.getRow() + " " + grow.getCol());
							}
						}
					}
				}
			}
		}	
	}

	public static void animateShooterPlants(ArrayList<Plant> plants) {
		//for each pea shooter
		for(int i=0;i<plants.size();i++) { 
			Plant tempPlant = plants.get(i);
			if(killPlant(tempPlant)) {
				plants.remove(tempPlant);
			} else {
				//if zombie in lane
				if(tempPlant.type == 2) { //PEA SHOOTER
					if(tempPlant.getCol() < 9) {
						//animation
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						b.putPeg("green", tempPlant.originRow, tempPlant.originCol);
						b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
						tempPlant.row = tempPlant.row;
						tempPlant.column = tempPlant.column + 1;
					} else {
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						tempPlant.row = tempPlant.originRow;
						tempPlant.column = tempPlant.originCol;
					}
				} else { //DOUBLE PEA SHOOTER
					if(tempPlant.getCol() < 8) {
						//animation
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						b.putPeg("green", tempPlant.originRow, tempPlant.originCol);
						b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
						b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 2);
						tempPlant.row = tempPlant.row;
						tempPlant.column = tempPlant.column + 1;
					} else if(tempPlant.getCol() < 9) {
						b.putPeg("red", tempPlant.getRow(),tempPlant.getCol() + 1);
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
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
	}

	public static void animateSunflowers(ArrayList<Plant> plants) {
		for(int i=0;i<plants.size();i++) {
			Plant tempPlant = plants.get(i);
			if(killPlant(tempPlant)) {
				plants.remove(tempPlant);
			} else {
				b.removePeg(tempPlant.getRow(), tempPlant.getCol());
				if(tempPlant.plantState == 1) {
					tempPlant.plantState = 2;
					sunCount += 25;
					StoredEnergy.setText("Stored Energy: " + sunCount);
					b.putPeg("black", tempPlant.getRow(),tempPlant.getCol());
				} else {
					tempPlant.plantState = 1;
					b.putPeg("yellow", tempPlant.getRow(),tempPlant.getCol());
				}

			}	
		}
	}

	public static void animateWallnut(ArrayList<Plant> plants) {
		for(int i=0;i<plants.size();i++) {
			Plant tempPlant = plants.get(i);
			if(killPlant(tempPlant)) {
				plants.remove(tempPlant);
			} else {
				if(tempPlant.hp < 7) {
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					b.putPeg("cyan", tempPlant.getRow(), tempPlant.getCol());
				} else if(tempPlant.hp < 3) {
					b.removePeg(tempPlant.getRow(), tempPlant.getCol());
					b.putPeg("pink", tempPlant.getRow(), tempPlant.getCol());
				}
			}
		}
	}

	public static boolean killPlant(Plant plant) {
		if(plant.hp == 0) {
			b.removePeg(plant.originRow, plant.originCol);
			return true;
		}
		return false;
	}
}
