package com.example.krishajivani.myapplication;

import android.content.Intent;
import android.graphics.Point;
import android.media.Image;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout myLayout;

    private int layoutHeight, layoutWidth; //height and width of screen

    private ImageView redSquare, redSquare2, redSquare3, greenSquare, greenSquare2, greenSquare3, purpleCircle; //images (purpleCircle is the player, redSquares should be avoided, greenSquares give the players points if hit)

    private float purpleX, purpleY, purpleMoveNum, originalPurpY;

    private float points;

    private boolean isGameOver = false, isPaused = false;

    private TextView scoreTxtView;

    private Handler handler = new Handler(); //communicates between UI thread and other background threads, basically it's the middle man: it recieves messages and then runs code to execute these messages.
    private Timer timer;//creates timer object

    private ArrayList<Squares> squareFeatures = new ArrayList<>();
    private ArrayList<ImageView> squareList = new ArrayList<>();

    @Override
    public void onPause(){ //when this activity is paused (user is on the Menu Activity)
        super.onPause();
        isPaused = true;
    }

    @Override
    public void onResume(){ //when this is the activity the user is currently on
        super.onResume();
        isPaused = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myLayout = (RelativeLayout)findViewById(R.id.myLayout); //assigns myLayout as the xml layout (the screen) object.

        scoreTxtView = (TextView)findViewById(R.id.scoreTxtView); //score textView

        //assigns the images to the declared ImageViews.
        redSquare = (ImageView)findViewById(R.id.redSquare);
        greenSquare = (ImageView)findViewById(R.id.greenSquare);
        redSquare2 = (ImageView)findViewById(R.id.redSquare2);
        greenSquare2 = (ImageView)findViewById(R.id.greenSquare2);
        redSquare3 = (ImageView)findViewById(R.id.redSquare3);
        greenSquare3 = (ImageView)findViewById(R.id.greenSquare3);
        purpleCircle = (ImageView)findViewById(R.id.purpleCircle);

        squareList.add(redSquare);
        squareList.add(redSquare2);
        squareList.add(redSquare3);
        squareList.add(greenSquare);
        squareList.add(greenSquare2);
        squareList.add(greenSquare3);


        for (int i = 0; i < 3; i++){ //red squares start from right of canvas
            squareFeatures.add(new Squares(0, 0, 10, "red"));
        }
        for (int i = 0; i < 3; i++){ //green squares start from left of canvas
            squareFeatures.add(new Squares(0, 0, -10, "green"));
        }

        purpleMoveNum = -10; //the number by which the x-coordinate of the image changes (shifts this many to the right or left)

        //In order to get  screen size x and y coordinates:
        WindowManager windowManager = getWindowManager(); //handles window transitions and animations (when rotating phone or opening/closing app)
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point(); //point object stores the coordinates (x and y) of a specific point
        display.getSize(point); //finds the farthest point (bottom right) on the display, and those x and y coordinates describe the length and width of the display
        layoutHeight = point.y;
        layoutWidth = point.x;

        //Set starting locations of the ImageViews out of the screen
        purpleCircle.setX(layoutWidth/2 - purpleCircle.getWidth());
        purpleCircle.setY(layoutHeight - purpleCircle.getHeight()-100);
        purpleY = purpleCircle.getY();
        purpleX = purpleCircle.getX();
        originalPurpY = purpleY;

        //Timer function
        timer = new Timer();
        timer.schedule(new TimerTask() { //helps tranform the images across the screen.
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isPaused){ //if you are currently on THIS activity (this activity is not paused)
                            if (isGameOver){
                                scoreTxtView.setText("GAME OVER, YOUR SCORE: " + points);
                                reset();
                            }
                            else{
                                scoreTxtView.setText("CURRENT SCORE: " + points);
                                moveImages(); //function that moves the images a specific amount every execution.
                                movePurple();
                                collision();
                            }
                        }

                    }
                });
            }
        },0,20); //delay describes the amount of time that passes before the timer executes for the first time, period is the gap time between each execution.

        myLayout.setOnTouchListener(new View.OnTouchListener() { //controls the actions that occur when the layout is touched (pressed down on or released).
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN) { //when the screen (layout) is pressed down on.
                    //Toast.makeText(getApplicationContext(), "pressed", Toast.LENGTH_SHORT).show();
                    purpleMoveNum = purpleMoveNum/3; //slows down speed of player

                } else if(event.getAction() == MotionEvent.ACTION_UP) { //when the screen (layout) is released.
                    //Toast.makeText(getApplicationContext(), "released", Toast.LENGTH_SHORT).show();
                    purpleMoveNum = purpleMoveNum * 3; //brings speed back to normal

                }
                return true;
            }
        });

    }

    public void reset(){ //resets game if player loses.
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        intent.putExtra("score", points);

        points = 0;
        isGameOver = false;
        squareFeatures.clear();
        squareList.clear();

        squareList.add(redSquare);
        squareList.add(redSquare2);
        squareList.add(redSquare3);
        squareList.add(greenSquare);
        squareList.add(greenSquare2);
        squareList.add(greenSquare3);


        for (int i = 0; i < 3; i++){ //red squares start from right of canvas
            squareFeatures.add(new Squares(0, 0, 10, "red"));
        }
        for (int i = 0; i < 3; i++){ //green squares start from left of canvas
            squareFeatures.add(new Squares(0, 0, -10, "green"));
        }

        purpleMoveNum = -10; //the number by which the x-coordinate of the image changes (shifts this many to the right or left)
        purpleCircle.setX(layoutWidth/2 - purpleCircle.getWidth());
        purpleCircle.setY(layoutHeight - purpleCircle.getHeight()-100); //-185
        purpleY = purpleCircle.getY();
        purpleX = purpleCircle.getX();

        startActivity(intent);
    }

    public void moveImages(){ //moves red and green squares back and forth horizontally as the "obstacles" of the game
        for (int i = 0; i < squareList.size(); i++){
            squareFeatures.get(i).changeX();

            if (squareList.get(i).getX() > layoutWidth){ //out of screen on the right side
                squareFeatures.get(i).changeX(layoutWidth); //x-coordinate is set equal to the layoutWidth
                squareFeatures.get(i).changeDirection();//changes x-direction of the square
                squareFeatures.get(i).changeY((float)Math.floor(Math.random() * (layoutHeight - squareList.get(i).getHeight()))); //random y-coordinate (height will be different when the square reenters the screen)
            }
            else if (squareList.get(i).getX() + squareList.get(i).getWidth() < 0){ //out of screen on the left side
                squareFeatures.get(i).changeX(-squareList.get(i).getWidth()); //x-coordinate is set equal to -1*square width
                squareFeatures.get(i).changeDirection();//changes x-direction of the square
                squareFeatures.get(i).changeY((float)Math.floor(Math.random() * (layoutHeight - squareList.get(i).getHeight()))); //random y-coordinate (height will be different when the square reenters the screen)
            }

            squareList.get(i).setX(squareFeatures.get(i).returnX());
            squareList.get(i).setY(squareFeatures.get(i).returnY());


        }
    }

    public void movePurple(){ //moves player, or the purple circle, up and down the screen.
        purpleY += purpleMoveNum;

        if (purpleCircle.getY() + purpleCircle.getHeight() > originalPurpY){ //touches screen edge on the bottom
            purpleY = originalPurpY - purpleCircle.getHeight();
            purpleMoveNum *= -1; //changes y-direction of the player
            points += 20; //player gets 20 points every time they reach a side of the device, i.e., "the walls" (top and bottom)

            for (int i = 0; i < squareFeatures.size(); i++){ //changes speed of squares every time the player hits a "wall".
                squareFeatures.get(i).changMoveNum(2);
            }

        }
        else if (purpleCircle.getY() < 0){ //touches screen edge on the top
            purpleY = 0;
            purpleMoveNum *= -1; //changes y-direction of the player
            points += 20;

            for (int i = 0; i < squareFeatures.size(); i++){ //changes speed of squares every time the player hits a "wall".
                squareFeatures.get(i).changMoveNum(2);
            }
        }

        purpleCircle.setX(purpleX);
        purpleCircle.setY(purpleY);
    }

    public void collision(){ //if the purple circle and the squares collide
        for (int i = 0; i < squareList.size(); i++){
            if (squareList.get(i).getX() + squareList.get(i).getWidth() > purpleX && squareList.get(i).getX() < purpleX + purpleCircle.getWidth() && squareList.get(i).getY() + squareList.get(i).getHeight() > purpleY && squareList.get(i).getY() < purpleCircle.getHeight() + purpleY){
                if (squareFeatures.get(i).returnType().equals("red")){ //red square
                    //System.out.println("Start Over");
                    isGameOver = true;
                }
                else{ //green square
                    //sets image outside of the screen so it seems like it "disappeared" on contact
                    squareList.get(i).setX(-80.0f);
                    squareList.get(i).setY(-80.0f);

                    //removes the square from lists
                    squareList.remove(squareList.get(i));
                    squareFeatures.remove(squareFeatures.get(i));

                    //give player points
                    points +=5;

                }
            }
        }
    }
}
