package com.gutierrez.knightsduty;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ShopItemHolder extends RecyclerView.ViewHolder
{
    private TextView mItemTitle;
    private TextView mItemDescription;
    private ShopItem mShopItem;
    private Button mBuyButton;
    private MainActivity mMainActivity;

    public ShopItemHolder(LayoutInflater inflater, ViewGroup parent, int layout, MainActivity mainActivity)
    {
        super(inflater.inflate(R.layout.shop_item, parent,false));
        mMainActivity = mainActivity;
        mItemTitle = itemView.findViewById(R.id.item_name);
        mItemDescription = itemView.findViewById(R.id.item_description);
        mBuyButton = itemView.findViewById(R.id.buy_button);
    }
    public void bind(ShopItem item, final int index){
        this.mShopItem = item;
        mItemTitle.setText(mShopItem.getName()); //temporary, im a dumbass and need to fix this
        mItemDescription.setText(mShopItem.getDescription());
        mBuyButton.setText("$"+mShopItem.getPrice());
        mBuyButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mMainActivity.notifyUnitPurchase(index);
            }
        });
    }
}
