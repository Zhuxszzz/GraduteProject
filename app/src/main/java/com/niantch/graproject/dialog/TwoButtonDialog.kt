package com.niantch.graproject.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.niantch.graproject.R
import com.niantch.graproject.utils.DensityUtils
import kotlin.math.min

/**
 * @description: Two Button Dialog with: 1. title 2. description 3. negative button 4. positive button
 * @since 2021-02-08 20:27
 * @author jitaoguo@tencent.com
 */
class TwoButtonDialog(context: Context?) : DialogWrapper<Any?>(context) {

    protected var mTitleText: TextView? = null
    protected var mDesText: TextView? = null
    protected var mNegativeBtn: TextView? = null
    protected var mPositiveBtn: TextView? = null
    protected var mClickListener: ActionClickListener? = null

    override fun onCreateView(layoutInflater: LayoutInflater): View {
        return layoutInflater.inflate(R.layout.dialog_two_button, null)
    }

    override fun onViewCreated(rootView: View) {
        mTitleText = rootView.findViewById(R.id.title_tv)
        mDesText = rootView.findViewById(R.id.description_tv)
        mNegativeBtn = rootView.findViewById(R.id.negative_btn)
        mPositiveBtn = rootView.findViewById(R.id.positive_btn)
    }

    override fun initListener() {
        super.initListener()
        if (mNegativeBtn != null) {
            mNegativeBtn!!.setOnClickListener { v: View? -> onActionBtn1Click(v) }
        }
        if (mPositiveBtn != null) {
            mPositiveBtn!!.setOnClickListener { v: View? -> onActionBtn2Click(v) }
        }
    }

    protected fun onActionBtn1Click(v: View?) {
        mClickListener?.onNegativeBtnClick(this)
        dismiss()
    }

    protected fun onActionBtn2Click(v: View?) {
        mClickListener?.onPositiveBtnClick(this)
        dismiss()
    }

    fun setActionClickListener(clickListener: ActionClickListener) {
        mClickListener = clickListener
    }

    override fun onDismiss() {
        super.onDismiss()
        mNegativeBtn?.setOnClickListener(null)
        mPositiveBtn?.setOnClickListener(null)
    }

    override fun setupWindow(dialog: Dialog) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            decorView.setPadding(0, 0, 0, 0)
            val wlp: WindowManager.LayoutParams = attributes
            wlp.gravity = Gravity.CENTER
            wlp.width = getProperWidth(context)
            wlp.dimAmount = DIM_AMOUNT
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            attributes = wlp
        }
        dialog.setCancelable(false)
    }

    //Get proper width limited by ratio and absolute max width
    private fun getProperWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = (point.y * WIDTH_RATIO).toInt()
        val maxWidth = DensityUtils.dp2px(MAX_WIDTH)
        return min(width, maxWidth)
    }

    override fun onBindData(data: Any?) {
        //no - op
    }

    override fun onClick(v: View) {
        //no - op
    }

    fun setTitle(title: CharSequence) {
        mTitleText?.text = title
    }

    fun setTitle(resId: Int) {
        mTitleText?.setText(resId)
    }

    fun setTitleMaxLines(maxLines: Int) {
        mTitleText?.maxLines = maxLines
    }

    fun setDescription(resId: Int) {
        mDesText?.setText(resId)
    }

    fun setDescription(desStr: CharSequence) {
        if (mDesText != null) {
            if (!TextUtils.isEmpty(desStr)) {
                mDesText!!.visibility = View.VISIBLE
                mDesText!!.text = desStr
            } else {
                mDesText!!.visibility = View.GONE
            }
        }
    }

    fun setDescTxtGravity(gravity: Int) {
        mDesText?.gravity = gravity
    }

    fun setDescriptionVisible(visible: Boolean) {
        mDesText?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun setNegativeBtnName(resId: Int) {
        mNegativeBtn?.setText(resId)
    }

    fun setNegativeBtnName(actionName: CharSequence?) {
        mNegativeBtn?.text = actionName
    }

    fun setPositiveBtnName(resId: Int) {
        mPositiveBtn?.setText(resId)
    }

    fun setPositiveBtnName(actionName: CharSequence?) {
        mPositiveBtn?.text = actionName
    }

    interface ActionClickListener {

        fun onNegativeBtnClick(dialogWrapper: DialogWrapper<*>?)

        fun onPositiveBtnClick(dialogWrapper: DialogWrapper<*>?)

    }

    open class ActionClickListenerAdapter : ActionClickListener,
        DialogDismissWrapperListener,
        DialogShowWrapperListener {
        override fun onDismiss(dialogWrapper: DialogWrapper<*>?) {}

        override fun onShow(dialogWrapper: DialogWrapper<*>?) {}

        override fun onNegativeBtnClick(dialogWrapper: DialogWrapper<*>?) {}

        override fun onPositiveBtnClick(dialogWrapper: DialogWrapper<*>?) {}

    }

    companion object {
        private const val DIM_AMOUNT = 0.3f
        private const val WIDTH_RATIO = 0.79f
        private const val MAX_WIDTH = 295f
    }

}