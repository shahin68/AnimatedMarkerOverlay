package com.shahin.overlay

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.shahin.overlay.R

@SuppressLint("CustomViewStyleable")
class BitmapOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint: Paint = Paint()
    private var mPoints = arrayListOf<Projection>()
    private var circularPaths = mutableListOf<Path>()

    private var maxSize = resources.getDimension(R.dimen.dimen48)
    private var inheritFromSrc = false
    private var minSize = 0f
    private var bitmapMarker = BitmapFactory.decodeResource(resources, R.drawable.ic_marker)

    private var dynamicSize = minSize
    private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(dynamicSize, maxSize)

    init {
        val style = context.obtainStyledAttributes(attrs, R.styleable.BitmapOverlay)
        maxSize = style.getDimension(R.styleable.BitmapOverlay_maximumSize, maxSize)
        bitmapMarker = BitmapFactory.decodeResource(resources, R.styleable.BitmapOverlay_src)
        minSize = style.getDimension(R.styleable.BitmapOverlay_minimumSize, minSize)
        if (inheritFromSrc) {
            minSize = bitmapMarker.width.toFloat()
        }
        style.recycle()
        valueAnimator.apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            startDelay = 700L
            interpolator = DecelerateInterpolator(2f)
        }.addUpdateListener { animation ->
            dynamicSize = animation.animatedValue as Float
            invalidate()
        }
        if (!valueAnimator.isRunning) {
            valueAnimator.start()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //region Draw Filled Circle
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.RED
        mPaint.alpha = if (valueAnimator.isRunning) (255 - 255 * ((dynamicSize - minSize) / (maxSize - minSize))).toInt() else 0
        mPaint.isDither = true
        mPaint.isAntiAlias = true
        mPoints.forEachIndexed { index, projection ->
            val circleCenterX = projection.point.x.toFloat()
            val circleCenterY = projection.point.y.toFloat()
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                canvas?.clipOutPath(circularPaths[index])
            } else {
                canvas?.clipPath(circularPaths[index], Region.Op.DIFFERENCE)
            }
            canvas?.drawCircle(circleCenterX, circleCenterY, dynamicSize, mPaint)
        }
        //endregion

    }

    fun set(projection: Projection) {

        val markerOptions = mPoints.map { it.point }
        val latlng = markerOptions.find {
            it.x == projection.point.x && it.y == projection.point.y
        }
        if (latlng == null) {
            mPoints.add(Projection(projection.point))
            addCutterPath(projection)
        } else {
            var pos = -1
            mPoints.forEachIndexed { index, proj ->
                if (proj.point.x == latlng.x && proj.point.y == latlng.y) {
                    pos = index
                }
            }
            if (pos != -1) {
                mPoints.removeAt(pos)
                circularPaths.removeAt(pos)
                mPoints.add(Projection(projection.point))
                addCutterPath(projection)
            }
        }
        invalidate()
    }

    fun clear() {
        circularPaths.clear()
        mPoints.clear()
        invalidate()
    }

    fun move(projection: Projection) {
        val proj = mPoints.find {
            it.point.x == projection.point.x && it.point.y == projection.point.y
        }
        if (proj != null) {
            var pos = -1
            mPoints.forEachIndexed { index, p ->
                if (p.point.x == proj.point.x && p.point.y == proj.point.y) {
                    pos = index
                }
            }
            if (pos != -1) {
                mPoints.removeAt(pos)
                circularPaths.removeAt(pos)
                mPoints.add(Projection(projection.point))
                addCutterPath(projection)
                invalidate()
            }
        }

    }

    fun start() {
        valueAnimator.start()
    }

    fun pause() {
        valueAnimator.pause()
    }

    private fun addCutterPath(projection: Projection) {
        val path = Path()
        val circleCenterX = projection.point.x.toFloat()
        val circleCenterY = projection.point.y.toFloat()
        path.addCircle(circleCenterX, circleCenterY, minSize, Path.Direction.CCW)
        circularPaths.add(path)
    }

}