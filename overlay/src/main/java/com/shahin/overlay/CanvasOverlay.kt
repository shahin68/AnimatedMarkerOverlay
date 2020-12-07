package com.shahin.overlay

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat

@SuppressLint("CustomViewStyleable")
class CanvasOverlay @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val mPaint: Paint = Paint()
    private var mPoints = arrayListOf<Projection>()
    private var circularPaths = mutableListOf<Path>()

    private var defaultColor = Color.BLUE
    private var maxRadius = resources.getDimension(R.dimen.dimen48)
    private var minRadius = 0f

    private var dynamicRadius = minRadius
    private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat(dynamicRadius, maxRadius)

    init {
        val style = context.obtainStyledAttributes(attrs, R.styleable.CanvasOverlay)
        defaultColor = style.getColor(R.styleable.CanvasOverlay_markerColor, Color.BLACK)
        maxRadius = style.getDimension(R.styleable.CanvasOverlay_markerMaximumRadius, maxRadius)
        minRadius = style.getDimension(R.styleable.CanvasOverlay_markerMinimumRadius, minRadius)
        style.recycle()
        minRadius = (ContextCompat.getDrawable(context, R.drawable.ic_marker)?.intrinsicWidth?.toFloat() ?: minRadius) / 2
        valueAnimator.apply {
            duration = 2000L
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            startDelay = 700L
            interpolator = DecelerateInterpolator(2f)
        }.addUpdateListener { animation ->
            dynamicRadius = animation.animatedValue as Float
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
        mPaint.color = defaultColor
        mPaint.alpha = if (valueAnimator.isRunning) (255 - 255 * ((dynamicRadius - minRadius) / (maxRadius - minRadius))).toInt() else 0
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
            canvas?.drawCircle(circleCenterX, circleCenterY, dynamicRadius, mPaint)
        }
        //endregion

    }

    fun set(projection: Projection) {

        val points = mPoints.map { it.latLng }
        val oldProjection = points.find {
            it.latitude == projection.latLng.latitude && it.longitude == projection.latLng.longitude
        }
        if (oldProjection == null) {
            mPoints.add(Projection(projection.point, projection.latLng))
            addCutterPath(projection)
        } else {
            var pos = -1
            mPoints.forEachIndexed { index, projectionItem ->
                if (projectionItem.latLng.latitude == oldProjection.latitude && projectionItem.latLng.longitude == oldProjection.longitude) {
                    pos = index
                }
            }
            if (pos != -1) {
                mPoints.removeAt(pos)
                circularPaths.removeAt(pos)
                mPoints.add(Projection(projection.point, projection.latLng))
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
        val oldProjection = mPoints.find {
            it.latLng.latitude == projection.latLng.latitude && it.latLng.longitude == projection.latLng.longitude
        }
        if (oldProjection != null) {
            var pos = -1
            mPoints.forEachIndexed { index, projectedItem ->
                if (projectedItem.latLng.latitude == oldProjection.latLng.latitude && projectedItem.latLng.longitude == oldProjection.latLng.longitude) {
                    pos = index
                }
            }
            if (pos != -1) {
                mPoints.removeAt(pos)
                circularPaths.removeAt(pos)
                mPoints.add(Projection(projection.point, projection.latLng))
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
        path.addCircle(circleCenterX, circleCenterY, minRadius, Path.Direction.CCW)
        circularPaths.add(path)
    }

}
