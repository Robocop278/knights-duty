package com.gutierrez.knightsduty;

/*
    Shop item class for all building types
    General features include:
    -price of item
    -short description
    -time to build/research the unit/upgrade
 */
public abstract class ShopItem
{
    private String mName;
    private String mDescription;
    private int mPrice;
    private int mBuildTime;

    public String getName()
    {
        return mName;
    }

    public void setName(String name)
    {
        mName = name;
    }

    public String getDescription()
    {
        return mDescription;
    }

    public void setDescription(String description)
    {
        mDescription = description;
    }

    public int getPrice()
    {
        return mPrice;
    }

    public void setPrice(int price)
    {
        mPrice = price;
    }

    public int getBuildTime()
    {
        return mBuildTime;
    }

    public void setBuildTime(int buildTime)
    {
        mBuildTime = buildTime;
    }
}
