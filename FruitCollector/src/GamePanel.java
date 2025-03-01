import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.*;
    /*
        image by Naetipoom
    */
public class GamePanel extends JPanel implements KeyListener, ActionListener {
    //width and height scale
    public static final int WIDTH = 480, HEIGHT = 854;

    //timer
    private Timer timer;

    //1 minute timer
    private int timeLeft = 60;

    //score
    private int score = 0;

    //high score
    private int highScore = 0;

    //basket image
    private Image basket;

    //starting basket position X
    private int basketX;

    //starting basket position Y
    private final int basketY = HEIGHT - 100;

    //basket speed when moving left and right
    private final int basketSpeed = 20;

    //array to store fruit
    private ArrayList<Fruit> fruits;

    //random for fruit falling speed
    private Random random;

    //max fruit for when fruit falling on screen will be max at 7
    private final int MAX_FRUITS = 7;

    //constructor
    public GamePanel() {

        //set size for panel
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        //make focusable for keyboard input
        setFocusable(true);

        //make code in addkeylistener listen for keyevent
        addKeyListener(this);

        //basket picture
        basket = new ImageIcon("res/basket.png").getImage();

        //basket position in X
        basketX = WIDTH / 2 - 50;

        //create new arraylist name 'fruits' and 'random'
        fruits = new ArrayList<>();
        random = new Random();

        //make timer 1000ms/60 = 60fps, draw game in 60fps
        timer = new Timer(1000 / 60, this);

        //call loadhighscore method that read highscore number in the textfile
        loadHighScore();

        //start timer
        timer.start();

        //create new timer that goes down by 1000ms (1 sec) when the game start
        new Timer(1000, e -> timeLeft--).start();
    }

    @Override
    public void paintComponent(Graphics g) {
        //ensure background and basket is rendered before fruit
        super.paintComponent(g);

        //draw background position and basket position
        g.drawImage(new ImageIcon("res/bg.png").getImage(), 0, 0, WIDTH, HEIGHT, this);
        g.drawImage(basket, basketX, basketY, 100, 60, this);

        //draw fruit in fruits array position
        for (Fruit fruit : fruits) {
            g.drawImage(fruit.image, fruit.x, fruit.y, 50, 50, this);
        }

        //draw score text and time left text in black color
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Time: " + timeLeft, 20, 30);
        g.drawString("Score: " + score, 20, 60);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //if time run out draw endgame screen
        if (timeLeft <= 0) {
            endGame();
            return;
        }

        //chance to add fruit on screen if fruits is under max fruit
        if (random.nextInt(100) < 5 && fruits.size() < MAX_FRUITS) {
            fruits.add(new Fruit());
        }

        //check when fruit go below screen, if yes then delete fruit
        for (int i = 0; i < fruits.size(); i++) {
            Fruit fruit = fruits.get(i);
            fruit.y += fruit.speed;
            if (fruit.y > HEIGHT) {
                fruits.remove(i);
                i--;

            //if fruit touch basket then add score and remove fruit
            } else if (fruit.y + 50 >= basketY && fruit.x >= basketX && fruit.x <= basketX + 100) {
                score += 10;
                fruits.remove(i);
                i--;
            }
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //if press left arrow make basket move left
        if (e.getKeyCode() == KeyEvent.VK_LEFT && basketX > 0) {
            basketX -= basketSpeed;

        //if press right arrow make basket move right
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && basketX < WIDTH - 100) {
            basketX += basketSpeed;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

        //end game popup
        private void endGame() {

            //stop timer
            timer.stop();

            //if current score higher than past highscore, then run savehighscore method
            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }

            //show game over popup
            int option = JOptionPane.showOptionDialog(this,
                    "Game Over!\nScore: " + score + "\nHigh Score: " + highScore,
                    "Game Over",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,

                    //restart and exit button
                    new String[]{"Restart", "Exit"},
                    "Restart");

            //if press restart run restart method
            if (option == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                //if press exit then exit game, end process
                System.exit(0);
            }
        }

        //restart game method
        private void restartGame() {

            //clear everything and reset position of basket, then start timer again
            score = 0;
            timeLeft = 60;
            fruits.clear();
            basketX = WIDTH / 2 - 50;
            timer.start();
        }


        //load highscore from number in textfile
        private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            highScore = Integer.parseInt(reader.readLine());
        } catch (Exception ignored) {}
    }

    //save highscore in textfile
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException ignored) {}
    }

    private class Fruit {
        //fruit image, position and speed
        int x, y, speed;
        Image image;

        public Fruit() {

            //fruit position in X within game panel, -50 so it dont spawn at the border
            x = random.nextInt(WIDTH - 50);
            y = 0;

            //random fruit speed
            speed = random.nextInt(5) + 2;

            //random image selected from 3 picture in string
            String[] fruitImages = {"res/apple.png", "res/banana.png", "res/orange.png"};
            image = new ImageIcon(fruitImages[random.nextInt(fruitImages.length)]).getImage();
        }
    }

    public static void main(String[] args) {

        //main panel of game
        //title of game panel
        JFrame frame = new JFrame("Fruit Collector");
        GamePanel panel = new GamePanel();
        frame.add(panel);
        frame.pack();

        //press close to exit
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //load game panel in center
        frame.setLocationRelativeTo(null);
        //set visible
        frame.setVisible(true);
    }
}
