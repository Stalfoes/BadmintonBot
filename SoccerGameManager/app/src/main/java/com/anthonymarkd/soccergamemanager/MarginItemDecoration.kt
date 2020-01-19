package com.anthonymarkd.soccergamemanager

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(private val topSpaceHeight: Int,private val bottomSpaceHeight: Int,private val leftSpaceHeight: Int,private val rightSpaceHeight: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = topSpaceHeight
            }
            left = leftSpaceHeight
            right = rightSpaceHeight
            bottom = bottomSpaceHeight
        }
    }
}