package com.niantch.graproject.adapter

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.niantch.graproject.R
import com.niantch.graproject.databinding.ActivityAddressBinding
import com.niantch.graproject.model.AddressModel
import com.niantch.graproject.ui.AddAddressActivity
import com.niantch.graproject.utils.DataUtil
import org.litepal.crud.DataSupport
import kotlin.collections.ArrayList

/**
 * author: niantchzhu
 * date: 2021
 */
class AddressActivity : AppCompatActivity() {
    val SELECTED_ADDRESS = "selected_address"
    val ADD_ADDRESS = 5001
    private val TAG = "AddressActivity"
    private lateinit var binding: ActivityAddressBinding

    private var addAddressAapter: AddressAdapter? = null
    private var list = ArrayList<AddressModel>()
    private var userId = 0
    private var isFromThreeFragment: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
    }

    fun initData() {
        isFromThreeFragment = intent.getBooleanExtra("threefragment", false)
        userId = DataUtil.getCurrentUser()?.userId ?: 1
        list = DataSupport.where("user_id = ?", userId.toString()).find(AddressModel::class.java) as ArrayList<AddressModel>
    }

     fun initView() {
        setListener()
        addAddressAapter = AddressAdapter(this, list)
        if (!isFromThreeFragment!!) {
            addAddressAapter?.setOnItemClickListener(object : AddressAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    setResult(RESULT_OK, Intent().putExtra("address", list[position]))
                    val addressModel = AddressModel()
                    addressModel.setToDefault("selected")
                    addressModel.updateAll("user_id = ?", userId.toString())

                    //刷新adapter
                    for (address in list) {
                        address.selected = 0
                    }
                    list[position].selected = 1
                    list[position].updateAll("user_id = ? and address = ? and phone = ?", userId.toString(), list[position].address
                            , list[position].phone)
                    addAddressAapter!!.notifyDataSetChanged()
                    finish()
                }
            })
        }
        addAddressAapter?.setOnItemLongClickListener(object : AddressAdapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int) {
                val alertDialog: AlertDialog = AlertDialog.Builder(this@AddressActivity)
                        .setTitle("设置")
                        .setMessage("确定将该地址设置成默认收货地址？")
                        .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                            val addressModel = AddressModel()
                            addressModel.setToDefault("selected")
                            addressModel.updateAll("user_id = ?", userId.toString())

                            //刷新adapter
                            for (address in list) {
                                address.selected = 0
                            }
                            list[position].selected = 1
                            list[position].updateAll("user_id = ? and address = ? and phone = ?", userId.toString(), list[position].address
                                    , list[position].phone)
                            addAddressAapter!!.notifyDataSetChanged()
                        })
                        .setNegativeButton(resources.getString(R.string.cancel), null)
                        .create()
                alertDialog.show()
                //设置Dialog中的文字样式
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.selected_color))
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.selected_color))
            }
        })
        addAddressAapter?.setOnItemDeleteListener(object : AddressAdapter.OnItemDeleteListener {
            override fun onItemDelete(position: Int) {
                DataSupport.deleteAll(AddressModel::class.java, "user_id = ? and address = ? and name = ? and phone = ?", userId.toString(), list[position].address, list[position].name
                        , list[position].phone)
                //删除某一项地址
                list.removeAt(position)
                addAddressAapter!!.notifyItemRemoved(position)
                addAddressAapter!!.notifyItemRangeChanged(0, list.size)

            }
        })
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvAddress.adapter = addAddressAapter
        binding.rvAddress.layoutManager = linearLayoutManager
    }

    fun setListener() {

        binding.header.ivBack.setOnClickListener { this.finish() }
        binding.header.ivAdd.setOnClickListener {

            val intent = Intent(this, AddAddressActivity::class.java)
            intent.putExtra("user",userId)
            startActivityForResult(intent, ADD_ADDRESS)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ADD_ADDRESS -> if (resultCode == RESULT_OK) {
                list.add(data?.getSerializableExtra("new_address") as AddressModel)
                addAddressAapter!!.notifyDataSetChanged()
            }
        }
    }

}