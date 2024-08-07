package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.settings.SettingsInteractor
import com.example.playlistmaker.domain.settings.models.ThemeSettings
import com.example.playlistmaker.domain.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    //Sharing
    private val sharingLiveData = MutableLiveData<Unit>()


    fun openTerms() {
        sharingLiveData.value = sharingInteractor.openTerms()
    }

    fun sharingApp() {
        sharingLiveData.value = sharingInteractor.sharingApp()
    }

    fun openSupport() {
        sharingLiveData.value = sharingInteractor.openSupport()

    }

    //Settings
    private val settingsLiveData = MutableLiveData<ThemeSettings>()
    fun getSettingsLiveData(): LiveData<ThemeSettings> = settingsLiveData
    fun switchTheme(isDark: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(isDark))
        settingsLiveData.value = settingsInteractor.getSettingTheme()
    }
}