import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.awt.event.*; 
import java.util.LinkedHashMap;


public class Game  extends JPanel implements MouseListener, MouseMotionListener{

	
	private BufferedImage back; 
	private int key, x, y, w, h; 
	private GameObject obj, selectedObject;
	private Level level;

	private Player player;
	private Point playerStartPosition = new Point(500, 100);
	private Point playerStartSize = new Point(50, 100);

	private String mouseMode;
	private Rectangle leftColumn, playView, rightColumn;
	private boolean gravity, playMode;

	private String resizing="";
	private boolean dragging=false;
	private int dragOffsetX, dragOffsetY;

	private Rectangle topSizer, bottomSizer, leftSizer, rightSizer;

	private JScrollPane assetScrollPane;
	private JPanel assetPanel;
	private JButton playButton;
	private JPanel propertiesPanel;
	private JTextField xField, yField, widthField, heightField;

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

		playButton = new JButton("Play");
		playButton.setFocusable(false);

		playButton.addActionListener(e -> togglePlayMode());

		add(playButton);

		this.addMouseListener(this);
		this.addMouseMotionListener(this);

		key =-1; 
		x=0;
		y=0;
		w=0;
		h=0;
		obj=new GameObject(new Rectangle(10,10,50,50), "blackSquare");
		selectedObject=null;
		level=new Level();
		player=new Player(playerStartPosition, playerStartSize.x, playerStartSize.y, "steve", 5, 1);
		mouseMode="click";
		gravity=true;
		playMode=false;

		setupKeyBindings();

		gameTimer = new Timer(16, e -> updateGame());

		leftColumn=new Rectangle(10, 10, w/6-15, h-20);
		playView=new Rectangle(w/6+5, 10, (int)(w*(4.0/6.0)-10), h-20);
		rightColumn=new Rectangle((int)(w*(5.0/6.0)+5), 10, w/6-15, h-20);

		createPropertiesPanel();
		updateLayout();

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

		playButton.setBounds(rightColumn.x+10, rightColumn.y+10, rightColumn.width-20, 35);
		propertiesPanel.setBounds(rightColumn.x+10, rightColumn.y+60, rightColumn.width-20, 270);

