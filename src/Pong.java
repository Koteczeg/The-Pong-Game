import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import javax.swing.*;

public final class Pong extends Applet
{
    private static final long serialVersionUID = 1L;
    private Image offScreenImage;
    private Graphics offScreenGraphics;
    private int pongPositionX;
    private int pongPositionY;
    private int playerRectanglePositionX;
    private int playerRectanglePositionY;
    private int computerRectanglePositionX;
    private int computerRectanglePositionY;

    private int width;
    private int height;

    private int pongVelocityX;
    private int pongVelocityY;

    private int pongWidth;
    private int pongHeight;
    private int rectangleWidth;
    private int rectangleHeight;

    private int playerScore;
    private boolean gameStarted;
    private boolean scoreChanged = true;

    private Timer timerGaming;

    private Font scoreFont = new Font("Consolas", Font.BOLD, 18);
    private Font startingFont = new Font("Times New Roman", Font.BOLD, 50);

    public void init()
    {
	setSize(800, 500);
	Frame c = (Frame) this.getParent().getParent();
	c.setTitle("The Pong Game - Pawe³ Duszak");
	c.setResizable(false);
	Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
	c.setLocation(dim.width / 2 - c.getSize().width / 2,
		dim.height / 2 - c.getSize().height / 2);

	width = getSize().width;
	height = getSize().height;
	setBackground(Color.black);
	rectangleHeight = 120;
	rectangleWidth = 15;
	pongHeight = 30;
	pongWidth = 30;
	addKeyListener(new StartGameKeyListener());
	addMouseMotionListener(new MovePlatformListener());
	playerRectanglePositionX = 35;
	computerRectanglePositionX = width - 35 - rectangleWidth;
	offScreenImage = createImage(width, height);

	offScreenGraphics = offScreenImage.getGraphics();
	offScreenGraphics = offScreenImage.getGraphics();
	startNewGame();
	Runnable myRunnable = new Runnable()
	{
	    public void run()
	    {
		timerGaming = new Timer(10, new ActionListener()
		{
		    public void actionPerformed(ActionEvent e)
		    {
			height = getSize().height;
			width = getSize().width;
			pongPositionX += pongVelocityX;
			pongPositionY += pongVelocityY;
			if (pongPositionY <= 25
				|| pongPositionY + pongHeight >= height)
			{
			    pongVelocityY = -pongVelocityY;
			}
			if (pongPositionX <= playerRectanglePositionX
				+ rectangleWidth
				&& pongPositionY + pongHeight >= playerRectanglePositionY
				&& pongPositionY <= playerRectanglePositionY
					+ rectangleHeight
				&& pongPositionX > playerRectanglePositionX)
			{
			    pongVelocityX = -pongVelocityX;
			    ++playerScore;
			    scoreChanged = true;
			}
			if (pongPositionX + pongWidth >= computerRectanglePositionX
				&& pongPositionY + pongHeight >= computerRectanglePositionY
				&& pongPositionY <= computerRectanglePositionY
					+ rectangleHeight
				&& pongPositionX < computerRectanglePositionX
					+ rectangleWidth)
			{
			    pongVelocityX = -pongVelocityX;
			}
			if (pongVelocityX < 0)
			{
			    if (computerRectanglePositionY + rectangleHeight
				    / 2 != height / 2)
			    {
				if (computerRectanglePositionY
					+ rectangleHeight / 2 > height / 2)
				{
				    computerRectanglePositionY -= -pongVelocityX;
				} else
				{
				    computerRectanglePositionY += -pongVelocityX;
				}
			    }
			} else
			{
			    if (pongPositionY + pongHeight / 2 <= computerRectanglePositionY
				    + rectangleHeight / 2)
			    {
				computerRectanglePositionY -= pongVelocityX;
			    } else
			    {
				computerRectanglePositionY += pongVelocityX;
			    }
			}
			if (computerRectanglePositionY < 0)
			{
			    computerRectanglePositionY = 0;
			}
			if (computerRectanglePositionY + rectangleHeight > height)
			{
			    computerRectanglePositionY = height
				    - rectangleHeight;
			}
			if (pongPositionX + pongWidth < 0)
			{
			    playerRectanglePositionY = height / 2
				    - rectangleHeight / 2;
			    timerGaming.stop();
			    gameStarted = false;
			    playerScore = 0;
			    scoreChanged = true;
			}
			repaint();
		    }
		});
	    }
	};

	Thread thread = new Thread(myRunnable);
	thread.start();
    }

