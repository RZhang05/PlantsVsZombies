//imports
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
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
	public int row;
	public int col = 9;
	public int type;
	public int hp;
	public Zombie(int xIn, int typeOfZombie) {
		row = xIn;
		type = typeOfZombie;
		hp = type * 10;
	}
	public int getRow()
	{
		return row;
	}
	public int getCol()
	{
		return col;
	}
}

public class Main {

	//global variables
	public static Timer globalTime = new Timer();

	//the plants currently in play
	static ArrayList<Plant> shooterPlants = new ArrayList<Plant>();
	static ArrayList<Plant> sunflowers = new ArrayList<Plant>();
	static ArrayList<Plant> wallnuts = new ArrayList<Plant>();
	static ArrayList<Zombie> zombies = new ArrayList<Zombie>();

	//plant selected and 
	public static int plantSelected = 0;
	public static int plantCount = 0; //identity number

	//The Board
	static Board b;

	//Currency
	static int sunCount = 50;
	static JLabel StoredEnergy;

	//Waves and Timer
	static int gameTime = 0;
	static JLabel gameTimer;
	static int wave = 1;
	static int minutes;
	static int seconds;

	//Game Start Flag
	static boolean plantDown = false;

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
		
		TimerTask animZombies = new TimerTask() {
			@Override
			public void run() {
				animateZombies(zombies);
			}
		};

		TimerTask spawn = new TimerTask() {
			@Override
			public void run() {
				if(plantDown)
					spawnZombies();
			}
		};

		TimerTask updateClock = new TimerTask() {
			@Override
			public void run() {
				incrementClock();
			}
		};

		//the main layout
		JFrame mainGame = new JFrame("Plants vs. Zombies");
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

		//the Timer for the level
		gameTimer = new JLabel();
		minutes = gameTime / 60;
		seconds = gameTime - (minutes * 60);
		if(seconds < 10) {
			gameTimer.setText("Time: " + minutes + ":0" + seconds);
		} else {
			gameTimer.setText("Time: " + minutes + ":" + seconds);
		}

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
		globalTime.scheduleAtFixedRate(peashooter, (long)1000, (long)250);
		globalTime.scheduleAtFixedRate(sunflowerAnim, (long)1000, (long)2500);
		globalTime.scheduleAtFixedRate(wallnutChange, (long)1000, (long)400);
		globalTime.scheduleAtFixedRate(animZombies, (long)20000, (long)2000);
		globalTime.scheduleAtFixedRate(spawn, (long)21000, (long)5000/((minutes * 2) + 1));
		globalTime.scheduleAtFixedRate(updateClock, (long)1000, (long)1000);


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
				mainGame.add(gameTimer);

				//if you click anywhere on the board
				Coordinate grow = b.getClick();

