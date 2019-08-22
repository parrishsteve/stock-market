package com.parrishsystems.stock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.parrishsystems.stock.viewmodel.SymbolViewModel
import kotlinx.coroutines.*


class SymbolAdapter(val context: Context, var data: ArrayList<SymbolViewModel.PriceQuote>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            holder.price.text = q.price
            holder.open.text = q.open
            holder.dayLow.text = q.low
            holder.dayHigh.text = q.high

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
        val dayLow = view.findViewById<TextView>(R.id.tvDayLow)
        val dayHigh = view.findViewById<TextView>(R.id.tvDayHigh)
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
        // TODO Add stuff here in the future if the UX changes
    }

    fun updateList(newList: List<SymbolViewModel.PriceQuote>) {
        result(newList)
        // Calling this will execute the diffResult in he background.
        // This is not needed now but maybe in the future we might need it.
        //backGroundResult(newList)
    }

    suspend fun getResult(oldList: List<SymbolViewModel.PriceQuote>, newList: List<SymbolViewModel.PriceQuote>) : DiffUtil.DiffResult {
        val diffResult = DiffUtil.calculateDiff(QuoteDiff(oldList, newList))
        return diffResult
    }

    fun result(newList: List<SymbolViewModel.PriceQuote>) {
        val diffResult = DiffUtil.calculateDiff(QuoteDiff(data, newList))
        data.clear();
        notifyDataSetChanged()
        data.addAll(newList);
        diffResult.dispatchUpdatesTo(this)
    }

    fun backGroundResult(newList: List<SymbolViewModel.PriceQuote>) {
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

    class QuoteDiff(val oldList: List<SymbolViewModel.PriceQuote>, val newList: List<SymbolViewModel.PriceQuote>): DiffUtil.Callback() {


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