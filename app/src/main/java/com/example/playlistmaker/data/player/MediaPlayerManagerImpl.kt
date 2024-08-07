package com.example.playlistmaker.data.player

import android.media.MediaPlayer
import com.example.playlistmaker.domain.player.PlayerState
import com.example.playlistmaker.domain.player.api.MediaPlayerManager

class MediaPlayerManagerImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerManager {

    override var state = PlayerState.DEFAULT

    override fun preparePlayer(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                state = PlayerState.PREPARED
            }
        }
    }

    override fun onCompletionWork(callback :()->Unit) {
        mediaPlayer.setOnCompletionListener {
            state = PlayerState.DONE
            callback()
        }
    }

    override fun play() {
        mediaPlayer.start()
        state = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer.pause()
        state = PlayerState.PAUSED
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}