				if(plantSelected > 0) {
					if(grow.getCol() > 4) { //checking if you're planting on grass
						b.displayMessage("You can only plant on grass!");
					} else {
						plantDown = true;
						if(plantSelected == 2) {
							if(sunCount < 100) {
								//not enough currency
								b.displayMessage("Need 100 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 100;
								shooterPlants.add(p);
								b.putPeg("peashooter", grow.getRow(), grow.getCol());
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
								b.putPeg("sunflower", grow.getRow(), grow.getCol());
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
								b.putPeg("doublepea", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Double Pea Shooter at " + grow.getRow() + " " + grow.getCol());
							}
						} else if(plantSelected == 3) {
							if(sunCount < 75) {
								b.displayMessage("Need 75 Stored Energy");
							} else {
								Plant p = new Plant(plantCount, grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 75;
								StoredEnergy.setText("Stored Energy: " + sunCount);
								wallnuts.add(p);
								b.putPeg("wallnut", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Wallnut at " + grow.getRow() + " " + grow.getCol());
							}
						}
					}
				}
			}
		}	
	}

	/**
	 * Animates the shooting plants
	 * pre: plants > 0
	 * post: moving and self-deleting pegs
	 */
	public static void animateShooterPlants(ArrayList<Plant> plants) {
		//for each pea shooter
		for(int i=0;i<plants.size();i++) { 
			Plant tempPlant = plants.get(i);
			if(killPlant(tempPlant)) {
				plants.remove(tempPlant);
			} else {
				boolean zombieInLane = false;
				ArrayList<Zombie> zomb = new ArrayList<Zombie>();
				//if zombie in lane
				for(int j=0;j<zombies.size();j++) {
					Zombie curZombie = zombies.get(i);
					if(curZombie.getRow() == tempPlant.getRow()) {
						zombieInLane = true;
						zomb.add(curZombie);
					}
				}
				if(zombieInLane) {
					if(tempPlant.type == 2) { //PEA SHOOTER
						if(tempPlant.getCol() < 9) {
							//animation
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());
							b.putPeg("peashooter", tempPlant.originRow, tempPlant.originCol);
							b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 1);
							
							int smallest = Integer.MAX_VALUE;
							//create a default zombie
							Zombie thisZomb = new Zombie(0,0);
							for(int j=0;j<zomb.size();j++) {
								Zombie curZombie = zomb.get(j);
								if(curZombie.getCol() < smallest) {
									smallest = curZombie.getCol();
									thisZomb = curZombie;
								}
							}
							tempPlant.column = tempPlant.column + 1;
							if(tempPlant.getCol() == smallest) {
								b.removePeg(tempPlant.getRow(), tempPlant.getCol());
								tempPlant.column = 9;
								thisZomb.hp -= 2;
							}
						} else {
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());
							tempPlant.row = tempPlant.originRow;
							tempPlant.column = tempPlant.originCol;
						}
					} else { //DOUBLE PEA SHOOTER
						if(tempPlant.getCol() < 8) {
							//animation
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());
							b.putPeg("peashooter", tempPlant.originRow, tempPlant.originCol);
							b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 1);
							b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 2);
							int smallest = Integer.MAX_VALUE;
							Zombie thisZomb = new Zombie(0,0);
							for(int j=0;j<zomb.size();j++) {
								Zombie curZombie = zomb.get(j);
								if(curZombie.getCol() < smallest) {
									smallest = curZombie.getCol();
									thisZomb.hp -= 2;
								}
							}
							tempPlant.column = tempPlant.column + 1;
							if(tempPlant.getCol() == smallest) {
								b.removePeg(tempPlant.getRow(), tempPlant.getCol());
								tempPlant.column = 9;
							}
							tempPlant.column = tempPlant.column + 1;
						} else if(tempPlant.getCol() < 9) {
							b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 1);
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());
							tempPlant.row = tempPlant.row;
							tempPlant.column = tempPlant.column + 1;
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());
							tempPlant.row = tempPlant.originRow;
							tempPlant.column = tempPlant.originCol;
						}
					}
				}
			}
		}
	}

	/**
	 * Animates the sunflowers
	 * pre: plants > 0
	 * post: "flashing" sunflowers that produce 25 sunCount every 5 seconds
	 */
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
					b.putPeg("sunfloweranim", tempPlant.getRow(),tempPlant.getCol());
				} else {
					tempPlant.plantState = 1;
					b.putPeg("sunflower", tempPlant.getRow(),tempPlant.getCol());
				}

			}	
		}
	}

	/**
	 * "Animates" the wallnut
	 * pre: plants > 0
	 * post: changes wallnut state based on damage taken
	 */
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
				} else {
					b.putPeg("wallnut", tempPlant.getRow(), tempPlant.getCol());
				}
			}
		}
	}

	/**
	 * Removes plants off board
	 * pre: plant.hp < 0
	 * post: Removed peg
	 */
	public static boolean killPlant(Plant plant) { 
		if(plant.hp == 0) {
			b.removePeg(plant.originRow, plant.originCol);
			return true;
		}
		return false;
	}

	/**
	 * Spawns zombies onto the board
	 * pre: none
	 * post: zombie peg down
	 */
	public static void spawnZombies() {
		Random r = new Random();
		int randomLane = r.nextInt(5);
		//check difficulty
		b.putPeg("zombie", randomLane, 9);
		Zombie curZombie = new Zombie(randomLane, 1);
		zombies.add(curZombie);
	}

	/**
	 * Moving Zombie Peg
	 * pre: zombies > 0
	 * post: zombie pegs moving left
	 */ 
	public static void animateZombies(ArrayList<Zombie> zombies) {
		for(int i=0;i<zombies.size();i++) {
			Zombie curZombie = zombies.get(i);
			if(curZombie.getCol() <= 0) {
				
			}
			b.removePeg(curZombie.getRow(), curZombie.getCol());
			b.putPeg("zombie", curZombie.getRow(), curZombie.getCol() - 1);
			curZombie.col -= 1;
			if(curZombie.hp <= 0) {
				zombies.remove(i);
				b.removePeg(curZombie.getRow(), curZombie.getCol());
			}
		}
	}

	/**
	 * Update Click
	 * pre: Game started
	 * post: Timer Increase
	 */
	public static void incrementClock() {
		gameTime += 1;
		minutes = gameTime / 60;
		if(minutes == 1) {
			wave = 1;
		} else if(minutes == 2) {
			wave = 2;
		} else if(minutes == 3) {
			wave = 3;
		}
		seconds = gameTime - (minutes * 60);
		if(seconds < 10) {
			gameTimer.setText("Time: " + minutes + ":0" + seconds);
		} else {
			gameTimer.setText("Time: " + minutes + ":" + seconds);
		}
	}

}
