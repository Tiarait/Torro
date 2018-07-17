package dev.tiar.torro.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import com.readystatesoftware.systembartint.SystemBarTintManager
import dev.tiar.torro.R
import dev.tiar.torro.fragments.MainFragment
import dev.tiar.torro.items.Category
import dev.tiar.torro.items.Statics
import kotlinx.android.synthetic.main.activity_main.*




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var doubleBackToExitPressedOnce: Boolean = false
    private var querySearch = ""
    private lateinit var startText : TextView
    private var exit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        //visibility statusbar
        if (pref.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        exit = pref.getBoolean("exit", false)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        Statics.urlRutor = pref.getString("rutor_furl", Statics.urlRutor)
        Statics.urlZooqle = pref.getString("zooqle_furl", Statics.urlZooqle)
        Statics.urlTpb = pref.getString("tpb_furl", Statics.urlTpb)
        Statics.urlBitru = pref.getString("bitru_furl", Statics.urlBitru)

        //statusbar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            this.window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimaryDarken)
        } else {
            val tintManager = SystemBarTintManager(this)
            tintManager.isStatusBarTintEnabled = true
            tintManager.setNavigationBarTintEnabled(true)
            tintManager.setTintColor(ContextCompat.getColor(this, R.color.colorPrimaryDarken))

            tintManager.setNavigationBarTintResource(ContextCompat.getColor(this, R.color.colorPrimaryDarken))
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        startText = findViewById(R.id.t_start)

        //start catalog
        Statics.curUrl = when (pref.getString("start_catalog", "Zooqle")) {
            "Zooqle" -> Statics.urlZooqle
            "Tpb" -> Statics.urlTpb
            "Bitru" -> Statics.urlBitru
            "Rutor" -> Statics.urlRutor
            else -> Statics.urlZooqle
        }
        setSubtitle("")
        invalidateOptionsMenu()
        onAttachedToWindow()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null
    }

    override fun onResume() {
        super.onResume()
        exit = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("exit", false)
        setSubtitle("")
        invalidateOptionsMenu()
        onAttachedToWindow()
    }

    private fun onPage(query: String) {
        startText.visibility = View.GONE
        invalidateOptionsMenu()
        onAttachedToWindow()

        setSubtitle(if (Statics.curCategoryTitle.trim().isNotEmpty())
            ": ${Statics.curCategoryTitle.trim()}"
        else "")

        if (query.trim().isNotEmpty() && Statics.curUrl.isNotEmpty()) {
            title = "Torro: $query"
            Statics.nextPage = true
            val fragment: Fragment = MainFragment(query)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        }
    }

    private fun setSubtitle(category: String) {
        toolbar.subtitle = when (Statics.curUrl) {
            Statics.urlZooqle -> "on Zooqle $category"
            Statics.urlBitru -> "on Bitru $category"
            Statics.urlTpb -> "on Tpb $category"
            Statics.urlRutor -> "on Rutor $category"
            else -> ""
        }
    }

    fun menuCategory(item: MenuItem) {
        var newItems = ""
        var newTitle = ""

        val builder = AlertDialog.Builder(this, 2)

        builder.setTitle("Select category")
        if (Statics.curUrl == Statics.urlZooqle || Statics.curUrl == Statics.urlTpb) {
            builder.setMultiChoiceItems(Category().getCategoryTitle()
                    , null) { _, i, _ ->
                if (newItems.contains(Category().getCategoryUrl()[i] + ",")) {
                    newItems = newItems.replace(Category().getCategoryUrl()[i] + ",", "")
                    newTitle = newTitle.replace(Category().getCategoryTitle()[i] + ",", "")
                } else {
                    newItems += Category().getCategoryUrl()[i] + ","
                    newTitle += Category().getCategoryTitle()[i] + ","
                }
            }
        } else if (Statics.curUrl == Statics.urlBitru || Statics.curUrl == Statics.urlRutor) {
            builder.setSingleChoiceItems(Category().getCategoryTitle(), -1, { _, i ->
                newItems = Category().getCategoryUrl()[i]
                newTitle = Category().getCategoryTitle()[i]
            })
        }
        builder.setPositiveButton("OK") { _, _ ->
            if (newItems.endsWith(",")) {
                newItems = newItems.dropLast(1)
                newTitle = newTitle.dropLast(1)
            }
            if (Statics.curUrl == Statics.urlZooqle && newItems.isNotEmpty()) {
                Statics.curCategoryUrl = "+category%3A" + newItems.replace(",", "%2C")
                Statics.curCategoryTitle = newTitle
            } else {
                Statics.curCategoryUrl = newItems
                Statics.curCategoryTitle = newTitle
            }
            onPage(querySearch)
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    fun menuSort(item: MenuItem) {
		var newItems = ""
        val builder = AlertDialog.Builder(this, 2)

        builder.setTitle("Sort by")
        .setSingleChoiceItems(Category().getSortTitle(), -1, { _, i ->
            newItems = Category().getSortUrl()[i]
        }).setPositiveButton("OK") { _, _ ->
			Statics.curSortUrl = newItems
            onPage(querySearch)
        }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }.create().show()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Snackbar.make(main_coordinator_layout, getString(R.string.PRESS_TO_EXIT), Snackbar.LENGTH_SHORT)
                    .setAction(getString(R.string.EXIT), { _ ->
                        super.onBackPressed()
                    }).show()

            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val sortItem = menu.findItem(R.id.action_sort)
        val searchView = searchItem?.actionView as SearchView

        sortItem.isVisible = Statics.curUrl != Statics.urlTpb


        searchView.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            view.setBackgroundResource(if (!view.isSelected)
                R.drawable.circle_pink
            else ContextCompat.getColor(this, R.color.colorGone))
            view.isSelected = b
        }
        searchView.setOnClickListener({
            searchView.isIconified = false
        })
        searchView.animate()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                querySearch = query
                onPage(querySearch)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                searchView.clearFocus()
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.isFocusable = true

        val searchImgId = android.support.v7.appcompat.R.id.search_button
        val v = searchView.findViewById<View>(searchImgId) as ImageView
        v.setImageResource(R.drawable.ic_menu_search)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.isFocusable = true
        navigationView.menu.findItem(R.id.nav_exit).isVisible = exit
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        Statics.curCategoryUrl = ""
        Statics.curCategoryTitle = ""
        when (item.itemId) {
            R.id.nav_setting -> {
                this.startActivity(Intent(this, SettingsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
            R.id.nav_zooqle -> {
                Statics.curUrl = Statics.urlZooqle
                onPage(querySearch)
            }
            R.id.nav_rutor -> {
                Statics.curUrl = Statics.urlRutor
                onPage(querySearch)
            }
            R.id.nav_bitru -> {
                Statics.curUrl = Statics.urlBitru
                onPage(querySearch)
            }
            R.id.nav_tpb -> {
                Statics.curUrl = Statics.urlTpb
                onPage(querySearch)
            }
            R.id.nav_exit -> {
                super.onBackPressed()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
