package com.iraimjanov.music

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.database.Cursor
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.iraimjanov.music.Objects.MediaPlayerService.mediaPlayer
import com.iraimjanov.music.Objects.Object
import com.iraimjanov.music.adapter.RecyclerViewAdapter
import com.iraimjanov.music.databinding.FragmentHomeBinding
import com.iraimjanov.music.models.Music

class HomeFragment : Fragment() {
    lateinit var handler: Handler
    private lateinit var binding: FragmentHomeBinding
    private lateinit var arrayListMusic: ArrayList<Music>
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private var positionMusic = Object.position
    private lateinit var runnable: Runnable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Object.homeFragment = true
        positionMusic = Object.position
        arrayListMusic = ArrayList()

        if (ActivityCompat.checkSelfPermission(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            buildRunnable()
            arrayListMusic = loadMusicInDevices()
            recyclerViewAdapter = RecyclerViewAdapter(requireActivity(),
                arrayListMusic,
                object : RecyclerViewAdapter.RvItemClick {
                    override fun itemClick(position: Int) {
                        positionMusic = position
                        mediaPlayer!!.stop()
                        mediaPlayer = MediaPlayer.create(requireActivity(), Uri.parse(arrayListMusic[position].musicPath))
                        playMusic(positionMusic)
                        mediaPlayer!!.start()
                    }
                })
            binding.recyclerView.adapter = recyclerViewAdapter

            if (Object.firstTime) {
                mediaPlayer = MediaPlayer.create(requireActivity(),
                    Uri.parse(arrayListMusic[positionMusic].musicPath))
                handler = Handler(Looper.getMainLooper())
                handler.postDelayed(runnable, 1)
                Object.firstTime = false
            }

            playMusic(positionMusic)
            buildMediaPlayerLayout()

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

            binding.imageBackMusic.setOnClickListener {
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

            binding.lyMusicPlay.setOnClickListener {
                Object.arrayListMusic = arrayListMusic
                Object.position = positionMusic
                findNavController().navigate(R.id.action_homeFragment_to_playFragment)
            }
        }
    }

    private fun buildRunnable() {
        runnable = object : Runnable {
            override fun run() {
                binding.progressBar.progress = mediaPlayer!!.currentPosition
                if (mediaPlayer!!.isPlaying) {
                    binding.imagePlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imagePlay.setImageResource(R.drawable.ic_play)
                }
                if (binding.progressBar.progress == binding.progressBar.max) {
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
                // So it doesn't work in another fragment
                if (Object.homeFragment) {
                    handler.postDelayed(this, 1)
                }
            }
        }
    }

    private fun buildMediaPlayerLayout() {
        binding.progressBar.progressTintList = ColorStateList.valueOf(Color.parseColor("#1E88E5"));
        binding.tvName.text = arrayListMusic[positionMusic].title
        binding.tvArtist.text = arrayListMusic[positionMusic].author
        binding.progressBar.max = mediaPlayer!!.duration
        if (mediaPlayer!!.isPlaying) {
            binding.imagePlay.setImageResource(R.drawable.ic_pause)
        }
    }

    private fun playMusic(position: Int) {
        binding.tvName.text = arrayListMusic[position].title
        binding.tvArtist.text = arrayListMusic[position].author
        Glide.with(requireActivity()).load(arrayListMusic[position].imagePath).centerCrop().into(binding.imageMusic)
        binding.progressBar.max = mediaPlayer!!.duration
        binding.imagePlay.setImageResource(R.drawable.ic_pause)
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1)
    }

    @SuppressLint("Range")
    private fun loadMusicInDevices(): ArrayList<Music> {
        // Initialize an empty  ArrayList of music
        val list = ArrayList<Music>()

        // Get the external storage media store audio uri
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        //val uri: Uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI

        // IS_MUSIC : Non-zero if the audio file is music
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"

        // Sort the musics
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        //val sortOrder = MediaStore.Audio.Media.TITLE + " DESC"

        // Query the external storage for music files
        val cursor: Cursor? = requireActivity().contentResolver.query(
            uri, // Uri
            null, // Projection
            selection, // Selection
            null, // Selection arguments
            sortOrder // Sort order
        )

        // If query result is not empty
        if (cursor != null && cursor.moveToFirst()) {
            val id: Int = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val title: Int = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val authorId: Int = cursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST)


            // Now loop through the music files
            do {
                val audioId: Long = cursor.getLong(id)
                val audioTitle: String = cursor.getString(title)
                val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                val uri = Uri.parse("content://media//external/audio/albumart")
                var imagePath = Uri.withAppendedPath(uri, albumIdC).toString()
                val musicPath: String =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val artist = cursor.getString(authorId)

                // Add the current music to the list
                list.add(Music(audioId, audioTitle, imagePath, musicPath, artist))
            } while (cursor.moveToNext())
        }
        // Finally, return the music files list
        return list
    }

    override fun onDestroyView() {
        Object.homeFragment = false
        super.onDestroyView()
    }

}



