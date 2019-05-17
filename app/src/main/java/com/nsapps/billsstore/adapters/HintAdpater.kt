package com.nsapps.billsstore.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nsapps.billsstore.R

class HintAdpater(var mContext: Context, var itemsList: List<String>, var onHintClick: OnHintClick) :
    RecyclerView.Adapter<HintAdpater.HintViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HintViewHolder {
        return HintViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_hint, parent, false))
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    override fun onBindViewHolder(holder: HintViewHolder, position: Int) {
        holder.bind(itemsList.get(position))
    }

    inner class HintViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var tvHint: TextView

        init {
            tvHint = view.findViewById(R.id.tv_hint)

            itemView.setOnClickListener(object : View.OnClickListener {
                override fun onClick(p0: View?) {
                    onHintClick.onClick(position)
                }
            })

        }


        fun bind(text: String) {

            tvHint.text = text.trim()
        }

    }

    interface OnHintClick {
        fun onClick(position: Int)
    }

}