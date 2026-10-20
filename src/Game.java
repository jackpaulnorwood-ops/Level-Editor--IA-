
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.*; 
import java.util.LinkedHashMap;


public class Game  extends JPanel implements KeyListener, MouseListener, MouseMotionListener{

	
	private BufferedImage back; 
	private int key, x, y, w, h; 
	private Object obj;
	private ArrayList<Object> objects;
	private ArrayList<Solid> solids;
	private ArrayList<Obstacle> obstacles;
	private Player player;
	private String mouseMode;
	private Rectangle leftColumn, playView, rightColumn;
	private Boolean cursorInScreen, gravity;

	private JScrollPane assetScrollPane;
	private JPanel assetPanel;
	private Timer gameTimer;
	private String selectedImageID = "brick";
	
	public Game() {
		setBackground(new Color(22,13,193));

		ImageManager.loadImage("brick", "/Images/brick.png");
		ImageManager.loadImage("lava", "/Images/lava.png");
		ImageManager.loadImage("steve", "/Images/steve.png");
		ImageManager.loadImage("blackSquare", "/Images/blackSquare.png");

		setLayout(null);

		assetPanel = new JPanel();
		assetPanel.setLayout(new BoxLayout(assetPanel, BoxLayout.Y_AXIS));

		assetScrollPane = new JScrollPane(assetPanel);
		assetScrollPane.setVerticalScrollBarPolicy(
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
		);
		assetScrollPane.setHorizontalScrollBarPolicy(
			JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
		);

		add(assetScrollPane);

		updateAssetSidebar();

		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		key =-1; 
		x=0;
		y=0;
		w=0;
		h=0;
		obj=new Object(new Rectangle(10,10,50,50), "blackSquare");
		objects=new ArrayList<Object>();
		solids=new ArrayList<Solid>();
		obstacles=new ArrayList<Obstacle>();
		player=new Player(new Rectangle(500,100,50,100), "steve", 5, 0.5);
		mouseMode="click";
		cursorInScreen=false;
		gravity=true;
		gameTimer = new Timer(16, e -> updateGame());

		leftColumn=new Rectangle(10, 10, w/6-15, h-20);
		playView=new Rectangle(w/6+5, 10, (int)(w*(4.0/6.0)-10), h-20);
		rightColumn=new Rectangle((int)(w*(5.0/6.0)+5), 10, w/6-15, h-20);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateLayout();
			}
		});
		gameTimer.start();
	
	}

	private void updateLayout() {
		w = getWidth();
		h = getHeight();

		leftColumn.setBounds(10, 10, w / 6 - 15, h - 20);
		playView.setBounds(w / 6 + 5, 10, (int)(w * 4.0 / 6.0) - 10, h - 20);
		rightColumn.setBounds((int)(w * 5.0 / 6.0) + 5, 10, w / 6 - 15, h - 20);

		assetScrollPane.setBounds(leftColumn);
	}
	private void updateGame() {
		player.move(solids, gravity);
		checks();
		repaint();
	}
	
	
	
	
	protected void paintComponent(Graphics g){
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setFont( new Font("Courier", Font.PLAIN, 24));
		
		g2d.setColor(Color.BLACK);

		drawRect(leftColumn, g2d);
		drawRect(playView, g2d);
		drawRect(rightColumn, g2d);
		//g2d.drawImage(obj.getImage(), obj.getRect().x, obj.getRect().y, obj.getRect().width, obj.getRect().height, null);

		drawObjects(g2d);
		drawPlayer(g2d);

		g2d.drawString(String.format("(%d, %d)", x, y), x, y);

	}

	public void drawObjects(Graphics g) {
		for(Object obj: objects) {
			drawImage(obj.getImageID(), obj.getRect(), g);
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
		drawImage(player.getImageID(), player.getRect(), g);
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

	public void drawImage(String imageID, Rectangle rect, Graphics g) {
		g.drawImage(ImageManager.get(imageID), (int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight(), null);
	}

	private void updateAssetSidebar() {
		assetPanel.removeAll();

		for (String id : ImageManager.imageKeyList()) {
			BufferedImage image = ImageManager.get(id);

			if (image == null) {
				continue;
			}

			// Create a button for this image
			JButton button = new JButton(id);

			// Scale the thumbnail to fit the button
			Image thumbnail = image.getScaledInstance(
				80, 80, Image.SCALE_SMOOTH
			);

			button.setIcon(new ImageIcon(thumbnail));
			button.setHorizontalTextPosition(
				SwingConstants.CENTER
			);
			button.setVerticalTextPosition(
				SwingConstants.BOTTOM
			);

			button.setAlignmentX(Component.CENTER_ALIGNMENT);

			// Remember which asset the user selected
			button.addActionListener(e -> {
				selectedImageID = id;
				System.out.println("Selected asset: " + id);
			});

			assetPanel.add(button);
			assetPanel.add(Box.createVerticalStrut(5));
		}

		assetPanel.revalidate();
		assetPanel.repaint();
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
		if(keyChar=='u') {
			String id = ImageManager.uploadImage();

			if (id != null) {
				updateAssetSidebar();
			}
		}
		if(keyChar=='g') {
			gravity=!gravity;
		}
	}




//DO NOT DELETE
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
		key= e.getKeyCode();
		System.out.println(key);
		
		player.setMove(e.getKeyChar(), true, gravity);
		System.out.println("key pressed: "+e.getKeyChar());
		
		if (key==KeyEvent.VK_SPACE) {
			player.jump(gravity);
		}
	
	}


	//DO NOT DELETE
	@Override
	public void keyReleased(KeyEvent e) {
		
		player.setMove(e.getKeyChar(), false, gravity);
		
		
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
					solids.remove(obj);
					obstacles.remove(obj);
					break;
				}
			}
		}
		if(mouseMode.equals("solid")) {
			Solid sol=new Solid(new Rectangle(x-25,y-25,50,50), selectedImageID);
			if(playView.contains(sol.getRect())) {
				solids.add(sol);
				objects.add(sol);
			}
		}
		if(mouseMode.equals("obstacle")) {
			Obstacle obs=new Obstacle(new Rectangle(x-25,y-25,50,50), selectedImageID);
			if(playView.contains(obs.getRect())) {
				obstacles.add(obs);
				objects.add(obs);
			}
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
