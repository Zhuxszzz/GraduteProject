package com.niantch.graproject.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.ImageView
import com.niantch.graproject.R

/**
 * author: niantchzhu
 * date: 2021
 */
class RoundAngleImageView(context: Context, attrs: AttributeSet?, defStyle: Int): ImageView(context,attrs,defStyle) {

    private var paint: Paint? = null

    /**
     * 个人理解是
     *
     *
     * 这两个都是画圆的半径
     */
    private var roundWidth = 20
    private var roundHeight = 20
    private var paint2: Paint? = null

    private fun init(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView)
            roundWidth = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundWidth, roundWidth)
            roundHeight = a.getDimensionPixelSize(R.styleable.RoundAngleImageView_roundHeight, roundHeight)
        } else {
            val density = context.resources.displayMetrics.density
            roundWidth = (roundWidth * density).toInt()
            roundHeight = (roundHeight * density).toInt()
        }
        paint = Paint()
        //设置画笔颜色
        paint!!.color = Color.WHITE
        //设置抗锯齿，如果不设置，加载位图的时候可能会出现锯齿状的边界，如果设置，边界就会变的稍微有点模糊，锯齿就看不到了。
        paint!!.isAntiAlias = true
        //设置图形重叠时的显示方式
        paint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        paint2 = Paint()
        paint2!!.xfermode = null
    }

    override fun draw(canvas: Canvas) {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas2 = Canvas(bitmap)
        super.draw(canvas2)
        drawLiftUp(canvas2)
        drawLiftDown(canvas2)
        drawRightUp(canvas2)
        drawRightDown(canvas2)
        canvas.drawBitmap(bitmap, 0f, 0f, paint2)
        bitmap.recycle()
    }

    private fun drawLiftUp(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, roundHeight.toFloat())
        path.lineTo(0f, 0f)
        path.lineTo(roundWidth.toFloat(), 0f)
        path.arcTo(RectF(0F, 0F, (roundWidth * 2).toFloat(), (roundHeight * 2).toFloat()), -90f, -90f)
        path.close()
        canvas.drawPath(path, paint!!)
    }

    private fun drawLiftDown(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, height - roundHeight.toFloat())
        path.lineTo(0f, height.toFloat())
        path.lineTo(roundWidth.toFloat(), height.toFloat())
        path.arcTo(RectF(0F, (height - roundHeight * 2).toFloat(), (roundWidth * 2).toFloat(), height.toFloat()), 90f, 90f)
        path.close()
        canvas.drawPath(path, paint!!)
    }

    private fun drawRightDown(canvas: Canvas) {
        val path = Path()
        path.moveTo(width - roundWidth.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height - roundHeight.toFloat())
        path.arcTo(RectF((width - roundWidth * 2).toFloat(), (height - roundHeight * 2).toFloat(), width.toFloat(), height.toFloat()), -0f, 90f)
        path.close()
        canvas.drawPath(path, paint!!)
    }

    private fun drawRightUp(canvas: Canvas) {
        val path = Path()
        path.moveTo(width.toFloat(), roundHeight.toFloat())
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width - roundWidth.toFloat(), 0f)
        path.arcTo(RectF((width - roundWidth * 2).toFloat(), 0F, width.toFloat(), (0 + roundHeight * 2).toFloat()), -90f, 90f)
        path.close()
        canvas.drawPath(path, paint!!)
    }

}