package com.iraimjanov.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.iraimjanov.music.databinding.ItemMusicBinding
import com.iraimjanov.music.models.FavoriteMusic

class FavoriteRecyclerViewAdapter(val context: Context, private val arrayList: ArrayList<FavoriteMusic> , private val rvItemClickFavorite: RvItemClickFavorite) :
    RecyclerView.Adapter<FavoriteRecyclerViewAdapter.VH>() {

    inner class VH(private var itemRV: ItemMusicBinding) : RecyclerView.ViewHolder(itemRV.root) {
        fun onBind(favoriteMusic: FavoriteMusic , position: Int) {
            itemRV.tvName.text = favoriteMusic.title
            itemRV.tvArtist.text = favoriteMusic.author
            Glide.with(context).load(favoriteMusic.imagePath).centerCrop().into(itemRV.imageMusic)

            itemRV.root.setOnClickListener {
                rvItemClickFavorite.itemClick(position)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemMusicBinding.inflate(LayoutInflater.from(parent.context) , parent , false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(arrayList[position] , position)
    }

    override fun getItemCount(): Int = arrayList.size

    interface RvItemClickFavorite {
        fun itemClick(position: Int)
    }

}