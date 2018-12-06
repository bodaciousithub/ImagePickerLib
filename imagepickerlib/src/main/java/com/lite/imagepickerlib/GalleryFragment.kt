package com.lite.imagepickerlib

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.gallery_fragment.view.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GalleryFragment : Fragment() {

    private var imagesList = ArrayList<ImageFile>()
    private var selectedImagesList = ArrayList<String>()
    private lateinit var fragmentView: View
    private var scale = true
    var multipleSelection = false
    private val MAX_SELECTION = 5
    private var noOfSelection = 0
    private var imagePickActivity: GalleryActivity? = null
    fun setImagePickActivity(imagePickActivity: GalleryActivity) { this.imagePickActivity = imagePickActivity }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentView = inflater.inflate(R.layout.gallery_fragment, container, false)

        fragmentView.list_view.layoutManager = GridLayoutManager(activity!!, 3) as RecyclerView.LayoutManager?
        fragmentView.list_view.addItemDecoration(EqualSpacingItemDecoration(3, 2, false, 0))
        fragmentView.list_view.adapter = GalleryAdapter(activity!!, imagesList, ListItemClickListener(), 0)

        fragmentView.scale_btn.setOnClickListener {
            if (scale) {
                fragmentView.selected_image.scaleType = ImageView.ScaleType.CENTER_CROP
                fragmentView.scale_btn.setBackgroundResource(R.drawable.selected_bg)
                scale = false
            } else {
                fragmentView.scale_btn.setBackgroundResource(R.drawable.un_selected_bg)
                fragmentView.selected_image.scaleType = ImageView.ScaleType.FIT_CENTER
                scale = true
            }
        }
        fragmentView.mutliple_btn.setOnClickListener {
            if (multipleSelection) {
                fragmentView.mutliple_btn.setBackgroundResource(R.drawable.un_selected_bg)
                multipleSelection = false
            } else {
                fragmentView.mutliple_btn.setBackgroundResource(R.drawable.selected_bg)
                multipleSelection = true
            }
            if (!multipleSelection) {
                for (imageFile in imagesList)
                    imageFile.checked = 0
                selectedImagesList.clear()
            }
            (fragmentView.list_view.adapter as GalleryAdapter).updateSelection(multipleSelection)
        }
        return fragmentView
    }

    fun notifyList(list: ArrayList<ImageFile>) {
        for (imageFile in list) {
            imagesList.add(imageFile)
        }
        fragmentView.list_view.adapter!!.notifyDataSetChanged()
        //fragmentView.selected_image.setUri(Uri.fromFile(File(imagesList[0].path)))
        fileUrl = imagesList[0].path
        Glide.with(activity!!).load(File(imagesList[0].path)).into(fragmentView.selected_image)
    }

    private inner class ListItemClickListener: GalleryAdapter.ItemClickListener {
        override fun onItemClick(position: Int) {
            //fragmentView.selected_image.setUri(Uri.fromFile(File(imageFile.path)))
            if (multipleSelection) {
                if (imagesList[position].checked == 0) {
                    if (noOfSelection == MAX_SELECTION) {
                        Toast.makeText(activity!!, "Max images selected.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    imagesList[position].checked = 1
                    selectedImagesList.add(imagesList[position].path)
                    noOfSelection++
                } else {
                    imagesList[position].checked = 0
                    selectedImagesList.remove(imagesList[position].path)
                    noOfSelection--
                }
                fragmentView.list_view.adapter!!.notifyItemChanged(position)
            }
            selectImage(position)
            fragmentView.app_bar_view.setExpanded(true)
        }
    }

    private fun selectImage(position: Int) {
        fileUrl = if (selectedImagesList.isNotEmpty()) {
            selectedImagesList[selectedImagesList.size-1]
        } else {
            imagesList[position].path
        }
        Glide.with(activity!!).load(File(fileUrl)).into(fragmentView.selected_image)
    }

    private var fileUrl: String? = null
    fun cropImage() {
        //val intent = Intent(activity!!, EditActivity::class.java)         // uncomment for edit images
        val intent = Intent()
        val arrayList = ArrayList<String>()
        if (selectedImagesList.isEmpty()) {
            arrayList.add(fileUrl!!)
        } else {
            for (path in selectedImagesList)
                arrayList.add(path)
        }
        intent.putExtra("images", arrayList)
        //startActivity(intent)                                             // uncomment for edit images
        imagePickActivity?.onFinishScreen(Activity.RESULT_OK, intent)       // comment this for edit images
    }

    private fun storeFileAndReturn(bitmap: Bitmap) {
        var fileUrl = Environment.getExternalStorageDirectory().absolutePath + "/TempImages"
        if (!File(fileUrl).exists())
            File(fileUrl).mkdirs()
        fileUrl = "$fileUrl/temp.png"
        if (!File(fileUrl).exists())
            File(fileUrl).createNewFile()
        try {
            FileOutputStream(fileUrl).use(block = { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val intent = Intent()
        val arrayList = ArrayList<String>()
        arrayList.add(fileUrl)
        intent.putExtra("images", arrayList)
        imagePickActivity?.onFinishScreen(Activity.RESULT_OK, intent)
    }

}

/*
https://stackoverflow.com/questions/14066038/why-does-an-image-captured-using-camera-intent-gets-rotated-on-some-devices-on-a

ExifInterface ei = new ExifInterface(photoPath);
int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                     ExifInterface.ORIENTATION_UNDEFINED);



                                     public static Bitmap rotateImage(Bitmap source, float angle) {
    Matrix matrix = new Matrix();
    matrix.postRotate(angle);
    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                               matrix, true);
}*/