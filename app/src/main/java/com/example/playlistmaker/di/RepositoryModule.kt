package com.example.playlistmaker.di

import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.media.ExternalFilesNavigatorImpl
import com.example.playlistmaker.data.media.FavoriteTrackRepositoryImpl
import com.example.playlistmaker.data.media.PlaylistRepositoryImpl
import com.example.playlistmaker.data.player.GetTrackImpl
import com.example.playlistmaker.data.player.MediaPlayerManagerImpl
import com.example.playlistmaker.data.search.impl.GetJsonFromTrackImpl
import com.example.playlistmaker.data.search.impl.SearchHistoryImpl
import com.example.playlistmaker.data.search.impl.SearchTrackRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.data.sharing.impl.SharingRepositoryImpl
import com.example.playlistmaker.domain.db.FavoriteTrackRepository
import com.example.playlistmaker.domain.db.PlaylistRepository
import com.example.playlistmaker.domain.media.api.ExternalFilesNavigator
import com.example.playlistmaker.domain.player.api.GetTrack
import com.example.playlistmaker.domain.player.api.MediaPlayerManager
import com.example.playlistmaker.domain.search.api.GetJsonFromTrack
import com.example.playlistmaker.domain.search.api.SearchHistory
import com.example.playlistmaker.domain.search.api.SearchTrackRepository
import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.SharingRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    // Settings and Sharing
    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidApplication())
    }

    single<SharingRepository> {
        SharingRepositoryImpl(androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    // Search
    single<SearchTrackRepository> {
        SearchTrackRepositoryImpl(get())
    }

    single<SearchHistory> {
        SearchHistoryImpl(get())
    }

    single<GetJsonFromTrack> {
        GetJsonFromTrackImpl()
    }

    // Player
    factory<MediaPlayerManager> {
        MediaPlayerManagerImpl(get())
    }

    single<GetTrack> {
        GetTrackImpl()
    }

    // Media
    factory {
        TrackDbConvertor()
    }

    factory {
        PlaylistDbConvertor(get())
    }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(), get(), androidContext())
    }

    single<ExternalFilesNavigator> {
        ExternalFilesNavigatorImpl(androidApplication())
    }
}