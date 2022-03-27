package com.iraimjanov.music.adapter

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iraimjanov.music.databinding.ItemMusicBinding
import com.iraimjanov.music.models.Music

class RecyclerViewAdapter(
    val context: Context,
    private val arrayList: ArrayList<Music>,
    private var rvItemClick: RvItemClick,
) :
    RecyclerView.Adapter<RecyclerViewAdapter.VH>() {

    inner class VH(var itemRV: ItemMusicBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(music: Music, position: Int) {
            itemRV.tvName.text = music.title
            itemRV.tvArtist.text = music.author

            Glide.with(context).load(music.imagePath).centerCrop().into(itemRV.imageMusic)

            itemView.setOnClickListener {

                rvItemClick.itemClick(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayList[position], position)
    }

    override fun getItemCount(): Int = arrayList.size

    interface RvItemClick {
        fun itemClick(position: Int)
    }

}