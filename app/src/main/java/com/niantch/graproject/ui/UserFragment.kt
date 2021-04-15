package com.niantch.graproject.ui

import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentUris
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.niantch.graproject.R
import com.niantch.graproject.databinding.UserFragmentBinding
import com.niantch.graproject.model.CouponBean
import com.niantch.graproject.model.UserBean
import com.niantch.graproject.utils.FileStorage
import com.niantch.graproject.utils.ImageUtil
import com.niantch.graproject.utils.HttpUtil
import com.niantch.graproject.viewmodel.UserViewModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import org.litepal.crud.DataSupport
import java.io.File
import java.io.IOException

class UserFragment : Fragment(R.layout.user_fragment) {

    companion object {
        const val REQUEST_LOGIN = 5 //登录
    }
    lateinit var binding: UserFragmentBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val TAG = "UserFragment"
    private val REQUEST_PICK_IMAGE = 1 //相册选取
    private val REQUEST_CAPTURE = 2 //拍照
    private val REQUEST_PICTURE_CUT = 3 //剪裁图片
    private val REQUEST_PERMISSION = 4 //权限请求
    private val progressBar: ProgressBar? = null


    private val couponBeanList: ArrayList<CouponBean>? = null

    private var imageUri //原图保存地址
            : Uri = Uri.EMPTY
    private var outputUri //剪切的地址
            : Uri = Uri.EMPTY
    private var imagePath: String = ""

    private var dialog: Dialog? =null

