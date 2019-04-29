package com.gutierrez.knightsduty;

/*
    Castle class, defines the HQ of the player.
    Will provide basic upgrades and most of the units available for purchase in the game
 */
public class Castle extends GameBuilding
{
    private float gold;

    private float income;
    private final float START_GOLD = 50;
    private final float START_INCOME = 0.015f;
    public Castle(float x, float y, int ownerID){
        setX(x);
        setY(y);
        setWidth(2.5f);
        setHeight(2.5f);
        setSquare(new Square(getWidth(), getHeight()));
        getSquare().setSpritesheet(MyApplication.getAppContext(), R.drawable.castle, 1,1,1,0.0f);
        setSpawnX(getX() + getWidth());
        setSpawnY(getY() + getHeight());

        setGold(START_GOLD);
        setIncome(START_INCOME);

        setOwner(ownerID); //assign this castle its ID

        setHealth(500);
        AddUnitType(new Knight(getSpawnX(),getSpawnY(),1,1, getOwner()),"A basic Knight to do your deeds for you", 10, 500);
        AddUpgrade(new Runnable()
        {
            @Override
            public void run()
            {
                System.out.println("I did a thing");
            }
        }, "Does a thing", 10, 500);
    }

    @Override
    public void update()
    {
        setGold(getGold() + getIncome());
    }

    @Override
    public boolean canPurchase(int index)
    {
        return getGold() > getUnitPrice(index);
    }

    public ShopUnit buildUnit(int i){
        setGold(getGold()-getUnitPrice(i));
        return getUnitListing(i);
    }

    public float getGold()
    {
        return gold;
    }

    public void setGold(float gold)
    {
        this.gold = gold;
    }

    public float getIncome()
    {
        return income;
    }

    public void setIncome(float income)
    {
        this.income = income;
    }
}
