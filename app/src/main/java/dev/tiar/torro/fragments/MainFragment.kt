package dev.tiar.torro.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import dev.tiar.torro.R
import dev.tiar.torro.adapters.MainAdapter
import dev.tiar.torro.items.ItemTorrent
import dev.tiar.torro.items.Statics
import dev.tiar.torro.parser.LoadCatalog

@SuppressLint("ValidFragment")
/**
 * Created by Tiar on 05.2018.
 */
data class MainFragment(private val query: String) : Fragment() {
    private lateinit var rv: RecyclerView
    private lateinit var pb: LinearLayout
    private lateinit var cl: CoordinatorLayout
    private lateinit var searchCount: TextView
    var itemTorrent = ItemTorrent()
    private var curPage = 1

    interface OnItemCatListener<ItemTorrent> {
        fun onSuccess(item: ItemTorrent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        rv = view.findViewById(R.id.rv_catalog) as RecyclerView
        pb = view.findViewById(R.id.progressL) as LinearLayout
        searchCount = view.findViewById(R.id.t_search) as TextView
        cl = view.findViewById(R.id.fragm_coord) as CoordinatorLayout
        searchCount.text = "$query: загрузка..."
        rv.layoutManager = GridLayoutManager(context, 1)
        val adapter = object : MainAdapter(context) {
            override fun getSwipeLayoutResourceId(position: Int): Int {
                TODO("not implemented")
            }

            @SuppressLint("SetTextI18n")
            override fun complate(count : Int) {
                if (count == 0) {
                    searchCount.text = getString(R.string.FOR_QUERY) + " \"$query\" " +
                            getString(R.string.NO_FOUND)
                } else {
                    searchCount.text = "$query: $count совпадений."
                }
            }

            override fun snackbar(string: String) {
                Snackbar.make(cl, string, Snackbar.LENGTH_SHORT)
                        .show()
            }
            override fun load() {
                if (Statics.nextPage) {
                    curPage++
                    onPage()
                    Log.d(ContentValues.TAG, "load: cur_page - $curPage")
                }
            }
        }
        rv.adapter = adapter
        onPage()
        return view
    }

    private fun onPage() {
        pb.visibility = View.VISIBLE
        val url = when {
            Statics.curUrl.contains(Statics.urlZooqle) -> Statics.urlZooqle +
                    "/search?pg=$curPage&q=${query.replace(" ", "+")}" +
                    "${ Statics.curCategoryUrl}&v=t${ Statics.curSortUrl}"
            Statics.curUrl.contains(Statics.urlBitru) -> Statics.urlBitru +
                    "/browse.php?page=$curPage&tmp=${Statics.curCategoryUrl}${Statics.curSortUrl}" +
                    "&s=${query.replace(" ", "+")}"
            Statics.curUrl.contains(Statics.urlTpb) -> Statics.urlTpb +
                    "/search/${query.replace(" ", "+")}" +
                    "/${curPage - 1}/7/${Statics.curCategoryUrl}/"
            Statics.curUrl.contains(Statics.urlRutor) -> {
                val c = if(Statics.curCategoryUrl.isEmpty()) "0" else Statics.curCategoryUrl
                val s = if(Statics.curSortUrl.isEmpty()) "0" else Statics.curSortUrl
                Statics.urlRutor + "/search/${curPage - 1}/$c/000/$s/" +
                        "${query.replace(" ", "%20")}/"}
            Statics.curUrl.contains(Statics.urlNnm) -> {
                val c = if(Statics.curCategoryUrl.isEmpty()) "" else Statics.curCategoryUrl
                val s = if(Statics.curSortUrl.isEmpty()) "" else Statics.curSortUrl
                val q = if(curPage == 1) query.replace(" ", "%20") else "\""
                Statics.urlNnm + "/forum/tracker.php?nm=$q&sd=1&shc=1&shf=-1&sha=-1&shr=1$c$s"}
            Statics.curUrl.contains(Statics.urlFileek) -> {
                val c = if(Statics.curCategoryUrl.isEmpty()) "" else "&ft%5B%5D=${Statics.curCategoryUrl}"
                val q = query.replace(" ", "%20")
                val s = if(Statics.curSortUrl.isEmpty()) "" else Statics.curSortUrl
                Statics.urlFileek + "/search/?q=$q$s$c&page=$curPage"}
            Statics.curUrl.contains(Statics.urlUnderverse) -> {
                val c = if(Statics.curCategoryUrl.isEmpty()) "" else "&ft%5B%5D=${Statics.curCategoryUrl}"
                val q = query.replace(" ", "+")
                val s = if(Statics.curSortUrl.isEmpty()) "" else Statics.curSortUrl
                Statics.urlUnderverse + "/tracker.php?nm=$q$s$c&page=$curPage"}
            else -> ""
        }

        if (url != "") load(url)
    }

    private fun load (url : String) {
        if (isOnline(activity as Activity)) {
            val someTask = LoadCatalog(url, object : OnItemCatListener<ItemTorrent> {
                override fun onSuccess(item: ItemTorrent) {
                    itemTorrent = item
                    updateRecycler(itemTorrent)
                    pb.visibility = View.GONE
                }
            }, itemTorrent)
            someTask.execute()
        } else {
            Snackbar.make(cl, getString(R.string.NO_CONNECT), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.RETRY), { _ ->
                        load(url)
                    }).show()
        }
    }

    private fun isOnline(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        return nInfo != null && nInfo.isConnected
    }

    private fun updateRecycler(item: ItemTorrent){
        (rv.adapter as MainAdapter).addItems(item)
        rv.recycledViewPool.clear()
        rv.adapter.notifyItemChanged(0)
    }

    constructor() : this(query = "")
}