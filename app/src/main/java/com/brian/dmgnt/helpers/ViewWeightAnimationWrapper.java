package com.brian.dmgnt.helpers;

import android.view.View;
import android.widget.LinearLayout;

public class ViewWeightAnimationWrapper {

    private View mView;

    public ViewWeightAnimationWrapper(View view) {
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams){
            mView = view;
        } else {
            throw new IllegalArgumentException("The view should have linear layout as parent");
        }
    }

    public void setWeight(float weight){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mView.getLayoutParams();
        params.weight = weight;
        mView.getParent().requestLayout();
    }

    public float getWeight(){
        return ((LinearLayout.LayoutParams) mView.getLayoutParams()).weight;
    }
}
