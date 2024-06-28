package com.rikikunproject.duinocoins.adapter

import android.content.ContentValues
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rikikunproject.duinocoins.databinding.CardMinersItemBinding
import com.rikikunproject.duinocoins.model.Miner

class MinersAdapter(private val minerList: List<Miner>, private val listener: OnItemClickListener
) : RecyclerView.Adapter<MinersAdapter.CardViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardMinersItemBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = minerList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = minerList.size

    inner class CardViewHolder(private val binding: CardMinersItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(miner: Miner) {
            binding.root.setOnClickListener {
                // Aksi saat item diklik
                Log.e(ContentValues.TAG, "Item clicked in MinersAdapter")
                onItemClickListener?.onItemClick(miner)
            }

            // Tambahkan logika pemformatan tanggal atau durasi sesuai kebutuhan
            binding.textHashrate.text = "${miner.hashrate/1000} kH/s"
            binding.textDiffMiners.text = "diff: ${miner.diff}"
            binding.textThreadsMiners.text = "thread: 1"
            binding.textNameMiners.text = "${miner.identifier}"
        }
    }

    interface OnItemClickListener {
        fun onItemClick(miner: Miner)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}