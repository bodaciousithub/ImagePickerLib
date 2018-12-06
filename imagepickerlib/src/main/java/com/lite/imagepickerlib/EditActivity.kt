package com.lite.imagepickerlib

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.edit_activity.*
import kotlinx.android.synthetic.main.filter_view.view.*
import java.io.File

class EditActivity : AppCompatActivity() {

    private lateinit var imagePath: String
    private lateinit var originalBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)

        imagePath = intent.getStringArrayListExtra("images")[0]

        Glide.with(this).asBitmap().load(File(imagePath)).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        originalBitmap = resource
                        setImage(originalBitmap)
                    }
                })
        Glide.with(this).asBitmap().load(File(imagePath)).apply(RequestOptions().override(140)).into(object : SimpleTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        setFilterViews(resource)
                    }
                })
    }

    private fun setImage(bitmap: Bitmap) {
        image.setImageBitmap(bitmap)
    }

    private fun setFilterViews(bitmap: Bitmap) {
        for (index in 0..8) {
            val filterView = LayoutInflater.from(this).inflate(R.layout.filter_view, null, false)
            filterView.filter_image.setImageBitmap(getFilterBitmap(bitmap, index))
            if (index == 0)
                filterView.filter_name.text = "Original"
            else
                filterView.filter_name.text = "$index"
            filterView.setOnClickListener(ClickListener(index))
            filter_views.addView(filterView)
        }
    }

    private inner class ClickListener(private val id: Int): View.OnClickListener {
        override fun onClick(view: View) {
            setImage(getFilterBitmap(originalBitmap, id))
        }
    }

    private fun getFilterBitmap(bitmap: Bitmap, value: Int) : Bitmap {
        return when(value) {
            /*1 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GRAY)
            2 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.RELIEF)
            3 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.AVERAGE_BLUR, 9)
            4 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OIL, 10)
            5 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.NEON,200, 50, 100)
            6 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.PIXELATE,9)
            7 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.TV)
            8 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.INVERT)
            9 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.BLOCK)
            10 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.OLD)
            11 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SHARPEN)
            12 -> {
                val width = bitmap.width
                val height = bitmap.height
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.LIGHT,width / 2, height / 2, Math.min(width / 2, height / 2))
            }
            13 -> {
                val radius = bitmap.width / 2 * 95 / 100
                ImageFilter.applyFilter(bitmap, ImageFilter.Filter.LOMO, radius)
            }
            14 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.HDR)
            15 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.GAUSSIAN_BLUR,1.2)
            16 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SOFT_GLOW,0.6)
            17 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.SKETCH)
            18 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.MOTION_BLUR,5,1)
            19 -> ImageFilter.applyFilter(bitmap, ImageFilter.Filter.MOTION_BLUR,5,1)*/
            else -> bitmap
        }
    }

}
