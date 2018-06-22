package com.ijp.app.drinkshopserver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ijp.app.drinkshopserver.Adapter.ViewHolder.MenuViewHolder;
import com.ijp.app.drinkshopserver.DrinkListActivity;
import com.ijp.app.drinkshopserver.Interface.IItemClickListner;
import com.ijp.app.drinkshopserver.Model.Category;
import com.ijp.app.drinkshopserver.R;
import com.ijp.app.drinkshopserver.UpdateCategory;
import com.ijp.app.drinkshopserver.Utils.Common;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuViewHolder> {

    Context context;
    List<Category> categoryList;

    public MenuAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(context).inflate(R.layout.menu_item_layout,parent,false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, final int position) {
        Picasso.with(context)
                .load(categoryList.get(position).Link)
                .into(holder.imgProduct);

        holder.txtProduct.setText(categoryList.get(position).Name);

        holder.setiItemClickListner(new IItemClickListner() {
            @Override
            public void onClick(View view, boolean isLongClick) {
                if (isLongClick)
                {
                    Common.currentCategory=categoryList.get(position);
                    context.startActivity(new Intent(context, UpdateCategory.class));

                }
                else {

                    Common.currentCategory=categoryList.get(position);
                    context.startActivity(new Intent(context, DrinkListActivity.class));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }
}
