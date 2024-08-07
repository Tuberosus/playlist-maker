package com.example.playlistmaker.domain.player.api

import com.example.playlistmaker.domain.models.Track

interface GetTrack {
    fun execute(jsonString: String): Track
}