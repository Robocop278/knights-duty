package com.gutierrez.knightsduty;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/*
    Unit class, extending GameObj
    Defines any game object considered a "unit"
    Conceptually, they currently act as their own blueprints, using the "copy" constructor to return a current version of themselves using any modified variables, might change this later though im not sure
    Differentiated through the following features:
    -can move
    -can be selected
    -can be commanded to move
    -can be commanded attack
 */
public abstract class GameUnit extends GameObj
{
    public enum State { //valid states for the unit, determine behaviour at any given moment
        IDLE, MOVING, ATTACK, COOLDOWN
    }
    public enum Facing { //facing of the unit, used to help in drawing correct animations
        N, NE, E, SE, S, SW, W, NW
    }

    private String mUnitName = "ERROR"; //default to this in case I forget to name something

    private float mDestinationx; //TEST: x and y coords of current travel destination

    private float mDestinationy;
    private float mMovementSpeed;
    private boolean mSelected = false;
    private State STATE = State.IDLE; //state of this unit, start in an IDLE state

    private Facing FACING = Facing.SW; //starts facing southwest
    private float mAggroRange = 12.0f; //range at which this unit will automatically seek out enemies to attack

    private int mAttackDamage;
    private float mAttackRange;
    private int mAttackSpeed;
    private GameObj mAttackTarget; //reference to a unit/building that this unit has been commanded to attack
    private int mCooldownTimer;
    public void update() {
        switch(STATE){
            case IDLE:
                //not really much to do, but nice to have in case I change my mind
                break;
            case MOVING:
                //get distance between current pos and dest
                float dist = (float) Math.hypot(getX()-getDestinationx(), getY()-getDestinationy());
                if(dist <= getMovementSpeed()) {
                    //set current pos to dest
                    setX(getDestinationx());
                    setY(getDestinationy());
                    setSTATE(State.IDLE);
                    getSquare().setAnimationSpeed(0);
                    getSquare().setCurrentFrame(0);
                }
                else {
                    float delta_x = getX() - getDestinationx();
                    float delta_y = getY() - getDestinationy();
                    double theta_radians = atan2(delta_y, delta_x);

                    setX((float) (getX() - getMovementSpeed() * cos(theta_radians)));
                    setY((float) (getY() - getMovementSpeed() * sin(theta_radians)));
                }
                break;
            case ATTACK:
                //get distance between current pos and attack target
                dist = (float) Math.hypot(getX()-getAttackTarget().getX(), getY()-getAttackTarget().getY());
                dist -= getWidth() + getAttackTarget().getWidth();
                if(dist <= getAttackRange()) {
                    getAttackTarget().dealDamage(getAttackDamage()); //can probably define a more elegant abstract attack function, to account for different types of attacks (immediate melee vs ranged projectiles needing time to arrive to their target)
                    System.out.println("I HAVE ATTACKED A DUDE");
                    cooldown();
                }
                else {
                    float delta_x = getX() - getAttackTarget().getX();
                    float delta_y = getY() - getAttackTarget().getY();
                    double theta_radians = atan2(delta_y, delta_x);

                    setX((float) (getX() - getMovementSpeed() * cos(theta_radians)));
                    setY((float) (getY() - getMovementSpeed() * sin(theta_radians)));
                }
                break;
            case COOLDOWN:
                //Do cooldown
                if(mCooldownTimer > 0)
                {
                    mCooldownTimer--;
                }
                else{
                    //check if dead(for now this just checks if their health is lower than 0, but can probably check general existence of the object since it will probably remove itself
                    if(getAttackTarget().getHealth() <= 0){
                        setSTATE(State.IDLE); //for now, set to idle, later on we can search for nearby enemies to beat up(using aggro range)
                    }
                    else {
                        setSTATE(State.ATTACK);
                    }
                }
                break;
            default:
                System.out.println("We really shouldn't be here");
                break;
        }
    }

