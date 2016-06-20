package com.ruddell.museumofthebible.views;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageButton;

/**
 * Created by chris on 12/28/15.
 */
public class AnimatedImageButton extends ImageButton {
    private AttachListener mListener = null;

    public AnimatedImageButton(Context context) {
        super(context);
    }

    public AnimatedImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimatedImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimatedImageButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setAttachListener(AttachListener listener) {
        mListener = listener;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mListener!=null) mListener.onAttached();
    }

    public interface AttachListener {
        void onAttached();
    }
}
