package com.parrishsystems.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parrishsystems.stock.model.Quote
import com.parrishsystems.stock.utils.Formatters
import kotlinx.coroutines.*


class SymbolAdapter(val context: Context, var data: ArrayList<Quote>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnClick {
        fun onDelete(view: View, position: Int, symbol: String)
    }

    private val SYMBOL_VIEW = R.layout.symbol_adapter_item
    private val EMPTY_VIEW = R.layout.symbol_adapter_empty

    var onClickListener: OnClick? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == SYMBOL_VIEW) {
            holder as Holder
            val q = data.get(position)
            holder.sym.text = q.symbol
            holder.name.text = q.name
            holder.price.text = ""
            q.price?.let {
                holder.price.text = Formatters.formatCurrency(it)
            }
            holder.open.text = ""
            q.open?.let {
                holder.open.text = Formatters.formatCurrency(it)
            }

            if (q.price != null && q.open != null) {
                if (q.price >= q.open) {
                    holder.price.setTextAppearance(R.style.CurrentPriceUp)
                } else {
                    holder.price.setTextAppearance(R.style.CurrentPriceDown)
                }
            }
        }
        // else do nothing, what we inflate is good enough.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == SYMBOL_VIEW) {
            return Holder(
                LayoutInflater.from(context).inflate(
                    R.layout.symbol_adapter_item,
                    parent,
                    false
                )
            )
        }
        else {
            return EmptyHolder(
                LayoutInflater.from(context).inflate(
                R.layout.symbol_adapter_empty,
                parent,
                false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        if (data.isEmpty()) return 1 else return data.size
    }

    override fun getItemViewType(position: Int): Int {
        if (data.isEmpty()) return EMPTY_VIEW else return SYMBOL_VIEW
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val sym = view.findViewById<TextView>(R.id.tvSymbol)
        val name = view.findViewById<TextView>(R.id.tvName)
        val price = view.findViewById<TextView>(R.id.tvPrice)
        val open = view.findViewById<TextView>(R.id.tvOpen)
        val delete = view.findViewById<ImageView>(R.id.ivDelete)

        init {
            delete.setOnClickListener { view
                val symbol = data.get(adapterPosition)?.symbol
                symbol?.let {
                    onClickListener?.onDelete(view, adapterPosition, it)
                }
            }
        }
    }

    inner class EmptyHolder(view: View) : RecyclerView.ViewHolder(view) {
        // TODO Add stuff here in the future
    }

    fun updateList(newList: List<Quote>) {
        result(newList)
        // Calling this will execute the diffResult in he background.
        // This is not needed now but maybe in the future we might need it.
        //backGroundResult(newList)
    }

    suspend fun getResult(oldList: List<Quote>, newList: List<Quote>) : DiffUtil.DiffResult {
        val diffResult = DiffUtil.calculateDiff(QuoteDiff(oldList, newList))
        return diffResult
    }

    fun result(newList: List<Quote>) {
        val diffResult = DiffUtil.calculateDiff(QuoteDiff(data, newList))
        data.clear();
        notifyDataSetChanged()
        data.addAll(newList);
        diffResult.dispatchUpdatesTo(this)
    }

    fun backGroundResult(newList: List<Quote>) {
        val uiScope = CoroutineScope(Dispatchers.Main)
        val bgScope = CoroutineScope(Dispatchers.IO)
        uiScope.launch {

            val result = bgScope.async {
                getResult(ArrayList(data),ArrayList(newList))
            }.await()
            data.clear();
            notifyDataSetChanged();
            data.addAll(newList);
            result.dispatchUpdatesTo(this@SymbolAdapter)
        }
    }

    class QuoteDiff(val oldList: List<Quote>, val newList: List<Quote>): DiffUtil.Callback() {


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
            return oldList.get(oldItemPosition).price == newList.get(newItemPosition).price
        }
    }

}