package com.lite.imagepickerlib

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.util.Log
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.gallery_activity.*
import java.util.*

class GalleryActivity : AppCompatActivity() {

    private var listOfAllFiles = ArrayList<ImageFile>()
    private val groupImagesList = ArrayList<ImageFile>()
    private var imageFlag: Boolean = false
    private var videoFlag: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.gallery_activity)
        supportActionBar?.hide()

        close_btn.setOnClickListener { onBackPressed() }
        view_pager.offscreenPageLimit = 3
        view_pager.setPagingEnabled(false)
        view_pager.adapter = GalleryViewPagerAdapter(this, supportFragmentManager)
        loadList()

        next_btn.setOnClickListener {
            (view_pager.adapter!!.instantiateItem(view_pager, 0) as GalleryFragment).cropImage()
        }
        gallery_btn.setOnClickListener {
            view_pager.setCurrentItem(0, true)
            updateView(true)

            gallery_bottom_view.visibility = View.VISIBLE
            photo_bottom_view.visibility = View.GONE
            video_bottom_view.visibility = View.GONE
        }
        photo_btn.setOnClickListener {
            view_pager.setCurrentItem(1, true)
            updateView(false)

            gallery_bottom_view.visibility = View.GONE
            photo_bottom_view.visibility = View.VISIBLE
            video_bottom_view.visibility = View.GONE

            val photoFragment = (view_pager.adapter!!.instantiateItem(view_pager, 1) as PhotoFragment)
            if (!photoFragment.getPhotoFlag()) {
                photoFragment.setPhotoFlag(true)
                photoFragment.changeView()
            }
        }
        video_btn.setOnClickListener {
            view_pager.setCurrentItem(1, true)
            updateView(false)

            gallery_bottom_view.visibility = View.GONE
            photo_bottom_view.visibility = View.GONE
            video_bottom_view.visibility = View.VISIBLE

            val photoFragment = (view_pager.adapter!!.instantiateItem(view_pager, 1) as PhotoFragment)
            if (photoFragment.getPhotoFlag()) {
                photoFragment.setPhotoFlag(false)
                photoFragment.changeView()
            }
        }
    }

    override fun onBackPressed() { onFinishScreen(Activity.RESULT_CANCELED, null) }

    fun onFinishScreen(resultCode: Int, data: Intent?) {
        if (data != null)
            setResult(resultCode, data)
        else
            setResult(resultCode)
        finish()
    }

    private fun updateView(headerViewVisibility: Boolean) {
        if (headerViewVisibility)
            header_view.visibility = View.VISIBLE
        else
            header_view.visibility = View.GONE
    }

    private fun loadList() {
        supportLoaderManager.initLoader(0, null, ImageLoader())
    }

    private inner class ImageLoader : LoaderManager.LoaderCallbacks<Cursor> {

        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            val projection = arrayOf(MediaStore.Images.Media.TITLE, MediaStore.Images.Media._ID, MediaStore.MediaColumns.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            return CursorLoader(this@GalleryActivity, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
            if (imageFlag)
                return
            val listOfAllImages = ArrayList<ImageFile>()
            var absolutePathOfImage: String
            var absoluteIdOfImage: String
            var absoluteTitleOfImage: String
            var absoluteNameOfImage: String
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    absoluteIdOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    absoluteTitleOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.TITLE))
                    absoluteNameOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                    absolutePathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))

                    val imageFile = ImageFile(0, absoluteIdOfImage, absoluteTitleOfImage, absoluteNameOfImage, absolutePathOfImage, 1, 0, 0)
                    listOfAllImages.add(imageFile)
                    listOfAllFiles.add(imageFile)
                }
            }

            Collections.sort(listOfAllImages, kotlin.Comparator { t1, t2 -> return@Comparator t2.groupName.compareTo(t1.groupName) })
            if (listOfAllImages!!.isNotEmpty()) {
                var groupName = ""
                for (item in listOfAllImages!!) {
                    if (item.groupName != groupName) {
                        //Log.e("testing", "ITEM : ${item.id} >>> ${item.title} >>> ${item.groupName}")
                        groupImagesList.add(item)
                        groupName = item.groupName
                    } else {
                        groupImagesList[groupImagesList.size-1].counter++
                    }
                }
            }
            imageFlag = true
            Log.e("TAG", "SIZE : " + listOfAllImages.size)

            (view_pager.adapter!!.instantiateItem(view_pager, 0) as GalleryFragment).notifyList(listOfAllImages)
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}
    }

}
