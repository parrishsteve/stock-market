package com.parrishsystems.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parrishsystems.stock.model.LookupSymbol
import com.parrishsystems.stock.utils.Formatters
import java.util.*
import kotlin.collections.ArrayList

class LookupAdapter (val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClick {
        fun onMore()
        fun onSelect(symbol: LookupSymbol)
    }

    var onClickListener: OnClick? = null

    private val keywords: ArrayList<LookupSymbol> = arrayListOf()

    private val KEYWORD = 0
    private val MORE_FOOTER = 1

    var moreEnabled: Boolean = false
        set(value) {
           field = value
           notifyDataSetChanged()
        }


    fun setItems(newList: List<LookupSymbol>) {
        val diffResult = DiffUtil.calculateDiff(LookupDiff(keywords, newList))
        keywords.clear()
        notifyDataSetChanged()
        keywords.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemViewType(position: Int): Int {
        if (position >= keywords.size) return MORE_FOOTER else return KEYWORD
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            KEYWORD -> {
                KeywordHolder(LayoutInflater.from(context).inflate(R.layout.lookup_adapter_item, parent, false))
            }
            else -> {
                MoreHolder(LayoutInflater.from(context).inflate(R.layout.lookup_adapter_more, parent, false))
            }
        }
    }

    override fun getItemCount(): Int {
        if (moreEnabled.not()) return keywords.size else return keywords.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is KeywordHolder) {
            val ls = keywords.get(position)
            holder.sym.text = ls.symbol
            holder.name.text = ls.name
            holder.exchange.text = ls.stockExchange
            ls.price?.apply {
                holder.price.text = Formatters.formatCurrency(this)
            }

        }
        else {

        }
    }

    inner class KeywordHolder(view: View): RecyclerView.ViewHolder(view) {
        val sym = view.findViewById<TextView>(R.id.tvSymbol)
        val name = view.findViewById<TextView>(R.id.tvName)
        val exchange = view.findViewById<TextView>(R.id.tvExchange)
        val price = view.findViewById<TextView>(R.id.tvPrice)
        init {
            view.setOnClickListener {
                val ls = keywords.get(adapterPosition)
                onClickListener?.onSelect(ls)
            }
        }
    }

    inner class MoreHolder(view: View): RecyclerView.ViewHolder(view) {
        init {
            view.setOnClickListener {
                onClickListener?.onMore()
            }
        }
    }

    class LookupDiff(val oldList: List<LookupSymbol>, val newList: List<LookupSymbol>): DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList.get(oldItemPosition).symbol.equals(newList.get(newItemPosition).symbol, true)
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }
    }
}