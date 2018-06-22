package com.ijp.app.drinkshopserver.Adapter.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ijp.app.drinkshopserver.Interface.IItemClickListner;
import com.ijp.app.drinkshopserver.R;

public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    public ImageView imgProduct;
    public TextView txtProduct;

    IItemClickListner iItemClickListner;

    public void setiItemClickListner(IItemClickListner iItemClickListner) {
        this.iItemClickListner = iItemClickListner;
    }

    public MenuViewHolder(View itemView) {
        super(itemView);

        imgProduct=itemView.findViewById(R.id.img_product);
        txtProduct=itemView.findViewById(R.id.txt_menu_name);

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {
        iItemClickListner.onClick(v,false);

    }

    @Override
    public boolean onLongClick(View v) {
        iItemClickListner.onClick(v,true);
        return false;
    }
}
