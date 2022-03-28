package com.iraimjanov.music

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iraimjanov.music.Objects.MediaPlayerService.mediaPlayer
import com.iraimjanov.music.Objects.Object
import com.iraimjanov.music.databinding.FragmentPlayBinding
import com.iraimjanov.music.models.AppDatabase
import com.iraimjanov.music.models.FavoriteMusic

class PlayFragment : Fragment() {
    lateinit var binding: FragmentPlayBinding
    lateinit var handler: Handler
    lateinit var runnable: Runnable
    lateinit var appDatabase: AppDatabase
    private var position = Object.position
    private var mode = "order"
    private var booleanProgress = true
    private var liked = false
    private val countDownTimer = object : CountDownTimer(1000, 1) {
        override fun onTick(millisUntilFinished: Long) {
            val layoutParams = binding.cardMusicPhoto.layoutParams
            layoutParams.width = binding.cardMusicPhoto.width
            layoutParams.height = binding.cardMusicPhoto.width
            binding.cardMusicPhoto.layoutParams = layoutParams
        }

        override fun onFinish() {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPlayBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Object.playFragment = true
        appDatabase = AppDatabase.getInstance(requireActivity())
        liked = buildLiked(Object.arrayListMusic[position].id)
        Glide.with(requireActivity()).load(Object.arrayListMusic[position].imagePath).centerCrop()
            .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        countDownTimer.start()
        buildRunnable()
        loadData()
        buildImageViewMode()

        if(liked){
            binding.imageLiked.setImageResource(R.drawable.ic_favorite)
        }

        binding.imageBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.imagePlay.setOnClickListener {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                binding.imagePlay.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer!!.start()
                binding.imagePlay.setImageResource(R.drawable.ic_pause)
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                booleanProgress = false
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                booleanProgress = true
                mediaPlayer!!.seekTo(seekBar!!.progress)
                binding.tvTimer.text = milliSecondsToTimer(seekBar.progress.toLong())
            }
        })

        binding.imageNextMusic.setOnClickListener {
            if (++position < Object.arrayListMusic.size) {
                mediaPlayer!!.release()
                mediaPlayer = MediaPlayer.create(requireActivity(),
                    Uri.parse(Object.arrayListMusic[position].musicPath))
                loadData()
                mediaPlayer!!.start()
            } else {
                position = 0
                mediaPlayer!!.release()
                mediaPlayer = MediaPlayer.create(requireActivity(),
                    Uri.parse(Object.arrayListMusic[position].musicPath))
                loadData()
                mediaPlayer!!.start()
            }
        }

        binding.imageBackMusic.setOnClickListener {
            if (--position >= 0) {
                mediaPlayer!!.release()
                mediaPlayer = MediaPlayer.create(requireActivity(),
                    Uri.parse(Object.arrayListMusic[position].musicPath))
                loadData()
                mediaPlayer!!.start()
            } else {
                position = Object.arrayListMusic.size - 1
                mediaPlayer!!.release()
                mediaPlayer = MediaPlayer.create(requireActivity(),
                    Uri.parse(Object.arrayListMusic[position].musicPath))
                loadData()
                mediaPlayer!!.start()
            }
        }

