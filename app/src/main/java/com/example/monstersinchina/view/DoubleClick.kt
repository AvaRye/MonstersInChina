package com.example.monstersinchina.view

import android.view.MotionEvent
import android.view.View

class OnDoubleClickListener(val onDoubleClick: () -> Unit) : View.OnTouchListener {
    private var count = 0
    private var firstClick = 0L
    private var secondClick = 0L
    private val doubleClickTime = 1000L

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (MotionEvent.ACTION_DOWN == event!!.action) {
            count++
            if (count == 1) {
                firstClick = System.currentTimeMillis()
            } else if (count == 2) {
                secondClick = System.currentTimeMillis()
                if (secondClick - firstClick < doubleClickTime) {
                    onDoubleClick()
                }
                count = 0
                firstClick = 0L
                secondClick = 0L
            }
        }
        return true
    }

}