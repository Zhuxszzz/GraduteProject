package com.niantch.graproject.ui

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.niantch.graproject.R
import com.niantch.graproject.adapter.MultipleOrderPopWinAdapter

class MultipleOrderPopupWindow(
        mContext: Context?,
        orderModeList: List<String>?,
        onMultipleOrderItemClickListener: MultipleOrderPopWinAdapter.OnMultipleOrderItemClickListener?
) : PopupWindow() {

    private var mContext: Context? = null
    private var orderModeList: List<String>? = null
    private var view: View? = null
    private var multipleOrderRecycler: RecyclerView? = null
    private var multipleOrderShadow: View? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var adapter: MultipleOrderPopWinAdapter? = null

    init {
        this.mContext = mContext
        this.orderModeList = orderModeList
        this.width = ViewGroup.LayoutParams.MATCH_PARENT
        this.height = ViewGroup.LayoutParams.WRAP_CONTENT
        setBackgroundDrawable(BitmapDrawable())
        this.isOutsideTouchable = true
        this.isFocusable = true
        view = LayoutInflater.from(mContext).inflate(R.layout.popup_multiple_order, null)
        multipleOrderRecycler =
                view!!.findViewById<View>(R.id.multiple_order_recycler) as RecyclerView
        multipleOrderShadow = view!!.findViewById(R.id.multiple_order_shadow)
        multipleOrderShadow?.setOnClickListener(View.OnClickListener { this@MultipleOrderPopupWindow.dismiss() })
        linearLayoutManager = LinearLayoutManager(mContext)
        multipleOrderRecycler?.layoutManager = linearLayoutManager
        adapter =
                MultipleOrderPopWinAdapter(mContext, orderModeList, onMultipleOrderItemClickListener)
        multipleOrderRecycler?.adapter = adapter
        this.contentView = view
    }

    fun getSelectedPosition(): Int {
        return adapter!!.getSelectedPosition()
    }

    fun setSelectedPosition(position: Int) {
        adapter!!.setSelectedPosition(position)
    }
}