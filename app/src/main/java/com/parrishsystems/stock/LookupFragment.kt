package com.parrishsystems.stock

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parrishsystems.stock.repo.StockMarketRepo
import com.parrishsystems.stock.viewmodel.LookupModelViewFactory
import com.parrishsystems.stock.viewmodel.LookupViewModel

class LookupFragment : Fragment() {

    companion object {
        fun newInstance() = LookupFragment()
    }

    private lateinit var viewModel: LookupViewModel
    private lateinit var keyword: EditText
    private lateinit var adapter: LookupAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.lookup_fragment, container, false)
        keyword = view.findViewById(R.id.etKeyword)
        keyword.setOnEditorActionListener { _, actionId, _ ->
            var ret = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(keyword.text.toString().trim(), false)
                ret = true
            }
            ret
        }

        keyword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.search(keyword.text.toString().trim())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })

        val layoutManager = LinearLayoutManager(this.context)
        val rvTerms: RecyclerView
        rvTerms = view.findViewById<RecyclerView>(R.id.rvTerms)
        rvTerms.layoutManager = layoutManager
        rvTerms.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        adapter = LookupAdapter(this.context!!)
        adapter.onClickListener = object : LookupAdapter.OnClick {
            override fun onMore() {
                viewModel.searchMore()
            }

            override fun onSelect(symbol: LookupViewModel.Symbol) {
                val intent = Intent()
                intent.putExtra(LookupActivity.RESULT_KEY, symbol.symbol)
                activity!!.setResult(LookupActivity.RESULT_CODE, intent)
                activity!!.finish()
                true
            }

        }
        rvTerms.adapter = adapter

        viewModel = ViewModelProviders.of(this, LookupModelViewFactory(StockMarketRepo.instance)).get(LookupViewModel::class.java)
        viewModel.search.observe(this, Observer<List<LookupViewModel.Symbol>> {
            adapter.setItems(it)
        })

        viewModel.moreData.observe(this, Observer<Boolean> {
            adapter.moreEnabled = it
        })

        viewModel.errorMsg.observe(this, Observer<String> {
            Toast.makeText(context!!.applicationContext, it, Toast.LENGTH_LONG).show()
        })

        return view
    }
}
