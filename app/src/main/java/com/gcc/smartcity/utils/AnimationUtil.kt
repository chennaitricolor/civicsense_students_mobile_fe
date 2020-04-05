package com.gcc.smartcity.utils

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.MotionEvent
import android.view.View

class AnimationUtil {
    companion object {
        fun buttonEffect(button: View, color: String) {
            button.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.background.setColorFilter(
                            Color.parseColor(color),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        v.invalidate()
                    }
                    MotionEvent.ACTION_UP -> {
                        v.background.clearColorFilter()
                        v.invalidate()
                    }
                }
                false
            }
        }
    }
}