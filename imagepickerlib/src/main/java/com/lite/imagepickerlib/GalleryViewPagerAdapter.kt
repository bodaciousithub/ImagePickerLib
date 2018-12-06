package com.lite.imagepickerlib

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class GalleryViewPagerAdapter(private val context: Context, fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 ->  {
                val galleryFragment = GalleryFragment()
                galleryFragment.setImagePickActivity(context as GalleryActivity)
                galleryFragment
            }
            else -> {
                val photoFragment = PhotoFragment()
                photoFragment.setImagePickActivity(context as GalleryActivity)
                photoFragment
            }
            /*else -> {
                val videoFragment = VideoFragment()
                videoFragment.setImagePickActivity(context as GalleryActivity)
                videoFragment
            }*/
        }
    }

    override fun getCount(): Int {
        return 2
    }
}