import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
/*import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;*/

import javax.swing.*;

@SuppressWarnings("serial")
public class GameFrame extends JFrame implements ActionListener
{
	//URL myImage;
	Image logo;
	ImageIcon rocket;
	File image=new File("icon.png");
	
	JButton replay;
	GamePanel panel;
	public GameFrame()
	{
		if(image.exists())
		{
			rocket=new ImageIcon("icon.png");
			this.setIconImage(rocket.getImage()); 
			//So that on the corner of the JFrame & on the Taskbar the rocket logo comes instead of java's
		}
		/*else
		 * .exe was not working with java.net but .jar file was, but I could not find the problem so for now I am putting this in comments
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
			
			if(connected)
			{
				try {
					myImage=new URL("https://miro.medium.com/max/186/1*xxiMKPx_ZnlH_pE6eLpr7w.png");
					logo=ImageIO.read(myImage);
					rocket=new ImageIcon(logo);
					
					this.setIconImage(rocket.getImage()); 
					//So that on the corner of the JFrame & on the Taskbar the rocket logo comes instead of java's
				} 
				catch (MalformedURLException e) {
					
				} 
				catch (IOException e) {
					
				}
			}
		}*/
		
		panel=new GamePanel();
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		panel.setFocusable(true); //This line is very important if I don't put this it doesn't work
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		this.setResizable(false);
		this.add(panel);
		this.setTitle("Dodge");
		this.setSize(515,620);
		//this.pack();
		//.pack() should always come after everything 
		this.setLocationRelativeTo(null);
		// .setLocationRelativeTo should come after .pack()
		
		replay=new JButton("Replay");
		replay.setFont(new Font("MV Boli", Font.BOLD, 30));
		replay.setBackground(new Color(255,255,255));
		replay.setForeground(Color.BLUE);
		replay.setFocusable(false);
		replay.addActionListener(this);
		this.add(replay,BorderLayout.SOUTH);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{

		if(e.getSource()==replay)
		{
			this.dispose();
			new GameFrame();
		}
	}
}
