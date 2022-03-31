package com.iraimjanov.music

import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.iraimjanov.music.Objects.MediaPlayerService
import com.iraimjanov.music.Objects.MediaPlayerService.mediaPlayer
import com.iraimjanov.music.Objects.Object
import com.iraimjanov.music.Objects.Object.booleanFavoriteMode
import com.iraimjanov.music.adapter.FavoriteRecyclerViewAdapter
import com.iraimjanov.music.adapter.RecyclerViewAdapter
import com.iraimjanov.music.databinding.FragmentFavoriteBinding
import com.iraimjanov.music.models.AppDatabase
import com.iraimjanov.music.models.FavoriteMusic
import com.iraimjanov.music.models.Music

class FavoriteFragment : Fragment() {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var favoriteRecyclerViewAdapter: FavoriteRecyclerViewAdapter
    lateinit var appDatabase: AppDatabase
    lateinit var arrayListFavoriteMusic: ArrayList<FavoriteMusic>
    lateinit var arrayListMusic: ArrayList<Music>
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private var positionMusic = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoriteBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        createBase()

        favoriteRecyclerViewAdapter = FavoriteRecyclerViewAdapter(requireActivity(),
            arrayListFavoriteMusic,
            object : FavoriteRecyclerViewAdapter.RvItemClickFavorite {
                override fun itemClick(position: Int) {
                    positionMusic = position
                    booleanFavoriteMode = true
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[position].musicPath))
                    playMusic(position)
                    mediaPlayer!!.start()
                }
            })
        binding.recyclerView.adapter = favoriteRecyclerViewAdapter

        binding.imagePlay.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                binding.imagePlay.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer!!.start()
                binding.imagePlay.setImageResource(R.drawable.ic_pause)
            }
        }

        binding.imageNextMusic.setOnClickListener {
            if (booleanFavoriteMode) {
                if (++positionMusic < arrayListFavoriteMusic.size) {
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                } else {
                    positionMusic = 0
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                }
            } else {
                if (++positionMusic < arrayListMusic.size) {
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                } else {
                    positionMusic = 0
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                }
            }
        }

        binding.imageBackMusic.setOnClickListener {
            if (booleanFavoriteMode) {
                if (--positionMusic >= 0) {
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                } else {
                    positionMusic = arrayListFavoriteMusic.size - 1
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                }
            } else {
                if (--positionMusic >= 0) {
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                } else {
                    positionMusic = arrayListMusic.size - 1
                    mediaPlayer!!.stop()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[positionMusic].musicPath))
                    playMusic(positionMusic)
                    mediaPlayer!!.start()
                }
            }
        }

        binding.lyMusicPlay.setOnClickListener {
            if (booleanFavoriteMode){
                Object.positionFavoriteMusic = positionMusic
            }else{
                Object.arrayListMusic = arrayListMusic
                Object.position = positionMusic
            }
            findNavController().navigate(R.id.action_favoriteFragment2_to_playFragment)
        }
    }

    private fun buildRunnable() {
        runnable = object : Runnable {
            override fun run() {
                println("FavoriteFragment!! runnable")
                binding.progressBar.progress = mediaPlayer!!.currentPosition
                if (mediaPlayer!!.isPlaying) {
                    binding.imagePlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imagePlay.setImageResource(R.drawable.ic_play)
                }
                if (binding.progressBar.progress == binding.progressBar.max) {
                    if (booleanFavoriteMode){
                        if (++positionMusic < arrayListFavoriteMusic.size) {
                            Object.positionFavoriteMusic = positionMusic
                            mediaPlayer!!.stop()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                            playMusic(positionMusic)
                            mediaPlayer!!.start()
                        } else {
                            positionMusic = 0
                            Object.positionFavoriteMusic = positionMusic
                            mediaPlayer!!.stop()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListFavoriteMusic[positionMusic].musicPath))
                            playMusic(positionMusic)
                            mediaPlayer!!.start()
                        }
                    }else {
                        if (++positionMusic < arrayListMusic.size) {
                            Object.position = positionMusic
                            mediaPlayer!!.stop()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListMusic[positionMusic].musicPath))
                            playMusic(positionMusic)
                            mediaPlayer!!.start()
                        } else {
                            positionMusic = 0
                            Object.position = positionMusic
                            mediaPlayer!!.stop()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListMusic[positionMusic].musicPath))
                            playMusic(positionMusic)
                            mediaPlayer!!.start()
                        }
                    }
                }
                // So it doesn't work in another fragment
                if (Object.favoriteFragment) {
                    handler.postDelayed(this, 1)
                }
            }
        }
    }

    private fun buildMediaPlayerLayout() {
        if (booleanFavoriteMode){
            binding.tvName.text = arrayListFavoriteMusic[positionMusic].title
            binding.tvArtist.text = arrayListFavoriteMusic[positionMusic].author
            Glide.with(requireActivity()).load(arrayListFavoriteMusic[positionMusic].imagePath).centerCrop()
                .into(binding.imageMusic)
        }else{
            binding.tvName.text = arrayListMusic[positionMusic].title
            binding.tvArtist.text = arrayListMusic[positionMusic].author
            Glide.with(requireActivity()).load(arrayListMusic[positionMusic].imagePath).centerCrop()
                .into(binding.imageMusic)
        }

        binding.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#1E88E5"));
        binding.progressBar.max = mediaPlayer!!.duration
        if (mediaPlayer!!.isPlaying) {
            binding.imagePlay.setImageResource(R.drawable.ic_pause)
        }
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1)
    }

    private fun playMusic(position: Int) {
        if (booleanFavoriteMode){
            binding.tvName.text = arrayListFavoriteMusic[position].title
            binding.tvArtist.text = arrayListFavoriteMusic[position].author
            Glide.with(requireActivity()).load(arrayListFavoriteMusic[position].imagePath).centerCrop()
                .into(binding.imageMusic)
        }else{
            binding.tvName.text = arrayListMusic[position].title
            binding.tvArtist.text = arrayListMusic[position].author
            Glide.with(requireActivity()).load(arrayListMusic[position].imagePath).centerCrop()
                .into(binding.imageMusic)
        }
        binding.progressBar.max = mediaPlayer!!.duration
        binding.imagePlay.setImageResource(R.drawable.ic_pause)
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1)
    }

    private fun buildPosition(id: Long?): Int {
        var int = 0
        for (i in 0 until arrayListMusic.size) {
            if (arrayListMusic[i].id == id) {
                int = i
            }
        }
        return int
    }

    private fun createBase() {
        buildRunnable()
        appDatabase = AppDatabase.getInstance(requireActivity())
        arrayListFavoriteMusic = ArrayList()
        arrayListMusic = ArrayList()
        arrayListMusic = Object.arrayListMusic
        arrayListFavoriteMusic = appDatabase.myDao().show() as ArrayList<FavoriteMusic>

        positionMusic = if (booleanFavoriteMode){
            Object.positionFavoriteMusic
        }else{
            Object.position
        }

        buildMediaPlayerLayout()
        Object.favoriteFragment = true
    }

    override fun onStop() {
        if (booleanFavoriteMode){
            Object.positionFavoriteMusic = positionMusic
            positionMusic = buildPosition(arrayListFavoriteMusic[positionMusic].id)
        }
        Object.position = positionMusic
        super.onStop()
    }

    override fun onDestroyView() {
        Object.favoriteFragment = false
        super.onDestroyView()
    }

}