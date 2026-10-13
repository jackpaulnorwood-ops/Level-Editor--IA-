
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
	private Rectangle leftColumn, playView, rightColumn;
	
	public Game() {
		ImageManager.loadImage("brick", "/Images/brick.png");
		ImageManager.loadImage("lava", "/Images/lava.png");
		ImageManager.loadImage("steve", "/Images/steve.png");
		ImageManager.loadImage("blackSquare", "/Images/blackSquare.png");

		new Thread(this).start();	
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		key =-1; 
		x=0;
		y=0;
		obj=new Object(new Rectangle(10,10,50,50), "blackSquare");
		objects=new ArrayList<Object>();
		solids=new ArrayList<Solid>();
		obstacles=new ArrayList<Obstacle>();
		player=new Player(new Rectangle(100,100,50,100), "steve", 5);
		mouseMode="click";

		leftColumn=new Rectangle(10, 10, getWidth()/6-15, getHeight()-20);
		playView=new Rectangle(getWidth()/6+5, 10, (int)(getWidth()*(4.0/6.0)-10), getHeight()-20);
		rightColumn=new Rectangle((int)(getWidth()*(5.0/6.0)+5), 10, getWidth()/6-15, getHeight()-20);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateLayout();
			}
		});
	
	}

	private void updateLayout() {
		int w = getWidth();
		int h = getHeight();

		leftColumn.setBounds(10, 10, w / 6 - 15, h - 20);
		playView.setBounds(w / 6 + 5, 10, (int)(w * 4.0 / 6.0) - 10, h - 20);
		rightColumn.setBounds((int)(w * 5.0 / 6.0) + 5, 10, w / 6 - 15, h - 20);
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

		drawRect(leftColumn, g2d);
		drawRect(playView, g2d);
		drawRect(rightColumn, g2d);
		//g2d.drawImage(obj.getImage(), obj.getRect().x, obj.getRect().y, obj.getRect().width, obj.getRect().height, null);

		drawObjects(g2d);
		drawPlayer(g2d);
		player.move(solids);
		checks();

		g2d.drawString(String.format("(%d, %d)", x, y), x, y);
	
		twoDgraph.drawImage(back, null, 0, 0);

	}

	public void drawObjects(Graphics g) {
		for(Object obj: objects) {
			g.drawImage(ImageManager.get(obj.getImageID()), obj.getRect().x, obj.getRect().y, obj.getRect().width, obj.getRect().height, null);
		}
		/* for(Solid sol: solids) {
			g.setColor(Color.BLACK);
			g.drawImage(sol.getImage(), sol.getRect().x, sol.getRect().y, sol.getRec t().width, sol.getRect().height, null);
		}
		for(Obstacle obs: obstacles) {
			g.setColor(Color.RED);
			g.drawImage(obs.getImage(), obs.getRect().x, obs.getRect().y, obs.getRect().width, obs.getRect().height, null);
		} */
	}

	public void drawPlayer(Graphics g) {
		g.setColor(Color.BLACK);
		g.drawImage(ImageManager.get(player.getImageID()), player.getRect().x, player.getRect().y, player.getRect().width, player.getRect().height, null);
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

	public void drawRect(Rectangle rect, Graphics g) {
		g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	}

	public void drawImage(Rectangle rect, String imageID, Graphics g) {
		g.drawImage(back, x, key, getFocusCycleRootAncestor())
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
		if(mouseMode.equals("click")) {
			Rectangle clickRect=new Rectangle(x, y, 1, 1);
			for(int i = objects.size()-1; i>=0; i--) {
				Object obj = objects.get(i);
				if(clickRect.intersects(obj.getRect())) {
					objects.remove(i);
					break;
				}
			}
		}
		if(mouseMode.equals("solid")) {
			Solid sol=new Solid(new Rectangle(x-25,y-25,50,50), "brick");
			solids.add(sol);
			objects.add(sol);
		}
		if(mouseMode.equals("obstacle")) {
			Obstacle obs=new Obstacle(new Rectangle(x-25,y-25,50,50), "lava");
			obstacles.add(obs);
			objects.add(obs);
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
