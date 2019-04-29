package com.gutierrez.knightsduty;

/*
    Class to store data about the type of units that will be available for purchase from buidlings
    -GameUnit associated with this item
 */
public class ShopUnit extends ShopItem
{
    private GameUnit mUnitType;

    public ShopUnit(GameUnit assignedUnit, String description, int price, int buildTime){
        mUnitType = assignedUnit;
        setName(assignedUnit.getUnitName());
        setDescription(description);
        setPrice(price);
        setBuildTime(buildTime);
    }

    public GameUnit unitInstance(){
        return mUnitType;
    }
}
