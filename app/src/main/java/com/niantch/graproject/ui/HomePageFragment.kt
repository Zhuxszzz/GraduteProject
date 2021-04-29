package com.niantch.graproject.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.adapter.AddressActivity
import com.niantch.graproject.adapter.HomePageAdapter
import com.niantch.graproject.databinding.OneFragemntHeadItemBinding
import com.niantch.graproject.databinding.OneFragmentBinding
import com.niantch.graproject.model.ShopDetailModel
import com.niantch.graproject.utils.DataUtil
import com.niantch.graproject.viewmodel.ResViewModel

/**
 * author: niantchzhu
 * date: 2021
 */
class HomePageFragment : Fragment(R.layout.one_fragment) {

    companion object {
        const val TAG = "HomePageFragment"
        const val RES_DETAIL = "res_detail"
        const val RES_TITLE = "res_title"
        const val ONE_FLOUR = "杨国福"
        const val TWO_FLOUR = "薯条炸鸡"
        const val THREE_FLOUR = "速食快餐"
        const val SWEET = "甜品饮品"
        const val DELIVER = "众包专送"
        const val SIMPLE = "炸鸡汉堡"
        const val FAVOUR = "新店特惠"
        const val FRUIT = "水果生鲜"
        const val COOK = "家常菜"
    }

    private lateinit var binding: OneFragmentBinding
    private val resViewModel: ResViewModel by activityViewModels()
    private var adapter: HomePageAdapter? = null
    private lateinit var recycleHeadView: OneFragemntHeadItemBinding

    private var homeRecResDetailList = mutableListOf<ShopDetailModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = OneFragmentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initRecycler()
        initObserver()
        resViewModel.fetchShopData()
    }

    override fun onStart() {
        super.onStart()
        binding.homeAddress.text = DataUtil.getDefaultAddress()
    }

    private fun initUI() {
        recycleHeadView = binding.headView
        val headFood = recycleHeadView.headIconFood
        val headOne = recycleHeadView.headIconOne
        val headTwo = recycleHeadView.headIconTwo
        val headThree = recycleHeadView.headIconThree
        val headSweet = recycleHeadView.headIconSweet
        val headDeliver = recycleHeadView.headIconDeliver
        val headSimple = recycleHeadView.headIconSimple
        val headPrefer = recycleHeadView.headIconPrefer
        val headFruit = recycleHeadView.headIconFruit
        val headCook = recycleHeadView.headIconCook

        binding.homeSearch.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            startActivity(intent)
        }
        headFood.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, "")
            startActivity(intent)
        }

        headOne.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_one))
            startActivity(intent)
        }

        headTwo.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_two))
            startActivity(intent)
        }

        headThree.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_three))
            startActivity(intent)
        }

        headSweet.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_sweet))
            startActivity(intent)
        }

        headDeliver.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_deliver))
            startActivity(intent)
        }

        headSimple.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_ham))
            startActivity(intent)
        }

        headPrefer.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_prefer))
            startActivity(intent)
        }

        headFruit.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_fruit))
            startActivity(intent)
        }

        headCook.setOnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(RES_TITLE, resources.getString(R.string.head_icon_cook))
            startActivity(intent)
        }
        binding.homeAddress.text = DataUtil.getDefaultAddress()
        binding.homeAddress.setOnClickListener {
            val intent = Intent(context, AddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initRecycler() {
        binding.oneFragmentRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        adapter = HomePageAdapter(context!!, homeRecResDetailList)

        binding.oneFragmentRv.adapter = adapter

    }

    fun initObserver() {
        resViewModel.homePageRes.observe(viewLifecycleOwner, Observer {
            homeRecResDetailList.clear()
            homeRecResDetailList.addAll(it)
            if (it.isNotEmpty()) {
                DataUtil.updateShops(it)
                adapter?.data = it
                adapter?.notifyDataSetChanged()
            }
        })
    }

}