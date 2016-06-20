package com.ruddell.museumofthebible.Bible;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ChristopherRuddell, Museum of the Bible, on 6/20/16.
 */

public class SimpleItemTouchListener implements RecyclerView.OnItemTouchListener {
    GestureDetector mGestureDetector;
    Context mContext;
    Listener mListener;

    public SimpleItemTouchListener(Context context, Listener listener) {
        mContext = context.getApplicationContext();
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });


    }

    @Override
    public boolean onInterceptTouchEvent(final RecyclerView recyclerView, final MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());

        if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
            mListener.onItemClicked(recyclerView.getChildViewHolder(child));
            return true;

        }


        return false;
    }

    @Override
    public void onTouchEvent(final RecyclerView rv, final MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(final boolean disallowIntercept) {

    }

    public interface Listener {
        public void onItemClicked(RecyclerView.ViewHolder viewHolder);
    }
}
