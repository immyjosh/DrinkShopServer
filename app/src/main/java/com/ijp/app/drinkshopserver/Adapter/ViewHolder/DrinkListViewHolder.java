package com.ijp.app.drinkshopserver.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijp.app.drinkshopserver.Interface.IItemClickListner;
import com.ijp.app.drinkshopserver.R;

public class DrinkListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgProduct;
    public TextView txtDrinkName,textDrinkPrice;

    IItemClickListner iItemClickListner;

    public IItemClickListner getiItemClickListner() {
        return iItemClickListner;
    }

    public void setiItemClickListner(IItemClickListner iItemClickListner) {
        this.iItemClickListner = iItemClickListner;
    }

    public DrinkListViewHolder(View itemView) {
        super(itemView);

        imgProduct=itemView.findViewById(R.id.img_product);
        txtDrinkName=itemView.findViewById(R.id.txt_drink_name);
        textDrinkPrice=itemView.findViewById(R.id.txt_price);
    }

    @Override
    public void onClick(View v) {
        iItemClickListner.onClick(v,false);
    }
}
