package com.example.venueapp.utils

import android.view.View
import android.widget.LinearLayout

/**
 * Helper class for showing animation
 *
 * @param view
 */
class ViewWeightAnimationWrapper(view: View) {

    private var view: View? = null

    fun setWeight(weight: Float){
        val params = view!!.layoutParams as LinearLayout.LayoutParams
        params.weight = weight
        view!!.parent.requestLayout()
    }

    fun getWeight(): Float{
        return (view!!.layoutParams as LinearLayout.LayoutParams).weight
    }

    init {
        if (view.layoutParams is LinearLayout.LayoutParams) {
            this.view = view
        } else {
            throw IllegalArgumentException("The view should have LinearLayout as parent")
        }
    }
}

