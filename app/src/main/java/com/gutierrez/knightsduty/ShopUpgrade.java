package com.gutierrez.knightsduty;

/*
    Class to hold data for specific upgrades that will be available to purchase in buildings

 */
public class ShopUpgrade extends ShopItem
{
    private Runnable upgradeCode;

    public ShopUpgrade(Runnable upgradeEffect, String description, int price, int buildTime){
        upgradeCode = upgradeEffect;
        setDescription(description);
        setPrice(price);
        setBuildTime(buildTime);
    }
}
