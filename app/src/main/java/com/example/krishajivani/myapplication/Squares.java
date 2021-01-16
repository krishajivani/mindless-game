package com.example.krishajivani.myapplication;

public class Squares {

    private float x;
    private float y;
    private float moveNum;
    private String type;

    public Squares(float x, float y, float moveNum, String type){
        this.x = x;
        this.y = y;
        this.moveNum = moveNum;
        this.type = type;
    }

    public float returnX(){
        return x;
    }

    public float returnY(){
        return y;
    }

    public float returnMoveNum(){
        return moveNum;
    }

    public String returnType(){
        return type;
    }

    public void changeX(){ //makes the square move "moveNum" amount in the x-direction
        x += moveNum;
    }

    public void changeX(float input){ //sets x-coordinate equal to whatever is inputted in as a parameter
        x = input;
    }

    public void changeY(float input){ //sets y-coordinate equal to whatever is inputted in as a parameter
        y = input;
    }

    public void changeDirection(){ //changes direction of square from left-right to right-left
        moveNum *=-1;
    }

    public void changMoveNum(float input){ //to change the speed, or moveNum, of the squares
        moveNum += input;
    }


}
