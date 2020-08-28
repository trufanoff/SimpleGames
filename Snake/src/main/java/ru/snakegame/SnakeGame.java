package ru.snakegame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class SnakeGame {

    final String TITLE_OF_PROGRAM = "Classic Game Snake";
    final String GAME_OVER = "GAME OVER";
    final int POINT_RADIUS = 20;
    final int FIELD_WIDTH = 30;
    final int FIELD_HEIGHT = 20;
    final int FIELD_DX = 6;
    final int FILED_DY = 28;
    final int START_LOCATION = 200;
    final int START_SNAKE_SIZE = 6;
    final int START_SNAKE_X = 10;
    final int START_SNAKE_Y = 10;
    final int SHOW_DELAY = 150;
    final int LEFT = 37;
    final int UP = 38;
    final int RIGHT = 39;
    final int DOWN = 40;
    final int START_DIRECTION = RIGHT;
    final Color DEFAULT_COLOR = Color.black;
    final Color FOOD_COLOR = Color.GREEN;
    final Color POISON_COLOR = Color.RED;
    Snake snake;
    Food food;
    JFrame frame;
    Canvas canvasPanel;
    Random random = new Random();
    boolean gameOver = false;

    public static void main(String[] args) {
        new SnakeGame().go();
    }

    void go() {
        frame = new JFrame(TITLE_OF_PROGRAM + " : " + START_SNAKE_SIZE);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(FIELD_WIDTH * POINT_RADIUS + FIELD_DX, FIELD_HEIGHT * POINT_RADIUS + FILED_DY);
        frame.setLocation(START_LOCATION, START_LOCATION);
        frame.setResizable(false);

        canvasPanel = new Canvas();
        canvasPanel.setBackground(Color.white);
        frame.getContentPane().add(BorderLayout.CENTER, canvasPanel);
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                snake.setDirection(e.getKeyCode());
            }
        });

        frame.setVisible(true);

        snake = new Snake(START_SNAKE_X, START_SNAKE_Y, START_SNAKE_SIZE, START_DIRECTION);
        food = new Food();

        while (!gameOver) {
            snake.move();
            if (food.isEaten()) {
                food.next();
            }
            canvasPanel.repaint();
            try {
                Thread.sleep(SHOW_DELAY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class Snake {
        ArrayList<Point> snake = new ArrayList<>();
        int direction;

        public Snake(int x, int y, int length, int direction) {
            for (int i = 0; i < length; i++) {
                Point point = new Point(x - i, y);
                snake.add(point);
            }
            this.direction = direction;
        }

        public void setDirection(int direction) {
            if (direction >= LEFT && direction <= DOWN) {
                if (Math.abs(this.direction - direction) != 2) {
                    this.direction = direction;
                }
            }
        }

        boolean isFood(Point food) {
            return ((snake.get(0).getX() == food.getX()) && snake.get(0).getY() == food.getY());
        }

        void move() {
            int x = snake.get(0).getX();
            int y = snake.get(0).getY();

            if (direction == LEFT) x--;
            if (direction == RIGHT) x++;
            if (direction == UP) y--;
            if (direction == DOWN) y++;

            if (x > FIELD_WIDTH - 1) x = 0;
            if (x < 0) x = FIELD_WIDTH - 1;
            if (y > FIELD_HEIGHT - 1) y = 0;
            if (y < 0) y = FIELD_HEIGHT - 1;

            gameOver = isInsideSnake(x, y);

            snake.add(0, new Point(x, y));

            if (isFood(food)) {
                food.eat();
                frame.setTitle(TITLE_OF_PROGRAM + " : " + snake.size());
            } else {
                snake.remove(snake.size() - 1);
            }
        }

        void paint(Graphics g) {
            for (Point point : snake) {
                point.paint(g);
            }
        }

        public boolean isInsideSnake(int x, int y) {
            for (Point point : snake) {
                if (point.getX() == x && point.getY() == y) {
                    return true;
                }
            }
            return false;
        }

    }

    class Food extends Point {

        public Food() {
            super(-1, -1);
            this.color = FOOD_COLOR;
        }

        public void eat() {
            this.setXY(-1, -1);
        }

        boolean isEaten() {
            return this.getX() == -1;
        }

        public void next() {
            int x;
            int y;
            do {
                x = random.nextInt(FIELD_WIDTH);
                y = random.nextInt(FIELD_HEIGHT);
            } while (snake.isInsideSnake(x, y));
            this.setXY(x, y);
        }

    }

    class Point {
        private int x;
        private int y;
        Color color = DEFAULT_COLOR;

        public Point(int x, int y) {
            setXY(x, y);
        }

        void setXY(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void paint(Graphics g) {
            g.setColor(color);
            g.fillOval(x * POINT_RADIUS, y * POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
//            g.fillRect(x*POINT_RADIUS, y*POINT_RADIUS, POINT_RADIUS, POINT_RADIUS);
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }

    public class Canvas extends JPanel {
        @Override
        public void paint(Graphics g) {
            super.paint(g);
            snake.paint(g);
            food.paint(g);
        }
    }
}
