package com.parrishsystems.stock

import android.content.Intent
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import android.view.MenuItem
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.parrishsystems.stock.repo.StockMarketRepo
import com.parrishsystems.stock.viewmodel.SymbolViewModel
import com.parrishsystems.stock.viewmodel.SymbolViewModelFactory

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    private lateinit var vm : SymbolViewModel
    private lateinit var rvSymbols: RecyclerView
    private lateinit var adapter: SymbolAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var isInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        this.title = getString(R.string.app_name)


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            val intent = Intent(this, LookupActivity::class.java)
            startActivityForResult(intent, LookupActivity.REQUEST_CODE)
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // TODO Don't need nav now but I will in future.
        navView.visibility = View.GONE
        toggle.setDrawerIndicatorEnabled(false);

        StockMarketRepo.init(application)

        val layoutManager = LinearLayoutManager(this)
        rvSymbols = findViewById<RecyclerView>(R.id.rvSymbols)
        rvSymbols.layoutManager = layoutManager
        rvSymbols.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        adapter = SymbolAdapter(baseContext, ArrayList<SymbolViewModel.PriceQuote>())
        adapter.onClickListener = object : SymbolAdapter.OnClick {
            override fun onClick(view: View, position: Int, symbol: SymbolViewModel.PriceQuote) {
                vm.selectSymbol(symbol.symbol)
                val intent = Intent(applicationContext, StockDetailsActivity::class.java)
                startActivity(intent)
            }

            override fun onDelete(view: View, position: Int, symbol: String) {
                vm.deleteSymbol(symbol)
            }

        }
        rvSymbols.adapter = adapter

        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            vm.refreshSymbols()
        }

      swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
        //swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,
            android.R.color.holo_green_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_blue_dark)

        vm = ViewModelProviders.of(this, SymbolViewModelFactory(StockMarketRepo.instance)).get(SymbolViewModel::class.java)
        vm.quotes.observe(this, Observer<List<SymbolViewModel.PriceQuote>> {
            swipeRefreshLayout.isRefreshing = false
            adapter.updateList(it)
        })

        vm.errorMsg.observe(this, Observer<String> {
            Toast.makeText(application, it, Toast.LENGTH_LONG).show()
        })

        vm.getSymbols()
    }

    override fun onStart() {
        super.onStart()
        if (isInit) vm.refreshSymbols()
        isInit = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LookupActivity.REQUEST_CODE && resultCode == LookupActivity.RESULT_CODE) {
            // Get the symbol
            val symbol = data?.extras?.getString(LookupActivity.RESULT_KEY)
            if (!symbol.isNullOrEmpty()) {
                vm.addSymbol(symbol)
            }
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_refresh -> {
                vm.getSymbols()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_tools -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
