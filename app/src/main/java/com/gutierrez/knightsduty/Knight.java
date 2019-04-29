package com.gutierrez.knightsduty;

public class Knight extends GameUnit
{
    private static float KNIGHT_MAX_SPEED = 0.05f;
    private static int KNIGHT_MAX_HEALTH = 25;
    private static int KNIGHT_ATTACK_DAMAGE = 5;
    private static float KNIGHT_ATTACK_RANGE = 0.6f;
    private static int KNIGHT_ATTACK_SPEED = 300; //probably in milliseconds? not sure, will confirm later

    public Knight(float newX, float newY, float newWidth, float newHeight, int owner) {
        setUnitName("Knight");
        setOwner(owner);
        setWidth(newWidth);
        setHeight(newHeight);
        setSquare(new Square(getWidth(), getHeight()));
        getSquare().setSpritesheet(MyApplication.getAppContext(), R.drawable.blue_knight_sw, 7,3,3,0.0f);
        setX(newX);
        setY(newY);
        setDestinationx(getX());
        setDestinationy(getY());
        setMovementSpeed(KNIGHT_MAX_SPEED);
        setHealth(KNIGHT_MAX_HEALTH);
        setAttackDamage(KNIGHT_ATTACK_DAMAGE);
        setAttackRange(KNIGHT_ATTACK_RANGE);
        setAttackSpeed(KNIGHT_ATTACK_SPEED);
    }

    /*
        This Knight constructor will probably be the most used constructor.
        The Castle will hold create its own Knight at the start using the primary constructor,
            and "build" new Knights using its own as a template.
        This will allow upgrades to modify the stats defined above, then create actual improved units through cloning
     */
    public Knight(Knight copyKnight){
        setUnitName("Knight");
        setOwner(copyKnight.getOwner());
        setWidth(copyKnight.getWidth());
        setHeight(copyKnight.getHeight());
        setSquare(new Square(getWidth(),getHeight()));
        getSquare().setSpritesheet(MyApplication.getAppContext(), R.drawable.blue_knight_sw, 7,3,3,0.0f);
        setX(copyKnight.getX());
        setY(copyKnight.getY());
        setDestinationx(getX());
        setDestinationy(getY());
        setMovementSpeed(copyKnight.getMaxKnightSpeed());
        setHealth(copyKnight.getKnightMaxHealth());
        setAttackDamage(copyKnight.getKnightAttackDamage());
        setAttackRange(copyKnight.getKnightAttackRange());
        setAttackSpeed(copyKnight.getKnightAttackSpeed());
    }

    @Override
    public void move(float xDest, float yDest){
        super.move(xDest,yDest);
        switch(getFACING()){
            case N:
                System.out.println("Walking North");
                break;
            case NE:
                System.out.println("Walking NorthEast");
                break;
            case E:
                System.out.println("Walking East");
                break;
            case SE:
                System.out.println("Walking SouthEast");
                break;
            case S:
                System.out.println("Walking South");
                break;
            case SW:
                System.out.println("Walking SouthWest");
                break;
            case W:
                System.out.println("Walking West");
                break;
            case NW:
                System.out.println("Walking NorthWest");
                break;
        }
    }

    public float getMaxKnightSpeed(){
        return KNIGHT_MAX_SPEED;
    }

    public static int getKnightMaxHealth() {
        return KNIGHT_MAX_HEALTH;
    }

    public static void setKnightMaxHealth(int knightMaxHealth) {
        KNIGHT_MAX_HEALTH = knightMaxHealth;
    }

    public static int getKnightAttackDamage() {
        return KNIGHT_ATTACK_DAMAGE;
    }

    public static void setKnightAttackDamage(int knightAttackDamage) {
        KNIGHT_ATTACK_DAMAGE = knightAttackDamage;
    }

    public static float getKnightAttackRange() {
        return KNIGHT_ATTACK_RANGE;
    }

    public static void setKnightAttackRange(float knightAttackRange) {
        KNIGHT_ATTACK_RANGE = knightAttackRange;
    }

    public static int getKnightAttackSpeed() {
        return KNIGHT_ATTACK_SPEED;
    }

    public static void setKnightAttackSpeed(int knightAttackSpeed) {
        KNIGHT_ATTACK_SPEED = knightAttackSpeed;
    }


    @Override
    public GameUnit copy() {
        return new Knight(this);
    }
}
