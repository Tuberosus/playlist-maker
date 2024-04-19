package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    var trackList = ArrayList<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount() = trackList.size

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])

        holder.itemView.setOnClickListener {
            val sharedPrefs = holder.itemView.context.getSharedPreferences(SETTING_PREFERENCES,
                                                                            Context.MODE_PRIVATE)
            SearchHistory(sharedPrefs).apply {
                addTrackToHistory(trackList[position])
                write(trackSearchHistory)
            }

            //переход в аудиоплеер
            clickListener.onTrackClick(trackList[position])
        }
    }

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }
}