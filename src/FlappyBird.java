import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360;
    int boardHeight = 640;

    //Images
    Image backgroundImage;
    Image birdImage;
    Image toppipImage;
    Image bottompipeImage;

    //Bird 
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34;
    int birdHeight = 24;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    //Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }
    
    

    //game logic
    Bird bird;
    Timer gameLoop;
    Timer placePipesTimer;
    int velocityX = -4; //moves the pipes to the left speed (simulates the bird moving right)
    int velocityY = 0;
    int gravity = 1;
    int score = 0;
    boolean end = false;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        //we need these two lines so our keyListener works 
        setFocusable(true);
        addKeyListener(this);

        //load Images
        backgroundImage = new ImageIcon(getClass().getResource("./images/flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./images/flappybird.png")).getImage();
        toppipImage = new ImageIcon(getClass().getResource("./images/toppipe.png")).getImage();
        bottompipeImage = new ImageIcon(getClass().getResource("./images/bottompipe.png")).getImage();

        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        startGame();
    }
    public void startGame() {
        // Initialize/reset variables
        end = false;
        score = 0;
        bird.y = boardHeight / 2;
        velocityY = 0;
        pipes.clear();
        
        // Start game timers
        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();

        placePipesTimer = new Timer(1500, e -> placePipes());
        placePipesTimer.start();
    }

    public void move() {
        //bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // Pipes movement
    for (int i = 0; i < pipes.size(); i += 2) { // Increment by 2 as pipes are in pairs (top & bottom)
        Pipe topPipe = pipes.get(i);
        Pipe bottomPipe = pipes.get(i + 1);
        
        topPipe.x += velocityX;
        bottomPipe.x += velocityX;

        // Collision detection for top pipe
        if (bird.x + bird.width > topPipe.x && bird.x < topPipe.x + topPipe.width) {
            if (bird.y < topPipe.y + topPipe.height || bird.y + bird.height > bottomPipe.y) {
                gameOver();
                break;
            }
        }

        // Remove pipes that are out of screen bounds
        if (topPipe.x + topPipe.width < 0) {
            pipes.remove(i);
            pipes.remove(i); // Remove the bottom pipe at the same index
            i -= 2; // Adjust index due to removal
        }

        //The pipe has been passed
        if (bird.x > topPipe.x + topPipe.width && topPipe.passed == false) {
            topPipe.passed = true;
            score++;
        }
    }

    // Check if bird has fallen below the screen
    if (bird.y > boardHeight) {
        gameOver();
    }

    }


    public void placePipes() {
        //Math.random() gives us a value btw 0-1
        int randomPipeY = (int) (pipeY - pipeHeight/4 - Math.random()*pipeHeight/2);
        int openingSpace = boardHeight/4;
        Pipe topPipe = new Pipe(toppipImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);
        Pipe bottomPipe = new Pipe(bottompipeImage);
        bottomPipe.y = pipeHeight + topPipe.y + openingSpace ;
        pipes.add(bottomPipe);
    }

    public void gameOver() {
        gameLoop.stop();
        placePipesTimer.stop();
        System.out.println("Game Over");
        end = true;
    }

    public void resetGame() {
        if (end) {
            startGame();  // Call startGame to reset the state
            repaint();  // Repaint the panel to update the display
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        //drawing the pipes
        for (int i = 0; i < pipes.size(); i ++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        // Cast to Graphics2D to set font weight
        Graphics2D g2d = (Graphics2D) g;

        // Set the color for the string
        g2d.setColor(Color.WHITE); // Change to any color you want

        // Set the font with desired weight and size
        g2d.setFont(new Font("Arial", Font.BOLD, 20)); // Use Font.BOLD for bold, Font.ITALIC for italic

        // Draw the string with the specified color and font
        g2d.drawString("Score: " + score, 10, 20);

        if (end) {
            g2d.drawString("Game Over", boardWidth/2 - 60, boardHeight/2 + 10);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (!end) {
                velocityY = -9; 
            } else {
                resetGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
