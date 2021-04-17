package com.niantch.graproject.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.niantch.graproject.NetModel
import com.niantch.graproject.model.ShopDetailModel
import org.junit.Test


/**
 * author: niantchzhu
 * date: 2021
 */
class HttpUtilTest {
    @Test
    fun testJson() {
        val json = "{\"Code\":0,\"Data\":[{\"ID\":1,\"ShopID\":1,\"ShopName\":\"杨国福\",\"ShopLogo\":\"https://i.bmp.ovh/imgs/2021/04/d07379882ea978c7.png\",\"ShopPic\":\"https://i.bmp.ovh/imgs/2021/04/d07379882ea978c7.png\",\"EvalDescription\":4.5,\"OrderNum\":4396,\"ShipMoney\":2,\"DeliverMoney\":1,\"PackExpense\":0,\"ShopAddr\":\"万象天地\",\"AvgDelitime\":10,\"ShopIntro\":\"杨国福永远滴神\"}]}\n"
                             val jsss = "[{\"id\":1,\"shop_id\":1,\"shop_name\":\"杨国福\",\"shop_logo\":\"https://i.bmp.ovh/imgs/2021/04/d07379882ea978c7.png\",\"shop_pic\":\"https://i.bmp.ovh/imgs/2021/04/d07379882ea978c7.png\",\"eval_description\":4.5,\"order_num\":4396,\"ship_money\":2,\"deliver_money\":1,\"pack_expense\":0,\"shop_addr\":\"万象天地\",\"avg_delitime\":10,\"shop_intro\":\"杨国福永远滴神\"}]"
        val list = Gson().fromJson<Any>(jsss, object : TypeToken<ArrayList<ShopDetailModel?>?>() {}.type) as ArrayList<ShopDetailModel>?
        print(list?.size)
    }
}