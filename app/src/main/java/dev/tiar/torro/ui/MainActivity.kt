package dev.tiar.torro.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.*
import com.readystatesoftware.systembartint.SystemBarTintManager
import dev.tiar.torro.R
import dev.tiar.torro.fragments.MainFragment
import dev.tiar.torro.items.Category
import dev.tiar.torro.items.Statics
import dev.tiar.torro.updater.Update
import kotlinx.android.synthetic.main.activity_main.*








class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var doubleBackToExitPressedOnce: Boolean = false
    private var querySearch = ""
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
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        setUrl(pref)

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

        toggle.isDrawerIndicatorEnabled = false
        val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ResourcesCompat.getDrawable(resources, R.drawable.ic_menu_category, this.theme)
        else ResourcesCompat.getDrawable(resources, R.drawable.ic_menu_c, this.theme)
        toggle.setHomeAsUpIndicator(drawable)
        toggle.toolbarNavigationClickListener = View.OnClickListener {
            if (drawer_layout.isDrawerOpen(GravityCompat.START))
                drawer_layout.closeDrawer(GravityCompat.START)
            else drawer_layout.openDrawer(GravityCompat.START)
        }

        //start catalog
        Statics.curUrl = when (pref.getString("start_catalog", "Zooqle")) {
            "Zooqle" -> Statics.urlZooqle
            "Tpb" -> Statics.urlTpb
            "Bitru" -> Statics.urlBitru
            "Rutor" -> Statics.urlRutor
            "Fileek" -> Statics.urlBitru
            "Nnm" -> Statics.urlNnm
            else -> Statics.urlZooqle
        }
        invalidateOptionsMenu()
        onAttachedToWindow()
        onRightDrawer()

        nav_view.setNavigationItemSelectedListener(this)
        nav_view.itemIconTintList = null

        if (pref.getBoolean("auto_update", true)) {
            val upd = Update(this)
            upd.execute()
        }
    }

    private fun setUrl(pref: SharedPreferences) {
        Statics.urlRutor = pref.getString("rutor_furl", Statics.urlRutor)
        Statics.urlZooqle = pref.getString("zooqle_furl", Statics.urlZooqle)
        Statics.urlTpb = pref.getString("tpb_furl", Statics.urlTpb)
        Statics.urlBitru = pref.getString("bitru_furl", Statics.urlBitru)
        Statics.urlNnm = pref.getString("nnm_furl", Statics.urlNnm)
        Statics.urlFileek = pref.getString("fileek_furl", Statics.urlFileek)
        exit = pref.getBoolean("exit", false)
    }

    override fun onResume() {
        super.onResume()
        setUrl(PreferenceManager.getDefaultSharedPreferences(this))
        invalidateOptionsMenu()
        onAttachedToWindow()
    }

    private fun onPage(query: String) {
        invalidateOptionsMenu()
        onAttachedToWindow()

        if (query.trim().isNotEmpty() && Statics.curUrl.isNotEmpty()) {
            title = "Torro"
            t_s.visibility = View.GONE
            Statics.nextPage = true
            val fragment: Fragment = MainFragment(query)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit()
        }
    }

    fun menuSort(item: MenuItem) {
        if (drawer_layout.isDrawerOpen(GravityCompat.END))
            drawer_layout.closeDrawer(GravityCompat.END)
        else drawer_layout.openDrawer(GravityCompat.END)
    }

    private fun onRightDrawer() {
        var itemsCat = ""
        var itemsCatT = ""
        val listViewCat = findViewById<ListView>(R.id.listViewCategory)
        if (Statics.curUrl == Statics.urlZooqle || Statics.curUrl == Statics.urlTpb) {
            listViewCat.adapter = ArrayAdapter<String>(this, R.layout.list_item_multiple_choice_white,
                    Category().getCategoryTitle())
            listViewCat.choiceMode = ListView.CHOICE_MODE_MULTIPLE
            listViewCat.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                if (itemsCat.contains(Category().getCategoryUrl()[i] + ",")) {
                    itemsCat = itemsCat.replace(Category().getCategoryUrl()[i] + ",", "")
                    itemsCatT = itemsCatT.replace(Category().getCategoryTitle()[i] + ",", "")
                } else {
                    itemsCat += Category().getCategoryUrl()[i] + ","
                    itemsCatT += Category().getCategoryTitle()[i] + ","
                }
            }
        } else {
            listViewCat.adapter = ArrayAdapter<String>(this, R.layout.list_item_single_choice_white,
                    Category().getCategoryTitle())
            listViewCat.choiceMode = ListView.CHOICE_MODE_SINGLE
            listViewCat.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
                itemsCat = Category().getCategoryUrl()[i]
                itemsCatT = Category().getCategoryTitle()[i]
            }
        }
        //-----------------------------------------------------
        var itemsSort = ""
        val viewSort = findViewById<LinearLayout>(R.id.viewSort)
        if (Statics.curUrl == Statics.urlTpb) viewSort.visibility = View.GONE
        else viewSort.visibility = View.VISIBLE
        val listViewSort = findViewById<ListView>(R.id.listViewSort)
        listViewSort.adapter = ArrayAdapter<String>(this, R.layout.list_item_single_choice_white,
                Category().getSortTitle())
        listViewSort.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            itemsSort = Category().getSortUrl()[i]
        }
        listViewSort.choiceMode = ListView.CHOICE_MODE_SINGLE
        //-----------------------------------------------------
        val btnOk = findViewById<Button>(R.id.btnViewOk)
        btnOk.setOnClickListener({
            drawer_layout.closeDrawer(GravityCompat.END)
            if (itemsCat.endsWith(",")) {
                itemsCat = itemsCat.dropLast(1)
                itemsCatT = itemsCatT.dropLast(1)
            }
            if (Statics.curUrl == Statics.urlZooqle && itemsCat.isNotEmpty()) {
                Statics.curCategoryUrl = "+category%3A" + itemsCat.replace(",", "%2C")
                Statics.curCategoryTitle = itemsCatT
            } else {
                Statics.curCategoryUrl = itemsCat
                Statics.curCategoryTitle = itemsCatT
            }
            Statics.curSortUrl = itemsSort
            onPage(querySearch)
        })
    }

    override fun onBackPressed() {
        when {
            drawer_layout.isDrawerOpen(GravityCompat.START) -> drawer_layout.closeDrawer(GravityCompat.START)
            drawer_layout.isDrawerOpen(GravityCompat.END) -> drawer_layout.closeDrawer(GravityCompat.END)
            else -> {
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
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        //val sortItem = menu.findItem(R.id.action_sort)
        //sortItem.isVisible = Statics.curUrl != Statics.urlTpb


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
        Statics.curSortUrl = ""
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
            R.id.nav_nnm -> {
                Statics.curUrl = Statics.urlNnm
                onPage(querySearch)
            }
            R.id.nav_fileek -> {
                Statics.curUrl = Statics.urlFileek
                onPage(querySearch)
            }
            R.id.nav_exit -> {
                super.onBackPressed()
            }
        }

        onRightDrawer()
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
