package com.example.playlistmaker.ui.media.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteTrackBinding
import com.example.playlistmaker.ui.media.view_model.FavoriteTrackViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTrackFragment : Fragment() {

    companion object {
        fun newInstance() = FavoriteTrackFragment()
    }

    private val viewModel by viewModel<FavoriteTrackViewModel>()

    private var _binding: FragmentFavoriteTrackBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteTrackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}