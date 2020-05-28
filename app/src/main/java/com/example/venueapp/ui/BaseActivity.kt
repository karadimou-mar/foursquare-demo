package com.example.venueapp.ui

import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.venueapp.R

abstract class BaseActivity: AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun setContentView(layoutResID: Int) {

        val constraintLayout: ConstraintLayout = layoutInflater.inflate(R.layout.activity_base,null) as ConstraintLayout
        val frameLayout: FrameLayout = constraintLayout.findViewById(R.id.activity_content)
        progressBar = constraintLayout.findViewById(R.id.progress_bar)

        layoutInflater.inflate(layoutResID, frameLayout, true)

        super.setContentView(constraintLayout)
    }

    fun showProgressBar(visibility: Boolean){
        if (visibility){
            progressBar.visibility = View.VISIBLE
        }else {
            progressBar.visibility = View.GONE
        }
    }
}