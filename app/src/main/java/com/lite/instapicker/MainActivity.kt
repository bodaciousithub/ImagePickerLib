package com.lite.instapicker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.lite.imagepickerlib.GalleryActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import com.nabinbhandari.android.permissions.PermissionHandler
import android.os.Build
import com.nabinbhandari.android.permissions.Permissions

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pick_image.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                Permissions.check(this/*context*/, Manifest.permission.WRITE_EXTERNAL_STORAGE, null, object : PermissionHandler() {
                    override fun onGranted() {
                        openImageLib()
                    }
                })
            } else {
                openImageLib()
            }
        }
    }

    private fun openImageLib() {
        image_view.setImageBitmap(null)
        val intent = Intent(this, GalleryActivity::class.java)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val imagePathList = data!!.getStringArrayListExtra("images")
            for (imagePath in imagePathList) {
                Log.e("TAG", "Path : $imagePath")
            }
            Glide.with(this).load(File(imagePathList[imagePathList.size-1])).into(image_view)
        }
    }

}
