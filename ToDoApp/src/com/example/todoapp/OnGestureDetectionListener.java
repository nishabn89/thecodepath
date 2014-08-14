package com.example.todoapp;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnGestureDetectionListener implements OnTouchListener {

    private final class GestureListener extends SimpleOnGestureListener {

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            doubleTap(e);
            return true;
        }
    }

    private final GestureDetector gestureDetector;

    public OnGestureDetectionListener (Context ctx){
        gestureDetector = new GestureDetector(ctx, new GestureListener());
    }

    public void doubleTap(MotionEvent e) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }
}
