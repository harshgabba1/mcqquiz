package com.example.mcqquiz.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mcqquiz.R

class OptionsAdapter(
    private val onClick: (Int) -> Unit
) : RecyclerView.Adapter<OptionsAdapter.OptionVH>() {

    private var items: List<String> = emptyList()
    private var reveal: Boolean = false
    private var correctIndex: Int? = null
    private var selectedIndex: Int? = null

    fun submitList(list: List<String>) {
        items = list
        reveal = false
        correctIndex = null
        selectedIndex = null
        notifyDataSetChanged()
    }

    fun setRevealState(revealState: Boolean, answerIndex: Int?) {
        reveal = revealState
        correctIndex = answerIndex
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OptionVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_option, parent, false)
        return OptionVH(view)
    }

    override fun onBindViewHolder(holder: OptionVH, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size

    inner class OptionVH(view: View) : RecyclerView.ViewHolder(view) {
        private val text: TextView = view.findViewById(R.id.optionText)

        init {
            view.setOnClickListener {
                if (!reveal) {
                    selectedIndex = adapterPosition
                    notifyDataSetChanged()
                    onClick(adapterPosition)
                }
            }
        }

        fun bind(option: String, pos: Int) {
            text.text = option
            // update UI depending on reveal + correct index
            if (reveal) {
                when {
                    correctIndex == pos -> {
                        itemView.setBackgroundResource(R.drawable.bg_option_correct)
                    }
                    selectedIndex == pos -> {
                        itemView.setBackgroundResource(R.drawable.bg_option_incorrect)
                    }
                    else -> {
                        itemView.setBackgroundResource(R.drawable.bg_option_default)
                    }
                }
            } else {
                if (selectedIndex == pos) {
                    itemView.setBackgroundResource(R.drawable.bg_option_selected)
                } else {
                    itemView.setBackgroundResource(R.drawable.bg_option_default)
                }
            }
        }
    }
}