        binding.imageBackMusicSecond.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.minus(10000))
            binding.tvTimer.text = milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
        }

        binding.imageNextMusicSecond.setOnClickListener {
            mediaPlayer!!.seekTo(mediaPlayer!!.currentPosition.plus(10000))
            binding.tvTimer.text = milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
        }

        binding.imageMode.setOnClickListener {
            when (mode) {
                "order" -> {
                    mode = "repeat"
                    buildImageViewMode()
                }
                "repeat" -> {
                    mode = "random"
                    buildImageViewMode()
                }
                "random" -> {
                    mode = "order"
                    buildImageViewMode()
                }
            }
        }

        binding.imageLiked.setOnClickListener {
            val boolean = liked
            if (boolean){
                val favoriteMusic = FavoriteMusic()
                favoriteMusic.id = Object.arrayListMusic[Object.position].id
                favoriteMusic.title = Object.arrayListMusic[Object.position].title
                favoriteMusic.imagePath = Object.arrayListMusic[Object.position].imagePath
                favoriteMusic.musicPath = Object.arrayListMusic[Object.position].musicPath
                favoriteMusic.author = Object.arrayListMusic[Object.position].author
                appDatabase.myDao().deleteMusic(favoriteMusic)
                binding.imageLiked.setImageResource(R.drawable.ic_favorite_border)
                liked = false
            }else{
                val favoriteMusic = FavoriteMusic()
                favoriteMusic.id = Object.arrayListMusic[Object.position].id
                favoriteMusic.title = Object.arrayListMusic[Object.position].title
                favoriteMusic.imagePath = Object.arrayListMusic[Object.position].imagePath
                favoriteMusic.musicPath = Object.arrayListMusic[Object.position].musicPath
                favoriteMusic.author = Object.arrayListMusic[Object.position].author
                appDatabase.myDao().addMusic(favoriteMusic)
                binding.imageLiked.setImageResource(R.drawable.ic_favorite)
                liked = true
            }

        }
    }

    private fun buildLiked(id: Long): Boolean {
        var boolean = false
        val arrayListLiked = appDatabase.myDao().show() as ArrayList
        for (i in arrayListLiked) {
            if (i.id == id){
                boolean = true
            }
        }
        return boolean
    }

    private fun buildRunnable() {
        runnable = object : Runnable {
            override fun run() {
                binding.tvTimer.text = milliSecondsToTimer(mediaPlayer!!.currentPosition.toLong())
                if (booleanProgress) {
                    binding.seekBar.progress = mediaPlayer!!.currentPosition
                }
                if (mediaPlayer!!.isPlaying) {
                    binding.imagePlay.setImageResource(R.drawable.ic_pause)
                } else {
                    binding.imagePlay.setImageResource(R.drawable.ic_play)
                }
                if (binding.tvTimer.text == binding.tvTime.text) {
                    if (++position < Object.arrayListMusic.size) {
                        mediaPlayer!!.release()
                        try {
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(Object.arrayListMusic[position].musicPath))
                        } catch (e: IllegalStateException) {
                            print(e)
                        }
                        loadData()
                        mediaPlayer!!.start()
                    } else {
                        position = 0
                        mediaPlayer!!.release()
                        try {
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(Object.arrayListMusic[position].musicPath))
                        } catch (e: IllegalStateException) {
                            print(e)
                        }
                        loadData()
                        mediaPlayer!!.start()
                    }
                }
                // So it doesn't work in another fragment
                if (Object.playFragment) {
                    handler.postDelayed(this, 1)
                }
            }
        }
    }

    private fun buildImageViewMode() {
        when (mode) {
            "order" -> {
                binding.imageMode.setImageResource(R.drawable.ic_order)
            }
            "repeat" -> {
                binding.imageMode.setImageResource(R.drawable.ic_repeat)
            }
            "random" -> {
                binding.imageMode.setImageResource(R.drawable.ic_shuffle)
            }
        }
    }

    private fun loadData() {
        binding.tvName.text = Object.arrayListMusic[position].title
        binding.tvArtist.text = Object.arrayListMusic[position].author
        Glide.with(requireActivity()).load(Object.arrayListMusic[position].imagePath).centerCrop()
            .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        binding.tvTime.text = milliSecondsToTimer(mediaPlayer!!.duration.toLong())
        binding.seekBar.progress = mediaPlayer!!.currentPosition
        binding.seekBar.max = mediaPlayer!!.duration
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1)
        if (mediaPlayer!!.isPlaying) {
            binding.imagePlay.setImageResource(R.drawable.ic_pause)
        } else {
            binding.imagePlay.setImageResource(R.drawable.ic_play)
        }

    }

    fun milliSecondsToTimer(milliseconds: Long): String {
        var finalTimerString = ""
        var secondsString = ""

        // Convert total duration into time
        val hours = (milliseconds / (1000 * 60 * 60)).toInt()
        val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
        val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
        // Add hours if there
        if (hours > 0) {
            finalTimerString = "$hours:"
        }

        // Prepending 0 to seconds if it is one digit
        secondsString = if (seconds < 10) {
            "0$seconds"
        } else {
            "" + seconds
        }
        finalTimerString = "$finalTimerString$minutes:$secondsString"

        // return timer string
        return finalTimerString
    }

    override fun onStop() {
        Object.position = position
        super.onStop()
    }

    override fun onDestroyView() {
        Object.playFragment = false
        super.onDestroyView()
    }
}