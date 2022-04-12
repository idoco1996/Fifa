import javax.swing.*;
import java.awt.*;

public class GameScene extends JPanel {

    public static final int GAME_SPEED = 6, SCORE_BOARD_WIDTH = 260, SCORE_BOARD_HEIGHT = 160,RESTART_WIDTH=80,RESTART_HEIGHT=20,
            START_GOAL_SPEED=20,REDUCE_SPEED=3,MAX_SPEED=4,GOAL_IN_GAME = 10;

    private Player player;
    private Stadium stadium;
    private Ball ball;
    private BackSound backSound;
    private ScoreBoard scoreBoard;
    private JButton exit;
    private boolean run;
    private JButton gameOver;
    private int goalSpeed;


    public GameScene(int x, int y, int width, int height,JButton main) {
        this.setBackground(Color.green);
        this.setLayout(null);
        this.setBounds(x, y, width, height);
        this.setDoubleBuffered(true);
        this.run=true;
        this.gameOver=main;
        this.goalSpeed=START_GOAL_SPEED;
        this.scoreBoard=new ScoreBoard(x+Stadium.BOUND_X,Stadium.BOUND_Y+Stadium.BOUND_HEIGHT-SCORE_BOARD_HEIGHT,SCORE_BOARD_WIDTH,SCORE_BOARD_HEIGHT);
        this.add(this.scoreBoard);
        this.backSound=new BackSound();
        this.backSound.backSound();
        this.stadium = new Stadium();
        this.player = new Player(this.getX() + this.getWidth() / 2, this.getY() + this.getHeight() / 2);
        this.ball = new Ball(this.player.legsX(), this.player.legsY());
       //this.stadium.goalMovement(this.ball);
        this.exit =new JButton("Exit");
        this.exit.setBounds(Stadium.BOUND_X+Stadium.BOUND_WIDTH-RESTART_WIDTH,Stadium.BOUND_Y+Stadium.BOUND_HEIGHT-RESTART_HEIGHT,RESTART_WIDTH,RESTART_HEIGHT);
        this.add(this.exit);
        this.exit.addActionListener((event)->{
            this.gameOver();


        });

        this.goalMovement();
        this.gameLoop();
        this.setVisible(true);
    }
    private void backgroundSound(){
        Thread thread=new Thread(()->{

        });
        thread.start();
    }
    private void goalMovement(){
        Thread t2=new Thread(()->{
            boolean goalMovement = true;
            while (this.run) {
                if (goalMovement) {
                    this.stadium.goalMoveRight();
                } else {
                    this.stadium.goalMoveLeft();
                }
                if (reachRightBound(this.stadium.getGoal().getX(),this.stadium.getGoal().getWidth())) {
                    goalMovement = false;
                } else if (reachLeftBound(this.stadium.getGoal().getX())) {
                    goalMovement = true;
                }
                try {
                    repaint();
                    Thread.sleep(this.goalSpeed);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });t2.start();
    }

    private void gameLoop() {
        Thread t1 = new Thread(() -> {
            keyControl();
            boolean shoot = false;
            while (this.run) {
                switch (this.player.getDirection()) {
                    case Player.RIGHT:
                        this.player.moveRight();
                        break;
                    case Player.LEFT:
                        this.player.moveLeft();
                        break;
                }
                if (reachRightBound(this.player.getLocation(),this.player.getBodyWidth())) {
                    this.player.moveLeft();
                } else if (reachLeftBound(this.player.getLocation())) {
                    this.player.moveRight();
                }
                if (!shoot) {
                    this.ball.dribble(this.player.legsX(), this.player.legsY());
                }
                if (this.ball.getDirection() == Ball.UP) {
                    shoot = true;
                    this.ball.goUp();
                }
                if (this.ball.getYLocation() == this.stadium.getBoundY() - 5) {
                    shoot = false;
                    this.ball.setDirection(Ball.NONE);
                }
                if (ballGetEnd()&&isGoal()) {
                    System.out.println("goal");
                    this.scoreBoard.addGoal();
                    if (this.goalSpeed>MAX_SPEED){
                        this.goalSpeed-=3;
                    }else this.goalSpeed=MAX_SPEED;
                }else if (ballGetEnd()&&!isGoal()){
                    System.out.println("Missed");
                    this.scoreBoard.lessFault();
                }
                if (this.scoreBoard.getGoals()== GOAL_IN_GAME){
                    System.out.println("victory!  game over");
                    this.gameOver();

                }else if (this.scoreBoard.getFault()==0){
                    System.out.println("game over");
                    this.gameOver();

                }
                repaint();
                try {
                    Thread.sleep(GAME_SPEED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t1.start();
    }
    private boolean ballGetEnd(){
        return this.ball.getYLocation() == this.stadium.getBoundY();
    }
    private boolean isGoal(){
        return this.ball.getXLocation() > this.stadium.getGoalX() && this.ball.getXLocation() < this.stadium.getGoalX() + this.stadium.getGoalWidth();
    }

    private boolean reachRightBound(int objectLocation,int objectWidth){
        return objectLocation == Stadium.BOUND_X + Stadium.BOUND_WIDTH - objectWidth;
    }
    private boolean reachLeftBound(int objectLocation){
        return objectLocation == Stadium.BOUND_X;
    }

    public void keyControl() {
        KeyControl keyControl = new KeyControl(this.player, this.ball);
        this.setFocusable(true);
        this.requestFocus();
        this.addKeyListener(keyControl);
    }

    public void gameOver(){
        run=false;
        this.setVisible(false);
        this.scoreBoard.setVisible(false);
        this.stadium.setVisible(false);
        this.gameOver.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g;
        graphics.setStroke(new BasicStroke(3));
        super.paintComponent(g);
        this.stadium.paint(g);
        this.ball.paint(g);
        this.player.paint(g);



    }


}