		revalidate();
		repaint();
	}

	private void togglePlayMode() {
		playMode = !playMode;

		resetPlayer();

		if (playMode) {
			playButton.setText("Stop");
		} else {
			playButton.setText("Play");
		}

		updatePropertiesPanel();

		repaint();
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

		if (playMode) {
			drawPlayer(g2d);
		}

		g2d.drawString(String.format("(%d, %d)", x, y), x, y);

	}

	public void drawObjects(Graphics g) {
		for(GameObject obj: level.getObjects()) {
			drawImage(obj.getImageID(), obj.getRect(), g);
		}
	}

	private void drawSelection(Graphics g) {
		if(selectedObject!=null) {
			Rectangle rect=selectedObject.getRect();

			if(mouseMode.equals("click")||mouseMode.equals("resize")) {
				g.setColor(Color.YELLOW);
				g.drawRect(rect.x-1, rect.y-1, rect.width+2, rect.height+2);
			}

			if(mouseMode.equals("resize")) {
				g.fillRect(rect.x+rect.width/2-4, rect.y-4, 8, 8);
				g.fillRect(rect.x+rect.width/2-4, rect.y+rect.height-4, 8, 8);
				g.fillRect(rect.x-4, rect.y+rect.height/2-4, 8, 8);
				g.fillRect(rect.x+rect.width-4, rect.y+rect.height/2-4, 8, 8);
			}
		}
	}

	public void drawPlayer(Graphics g) {
		g.setColor(Color.BLACK);
		drawImage(player.getImageID(), player.getRect(), g);
	}

	public void updateSizer() {
		if(selectedObject!=null) {
			Rectangle rect=selectedObject.getRect();
			topSizer=new Rectangle(rect.x+rect.width/2-4, rect.y-4, 8, 8);
			bottomSizer=new Rectangle(rect.x+rect.width/2-4, rect.y+rect.height-4, 8, 8);
			leftSizer=new Rectangle(rect.x-4, rect.y+rect.height/2-4, 8, 8);
			rightSizer=new Rectangle(rect.x+rect.width-4, rect.y+rect.height/2-4, 8, 8);
		}
		else {
			topSizer=bottomSizer=leftSizer=rightSizer=null;
		}
	}

	public void resetPlayer() {
		player = new Player(
			new Point(playerStartPosition),
			playerStartSize.x,
			playerStartSize.y,
			"steve",
			5,
			1
		);

		repaint();
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
			button.setFocusable(false);

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

	private void createPropertiesPanel() {
		propertiesPanel = new JPanel(new BorderLayout(5, 5));
		propertiesPanel.setBackground(Color.LIGHT_GRAY);
		propertiesPanel.setBorder(
			BorderFactory.createTitledBorder("Object Properties")
		);

		JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 5, 5));

		fieldsPanel.add(new JLabel("X:"));
		xField = new JTextField("0", 5);
		fieldsPanel.add(xField);

		fieldsPanel.add(new JLabel("Y:"));
		yField = new JTextField("0", 5);
		fieldsPanel.add(yField);

		fieldsPanel.add(new JLabel("Width:"));
		widthField = new JTextField("50", 5);
		fieldsPanel.add(widthField);

		fieldsPanel.add(new JLabel("Height:"));
		heightField = new JTextField("50", 5);
		fieldsPanel.add(heightField);

		propertiesPanel.add(fieldsPanel, BorderLayout.CENTER);

		JButton applyButton = new JButton("Apply");
		applyButton.setFocusable(false);
		applyButton.addActionListener(e -> applyProperties());

		propertiesPanel.add(applyButton, BorderLayout.SOUTH);

		add(propertiesPanel);

		updatePropertiesPanel();

		propertiesPanel.revalidate();
		propertiesPanel.repaint();
	}

	private void updatePropertiesPanel() {
		boolean enabled = selectedObject != null && !playMode;

		xField.setEnabled(enabled);
		yField.setEnabled(enabled);
		widthField.setEnabled(enabled);
		heightField.setEnabled(enabled);

		if (selectedObject == null) {
			xField.setText("");
			yField.setText("");
			widthField.setText("");
			heightField.setText("");
			return;
		}

		Rectangle rect = selectedObject.getRect();

		xField.setText(String.valueOf(rect.x));
		yField.setText(String.valueOf(rect.y));
		widthField.setText(String.valueOf(rect.width));
		heightField.setText(String.valueOf(rect.height));
	}

	private void applyProperties() {
		if (selectedObject == null || playMode) {
			return;
		}

		try {
			int x = Integer.parseInt(xField.getText());
			int y = Integer.parseInt(yField.getText());
			int width = Integer.parseInt(widthField.getText());
			int height = Integer.parseInt(heightField.getText());

			if (width <= 0 || height <= 0) {
				JOptionPane.showMessageDialog(
					this, "Width and height must be positive."
				);
				return;
			}

			Rectangle rect = selectedObject.getRect();

			Rectangle newRect = new Rectangle(x, y, width, height);
			newRect = keepInsideLevel(newRect);

			rect.setBounds(newRect);
			updatePropertiesPanel();
			repaint();

			repaint();

		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
				this, "Please enter valid whole numbers."
			);
		}
	}

	private void saveLevel() {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle("Save Level");

    // Give the file a default name
    chooser.setSelectedFile(new File("level.txt"));

    int result = chooser.showSaveDialog(this);

    if (result == JFileChooser.APPROVE_OPTION) {
        File file = chooser.getSelectedFile();

        try (PrintWriter writer = new PrintWriter(file)) {

            for (GameObject obj : level.getObjects()) {
                Rectangle rect = obj.getRect();
                String type;

                if (obj instanceof Solid) {
                    type = "SOLID";
                } else if (obj instanceof Obstacle) {
                    type = "OBSTACLE";
                } else {
                    type = "OBJECT";
                }

                writer.println(
                    type + "," +
                    obj.getImageID() + "," +
                    rect.x + "," +
                    rect.y + "," +
                    rect.width + "," +
                    rect.height
                );
            }

            JOptionPane.showMessageDialog(
                this, "Level saved successfully!"
            );

        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this, "Error saving level: " + e.getMessage()
            );
        }
    }
}
	
	private void loadLevel() {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Load Level");

		int result = chooser.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			// Load into a temporary level first
			Level loadedLevel = new Level();

			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;

				while ((line = reader.readLine()) != null) {
					String[] parts = line.split(",");

					if (parts.length != 6) {
						throw new IOException("Invalid line in level file.");
					}

					String type = parts[0];
					String imageID = parts[1];

					int x = Integer.parseInt(parts[2]);
					int y = Integer.parseInt(parts[3]);
					int width = Integer.parseInt(parts[4]);
					int height = Integer.parseInt(parts[5]);

					Rectangle rect = new Rectangle(x, y, width, height);

					if (type.equals("SOLID")) {
						Solid sol = new Solid(rect, imageID);
						loadedLevel.addSolid(sol);

					} else if (type.equals("OBSTACLE")) {
						Obstacle obs = new Obstacle(rect, imageID);
						loadedLevel.addObstacle(obs);

					} else if (type.equals("OBJECT")) {
						GameObject obj = new GameObject(rect, imageID);
						loadedLevel.addObject(obj);

					} else {
						throw new IOException("Unknown object type: " + type);
					}
				}

				// Replace the current level only after loading succeeds
				level = loadedLevel;
				selectedObject = null;
				repaint();

				JOptionPane.showMessageDialog(this, "Level loaded successfully!");

			} catch (IOException | NumberFormatException e) {
				JOptionPane.showMessageDialog(
					this,
					"Error loading level: " + e.getMessage()
				);
			}
		}
	}

	private Rectangle keepInsideLevel(Rectangle rect) {
		int minX = playView.x;
		int minY = playView.y;
		int maxX = playView.x + playView.width;
		int maxY = playView.y + playView.height;

		int width = Math.min(rect.width, playView.width);
		int height = Math.min(rect.height, playView.height);

		int x = Math.max(minX, Math.min(rect.x, maxX - width));
		int y = Math.max(minY, Math.min(rect.y, maxY - height));

		return new Rectangle(x, y, width, height);
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
			() -> {System.out.println("jump key triggered"); player.jump(gravity);});

		// Editor modes
		bindKey(input, actions, "pressed 0", "deleteMode",
			() -> mouseMode = "click");
		bindKey(input, actions, "pressed 1", "solidMode",
			() -> mouseMode = "solid");
		bindKey(input, actions, "pressed 2", "obstacleMode",
			() -> mouseMode = "obstacle");
		bindKey(input, actions, "pressed 3", "resizeMode",
			() -> mouseMode = "resize");

		// Other controls
		bindKey(input, actions, "pressed G", "toggleGravity",
			() -> gravity = !gravity);
		bindKey(input, actions, "pressed P", "togglePlay",
			() -> playButton.doClick());
		bindKey(input, actions, "pressed U", "uploadImage",
			() -> {
				String id = ImageManager.uploadImage();
				if (id != null) {
					updateAssetSidebar();
				}
			});
		bindKey(input, actions, "pressed R", "resetPlayer",
    		() -> resetPlayer());
		bindKey(input, actions, "ctrl S", "saveLevel",
    		() -> saveLevel());
		bindKey(input, actions, "ctrl O", "loadLevel",
    		() -> loadLevel());
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
		if (playMode) {
			return;
		}

		int mouseX=e.getX();
		int mouseY=e.getY();

		if(dragging&&selectedObject!=null) {
			selectedObject.setX(mouseX-dragOffsetX);
			selectedObject.setY(mouseY-dragOffsetY);

			Rectangle corrected = keepInsideLevel(selectedObject.getRect());
			selectedObject.getRect().setBounds(corrected);
		}

		if(selectedObject!=null) {
			Rectangle rect = selectedObject.getRect();

			if (resizing.equals("right") && selectedObject != null) {
				rect.width = Math.max(10, mouseX - rect.x);

				Rectangle corrected = keepInsideLevel(selectedObject.getRect());
				selectedObject.getRect().setBounds(corrected);
			}

			else if (resizing.equals("bottom") && selectedObject != null) {
				rect.height = Math.max(10, mouseY - rect.y);
				Rectangle corrected = keepInsideLevel(selectedObject.getRect());
				selectedObject.getRect().setBounds(corrected);
			}

			else if (resizing.equals("left")) {
				int rightEdge = rect.x + rect.width;

				rect.x = Math.min(mouseX, rightEdge - 10);
				rect.width = rightEdge - rect.x;

				Rectangle corrected = keepInsideLevel(selectedObject.getRect());
				selectedObject.getRect().setBounds(corrected);
			}

			else if (resizing.equals("top")) {
				int bottomEdge = rect.y + rect.height;

				rect.y = Math.min(mouseY, bottomEdge - 10);
				rect.height = bottomEdge - rect.y;

				Rectangle corrected = keepInsideLevel(selectedObject.getRect());
				selectedObject.getRect().setBounds(corrected);
			}
		}

		updatePropertiesPanel();

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
				GameObject obj = level.getObjects().get(i);

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

		updatePropertiesPanel();

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
		if (playMode) {
			return;
		}

		int mouseX=e.getX();
		int mouseY=e.getY();

		if(selectedObject==null) {
			return;
		}

		updateSizer();

		if(mouseMode.equals("resize")) {
			if (rightSizer.contains(mouseX, mouseY)) {
            	resizing = "right";
			} else if (bottomSizer.contains(mouseX, mouseY)) {
				resizing = "bottom";
			} else if (leftSizer.contains(mouseX, mouseY)) {
				resizing = "left";
			} else if (topSizer.contains(mouseX, mouseY)) {
				resizing = "top";
			}
		}

		if(mouseMode.equals("click")&&selectedObject.getRect().contains(mouseX, mouseY)) {
			Rectangle rect=selectedObject.getRect();

			dragOffsetX=mouseX-rect.x;
			dragOffsetY=mouseY-rect.y;

			dragging=true;
		}

		
		
	}



	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		dragging=false;
		resizing="";
	}
	
	
	

	
}
