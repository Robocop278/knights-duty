package com.gutierrez.knightsduty;
import android.media.Image;

/*
    General game object class
    contains general data that each game object should have:
    -x and y coords
    -GL Square object
    -width and height
    -health
    -basic touch detection
 */
public abstract class GameObj
{
    private Square mSquare;

    private float x; //absolute locations of game object
    private float y;
    private float mWidth; //Width and Height assigned to mSquare object
    private float mHeight;
    private int mOwner; //integer ID identifying which player this object belongs to
    private int mHealth; //Health of the game object

    public Square getSquare()
    {
        return mSquare;
    }

    public void setSquare(Square square)
    {
        mSquare = square;
    }

    public float getWidth()
    {
        return mWidth;
    }

    public void setWidth(float width)
    {
        mWidth = width;
    }

    public float getHeight()
    {
        return mHeight;
    }

    public void setHeight(float height)
    {
        mHeight = height;
    }

    public float getX()
    {
        return x;
    }

    public void setX(float x)
    {
        this.x = x;
    }

    public float getY()
    {
        return y;
    }

    public void setY(float y)
    {
        this.y = y;
    }

    public int getOwner() {
        return mOwner;
    }

    public void setOwner(int mOwner) {
        this.mOwner = mOwner;
    }

    public int getHealth()
    {
        return mHealth;
    }

    public void setHealth(int health)
    {
        mHealth = health;
    }

    public void dealDamage(int damage) {
        setHealth(getHealth()-damage);
        System.out.println("Oof ouch I was attacked my health is now " + getHealth());
        if(getHealth() <= 0){
            System.out.println("That is it I am done that was the last straw I am truly finished");
        }
    }

    public void draw(float[] mvpMatrix)
    {
        mSquare.draw(mvpMatrix);
    }

    //currently has hardcoded values, should think of a smarter solution for this
    public boolean touched(float xIn, float yIn)
    {
        System.out.println("Tested object's box boundaries:");
        System.out.println("["+(x- mWidth)+","+(x+ mWidth)+"]");
        System.out.println("["+(y- mHeight)+","+(y+ mHeight)+"]");
        return ((xIn<x+ mWidth) && (xIn>x- mWidth) && (yIn<y+ mHeight) && (yIn>y- mHeight));
    }
}