    public void update(Graphics g)
    {
	paint(g);
    }
    
    public void paint(Graphics g)
    {
	offScreenGraphics.clearRect(0, 0, width, height);
	// drawing border
	offScreenGraphics.setColor(Color.YELLOW);
	offScreenGraphics.drawRect(0, 0, width - 1, height - 1);

	// drawing rectangles
	offScreenGraphics.setColor(Color.WHITE);
	offScreenGraphics.fillRect(playerRectanglePositionX,
		playerRectanglePositionY, rectangleWidth, rectangleHeight);
	offScreenGraphics.fillRect(computerRectanglePositionX,
		computerRectanglePositionY, rectangleWidth, rectangleHeight);

	if (gameStarted)
	{
	    offScreenGraphics.setColor(Color.RED);
	    offScreenGraphics.fillArc(pongPositionX, pongPositionY, pongWidth,
		    pongHeight, 0, 360);
	    // printing score
		offScreenGraphics.setColor(Color.YELLOW);
		offScreenGraphics.setFont(scoreFont);
		offScreenGraphics.drawString("Your score: " + playerScore, 20,
			20);
	} else
	{
	    // printing starting text
	    Rectangle2D r = startingFont.getStringBounds("The Pong Game",
		    new FontRenderContext(null,
			    RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT,
			    RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
	    offScreenGraphics.setFont(startingFont);
	    offScreenGraphics.setColor(Color.CYAN);
	    offScreenGraphics.drawString("The Pong Game",
		    (int) (width / 2 - r.getWidth() / 2),
		    (int) (height / 2 - r.getHeight()));
	    offScreenGraphics.setFont(scoreFont);
	    offScreenGraphics.drawString("Press SPACE to start.",
		    (int) (width / 2 - r.getWidth() / 2), height / 2);
	}
	g.drawImage(offScreenImage, 0, 0, this);

    }

    public void startNewGame()
    {
	// computing start positions for both rectangles
	playerRectanglePositionY = height / 2 - rectangleHeight / 2;
	computerRectanglePositionY = playerRectanglePositionY;
	// positioning pong in center of window
	pongPositionX = width / 2 - pongWidth / 2;
	pongPositionY = height / 2 - pongHeight / 2;
	pongVelocityX = 10;
	pongVelocityY = 10;
	playerScore = 0;
    }

    private class MovePlatformListener implements MouseMotionListener {
	@Override
    	public void mouseMoved(MouseEvent e)
    	{
		if (gameStarted == true)
		{
		    int playerPlatformHeight = e.getY();
		    if (playerPlatformHeight + rectangleHeight / 2 > height)
		    {
			playerPlatformHeight = height - rectangleHeight / 2;
		    }
		    if (playerPlatformHeight < rectangleHeight / 2 + 25)
		    {
			playerPlatformHeight = rectangleHeight / 2 + 25;
		    }
		    playerRectanglePositionY = playerPlatformHeight - rectangleHeight
			    / 2;
		    repaint();
		}
    	}

	@Override
	public void mouseDragged(MouseEvent e)
	{}
    }
   

    private class StartGameKeyListener implements KeyListener{
	@Override
	public void keyPressed(KeyEvent e)
	{
		if (Character.isSpaceChar(e.getKeyChar()))
		{
		    gameStarted = true;
		    startNewGame();
		    timerGaming.start();
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{}

	@Override
	public void keyTyped(KeyEvent e)
	{}
    }
}
