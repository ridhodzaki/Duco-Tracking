package com.rikikunproject.duinocoins.adapter

import android.content.ContentValues.TAG
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.rikikunproject.duinocoins.R
import com.rikikunproject.duinocoins.databinding.CardTransactionItemBinding
import com.rikikunproject.duinocoins.model.Transaction

class TransactionAdapter(private val transactionList: List<Transaction>, private val listener: OnItemClickListener, private val username: String
) : RecyclerView.Adapter<TransactionAdapter.CardViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardTransactionItemBinding.inflate(inflater, parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = transactionList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = transactionList.size

    inner class CardViewHolder(private val binding: CardTransactionItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.root.setOnClickListener {
                // Aksi saat item diklik
                Log.e(TAG, "Item clicked in TransactionAdapter")
                onItemClickListener?.onItemClick(transaction)
            }

            // Tambahkan logika pemformatan tanggal atau durasi sesuai kebutuhan
            binding.textMessageSender.text = transaction.memo
            binding.textSender.text = transaction.sender
            if (transaction.sender === username) {
                binding.imageView2.setImageResource(R.drawable.ic_up)
                binding.textDuco.text = "- ${transaction.amount}"
                binding.textDuco.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_200))
            } else {
                binding.imageView2.setImageResource(R.drawable.ic_down)
                binding.textDuco.text = "+ ${transaction.amount}"
                binding.textDuco.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green_200))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(transaction: Transaction)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}
