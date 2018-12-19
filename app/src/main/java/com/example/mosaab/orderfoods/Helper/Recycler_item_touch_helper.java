package com.example.mosaab.orderfoods.Helper;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.example.mosaab.orderfoods.Interface.Recycler_item_touch_helper_listner;
import com.example.mosaab.orderfoods.ViewHolder.CartAdapter;
import com.example.mosaab.orderfoods.ViewHolder.Favorties_Adapter;

public  class Recycler_item_touch_helper extends ItemTouchHelper.SimpleCallback
{

    private Recycler_item_touch_helper_listner recycler_item_touch_helper_listner;

    public Recycler_item_touch_helper(int dragDirs, int swipeDirs, Recycler_item_touch_helper_listner recycler_item_touch_helper_listner) {
        super(dragDirs, swipeDirs);
        this.recycler_item_touch_helper_listner = recycler_item_touch_helper_listner;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        if (recycler_item_touch_helper_listner != null)
        {
            recycler_item_touch_helper_listner.onSwiped(viewHolder,i,viewHolder.getAdapterPosition());
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {

        if(viewHolder instanceof CartAdapter.CartViewHolder)
        {
        View forgroundView = ((CartAdapter.CartViewHolder)viewHolder).forground_View;

        getDefaultUIUtil().clearView(forgroundView);
        }
        else if(viewHolder instanceof Favorties_Adapter.FavrotiesViewHolder)
        {
            View forgroundView = ((Favorties_Adapter.FavrotiesViewHolder)viewHolder).forground_View;

            getDefaultUIUtil().clearView(forgroundView);
        }

    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if(viewHolder instanceof CartAdapter.CartViewHolder ) {
            View forgroundView = ((CartAdapter.CartViewHolder) viewHolder).forground_View;
            getDefaultUIUtil().onDraw(c, recyclerView, forgroundView, dX, dY, actionState, isCurrentlyActive);
        }
        else if(viewHolder instanceof Favorties_Adapter.FavrotiesViewHolder)
        {
            View forgroundView = ((Favorties_Adapter.FavrotiesViewHolder) viewHolder).forground_View;
            getDefaultUIUtil().onDraw(c, recyclerView, forgroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

        if (viewHolder instanceof CartAdapter.CartViewHolder) {
            View forgroundView = ((CartAdapter.CartViewHolder) viewHolder).forground_View;
            getDefaultUIUtil().onDrawOver(c, recyclerView, forgroundView, dX, dY, actionState, isCurrentlyActive);
        }
        else if(viewHolder instanceof Favorties_Adapter.FavrotiesViewHolder){
                View forgroundView = ((Favorties_Adapter.FavrotiesViewHolder) viewHolder).forground_View;
                getDefaultUIUtil().onDrawOver(c, recyclerView, forgroundView, dX, dY, actionState, isCurrentlyActive);
            }
    }
    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {

        if (viewHolder != null)
        {
           if(viewHolder instanceof CartAdapter.CartViewHolder)
           {
               View forgroundView = ((CartAdapter.CartViewHolder)viewHolder).forground_View;
               getDefaultUIUtil().onSelected(forgroundView);
           }
           else if(viewHolder instanceof Favorties_Adapter.FavrotiesViewHolder){
                   View forgroundView = ((Favorties_Adapter.FavrotiesViewHolder)viewHolder).forground_View;
                   getDefaultUIUtil().onSelected(forgroundView);
               }
           }
        }



}
