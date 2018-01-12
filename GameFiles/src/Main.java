//imports
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

//Plant class
class Plant {
	//basic points
	public int row;
	public int column;
	public int type;
	public int originRow;
	public int originCol;
	public int lastShot;
	public Plant ( int xIn, int yIn, int typeOfPlant) {
		row = xIn; 
		column = yIn; 
		type  = typeOfPlant;
		originRow = xIn;
		originCol = yIn;

	}
	//the basic attributes of a plant
	public int hp = 10;
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

	//plants
	public static int plantSelected = 0;
	public static int index; //dead plant

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

	//Game Start Flag Var
	static boolean plantDown = false;
	static boolean spawnStarted = false;

	//Game End Flag
	static boolean isRunning = true;
	static int gameLost = 0; //did you lose the game (0 = still going, 1 = won, 2 = loss)

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
				spawnZombies();
			}
		};

		TimerTask updateClock = new TimerTask() {
			@Override
			public void run() {
				incrementClock();
			}
		};

		TimerTask keepZombies = new TimerTask() {
			@Override
			public void run() {
				keepZombieSprites();
			}
		};

		TimerTask playZombieNoises = new TimerTask() {
			@Override
			public void run() {
				playRandomGroans();
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
		gameTimer = new JLabel("Time");
		minutes = gameTime / 60;
		seconds = gameTime - (minutes * 60);
		if(seconds < 10) {
			gameTimer.setText("Time: " + minutes + ":0" + seconds);
		} else {
			gameTimer.setText("Time: " + minutes + ":" + seconds);
		}

		//new buttons and their action listeners
		JButton button1 = new JButton("Sunflower", new ImageIcon("sunflowerPacket.png"));
		button1.setActionCommand("Sunflower");
		button1.setVisible(true);
		button1.setSize(150,150);

		button1.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 1; //what type of plant is it
				b.displayMessage("Selecting Sunflower...");
			}
		});

		//another button
		JButton button2 = new JButton("Pea Shooter", new ImageIcon("peashooterPacket.png"));
		button2.setActionCommand("Pea Shooter");
		button2.setVisible(true);
		button2.setSize(150,150);
		button2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 2; //type of plant
				b.displayMessage("Selecting Pea Shooter...");
			}
		});

		//another button
		JButton button3 = new JButton("Wallnut", new ImageIcon("wallnutPacket.png"));
		button3.setActionCommand("Wallnut");
		button3.setVisible(true);
		button3.setSize(150,150);

		button3.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 3;
				b.displayMessage("Selecting Wallnut...");
			}
		});

		//another button
		JButton button4 = new JButton("Repeater", new ImageIcon("RepeaterPacket.png"));
		button4.setActionCommand("Double Pea");
		button4.setVisible(true);
		button4.setSize(150,150);

		button4.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				plantSelected = 4;
				b.displayMessage("Selecting Double Pea Shooter...");
			}
		});

		//timer events keeping character sprites in place
		globalTime.scheduleAtFixedRate(keepZombies, (long)1000, (long)250);

		//timer animation events
		globalTime.scheduleAtFixedRate(peashooter, (long)1000, (long)250);
		globalTime.scheduleAtFixedRate(sunflowerAnim, (long)1000, (long)2500);
		globalTime.scheduleAtFixedRate(wallnutChange, (long)1000, (long)400);
		globalTime.scheduleAtFixedRate(animZombies, (long)20000, (long)2000);
		globalTime.schedule(playZombieNoises, (long)21000, (long)6500);


		while(isRunning) { //run this while the game is happening

			//if a sunflower was planted
			if(plantDown && !spawnStarted) {
				spawnStarted = true;
				playBackgroundMusic();
				globalTime.scheduleAtFixedRate(spawn, (long)(21000), (long)5000/((minutes * 2) + 1));
			}

			if(startScreen.menuVisible) {
				startScreen.setVisible(true);
				mainGame.repaint();
			}
			else {
				if(gameTime == 0) {
					globalTime.scheduleAtFixedRate(updateClock,(long)0, (long)1000);
				}

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
				
				//end game
				if(gameLost == 2) {
					System.out.println("Running processes");

					//end game screens
					b.setVisible(false);
					button1.setVisible(false);
					button2.setVisible(false);
					button3.setVisible(false);
					button4.setVisible(false);
					StoredEnergy.setVisible(false);
					gameTimer.setVisible(false);
					globalTime.cancel();
					mainGame.repaint();

					EndGame endScreen = new EndGame();
					mainGame.add(endScreen);
					endScreen.setVisible(true);
				}

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
								playPlantedNoise();
								Plant p = new Plant(grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 100;
								shooterPlants.add(p);
								b.putPeg("peashooter", grow.getRow(), grow.getCol());
								b.displayMessage("Planted a Pea Shooter at " + grow.getRow() + " " + grow.getCol());
							}
						} else if(plantSelected == 1) {
							if(sunCount < 50) {
								b.displayMessage("Need 50 Stored Energy");
							} else {
								playPlantedNoise();
								Plant p = new Plant(grow.getRow(), grow.getCol(), plantSelected);
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
								playPlantedNoise();
								Plant p = new Plant(grow.getRow(), grow.getCol(), plantSelected);
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
								playPlantedNoise();
								Plant p = new Plant(grow.getRow(), grow.getCol(), plantSelected);
								sunCount -= 75;
								StoredEnergy.setText("Stored Energy: " + sunCount);
								wallnuts.add(p);
								p.hp = 20;
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
			//if a zombie is in the lane
			boolean zombieInLane = false;

			int smallest = Integer.MAX_VALUE;
			//the index of the closed zombie to the plant
			int index = 0;
			//create a default zombie
			Zombie thisZomb = new Zombie(0,0);
			//if zombie in lane
			for(int j=0;j<zombies.size();j++) {
				Zombie curZombie = zombies.get(j);
				//check for comparison 
				System.out.println("Comparing " + tempPlant.originRow + " to " + curZombie.getRow());
				if(curZombie.getRow() == tempPlant.originRow) {
					zombieInLane = true;
					if(curZombie.getCol() < smallest) {
						smallest = curZombie.getCol();
						thisZomb = curZombie;
					}
				}
			}
			if(zombieInLane) {

				//cooldown
				if(tempPlant.lastShot != 0 && tempPlant.lastShot >= gameTime && tempPlant.getCol() <= tempPlant.originCol) {
					System.out.println("Cooldown");
					return; //1 second has passed?
				}

				tempPlant.lastShot = gameTime;
				System.out.println(tempPlant.lastShot + " compared to: " + gameTime);
				// Testing for zombie in lane
				// System.out.println("Shooting at Zombie");
				if(tempPlant.type == 2) { //PEA SHOOTER
					if(tempPlant.getCol() < 9) {
						//animation
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						b.putPeg("peashooter", tempPlant.originRow, tempPlant.originCol);
						b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 1);

						//shooting noise
						if(tempPlant.getCol() == tempPlant.originCol + 1) {
							playPeaShoot();
						}

						tempPlant.column = tempPlant.column + 1;
						if(tempPlant.getCol() == smallest) {
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());

							//reset the plant animation
							tempPlant.row = tempPlant.originRow;
							tempPlant.column = tempPlant.originCol;

							thisZomb.hp -= 2;
							playHitZombie();
							if(thisZomb.hp <= 0) {
								//testing for which zombie is dying
								System.out.println(zombies.get(index).getRow());
								zombies.remove(index);

								b.removePeg(thisZomb.getRow(), thisZomb.getCol());
								try{Thread.sleep(1);}catch(InterruptedException e){};
							}
						}
					} else {
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						tempPlant.row = tempPlant.originRow;
						tempPlant.column = tempPlant.originCol;
					}
				} else { //DOUBLE PEA SHOOTER
					if(tempPlant.getCol() < 9) {
						//animation
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						b.putPeg("doublepea", tempPlant.originRow, tempPlant.originCol);
						b.putPeg("pea", tempPlant.getRow(),tempPlant.getCol() + 1);

						//shooting noise
						if(tempPlant.getCol() == tempPlant.originCol + 1) {
							playPeaShoot();
						}

						tempPlant.column = tempPlant.column + 1;
						if(tempPlant.getCol() == smallest) {
							b.removePeg(tempPlant.getRow(), tempPlant.getCol());

							//reset the plant animation
							tempPlant.row = tempPlant.originRow;
							tempPlant.column = tempPlant.originCol;

							thisZomb.hp -= 4;
							playHitZombie();
							if(thisZomb.hp <= 0) {
								//testing for which zombie is dying
								System.out.println(zombies.get(index).getRow());
								zombies.remove(index);

								b.removePeg(thisZomb.getRow(), thisZomb.getCol());
							}
						}
					} else {
						b.removePeg(tempPlant.getRow(), tempPlant.getCol());
						tempPlant.row = tempPlant.originRow;
						tempPlant.column = tempPlant.originCol;
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

	/**
	 * "Animates" the wallnut
	 * pre: plants > 0
	 * post: changes wallnut state based on damage taken
	 */
	public static void animateWallnut(ArrayList<Plant> plants) {
		for(int i=0;i<plants.size();i++) {
			Plant tempPlant = plants.get(i);
			if(tempPlant.hp < 7) {
				b.removePeg(tempPlant.getRow(), tempPlant.getCol());
				b.putPeg("wallnut", tempPlant.getRow(), tempPlant.getCol());
			} else if(tempPlant.hp < 3) {
				b.removePeg(tempPlant.getRow(), tempPlant.getCol());
				b.putPeg("wallnut", tempPlant.getRow(), tempPlant.getCol());
			} else {
				b.putPeg("wallnut", tempPlant.getRow(), tempPlant.getCol());
			}
		}
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
			//zombie has gotten into house
			if(curZombie.getCol() <= 0) {
				gameLost = 2;
				System.out.println("Game over!");
				try {
					Thread.sleep(1);
				} catch(InterruptedException e){};
			}
			//plant is under attack
			if(gameLost == 0) {
				Plant curPlant = closestPlant(curZombie);
				if(curZombie.getCol() == curPlant.originCol + 1) {
					curPlant.hp -= 2;
					if(curPlant.hp == 0) {
						b.removePeg(curPlant.originCol, curPlant.originRow);
						if(curPlant.type == 2 || curPlant.type == 4) {
							sunflowers.remove(index);
						} else if(curPlant.type == 3) {
							wallnuts.remove(index);
						} else if(curPlant.type == 1) {
							sunflowers.remove(index);
						}
					}
					playZombieEating();
					System.out.println("attacking plant: " + curPlant.originCol);
				} else {
					b.removePeg(curZombie.getRow(), curZombie.getCol());
					b.putPeg("zombie", curZombie.getRow(), curZombie.getCol() - 1);
					curZombie.col -= 1;
				}
			}
		}
		try {
			Thread.sleep(1);
		} catch(InterruptedException e){};
	}

	/**
	 * Check for the plant closest to the zombie in an array
	 * pre: Number of Plants > 0
	 * post: Plant with the least distance value
	 */
	public static Plant closestPlant(Zombie curZombie) {
		//create a "default" plant
		Plant thisPlant = new Plant(-10,-10,0);
		int minDistance = Integer.MAX_VALUE;
		for(int j=0;j<shooterPlants.size();j++) {
			Plant curPlant = shooterPlants.get(j);
			if(curPlant.getRow() == curZombie.getRow() && Math.abs(curZombie.getCol() - curPlant.originCol) < minDistance) {
				minDistance = Math.abs(curZombie.getCol() - curPlant.originCol);
				thisPlant = curPlant;
				index = j;
			}
		}
		for(int j=0;j<sunflowers.size();j++) {
			Plant curPlant = sunflowers.get(j);
			if(curPlant.getRow() == curZombie.getRow() && Math.abs(curZombie.getCol() - curPlant.originCol) < minDistance) {
				minDistance = Math.abs(curZombie.getCol() - curPlant.originCol);
				thisPlant = curPlant;
				index = j;
			}
		}
		for(int j=0;j< wallnuts.size();j++) {
			Plant curPlant = wallnuts.get(j);
			if(curPlant.getRow() == curZombie.getRow() && Math.abs(curZombie.getCol() - curPlant.originCol) < minDistance) {
				minDistance = Math.abs(curZombie.getCol() - curPlant.originCol);
				thisPlant = curPlant;
				index = j;
			}
		}
		System.out.println("Closest plant: " + thisPlant.getRow() + " , " + thisPlant.getCol());
		return thisPlant;
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

	/**
	 * Keep Zombie Sprites on screen
	 * pre: Zombie ArrayList > 0
	 * post: Zombie sprite
	 */

	public static void keepZombieSprites() {
		for(int i=0;i<zombies.size();i++) {
			Zombie curZombie = zombies.get(i);
			b.removePeg(curZombie.getRow(), curZombie.getCol());
			b.putPeg("zombie", curZombie.getRow(), curZombie.getCol());
		}
	}


	//Music and Sound Effects
	public static void playPlantedNoise() {
		try {
			// Open an audio input stream.           
			File soundFile = new File("planted.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playPeaShoot() {
		try {
			// Open an audio input stream.           
			File soundFile = new File("shoot.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playHitZombie() {
		try {
			// Open an audio input stream.           
			File soundFile = new File("splat.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playZombieEating() {
		try {
			// Open an audio input stream.           
			File soundFile = new File("ZombieBite.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public static void playRandomGroans() {
		if(zombies.size() > 0) {
			Random r = new Random();
			int ran = r.nextInt(3) + 1;
			try {
				// Open an audio input stream.   
				if(ran % 2 == 0) {
					File soundFile = new File("Groan.wav");
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
					// Get a sound clip resource.
					Clip clip = AudioSystem.getClip();
					// Open audio clip and load samples from the audio input stream.
					clip.open(audioIn);
					clip.start();
				} else {
					File soundFile = new File("Groan2.wav");
					AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
					// Get a sound clip resource.
					Clip clip = AudioSystem.getClip();
					// Open audio clip and load samples from the audio input stream.
					clip.open(audioIn);
					clip.start();
				}
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}
	}

	public static void playBackgroundMusic() {
		try {
			// Open an audio input stream.           
			File soundFile = new File("grassLevel.wav");
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);              
			// Get a sound clip resource.
			Clip clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

}