    public void move(float xDest, float yDest){
        setDestinationx(xDest);
        setDestinationy(yDest);
        float delta_x = getX() - getDestinationx();//fetch the initial walking angle, this wont be a problem unless we implement pathfinding which is a hard maybe at this point
        float delta_y = getY() - getDestinationy();
        double theta_degrees = Math.toDegrees(atan2(delta_y, delta_x));
        //larger digits get preference when determining angle
        if(theta_degrees > -157.5 && theta_degrees <= -112.5){
            setFACING(Facing.NW);
        }
        else if(theta_degrees > -112.5 && theta_degrees <= -67.5){
            setFACING(Facing.N);
        }
        else if(theta_degrees > -67.5 && theta_degrees <= -22.5){
            setFACING(Facing.NE);
        }
        else if(theta_degrees > -22.5 && theta_degrees <= 22.5){
            setFACING(Facing.E);
        }
        else if(theta_degrees > 22.5 && theta_degrees <= 67.5 ){
            setFACING(Facing.SE);
        }
        else if(theta_degrees > 67.5 && theta_degrees <= 112.5){
            setFACING(Facing.S);
        }
        else if(theta_degrees > 112.5 && theta_degrees <= 157.5){
            setFACING(Facing.SW);
        }
        else {
            setFACING(Facing.W);
        }
        //set correct animation sheet by getting angle between current pos and destination point
        //should be handled by implementing classes
        getSquare().setAnimationSpeed(0.3f);
        setSTATE(State.MOVING);
    }

    public void attack(GameObj target){
        setAttackTarget(target);
        getSquare().setAnimationSpeed(0.3f);
        setSTATE(State.ATTACK);
    }

    public void cooldown(){
        mCooldownTimer = 200;
        getSquare().setAnimationSpeed(0f);
        setSTATE(State.COOLDOWN);
    }

    public float getDestinationx()
    {
        return mDestinationx;
    }

    public void setDestinationx(float destinationx)
    {
        mDestinationx = destinationx;
    }

    public float getDestinationy()
    {
        return mDestinationy;
    }

    public void setDestinationy(float destinationy)
    {
        mDestinationy = destinationy;
    }

    public float getMovementSpeed()
    {
        return mMovementSpeed;
    }

    public void setMovementSpeed(float movementSpeed)
    {
        mMovementSpeed = movementSpeed;
    }

    public boolean isSelected()
    {
        return mSelected;
    }

    public void setSelected(boolean selected)
    {
        mSelected = selected;
    }

    public int getAttackDamage() {
        return mAttackDamage;
    }

    public void setAttackDamage(int mAttackDamage) {
        this.mAttackDamage = mAttackDamage;
    }

    public float getAttackRange() {
        return mAttackRange;
    }

    public void setAttackRange(float mAttackRange) {
        this.mAttackRange = mAttackRange;
    }

    public int getAttackSpeed() {
        return mAttackSpeed;
    }

    public void setAttackSpeed(int mAttackSpeed) {
        this.mAttackSpeed = mAttackSpeed;
    }

    public State getSTATE()
    {
        return STATE;
    }

    public void setSTATE(State STATE)
    {
        this.STATE = STATE;
    }

    public Facing getFACING()
    {
        return FACING;
    }

    public void setFACING(Facing FACING)
    {
        this.FACING = FACING;
    }

    public float getAggroRange()
    {
        return mAggroRange;
    }

    public void setAggroRange(float aggroRange)
    {
        mAggroRange = aggroRange;
    }

    public GameObj getAttackTarget()
    {
        return mAttackTarget;
    }

    public void setAttackTarget(GameObj attackTarget)
    {
        mAttackTarget = attackTarget;
    }

    public String getUnitName()
    {
        return mUnitName;
    }

    public void setUnitName(String unitName)
    {
        mUnitName = unitName;
    }

    public abstract GameUnit copy();
}
