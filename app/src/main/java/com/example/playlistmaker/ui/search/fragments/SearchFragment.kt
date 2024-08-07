package com.example.playlistmaker.ui.search.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.audioPlayer.fragment.AudioPlayerFragment
import com.example.playlistmaker.ui.search.SearchScreenState
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!


    private val viewModel by viewModel<SearchViewModel>()

    private var inputTextValue = DEF_TEXT

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var adapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var searchHistoryView: LinearLayout
    private lateinit var recyclerHistory: RecyclerView
    private lateinit var clearSearchHistoryBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchHistoryViewInit(binding.root) //инициализация view history

        viewModel.getState().observe(viewLifecycleOwner) { state ->
            when (state) {
                SearchScreenState.Loading -> {
                    loadingSearch()
                }

                is SearchScreenState.Content -> {
                    showTracks(state.trackList)
                }

                is SearchScreenState.Empty -> {
                    showEmptyError()
                }

                is SearchScreenState.Error -> {
                    showError()
                }

                is SearchScreenState.SearchHistoryContent -> {
                    hideTracks()
                    showHistory(state.trackList)
                }

                else -> {}
            }
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.onTrackClick(track)
        }

        viewModel.getTrackClickEvent().observe(viewLifecycleOwner) { track ->
            openPlayer(track)
        }

        adapter = TrackAdapter {
            onTrackClickDebounce(it)
        }
        historyAdapter = TrackAdapter {
            onTrackClickDebounce(it)
        }

        if (savedInstanceState != null) binding.inputEditText.setText(inputTextValue)


        //Выполнение запроса на поиск треков с кнопки на клавиатуре
        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingSearch()
                viewModel.searchDebounce(
                    changedText = inputTextValue
                )
                true
            }
            false
        }

        //Логика отображения истории поиска
        binding.inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputTextValue.isEmpty()) viewModel.showHistory()
        }

        //Отображение кнопки очистки поля поиска
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(p0)
                inputTextValue = p0.toString()

                if (inputTextValue.isNotEmpty()) {
                    viewModel.searchDebounce(
                        changedText = inputTextValue
                    )
                    adapter.trackList.clear()
                } else {
                    adapter.trackList.clear()
                    adapter.notifyDataSetChanged()
                }

                val hasHistory = historyAdapter.trackList.isNotEmpty()

                if (binding.inputEditText.hasFocus()
                    && inputTextValue.isEmpty()
                    && hasHistory
                ) {
                    viewModel.showHistory()
                    hideTracks()
                } else {
                    searchHistoryView.visibility = View.GONE
                }

            }

            override fun afterTextChanged(p0: Editable?) {
                //TODO("Not yet implemented")
            }
        }

        binding.inputEditText.addTextChangedListener(simpleTextWatcher)

        binding.clearIcon.setOnClickListener {
            binding.clearIcon.visibility = View.GONE
            binding.inputEditText.setText("")
            val inputMethodManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
            adapter.trackList.clear()
            clearPlaceholder()
            adapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
        }

        //Обновление результатов поиска, если интернет не подключен
        binding.btnRefresh.setOnClickListener {
            clearPlaceholder()
            viewModel.loadData(inputTextValue)
        }

        //Очистка истории поиска
        clearSearchHistoryBtn.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun loadingSearch() {
        Log.d("MyTag", "loading")
        clearPlaceholder()
        hideHistory()
        binding.searchProgressbar.visibility = View.VISIBLE
    }

    private fun showHistory(trackList: List<Track>?) {
        binding.searchProgressbar.visibility = View.GONE
        recyclerHistory.adapter = historyAdapter
        if (trackList.isNullOrEmpty()) {
            historyAdapter.trackList.clear()
            searchHistoryView.visibility = View.GONE
        } else {
            historyAdapter.trackList.clear()
            historyAdapter.trackList.addAll(trackList)
            searchHistoryView.visibility = View.VISIBLE
        }
        historyAdapter.notifyDataSetChanged()
    }

    private fun hideHistory() {
        searchHistoryView.visibility = View.GONE
    }

    private fun showTracks(trackList: List<Track>) {
        Log.d("MyTag", "show track")
        adapter.trackList.clear()
        binding.searchProgressbar.visibility = View.GONE
        binding.trackView.visibility = View.VISIBLE
        adapter.trackList.addAll(trackList)
        binding.trackView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun hideTracks() {
        adapter.trackList.clear()
        adapter.notifyDataSetChanged()
        binding.trackView.visibility = View.GONE
    }

    private fun showEmptyError() {
        binding.searchProgressbar.visibility = View.GONE
        searchHistoryView.visibility = View.GONE
        binding.ivPlaceholder.setImageResource(R.drawable.pic_search_error)
        binding.textError.setText(R.string.trackNotFound)
    }

    private fun showError() {
        binding.searchProgressbar.visibility = View.GONE
        searchHistoryView.visibility = View.GONE
        binding.ivPlaceholder.setImageResource(R.drawable.pic_network_error)
        binding.textError.setText(R.string.networkError)
        binding.btnRefresh.visibility = View.VISIBLE
    }

    private fun clearPlaceholder() {
        binding.ivPlaceholder.setImageResource(0)
        binding.textError.text = ""
        binding.btnRefresh.visibility = View.GONE
    }

    private fun openPlayer(jsonTrack: String) {
        findNavController().navigate(
            R.id.action_searchFragment_to_audioPlayerFragment,
            AudioPlayerFragment.createArgs(jsonTrack))
    }

    private fun searchHistoryViewInit(view: View) {
        searchHistoryView = view.findViewById(R.id.search_history)
        recyclerHistory = view.findViewById(R.id.rv_track_search_history)
        clearSearchHistoryBtn = view.findViewById(R.id.btn_clear_search_history)
    }

    companion object {
        const val DEF_TEXT = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}