package com.gutierrez.knightsduty;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopItemHolder>
{
    private ArrayList<ShopUnit> mShopItems;
    private MainActivity mMainActivity;

    public ShopAdapter(ArrayList<ShopUnit> items, MainActivity mainActivity){
        this.mShopItems = items;
        mMainActivity = mainActivity;
    }

    @NonNull
    @Override
    public ShopItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(mMainActivity.getApplication());
        return new ShopItemHolder(layoutInflater, viewGroup, 0, mMainActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopItemHolder shopItemHolder, int position)
    {
        ShopItem item = mShopItems.get(position);
        shopItemHolder.bind(item, position);
    }

    @Override
    public int getItemViewType(int position) { //can use this to maybe assign the different buy button actions
//        if (mShopItems.get(position) instanceof ShopUnit) {
//            return 1;
//        } else if(mShopItems.get(position) instanceof ShopUpgrade){
//            return 0;
//        }
//        else {
//            return -1;
//        }
        return 0;
    }

    @Override
    public int getItemCount()
    {
        return mShopItems.size();
    }
}
