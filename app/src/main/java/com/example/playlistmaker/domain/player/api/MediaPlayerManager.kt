package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.domain.player.PlayerState

interface MediaPlayerManager {
    var state: PlayerState
    fun preparePlayer(url: String,
//                      callback: () -> Unit
    )
    fun play()
    fun pause()
    fun release()
    fun getCurrentPosition(): Int
}