package com.gutierrez.knightsduty;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
    Building class, extending GameObj
    Defines game objects that classify as "buildings", differentiated as follows:
    -cannot move
    -touching will (probably) bring up some sort of shop interface (available items list)
    -has a building/upgrade queue
    -list of available units to purchase
    -spawn location for units
 */
public abstract class GameBuilding extends GameObj
{
    private float mSpawnX;
    private float mSpawnY;
//    private Queue<ShopItem> mBuildQueue = new LinkedList<>();
    private ArrayList<ShopUnit> mUnitList = new ArrayList<>();
    private ArrayList<ShopUpgrade> mAvailableUpgrades = new ArrayList<>();

    public void AddUnitType(GameUnit newUnit, String description, int price, int buildTime){
        ShopUnit newShopUnit = new ShopUnit(newUnit, description, price, buildTime);
        mUnitList.add(newShopUnit);
    }

    public void AddUpgrade(Runnable upgradeCode, String description, int price, int buildTime){
        ShopUpgrade newShopUpgrade = new ShopUpgrade(upgradeCode, description, price, buildTime);
    }

    public GameUnit getUnitFromList(int i){
        return mUnitList.get(i).unitInstance().copy();
    }

    public ShopUnit getUnitListing(int i){
        return mUnitList.get(i);
    }

    public int getUnitPrice(int i){
        return mUnitList.get(i).getPrice();
    }

    public ArrayList<ShopUnit> getUnitList()
    {
        return mUnitList;
    }

    public void setUnitList(ArrayList<ShopUnit> unitList)
    {
        mUnitList = unitList;
    }

    public ArrayList<ShopUpgrade> getAvailableUpgrades()
    {
        return mAvailableUpgrades;
    }

    public void setAvailableUpgrades(ArrayList<ShopUpgrade> availableUpgrades)
    {
        mAvailableUpgrades = availableUpgrades;
    }

    public float getSpawnX()
    {
        return mSpawnX;
    }

    public void setSpawnX(float spawnX)
    {
        mSpawnX = spawnX;
    }

    public float getSpawnY()
    {
        return mSpawnY;
    }

    public void setSpawnY(float spawnY)
    {
        mSpawnY = spawnY;
    }

    public abstract void update();

    public abstract boolean canPurchase(int index);
}
