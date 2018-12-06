package com.lite.imagepickerlib

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.gallery_images_list_item_layout.view.*
import java.io.File

class GalleryAdapter(private val context: Context, private val items: List<ImageFile>, private val itemClickListener: ItemClickListener?,
                     private var totalSelected: Int) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var selectionFlag = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent!!.context).inflate(R.layout.gallery_images_list_item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile = items[position]
        Glide.with(context).load(File(imageFile.path)).apply(RequestOptions().override(180)).into(holder!!.itemView.image)
        holder!!.itemView.check_icon.visibility = if (selectionFlag) View.VISIBLE else View.GONE
        if (imageFile.checked == 1) {
            holder.itemView.check_icon.setImageResource(R.drawable.ic_checked_icon)
        } else {
            holder.itemView.check_icon.setImageResource(R.drawable.ic_un_checked_icon)
        }

        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClick(position)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }

    fun updateSelection(selection: Boolean) {
        selectionFlag = selection
        notifyDataSetChanged()
    }

}