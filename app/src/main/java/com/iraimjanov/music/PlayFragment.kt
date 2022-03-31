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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.iraimjanov.music.Objects.MediaPlayerService.mediaPlayer
import com.iraimjanov.music.Objects.Object
import com.iraimjanov.music.Objects.Object.arrayListMusic
import com.iraimjanov.music.databinding.FragmentPlayBinding
import com.iraimjanov.music.models.AppDatabase
import com.iraimjanov.music.models.FavoriteMusic

class PlayFragment : Fragment() {
    private lateinit var binding: FragmentPlayBinding
    private lateinit var arrayListFavoriteMusic: ArrayList<FavoriteMusic>
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var appDatabase: AppDatabase
    private var position = 0
    private var mode = "order"
    private var booleanProgress = true
    private var liked = false
    private var favorideMode = false
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
        appDatabase = AppDatabase.getInstance(requireActivity())
        arrayListFavoriteMusic = appDatabase.myDao().show() as ArrayList<FavoriteMusic>
        favorideMode = Object.booleanFavoriteMode
        position = if (favorideMode) {
            Object.positionFavoriteMusic
        } else {
            Object.position
        }
        Object.playFragment = true
        appDatabase = AppDatabase.getInstance(requireActivity())

        if (favorideMode) {
            liked = buildLiked(arrayListFavoriteMusic[position].id!!)
            Glide.with(requireActivity()).load(arrayListMusic[position].imagePath).centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        } else {
            liked = buildLiked(arrayListMusic[position].id)
            Glide.with(requireActivity()).load(arrayListMusic[position].imagePath).centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        }

        countDownTimer.start()
        buildRunnable()
        loadData()
        buildImageViewMode()

        if (liked) {
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
            if (favorideMode) {
                if (++position < arrayListFavoriteMusic.size) {
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                } else {
                    position = 0
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                }
            } else {
                if (++position < arrayListMusic.size) {
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                } else {
                    position = 0
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                }
            }
        }

        binding.imageBackMusic.setOnClickListener {
            if (favorideMode) {
                if (--position >= 0) {
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                } else {
                    position = arrayListFavoriteMusic.size - 1
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListFavoriteMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                }
            } else {
                if (--position >= 0) {
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                } else {
                    position = arrayListMusic.size - 1
                    mediaPlayer!!.release()
                    mediaPlayer = MediaPlayer.create(requireActivity(),
                        Uri.parse(arrayListMusic[position].musicPath))
                    loadData()
                    mediaPlayer!!.start()
                }
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
            if (boolean) {
                val favoriteMusic = FavoriteMusic()
                favoriteMusic.id = arrayListMusic[Object.position].id
                favoriteMusic.title = arrayListMusic[Object.position].title
                favoriteMusic.imagePath = arrayListMusic[Object.position].imagePath
                favoriteMusic.musicPath = arrayListMusic[Object.position].musicPath
                favoriteMusic.author = arrayListMusic[Object.position].author
                appDatabase.myDao().deleteMusic(favoriteMusic)
                binding.imageLiked.setImageResource(R.drawable.ic_favorite_border)
                liked = false
            } else {
                val favoriteMusic = FavoriteMusic()
                favoriteMusic.id = arrayListMusic[Object.position].id
                favoriteMusic.title = arrayListMusic[Object.position].title
                favoriteMusic.imagePath = arrayListMusic[Object.position].imagePath
                favoriteMusic.musicPath = arrayListMusic[Object.position].musicPath
                favoriteMusic.author = arrayListMusic[Object.position].author
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
            if (i.id == id) {
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
                    if (favorideMode) {
                        if (++position < arrayListFavoriteMusic.size) {
                            Object.positionFavoriteMusic = position
                            mediaPlayer!!.release()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListFavoriteMusic[position].musicPath))
                            loadData()
                            mediaPlayer!!.start()
                        } else {
                            position = 0
                            Object.positionFavoriteMusic = position
                            mediaPlayer!!.release()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListFavoriteMusic[position].musicPath))
                            loadData()
                            mediaPlayer!!.start()
                        }
                    } else {
                        if (++position < arrayListMusic.size) {
                            Object.position = position
                            mediaPlayer!!.release()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListMusic[position].musicPath))
                            loadData()
                            mediaPlayer!!.start()
                        } else {
                            position = 0
                            Object.position = position
                            mediaPlayer!!.release()
                            mediaPlayer = MediaPlayer.create(requireActivity(),
                                Uri.parse(arrayListMusic[position].musicPath))
                            loadData()
                            mediaPlayer!!.start()
                        }
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
        if (favorideMode) {
            binding.tvName.text = arrayListFavoriteMusic[position].title
            binding.tvArtist.text = arrayListFavoriteMusic[position].author
            Glide.with(requireActivity()).load(arrayListFavoriteMusic[position].imagePath)
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        } else {
            binding.tvName.text = arrayListMusic[position].title
            binding.tvArtist.text = arrayListMusic[position].author
            Glide.with(requireActivity()).load(arrayListMusic[position].imagePath).centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.song)).into(binding.imageMusicPhoto)
        }

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

    private fun buildPosition(id: Long?): Int {
        var int = 0
        for (i in 0 until arrayListMusic.size) {
            if (arrayListMusic[i].id == id) {
                int = i
            }
        }
        return int
    }

    override fun onStop() {
        if (favorideMode) {
            Object.positionFavoriteMusic = position
        } else {
            Object.position = position
        }
        super.onStop()
    }

    override fun onDestroyView() {
        Object.playFragment = false
        if (favorideMode) {
            if (!liked) {
                Object.booleanFavoriteMode = false
                Object.position = buildPosition(arrayListFavoriteMusic[position].id)
            }
        }
        super.onDestroyView()
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
}
