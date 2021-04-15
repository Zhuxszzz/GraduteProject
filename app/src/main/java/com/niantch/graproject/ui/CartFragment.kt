package com.niantch.graproject.ui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.adapter.CartFragmentAdapter
import com.niantch.graproject.databinding.CartFragmentBinding
import com.niantch.graproject.model.GoodsBuyCategoryNum
import com.niantch.graproject.model.GoodsBuyItemNum
import com.niantch.graproject.model.UserModel
import org.litepal.crud.DataSupport

class CartFragment: Fragment(R.layout.cart_fragment) {
    lateinit var binding: CartFragmentBinding
    var cartFragmentAdapter: CartFragmentAdapter? = null
    var resBuyItemNumList = ArrayList<GoodsBuyItemNum>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CartFragmentBinding.bind(view)
    }


    override fun onResume() {
        super.onResume()
        initUI()

    }

    fun initUI(){
        if (DataSupport.findAll(UserModel::class.java).size > 0) {
            //本地数据库查询所有ResBuyItemNum数据
            resBuyItemNumList = DataSupport.findAll(GoodsBuyItemNum::class.java) as ArrayList<GoodsBuyItemNum>
            binding.loginBtn.visibility = View.GONE
            if (resBuyItemNumList.size > 0) {
                val linearLayoutManager = LinearLayoutManager(context)
                binding.cartFragmentRecycler.layoutManager = linearLayoutManager
                cartFragmentAdapter = CartFragmentAdapter(context!!, resBuyItemNumList)
                cartFragmentAdapter?.setItemDeleteBtnListener(getDeleteListener())
                binding.cartFragmentRecycler.adapter = cartFragmentAdapter
                binding.cartFragmentRecycler.visibility = View.VISIBLE
                binding.cartEmpty.visibility = View.GONE
            } else {
                binding.cartFragmentRecycler.visibility = View.GONE
                binding.cartEmpty.visibility = View.VISIBLE
            }
        } else {
            binding.cartFragmentRecycler.visibility = View.GONE
            binding.loginBtn.visibility = View.VISIBLE
            binding.cartEmpty.visibility = View.GONE
            binding.loginBtn.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
            })
        }
    }

    //购物车每个子项垃圾桶图标的监听方法

    fun getDeleteListener():  CartFragmentAdapter.ItemDeleteBtnListener {
        return object :  CartFragmentAdapter.ItemDeleteBtnListener {
            override fun onItemDeleteBtnListener(btn: ImageView?, position: Int, resId: String?) {
                doOnClick(btn,position, resId)
            }

        }
    }
    fun doOnClick(btn: ImageView?, position: Int, resId: String?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setTitle(context!!.resources.getString(R.string.delete_res))
        alertDialog.setMessage(context!!.resources.getString(R.string.delete_res_goods))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context!!.resources.getString(R.string.delete), DialogInterface.OnClickListener { dialog, which ->
            DataSupport.deleteAll(GoodsBuyItemNum::class.java, "resId = ?", resId)
            DataSupport.deleteAll(GoodsBuyCategoryNum::class.java, "resId = ?", resId)
            resBuyItemNumList = DataSupport.findAll(GoodsBuyItemNum::class.java) as ArrayList<GoodsBuyItemNum>
            if (resBuyItemNumList.size > 0) {
                //注意不能使用resBuyItemNumList.remove(position);cartFragmentAdapter.notifyItemRemoved(position);notifyItemRangeChanged(0,resBuyItemNumList.size());
                // 删除，因为position不是resBuyItemNumList计算出来的，现在暂时使用重新设置cartFragmentAdapter方法刷新数据
                cartFragmentAdapter = CartFragmentAdapter(context!!, resBuyItemNumList)
                cartFragmentAdapter?.setItemDeleteBtnListener(getDeleteListener())
                binding.cartFragmentRecycler.adapter = cartFragmentAdapter
            } else {
                binding.cartFragmentRecycler.visibility = View.GONE
                binding.cartEmpty.visibility = View.VISIBLE
            }
        })
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context!!.resources.getString(R.string.cancel), DialogInterface.OnClickListener { dialog, which -> })
        alertDialog.show()
        //设置Dialog中的文字样式
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context!!.resources.getColor(R.color.bottom_tab_text_selected_color))
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context!!.resources.getColor(R.color.bottom_tab_text_selected_color))

    }
}