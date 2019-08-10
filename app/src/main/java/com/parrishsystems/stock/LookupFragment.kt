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
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parrishsystems.stock.model.LookupSymbol
import com.parrishsystems.stock.viewmodel.LookupViewModel

class LookupFragment : Fragment() {

    private val KEYWORD_LENGTH_TO_START_SEARCH = 1

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
        keyword.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val len = keyword.text.toString().length
                if (len != 0 && len <= KEYWORD_LENGTH_TO_START_SEARCH) {
                    viewModel.search(keyword.text.toString().trim())
                }
                true
            }
            false
        }

        keyword.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length > KEYWORD_LENGTH_TO_START_SEARCH) {
                    viewModel.search(keyword.text.toString().trim())
                }
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

            override fun onSelect(symbol: LookupSymbol) {
                val intent = Intent()
                intent.putExtra(LookupActivity.RESULT_KEY, symbol.symbol)
                activity!!.setResult(LookupActivity.RESULT_CODE, intent)
                activity!!.finish()
                true
            }

        }
        rvTerms.adapter = adapter

        viewModel = ViewModelProviders.of(this).get(LookupViewModel::class.java)
        viewModel.search.observe(this, Observer<List<LookupSymbol>> {
            adapter.setItems(it)
        })

        viewModel.isMore.observe(this, Observer<Boolean> {
            adapter.moreEnabled = it
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //viewModel = ViewModelProviders.of(this).get(LookupViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
