package com.anthonymarkd.badmintontrainingbot

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


class LocationMarker : ImageView {
    private var mPaint: Paint? = null
    private val mBackgroundHeight = 0
    private val mBackgroundWidth = 0
    private var points = mutableListOf<Point>()
    private lateinit var pin : Bitmap
    //private Context mContext;
    private fun initMapPinView() {
        mPaint = Paint()
        mPaint!!.color = Color.RED
        pin = getBitmapFromVectorDrawable(context,R.drawable.ic_place_black_24dp)!!

    }

    constructor(c: Context?) : super(c) {
        initMapPinView()
    }

    constructor(c: Context?, attrs: AttributeSet?) : super(c, attrs) {
        initMapPinView()
    }

    fun setPinArray(newPoints: MutableList<Point>){
        points = newPoints
    }
    fun deleteLastPointEntry(){
        points.removeAt(points.lastIndex)
    }
    fun resetPoints(){
        points.clear()
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for(point in points){
            canvas.drawBitmap(pin,point.x, point.y, null)
        }

    }

    companion object {
        private const val TAG = "MapPin"
    }

    fun getBitmapFromVectorDrawable(
        context: Context?,
        drawableId: Int
    ): Bitmap? {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}