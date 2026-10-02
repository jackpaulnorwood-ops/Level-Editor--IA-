import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame{
	
	public Main () {
		super("KeyListener Demo");
		
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = ge.getDefaultScreenDevice();

		if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(this);
        } else {
            // Fallback to maximized window if exclusive fullscreen is unsupported
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setVisible(true);
        }

		Game play = new Game();
		((Component) play).setFocusable(true);
		
		Color RoyalBlue = new Color(22,13,193);
		
		
		setBackground(RoyalBlue);
		
		
		getContentPane().add(play);
		setVisible(true);
		play.requestFocusInWindow();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	

	public static void main(String[] args) {
		Main run = new Main();
		

	}


}
