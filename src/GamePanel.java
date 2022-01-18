import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.io.File;
import java.io.FileNotFoundException;
//import javax.imageio.ImageIO;
//import java.net.MalformedURLException;
//import java.net.URL;
//import java.net.URLConnection;

import java.util.Scanner;
import java.util.Formatter;
import java.util.Random;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements KeyListener, ActionListener
{
	final int screen_width=500;
	final int screen_height=500;
	final int unit_size=20;
	final int delay=120;
	
	String High_score;
	File file=new File("D01101111dge.txt");
	Formatter format;
	Scanner reader;
	String current_high;
	//URL myImage;
	Random rand;
	
	boolean paused; //Default for boolean is false
	boolean running;
	boolean shooting;
	int bulletshot=0;
	int bulletsleft=10; //Giving ten bullets for use in critical times
	int bulletX;
	int bulletY;
	int level;
	int rocketX;
	int rocketY;
	int obstacleCount=0;
	int kill_count=0; //This is so that enemies increase only after levels and not by killing
	int rockXlength=10;
	int rockYlength=10;
	int shooterXlength=0;
	int bonuslength=0;
  
	int[] rockX=new int[rockXlength];
	int[] rockY=new int[rockYlength];
	int[] shooterX=new int[shooterXlength]; //Enemies
	int[] shooterY=new int[shooterXlength];
	int[] bulletshotX=new int[shooterXlength]; //Bullets shot by enemies
	int[] bulletshotY=new int[shooterXlength];
	int[] bonusX=new int[bonuslength]; //Gives bullets
	int[] bonusY=new int[bonuslength];
	
	Timer timer;
	
	File image=new File("rocket.png");
	JLabel label;
	Image rocketImage;
	ImageIcon rocket;
	int keypressed;
	
	public GamePanel()
	{
		file.setWritable(false); //Prevents user to change high-score or or overwriting the file
		
		if(!file.exists())
		{
			try {
				format=new Formatter(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			format.format("%s", String.valueOf(0));
			format.close();
		}
		try {
			reader=new Scanner(file);
		} 
		catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		current_high=reader.next();
		reader.close();
		
		this.setPreferredSize(new Dimension(screen_width,screen_height));
		this.setFocusable(true);
		this.addKeyListener(this);
		this.setBackground(Color.BLACK);
		this.setLayout(null); //Layout of Panel is important for image to appear
		
		rocketX=(240);
		rocketY=(360); // 3/4th the height
		rand = new Random();
		
		if(image.exists())
		{
			rocket=new ImageIcon("rocket.png");
			//To get the relative path don't put the image in src folder, PUT IN THE OUTERMOST FOLDER
			
			label=new JLabel(rocket, JLabel.CENTER);
		}
		/*else 
		 * .exe was not working with java.net but .jar file was, but I could not find the problem so for now I am putting this in comments
		//This whole thing is here in-case image file is removed
		{
			boolean connected;
			try {
		         URL url = new URL("http://www.google.com");
		         URLConnection connection = url.openConnection();
		         connection.connect();
		         connected=true;
		    }
			catch (MalformedURLException e) {
		         connected=false;
		    } 
			catch (IOException e) {
		         connected=false;		         
		    }
			//This above section is to check the Internet connection so that if it connected, get image
			//If its not connected to prevent errors
			
			if(connected)
			{
				try {
					myImage=new URL("https://miro.medium.com/max/22/1*qmMwx_hZWVFM2E0WIhxrdA.png");
					rocketImage=ImageIO.read(myImage);
					
					rocket=new ImageIcon(rocketImage);
					//Using url in-case of no local file	
					label=new JLabel(rocket, JLabel.CENTER); 
					//Making the image as the label, With center alignment of the icon
					//Previously, since the drawing starts from the top-left corner, the height was 20 pixels but width was 11 so it was closer to the left side by breadth
				} 
				catch (MalformedURLException e) {
					label=new JLabel();
					label.setBackground(Color.WHITE);
					label.setOpaque(true);
				}
				catch (IOException e) {
					label=new JLabel();
					label.setBackground(Color.WHITE);
					label.setOpaque(true);
				}
			}
			else
			{
				label=new JLabel();
				label.setBackground(Color.WHITE);
				label.setOpaque(true);
				//In the case of the file neither being in the computer, nor the computer being connected
				//The rocket will be made as a white square
			}
		}*/
		else
		{
			label=new JLabel();
			label.setBackground(Color.WHITE);
			label.setOpaque(true);
			//In the case of the file neither being in the computer, nor the computer being connected
			//The rocket will be made as a white square
		}
		
		label.setVisible(true);		

		label.setBounds(rocketX,rocketY,unit_size,unit_size);
		
		this.add(label);
		//First I had put the timer in getObstacles which was a mistake
		//When getObstacles was called again program was again starting
		running=true;
		timer=new Timer(delay,this); //TIMER is very important for the delay of repainting, one of the best features to show movement. If I dont start timer, ActionListener DOES NOT START
		timer.start();
		
		getObstacles();
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}
	public void getBullet()
	{
		if(shooting)
		{
			bulletX=rocketX;
			bulletY=rocketY;
		}
	}
	public void getObstacles()
	{
		for(int i=0;i<rockXlength;i++) //Rocks
		{
			rockX[i]=rand.nextInt((int)screen_width/unit_size)*unit_size; 
			rockY[i]=rand.nextInt(((int)screen_height/unit_size)/3)*unit_size;
			// I have divided the whole thing by 3 so that the obstacles only spawn in the first third of the board
		}
		for(int i=0;i<shooterXlength;i++) //Enemies
		{
			if(i==1)
				shooterX[i]=shooterX[0]+200; 
				//So that someone doesn't shoot both, since if you shoot the last one it wont go behind any other
			else
				shooterX[i]=rand.nextInt((int)screen_width/unit_size)*unit_size; 
			shooterY[i]=rand.nextInt(((int)screen_height/unit_size)/3)*unit_size;
		}
		for(int i=0;i<shooterXlength;i++) //Bullets of enemies
		{
			bulletshotX[i]=shooterX[i];
			bulletshotY[i]=shooterY[i];
		}
		for(int i=0;i<bonuslength;i++) //Points Giver
		{	
			if(i==1)
			{
				bonusX[i]=bonusX[0]+200; 
				for(int j=0;j<bonuslength;j++)
				{
					for(int j1=0;j1<rockXlength;j1++)
					{
						if(bonusX[i]==rockX[j1])
							rockX[j1]+=600;
						//Moving the rock out of the screen so that player doesn't collide and get out with it
						//Here, I have moved the rock since there are many rocks and otherwise they keep overlapping and the green dots will be very less
					}
					for(int j1=0;j1<shooterXlength;j1++)
					{
						if(bonusX[i]==shooterX[j1])
							bonusX[i]+=600;
						//Moving the point giver, out of the screen
					}
				}
			}
			else
			{
				bonusX[i]=rand.nextInt((int)screen_width/unit_size)*unit_size;
				for(int j=0;j<bonuslength;j++)
				{
					for(int j1=0;j1<rockXlength;j1++)
					{
						if(bonusX[i]==rockX[j1])
							rockX[j1]+=600;
						//Moving the rock out of the screen so that player doesn't collide and get out with it
						//Here, I have moved the rock since there are many rocks and otherwise they keep overlapping and the green dots will be very less
					}
					for(int j1=0;j1<shooterXlength;j1++)
					{
						if(bonusX[i]==shooterX[j1])
							bonusX[i]+=600;
						//Moving the point giver, out of the screen
					}
				}
			}
			bonusY[i]=rand.nextInt(((int)screen_height/unit_size)/3)*unit_size;
		}
	}
	public void checkRockCollision()	
	{
		if(obstacleCount<rockXlength)
		{
			for(int i=0;i<rockXlength;i++)
			{
				if(rockY[i]>screen_height)
					obstacleCount++;
			}
		}
		else
		{
			obstacleCount=0;
			rockXlength+=2;
			rockYlength+=2;
			rockX=new int[rockXlength];
			rockY=new int[rockYlength];
			
			level++;
			
			kill_count++;	
			if(kill_count%5==0)
			{
				if(shooterXlength==0)
				{
					shooterXlength+=2;
					bonuslength+=2;
				}
				else
				{
					bonuslength++;
					shooterXlength++;
				}
				
				bonusX=new int[bonuslength];
				bonusY=new int[bonuslength];
				
				shooterX=new int[shooterXlength];
				shooterY=new int[shooterXlength];
				bulletshotX=new int[shooterXlength];
				bulletshotY=new int[shooterXlength];
			}
			getObstacles();
		}
	}
	public void checkRocketCollision()
	{
		for(int i=0; i<rockXlength; i++)
		{
			if(rocketX==rockX[i]&&rocketY==rockY[i])
				running=false;
		}
		for(int i=0; i<shooterXlength; i++)
		{
			if(rocketX==shooterX[i]&&rocketY==shooterY[i])
				running=false;
		}
		for(int i=0; i<bonuslength; i++)
		{
			if(rocketX==bonusX[i]&&rocketY==bonusY[i])
			{
				if(i==0) 
				{
					bonusX[i]=bonusX[i+1];
					bonusY[i]=bonusY[i+1];
				}
				else 
				{
					bonusX[i]=bonusX[i-1];
					bonusY[i]=bonusY[i-1];
				}
				bulletsleft+=2; 
			}
		}
		
		//Checks rocket's collision with borders
		if(rocketX<0) 
			running=false;
		if(rocketX>=screen_width) 
			running=false;
		
		if(!running)
			timer.stop();
	}
	public void checkBulletCollision()
	{
		for(int i=0; i<rockXlength; i++)
		{
			if((rockX[i]==bulletX&&rockY[i]==bulletY)||(rockX[i]==bulletX&&rockY[i]==(bulletY+unit_size))||(rockX[i]==bulletX&&rockY[i]==(bulletY-unit_size))) 
			//Over here I have done plus-minus unit_size to avoid errors arising due to DELAY
			{
				if(i==0) //So that the index doesn't go out of bounds
				{
					rockX[i]=rockX[i+1];
					rockY[i]=rockY[i+1];
				}
				else 
				{
					rockX[i]=rockX[i-1];
					rockY[i]=rockY[i-1];
				}
				bulletshot=0;
				shooting=false;
			}
		}
		for(int i=0; i<shooterXlength; i++)
		{
			if((shooterX[i]==bulletX&&shooterY[i]==bulletY)||(shooterX[i]==bulletX&&shooterY[i]==(bulletY+unit_size))||(shooterX[i]==bulletX&&shooterY[i]==(bulletY-unit_size))) 
			{
				if(i==0) 
				{
					shooterX[i]=shooterX[i+1];
					shooterY[i]=shooterY[i+1];
				}
				else 
				{
					shooterX[i]=shooterX[i-1];
					shooterY[i]=shooterY[i-1];
				}
				bulletshot=0;
				shooting=false;
				level++; //Extra Points for shooting the enemy
			}
		}
		for(int i=0; i<bonuslength; i++)
		{
			if((bonusX[i]==bulletX&&bonusY[i]==bulletY)||(bonusX[i]==bulletX&&bonusY[i]==(bulletY+unit_size))||(bonusX[i]==bulletX&&bonusY[i]==(bulletY-unit_size))) 
			{
				//Over here technically, we are hiding the obstacle behind the other
				if(i==0) 
				{
					bonusX[i]=bonusX[i+1];
					bonusY[i]=bonusY[i+1];
				}
				else 
				{
					bonusX[i]=bonusX[i-1];
					bonusY[i]=bonusY[i-1];
				}
				bulletsleft+=2;
				bulletshot=0;
				shooting=false;
			}
		}
		if(bulletY<0)//If bullet goes to the top and crosses the screen
		{
			bulletshot=0;
			shooting=false;
		}
	}
	public void checkShootingCollision() //For bullets shot by enemies
	{
		for(int i=0; i<shooterXlength; i++)
		{
			if((bulletshotX[i]==rocketX&&bulletshotY[i]==rocketY)||(bulletshotX[i]==rocketX&&bulletshotY[i]==(rocketY+unit_size))||(bulletshotX[i]==rocketX&&bulletshotY[i]==(rocketY-unit_size))) 
				running=false;
		}
		for(int i=0; i<shooterXlength; i++)
		{
			//If the player's and the ememie's bullets collide both of them are places outside the panel
			if((bulletshotX[i]==bulletX&&bulletshotY[i]==bulletY)||(bulletshotX[i]==bulletX&&bulletshotY[i]==(bulletY+unit_size))||(bulletshotX[i]==bulletX&&bulletshotY[i]==(bulletY-unit_size)))
			{
				bulletY=0-unit_size;
				bulletshotY[i]=screen_height+unit_size;
			}
		}
	}
	public void draw(Graphics g)
	{
		if(paused)
		{
			g.setColor(Color.BLUE); 
			g.setFont(new Font("MV Boli", Font.BOLD, 25));
			FontMetrics metrics = getFontMetrics(g.getFont()); 
			g.drawString("Bullets: "+bulletsleft,(screen_width- (metrics.stringWidth("Bullets :"+bulletsleft)))/2, g.getFont().getSize());
			//g.drawString("Enemies and Gain Extra Points",(screen_width- (metrics.stringWidth("Space to Shoot")))/2-50, g.getFont().getSize()+15);
			
			g.setColor(Color.BLUE); 
			g.setFont(new Font("MV Boli", Font.BOLD, 25));
			g.drawString("Score: "+level,(screen_width- (metrics.stringWidth("Score: "+level))-15), g.getFont().getSize());
			
			g.setColor(Color.RED); 
			g.setFont(new Font("MV Boli", Font.BOLD, 15));
			g.drawString("P to Pause/Play",15, g.getFont().getSize()+35);
			
			g.setColor(Color.BLUE); 
			g.setFont(new Font("MV Boli", Font.BOLD, 25));
			g.drawString("High: "+current_high,15,g.getFont().getSize());	
			
			g.setColor(Color.RED); 
			g.setFont(new Font("MV Boli", Font.BOLD, 52));
			g.drawString("Game Paused",(screen_width- (metrics.stringWidth("Game Paused")))/4-5, g.getFont().getSize()+250);
		}
		else
		{
			if(running)
			{
				if(shooting)
				{
					g.setColor(Color.ORANGE);
					g.fillRect(bulletX,bulletY,unit_size,unit_size);
				}
				/*for(int i=0; i<(screen_width/unit_size); i++) //for number of horizontal boxes
				{
					g.drawLine(i*unit_size,0,i*unit_size,screen_height);
					//starting x,y and ending x,y
				}
				
				for(int i=0; i<(screen_height/unit_size); i++)
				{
					g.drawLine(0,i*unit_size,screen_width,i*unit_size);
				}*/
				for(int i=0; i<rockXlength; i++)
				{
					g.setColor(Color.GRAY);
					g.fillOval(rockX[i],rockY[i],unit_size,unit_size);
				}
				for(int i=0; i<shooterXlength; i++)
				{
					g.setColor(Color.RED);
					g.fillOval(shooterX[i],shooterY[i],unit_size,unit_size);
				}
				for(int i=0; i<shooterXlength; i++)
				{
					g.setColor(Color.GREEN);
					g.fillOval(bonusX[i],bonusY[i],unit_size,unit_size);
				}
				for(int i=0;i<shooterXlength;i++)
				{
					g.setColor(Color.ORANGE);
					g.fillRect(bulletshotX[i],bulletshotY[i],unit_size,unit_size);
				}
				g.setColor(Color.BLUE); 
				g.setFont(new Font("MV Boli", Font.BOLD, 25));
				FontMetrics metrics = getFontMetrics(g.getFont()); 
				g.drawString("Bullets: "+bulletsleft,(screen_width- (metrics.stringWidth("Bullets :"+bulletsleft)))/2, g.getFont().getSize());
				//g.drawString("Enemies and Gain Extra Points",(screen_width- (metrics.stringWidth("Space to Shoot")))/2-50, g.getFont().getSize()+15);
				
				g.setColor(Color.BLUE); 
				g.setFont(new Font("MV Boli", Font.BOLD, 25));
				g.drawString("Score: "+level,(screen_width- (metrics.stringWidth("Score: "+level))-15), g.getFont().getSize());
				
				g.setColor(Color.RED); 
				g.setFont(new Font("MV Boli", Font.BOLD, 15));
				g.drawString("P to Pause/Play",15, g.getFont().getSize()+35);
				
				g.setColor(Color.BLUE); 
				g.setFont(new Font("MV Boli", Font.BOLD, 25));
				g.drawString("High: "+current_high,15,g.getFont().getSize());		
			}
			else
			{
				gameOver(g);
				timer.stop();
			}	
		}
	}
	public void gameOver(Graphics g)
	{		
		try {
			reader=new Scanner(file);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		High_score=reader.next();
		int high=Integer.parseInt(High_score);
		
		if(level>high)
		{
			g.setColor(Color.GREEN); 
			g.setFont(new Font("MV Boli", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont()); 
			g.drawString("New High Score!",(screen_width- metrics.stringWidth("New High Score"))/2, (g.getFont().getSize())*3);
			
			file.delete(); //Since the file.setWritable is false, I will have to delete the file first then create a new one
			//Since I can't directly Overwrite it
			
			try {
				format=new Formatter(file);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			format.format("%s",String.valueOf(level));
			format.close();
		}
		
		g.setColor(Color.BLUE); 
		g.setFont(new Font("MV Boli", Font.BOLD, 35));
		FontMetrics metrics = getFontMetrics(g.getFont()); 
		g.drawString("Score: "+level,(screen_width- metrics.stringWidth("Score: "+level))/2, g.getFont().getSize());
		
		g.setColor(Color.RED);
		g.setFont(new Font("MV Boli", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont()); 
		g.drawString("Game Over",(screen_width- metrics2.stringWidth("Game Over!"))/2, screen_height/2);
		label.setVisible(false);
	}
	public void moveObstacles()
	{
		if(shooting)
			bulletY-=unit_size;
		for(int i=0;i<rockXlength;i++)
			rockY[i]+=unit_size;
		for(int i=0;i<shooterXlength;i++)
			shooterY[i]+=unit_size;
		for(int i=0;i<bonuslength;i++)
			bonusY[i]+=unit_size;
		for(int i=0;i<shooterXlength;i++)
			bulletshotY[i]=bulletshotY[i]+(2*unit_size);
	}
	@Override
	public void actionPerformed(ActionEvent e) //Timer is very important for this game to work
	{
		if(shooting)
			checkBulletCollision();
		moveObstacles();
		checkRockCollision();
		checkRocketCollision();
		checkShootingCollision();
		repaint();
	}
	
	@Override
	public void keyTyped(KeyEvent e) 
	{
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		if(paused) //So that player cannot move when paused
		{
			switch(e.getKeyCode())
			{
				case 80:
					timer.start();
					paused=false;
					this.setBackground(Color.BLACK);
					label.setVisible(true);
					break;
			}
		}
		else
		{
			if(running) //Only work if program is working
			{
				switch(e.getKeyCode())
				{
					case 37: //The code of left arrow key is KeyEvent.VK_LEFT or 37
						rocketX=rocketX-unit_size;
						label.setLocation(rocketX,rocketY); 
						break;
            
					case 39: //39 is right
						rocketX=rocketX+unit_size;
						label.setLocation(rocketX,rocketY);
						break;
					
					case 32: //32 code for space
						if(bulletshot<1) 
						//bulletshot is so that in the frame one bullet is seen and not the bullet re-spawning at the rocket again
						{
							if(bulletsleft>0)
							{
								bulletsleft--;
								bulletshot++;
								shooting=true;
								getBullet();
							}
						}
						break;
						
					case 65:
						rocketX=rocketX-unit_size;
						label.setLocation(rocketX,rocketY); 
						break;
					
					case 68:
						rocketX=rocketX+unit_size;
						label.setLocation(rocketX,rocketY);
						break;
						
					case 80: //P for pausing the game
						timer.stop();
						paused=true;
						//I am hiding everything otherwise it is easy to keep pausing and seeing where to go
						this.setBackground(Color.GRAY);
						label.setVisible(false);
						break;
				}
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		
	}	
}