    private var userBean: UserBean? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = UserFragmentBinding.bind(view)
        initObserver()
    }

    override fun onStart() {
        super.onStart()
        userViewModel.initUser()
        initListener()
    }

    fun initObserver() {
        userViewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            userBean = it
            setUserInfo(it)
        })
    }

    fun initListener() {
        binding.userName.setOnClickListener {
            alterUserNameDialog()
        }
        binding.userSex.setOnClickListener {
            alterUserSexDialog()
        }
        binding.ivUserThumbnial.setOnClickListener {
            initPicPopWindow()
        }
        binding.fragmentMyCouponLl.setOnClickListener {
            val intentRedPaper = Intent(activity, CouponActivity::class.java)
            intentRedPaper.putExtra("coupon_list", couponBeanList)
            startActivity(intentRedPaper)
        }
        binding.logOut.setOnClickListener {
            userViewModel.doOUserLogout()
        }
    }

    fun setUserInfo(bean: UserBean?) {
        if (bean == null) {
            binding.tvSignMask.visibility = View.VISIBLE
            return
        }
        binding.tvSignMask.visibility = View.GONE
        binding.logOut.visibility = View.VISIBLE
        if (File(PreferenceManager.getDefaultSharedPreferences(activity).getString(bean.userId.toString() + "", "")).exists()) {
            ImageUtil.load(activity!!, PreferenceManager.getDefaultSharedPreferences(activity).getString(bean.userId.toString() + "", ""), binding.ivUserThumbnial, ImageUtil.REQUEST_OPTIONS)
        } else {
            ImageUtil.load(activity!!, bean.userId, binding.ivUserThumbnial, ImageUtil.REQUEST_OPTIONS)
        }
        binding.userNameText.text = bean.userName
        if (bean.userSex === 0) {
            binding.userSexText.setText("女")
        } else {
            binding.userSexText.setText("男")
        }
        val phoneNumber: String = bean.userPhone!!.substring(0, 3) + "****" + bean.userPhone!!.substring(7, bean.userPhone!!.length)
        binding.userPhoneText.text = phoneNumber
        binding.alterUserPwd.text = "修改"
    }

    private fun alterUserNameDialog() {
        val view: View = LayoutInflater.from(activity).inflate(R.layout.alter_edit, null)
        val edit = view.findViewById<View>(R.id.edit) as EditText
        val alertDialog: AlertDialog = AlertDialog.Builder(activity).setTitle(resources.getString(R.string.user_name))
                .setView(view)
                .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { _, _ ->
                    val input = edit.text.toString()
                    if (!TextUtils.isEmpty(input)) {
                        if (userBean!=null) {
                            //                                userBean.setUserName(input);
                            //                                userBean.save();
                            //                                将更改保存到远程数据库
                            progressBar?.visibility = View.VISIBLE
                            val hash = HashMap<String, String?>()
                            hash["user_id"] = java.lang.String.valueOf(userBean!!.userId)
                            hash["user_name"] = input
                            HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.SAVE_USER_NAME, hash, object : Callback {
                                override fun onFailure(call: Call, e: IOException) {
                                    Log.d("ThreeFragment", e.toString())
                                }

                                @Throws(IOException::class)
                                override fun onResponse(call: Call, response: Response) {
                                    val responseText = response.body().string()
                                    try {
                                        val jsonObject = JSONObject(responseText)
                                        val msg = jsonObject.getString("msg")
                                        val status = jsonObject.getInt("status")
                                        activity!!.runOnUiThread {
                                            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                            progressBar?.visibility = View.GONE
                                            if (status != 0) {
                                                binding.userNameText.text = input
                                                //将更改保存到本地数据库 ]
                                                userBean!!.userName = input
                                                userBean?.save()
                                            }
                                        }
                                    } catch (e: JSONException) {
                                        activity!!.runOnUiThread { progressBar?.setVisibility(View.GONE) }
                                    }
                                }
                            })
                        }
                    } else {
                        Toast.makeText(activity, resources.getString(R.string.user_name_not_empty), Toast.LENGTH_SHORT).show()
                    }
                })
                .setNegativeButton(resources.getString(R.string.cancel), null)
                .create()
        alertDialog.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        alertDialog.show()
    }

    private fun alterUserSexDialog() {
        val viewSex: View = LayoutInflater.from(activity).inflate(R.layout.alter_sex, null)
        val selected = viewSex.findViewById<View>(R.id.radio_group) as RadioGroup
        val man = viewSex.findViewById<View>(R.id.man) as RadioButton
        val woman = viewSex.findViewById<View>(R.id.woman) as RadioButton
        if (binding.userSexText.getText().toString() == "男") {
            man.isChecked = true
        } else {
            woman.isChecked = true
        }
        AlertDialog.Builder(activity)
                .setTitle(resources.getString(R.string.user_sex))
                .setView(viewSex)
                .setPositiveButton(resources.getString(R.string.yes), DialogInterface.OnClickListener { dialog, which ->
                    val rb = viewSex.findViewById<View>(selected.checkedRadioButtonId) as RadioButton
                    val sex = rb.text.toString()
                    //                        userSexText.text = sex;
                    //将修改保存到本地数据库
                    val list: List<UserBean> = DataSupport.findAll(UserBean::class.java)
                    //将更改保存到远程数据库
                    progressBar?.visibility = View.VISIBLE
                    val hash = HashMap<String, String?>()
                    hash["user_id"] = java.lang.String.valueOf(list[0].userId)
                    if (sex == "男") {
                        hash["user_sex"] = 1.toString() + ""
                    } else {
                        hash["user_sex"] = 0.toString() + ""
                    }
                    HttpUtil.sendOkHttpPostRequest(HttpUtil.HOME_PATH + HttpUtil.SAVE_USER_SEX, hash, object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Log.d("ThreeFragment", e.toString())
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call, response: Response) {
                            val responseText = response.body().string()
                            try {
                                val jsonObject = JSONObject(responseText)
                                val msg = jsonObject.getString("msg")
                                val status = jsonObject.getInt("status")
                                activity!!.runOnUiThread {
                                    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
                                    progressBar?.visibility = View.GONE
                                    if (status != 0) {
                                        binding.userSexText.text = sex
                                        //保存到本地数据库
                                        val userBean: UserBean = list[0]
                                        if (sex == "男") {
                                            userBean.userSex = 1
                                        } else {
                                            userBean.userSex = 0
                                        }
                                        userBean.save()
                                    }
                                }
                            } catch (e: JSONException) {
                            }
                        }
                    })
                })
                .setNegativeButton(resources.getString(R.string.cancel), null)
                .show()
    }

    private fun initPicPopWindow() {
        dialog = Dialog(activity!!, R.style.ActionSheetDialogStyle)
        //填充对话框的布局
        val view: View = LayoutInflater.from(activity).inflate(R.layout.popup_slide_from_bottom, null)
        val tv1 = view.findViewById<View>(R.id.tx_1) as TextView
        val tv2 = view.findViewById<View>(R.id.tx_2) as TextView
        val tv3 = view.findViewById<View>(R.id.tx_3) as TextView
//        tv1.setOnClickListener(this)
//        tv2.setOnClickListener(this)
//        tv3.setOnClickListener(this)
        // TODO: 4/12/21
        //将布局设置给Dialog
        dialog?.setContentView(view)
        //获取当前Activity所在的窗体
        val dialogWindow: Window? = dialog?.getWindow()
        //设置Dialog从窗体底部弹出
        dialogWindow?.setGravity(Gravity.BOTTOM)
        val lp = dialogWindow?.attributes
        lp?.y = 0 //设置Dialog距离底部的距离
        //设置dialog宽度满屏
        val m = dialogWindow?.windowManager
        val d = m?.defaultDisplay
        lp?.width = d?.width
        //将属性设置给窗体
        dialogWindow?.attributes = lp
        dialog?.show() //显示对话框
    }

    /**
     * 打开系统相机
     */
    private fun openCamera() {
        val file: File = FileStorage().createIconFile() //用到了sd卡权限,运行时权限处理
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(context!!, "com.example.rjq.myapplication.fileprovider", file) //通过FileProvider创建一个content类型的Uri
        } else {
            Uri.fromFile(file)
        }
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        intent.action = MediaStore.ACTION_IMAGE_CAPTURE //设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri) //将拍取的照片保存到指定URI
        startActivityForResult(intent, REQUEST_CAPTURE)
    }

    /**
     * 从相册选择
     */
    private fun selectFromAlbum() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*") //打开指定URI目录下的照片
        startActivityForResult(intent, REQUEST_PICK_IMAGE)
    }

    /**
     * 裁剪
     */
    private fun cropPhoto() {
        val file: File = FileStorage().createCropFile() //用到了sd卡权限，运行时权限处理
        outputUri = Uri.fromFile(file)
        val intent = Intent("com.android.camera.action.CROP")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        intent.setDataAndType(imageUri, "image/*") //打开指定URI目录下的照片
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri) //将裁剪完的照片保存到指定URI
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, REQUEST_PICTURE_CUT)
    }

    @TargetApi(19)
    private fun handleImgUri2String(uri: Uri): String {
        var path: String? = ""
        if (DocumentsContract.isDocumentUri(activity, uri)) {
            //如果是document类型的uri,则通过document id处理
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents" == uri.authority) {
                val id = docId.split(":").toTypedArray()[1] //解析出数字格式的id
                val selection = MediaStore.Images.Media._ID + "=" + id
                path = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.downloads.documents" == uri.authority) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(docId))
                path = getImagePath(contentUri, null)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            //如果是content类型的Uri，则使用普通方式处理
            path = getImagePath(uri, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            //如果是file类型的Uri,直接获取图片路径即可
            path = uri.path
        }
        return path!!
    }

    private fun handleImgUri2StringBeforeKitKat(uri: Uri): String {
        val path: String? = getImagePath(uri, null)
        return path!!
    }

    private fun getImagePath(uri: Uri, selection: String?): String? {
        var path: String? = null
        //通过Uri和selection来获取真实的图片路径
        val cursor = activity!!.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
            cursor.close()
        }
        return path
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_PICK_IMAGE -> if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.data!!
                    cropPhoto()
                }
            }
            REQUEST_CAPTURE -> if (resultCode == Activity.RESULT_OK) {
                cropPhoto()
            }
            REQUEST_PICTURE_CUT -> if (resultCode == Activity.RESULT_OK) {
                if (Build.VERSION.SDK_INT >= 19) {
                    imagePath = handleImgUri2String(outputUri)
                } else {
                    imagePath = handleImgUri2StringBeforeKitKat(outputUri)
                }

                //将图片保存到MySql
                progressBar?.setVisibility(View.VISIBLE)
                val files: MutableList<String> = ArrayList()
                files.add(imagePath!!)
                val hashMap = HashMap<String, String>()
                hashMap["user_id"] = PreferenceManager.getDefaultSharedPreferences(activity).getInt("user_id", -1).toString()
                if (DataSupport.findAll(UserBean::class.java).get(0).userImg != null) {
                    hashMap["user_img"] = DataSupport.findAll(UserBean::class.java)[0].userImg ?: ""
                }
                HttpUtil.upLoadImgsRequest(HttpUtil.HOME_PATH + HttpUtil.UPLOAD_IMG_API, hashMap, files, object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.d(TAG, e.message)
                        activity!!.runOnUiThread {
                            progressBar?.setVisibility(View.GONE)
                            Toast.makeText(context, "网络错误，请检查网络状态!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        //将更改的头像保存到本地用户中
                        val responseText = response.body().string()
                        try {
                            val jsonObject = JSONObject(responseText)
                            //将新上传的图片的服务器路径保存到用户信息中
                            val list: List<UserBean> = DataSupport.findAll(UserBean::class.java)
                            val userBean: UserBean = list[0]
                            userBean.userImg = jsonObject["url"] as String
                            userBean.save()
                            activity!!.runOnUiThread { //保存本地图片头像的路径
                                PreferenceManager.getDefaultSharedPreferences(activity).edit().putString(userBean.userId.toString() + "", imagePath).commit()
                                ImageUtil.load(activity!!, imagePath, binding.ivUserThumbnial, ImageUtil.REQUEST_OPTIONS)
                                progressBar?.setVisibility(View.GONE)
                            }
                        } catch (e: JSONException) {
                            Log.d(TAG, e.message)
                            activity!!.runOnUiThread {
                                progressBar?.setVisibility(View.GONE)
                                Toast.makeText(activity, "上传头像失败!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            }
        }
    }

}