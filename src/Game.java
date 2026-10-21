
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.*; 
import java.util.LinkedHashMap;


public class Game  extends JPanel implements MouseListener, MouseMotionListener{

	
	private BufferedImage back; 
	private int key, x, y, w, h; 
	private Object obj, selectedObject;
	private Level level;
	private Player player;
	private String mouseMode;
	private Rectangle leftColumn, playView, rightColumn;
	private boolean cursorInScreen, gravity, playMode;

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

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		key =-1; 
		x=0;
		y=0;
		w=0;
		h=0;
		obj=new Object(new Rectangle(10,10,50,50), "blackSquare");
		selectedObject=null;
		level=new Level();
		player=new Player(new Point(500, 100), 50, 100, "steve", 5, 1);
		mouseMode="click";
		cursorInScreen=false;
		gravity=true;
		playMode=false;

		setupKeyBindings();

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
		if(playMode) {
			player.move(level.getSolids(), gravity);
			checks();
		}

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
		drawSelection(g2d);

		drawPlayer(g2d);

		g2d.drawString(String.format("(%d, %d)", x, y), x, y);

	}

	public void drawObjects(Graphics g) {
		for(Object obj: level.getObjects()) {
			drawImage(obj.getImageID(), obj.getRect(), g);
		}
	}

	private void drawSelection(Graphics g) {
		if(selectedObject!=null&&mouseMode.equals("click")) {
			Rectangle rect=selectedObject.getRect();

			g.setColor(Color.YELLOW);
			g.drawRect(rect.x-1, rect.y-1, rect.width+2, rect.height+2);
		}
	}

	public void drawPlayer(Graphics g) {
		g.setColor(Color.BLACK);
		drawImage(player.getImageID(), player.getRect(), g);
	}

	public void resetPlayer() {
		player.setX(player.getSpawn().x);
		player.setY(player.getSpawn().y);
	}

	public void checks() {
		for(Obstacle obs: level.getObstacles()) {
			if(player.getRect().intersects(obs.getRect())) {
				resetPlayer();
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

	private void setupKeyBindings() {
		InputMap input = getInputMap(
			JComponent.WHEN_IN_FOCUSED_WINDOW
		);
		ActionMap actions = getActionMap();

		// Movement: A and D
		bindKey(input, actions, "pressed A", "leftPressed",
			() -> player.setMove('a', true, gravity));
		bindKey(input, actions, "released A", "leftReleased",
			() -> player.setMove('a', false, gravity));

		bindKey(input, actions, "pressed D", "rightPressed",
			() -> player.setMove('d', true, gravity));
		bindKey(input, actions, "released D", "rightReleased",
			() -> player.setMove('d', false, gravity));

		// Vertical movement when gravity is disabled
		bindKey(input, actions, "pressed W", "upPressed",
			() -> player.setMove('w', true, gravity));
		bindKey(input, actions, "released W", "upReleased",
			() -> player.setMove('w', false, gravity));

		bindKey(input, actions, "pressed S", "downPressed",
			() -> player.setMove('s', true, gravity));
		bindKey(input, actions, "released S", "downReleased",
			() -> player.setMove('s', false, gravity));

		// Jump
		bindKey(input, actions, "pressed SPACE", "jump",
			() -> player.jump(gravity));

		// Editor modes
		bindKey(input, actions, "pressed 0", "deleteMode",
			() -> mouseMode = "click");
		bindKey(input, actions, "pressed 1", "solidMode",
			() -> mouseMode = "solid");
		bindKey(input, actions, "pressed 2", "obstacleMode",
			() -> mouseMode = "obstacle");

		// Other controls
		bindKey(input, actions, "pressed G", "toggleGravity",
			() -> gravity = !gravity);
		bindKey(input, actions, "pressed P", "togglePlay",
			() -> playMode = !playMode);
		bindKey(input, actions, "pressed U", "uploadImage",
			() -> {
				String id = ImageManager.uploadImage();
				if (id != null) {
					updateAssetSidebar();
				}
			});
	}

	private void bindKey(
		InputMap input,
		ActionMap actions,
		String keyStroke,
		String actionName,
		Runnable action
	) {
		input.put(KeyStroke.getKeyStroke(keyStroke), actionName);

		actions.put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				action.run();
			}
		});
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		int mouseX=e.getX();
		int mouseY=e.getY();

		if(selectedObject!=null) {
			Rectangle objRect=selectedObject.getRect();

			if(mouseMode.equals("click")&&objRect.contains(mouseX, mouseY)) {
				selectedObject.setX(mouseX-objRect.width/2);
				selectedObject.setY(mouseY-objRect.height/2);
			}
		}

		repaint();
	}



	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		x=e.getX();
		y=e.getY();
		obj.setX(x);
		obj.setY(y);
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int mouseX=e.getX();
		int mouseY=e.getY();

		if(mouseMode.equals("click")) {
			selectedObject=null;

			for(int i = level.getObjects().size()-1; i>=0; i--) {
				Object obj = level.getObjects().get(i);

				if(obj.getRect().contains(mouseX, mouseY)) {
					selectedObject=obj;
					break;
				}
			}
		}

		if(mouseMode.equals("solid")) {
			Solid sol=new Solid(new Rectangle(mouseX-25,mouseY-25,50,50), selectedImageID);

			if(playView.contains(sol.getRect())) {
				level.addSolid(sol);
			}
		}

		if(mouseMode.equals("obstacle")) {
			Obstacle obs=new Obstacle(new Rectangle(mouseX-25,mouseY-25,50,50), selectedImageID);

			if(playView.contains(obs.getRect())) {
				level.addObstacle(obs);
			}
		}

		repaint();
	}



	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("entered");
	}



	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		System.out.println("exited");
	}



	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
		System.out.println("you clicked at"+ e.getY());
		x=e.getX();
		y=e.getY();
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	

	
}
