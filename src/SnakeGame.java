import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    private final int boardWidth;
    private final int boardHeight;
    private static final int TILE_SIZE = 25;

    //Snake
    private Tile snakeHead;
    private List<Tile> snakeBody;

    // Food
    private Tile food;
    private Random random;

    // Game logic
    private Timer gameLoop;
    private int velocityX;
    private int velocityY;
    private boolean gameOver = false;

    public SnakeGame(int boardWidth, int boardHeight) {
        this.boardHeight = boardHeight;
        this.boardWidth = boardWidth;

        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.black);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        // Grid
//        g.setColor(Color.DARK_GRAY);
//        for (int i = 0; i < boardWidth / TILE_SIZE; ++i) {
//            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, boardHeight);
//            g.drawLine(0, i * TILE_SIZE, boardWidth, i * TILE_SIZE);
//        }

        // Food
        g.setColor(Color.red);
        g.fill3DRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);


        // Snake head
        g.setColor(Color.green);
        g.fill3DRect(snakeHead.x * TILE_SIZE, snakeHead.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);

        // Snake body
        for (Tile snakePart : snakeBody) {
            g.fill3DRect(snakePart.x * TILE_SIZE, snakePart.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game over: " + String.valueOf(snakeBody.size()), TILE_SIZE - 16, TILE_SIZE);
        } else {
            g.drawString("Score: " + String.valueOf(snakeBody.size()), TILE_SIZE - 16, TILE_SIZE);
        }
    }

    private void placeFood() {
        food.x = random.nextInt(boardWidth / TILE_SIZE);
        food.y = random.nextInt(boardHeight / TILE_SIZE);
    }

    private void move() {
        // Eat food
        if (checkCollision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        // Snake Body
        for (int i = snakeBody.size() - 1; i >= 0; --i) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile prevSnakePart = snakeBody.get(i - 1);
                snakePart.x = prevSnakePart.x;
                snakePart.y = prevSnakePart.y;
            }
        }

        // Set new coordinates
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game over conditions
        for (Tile snakePart : snakeBody) {
            if (checkCollision(snakeHead, snakePart)) {
                gameOver = true;
                break;
            }
        }
        if (snakeHead.x * TILE_SIZE < 0 || snakeHead.x * TILE_SIZE > boardWidth ||
                snakeHead.y * TILE_SIZE < 0 || snakeHead.y * TILE_SIZE > boardHeight) {
            gameOver = true;
        }
    }

    private boolean checkCollision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
    }

    // Do not need
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
