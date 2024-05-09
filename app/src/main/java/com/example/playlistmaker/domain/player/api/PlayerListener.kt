package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.domain.models.PlayerState

interface PlayerListener {
    fun onStateChange(state: PlayerState)
}