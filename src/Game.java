
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.*; 


public class Game  extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener{

	
	private BufferedImage back; 
	private int key, x, y; 
	private Object obj;
	private ArrayList<Object> objects;
	private ArrayList<Solid> solids;
	private ArrayList<Obstacle> obstacles;
	private Player player;
	private String mouseMode, movementMode;
	
	public Game() {
		new Thread(this).start();	
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		key =-1; 
		x=0;
		y=0;
		obj=new Object(new Rectangle(10,10,50,50));
		solids=new ArrayList<Solid>();
		obstacles=new ArrayList<Obstacle>();
		player=new Player(new Rectangle(100,100,50,100), 5);
		mouseMode="click";
	
	}

	
	
	public void run()
	   {
	   	try
	   	{
	   		while(true)
	   		{
	   		   Thread.currentThread();
			   Thread.sleep(5);
	            repaint();
	         }
	      }
	   		catch(Exception e)
	      {
	      }
	  	}
	

	
	
	
	public void paint(Graphics g){
		
		Graphics2D twoDgraph = (Graphics2D) g; 
		if( back ==null)
			back=(BufferedImage)( (createImage(getWidth(), getHeight()))); 
		

		Graphics g2d = back.createGraphics();
	
		g2d.clearRect(0,0,getSize().width, getSize().height);
	
		g2d.setFont( new Font("Courier", Font.PLAIN, 24));
		
		g2d.setColor(Color.BLACK);

		g2d.drawRect(10, 10, getWidth()/6-15, getHeight()-20);
		g2d.drawRect(getWidth()/6+5, 10, (int)(getWidth()*(4.0/6.0)-10), getHeight()-20);
		g2d.drawRect((int)(getWidth()*(5.0/6.0)+5), 10, getWidth()/6-15, getHeight()-20);
		g2d.fillRect(obj.getRect().x, obj.getRect().y, obj.getRect().width, obj.getRect().height);

		drawObjects(g2d);
		drawPlayer(g2d);
		player.move(solids);
		checks();

		g2d.drawString(String.format("(%d, %d)", x, y), x, y);
	
		twoDgraph.drawImage(back, null, 0, 0);

	}

	public void drawObjects(Graphics g) {
		for(Solid sol: solids) {
			g.setColor(Color.BLACK);
			g.fillRect(sol.getRect().x, sol.getRect().y, sol.getRect().width, sol.getRect().height);
		}
		for(Obstacle obs: obstacles) {
			g.setColor(Color.RED);
			g.fillRect(obs.getRect().x, obs.getRect().y, obs.getRect().width, obs.getRect().height);
		}
	}

	public void drawPlayer(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(player.getRect().x, player.getRect().y, player.getRect().width, player.getRect().height);
	}

	public void checks() {
		/* if(player.getRect().intersects(obj.getRect())) {
			System.out.println("collision");
		} */
		for(Solid sol: solids) {
			if(player.getRect().intersects(sol.getRect())) {
				System.out.println("collision (solid)");
			}
		}
		for(Obstacle obs: obstacles) {
			if(player.getRect().intersects(obs.getRect())) {
				System.out.println("collision (obstacle)");
				player.setX(100);
				player.setY(100);
			}
		}
	}

	//DO NOT DELETE
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		char keyChar = e.getKeyChar();
		if(keyChar=='0') {
			mouseMode="click";
		}
		if(keyChar=='1') {
			mouseMode="solid";
		}
		if(keyChar=='2') {
			mouseMode="obstacle";
		}
	}




//DO NOT DELETE
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		key= e.getKeyCode();
		System.out.println(key);
		
		player.setMove(e.getKeyChar(), true);
		System.out.println("key pressed: "+e.getKeyChar());
		
	
	}


	//DO NOT DELETE
	@Override
	public void keyReleased(KeyEvent e) {
		
		player.setMove(e.getKeyChar(), false);
		
		
	}



	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		x=arg0.getX();
		y=arg0.getY();
		obj.setX(x);
		obj.setY(y);
	}



	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		if(mouseMode.equals("solid")) {
			solids.add(new Solid(new Rectangle(x,y,50,50)));
		}
		if(mouseMode.equals("obstacle")) {
			obstacles.add(new Obstacle(new Rectangle(x,y,50,50)));
		}
	}



	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("entered");
	}



	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println("exited");
	}



	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		System.out.println("you clicked at"+ arg0.getY());
		x=arg0.getX();
		y=arg0.getY();
		
	}



	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	

	
}
