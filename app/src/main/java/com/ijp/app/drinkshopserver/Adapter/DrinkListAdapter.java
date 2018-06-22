package com.ijp.app.drinkshopserver.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ijp.app.drinkshopserver.Adapter.ViewHolder.DrinkListViewHolder;
import com.ijp.app.drinkshopserver.Interface.IItemClickListner;
import com.ijp.app.drinkshopserver.Model.Drink;
import com.ijp.app.drinkshopserver.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DrinkListAdapter extends RecyclerView.Adapter<DrinkListViewHolder> {

    Context context;
    List<Drink> drinkList;

    public DrinkListAdapter(Context context, List<Drink> drinkList) {
        this.context = context;
        this.drinkList = drinkList;
    }

    @NonNull
    @Override
    public DrinkListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.drink_item_layout,parent,false);
        return new DrinkListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DrinkListViewHolder holder, int position) {
        Picasso.with(context).load(drinkList.get(position).Link).into(holder.imgProduct);
        holder.textDrinkPrice.setText(new StringBuilder("$").append(drinkList.get(position).Price).toString());
        holder.txtDrinkName.setText(drinkList.get(position).Name);

        // anti crash null item click
        holder.setiItemClickListner(new IItemClickListner() {
            @Override
            public void onClick(View view, boolean isLongClick) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return drinkList.size();
    }
}
