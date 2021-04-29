package com.niantch.graproject.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.adapter.ClassifyAdapter
import com.niantch.graproject.databinding.ActivitySearchBinding
import com.niantch.graproject.ui.HomePageFragment.Companion.RES_TITLE
import com.niantch.graproject.viewmodel.ResViewModel
import java.util.*

/**
 * author: niantchzhu
 * date: 2021
 */
class SearchActivity : AppCompatActivity(R.layout.activity_search) {
    private lateinit var binding: ActivitySearchBinding
    private var classifyAdapter: ClassifyAdapter? = null
    private val resViewModel: ResViewModel by viewModels()
    private val handler = Handler()
    var keyword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        keyword = intent.getStringExtra(RES_TITLE)
        initUI()
        initObserver()
        resViewModel.searchShop(keyword)

    }

    private fun initUI() {
        classifyAdapter = ClassifyAdapter(this, emptyList())
        binding.searchRecycler.adapter = classifyAdapter
        binding.searchRecycler.layoutManager = LinearLayoutManager(applicationContext)
        binding.searchBtn.setOnClickListener {
            binding.firstLoad.visibility = View.VISIBLE
            resViewModel.searchShop(keyword)
        }
        binding.backBtn.setOnClickListener { finish() }
        //让软键盘延时弹出，以更好的加载Activity
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.showSoftInput(binding.searchEt, 0)
            }
        }, 300)

        //软键盘 搜索键 监听
        //软键盘 搜索键 监听
        binding.searchEt.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = binding.searchEt.text.toString()
                binding.firstLoad.visibility = View.VISIBLE
                resViewModel.searchShop(keyword)
                return@OnEditorActionListener true
            }
            false
        })
        binding.searchEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(s.toString())) {
                    binding.searchRecycler.setVisibility(View.GONE)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun initObserver() {
        resViewModel.shopListLiveData.observe(this, Observer {
            handler.postDelayed(
                    {
                        if (it == null || it.isEmpty()) {
                            binding.emptyView.visibility = View.VISIBLE
                            binding.firstLoad.visibility = View.GONE
                        } else {
                            binding.searchRecycler.visibility = View.VISIBLE
                            binding.emptyView.visibility = View.GONE
                            classifyAdapter?.homeRecShopDetailModelList = it
                            classifyAdapter?.notifyDataSetChanged()
                            binding.firstLoad.visibility = View.GONE
                        }
                    }, 300)
        })
    }
}