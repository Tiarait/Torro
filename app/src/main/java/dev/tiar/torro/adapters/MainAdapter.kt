package dev.tiar.torro.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.tiar.torro.R
import dev.tiar.torro.items.ItemTorrent
import kotlinx.android.synthetic.main.item_catalog_line.view.*
import java.util.*













/**
 * Created by Tiar on 03.2018.
 */
abstract class MainAdapter(private val context: Context?) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
    private var item: ItemTorrent? = null
    private val TAG = "MainAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catalog_line, parent, false)
        return MainViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainViewHolder, pos: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val defSettings = HashSet(Arrays.asList("title", "desc", "size", "sid lich", "torrent", "magnet"))
        val uiSettings = pref.getStringSet("catalog_ui", defSettings)
        val position = holder.adapterPosition
        val cur = item!!
        val type = cur.type[position].trim().toLowerCase()
        //set value
        val t = if (pref.getString("ui_category", "icon") == "icon")
            when (type) {
                "tv", "serial", "video" -> "\uD83D\uDCFA "//📺
                "movies", "movie" -> "\uD83C\uDFAC "//🎬
                "music", "audio" -> "\uD83C\uDFA7 "//🎧
                "game", "games" -> "\uD83C\uDFAE "//🎮
                "book", "literature" -> "\uD83D\uDCD6 "//📖
                "soft", "app", "applications" -> "\uD83D\uDCBB "//💻
                "anime" -> "\uD83D\uDC79 "//👹
                "files" -> "\uD83D\uDCC1 "//📁
                "audiobook" -> "\uD83C\uDFA7\uD83D\uDCD6 "//🎧📖
                "xxx", "porn" -> "\uD83D\uDD1E "//🔞
                "image" -> "\uD83C\uDFA8"//🎨
                "other" -> "\u2753"//❓
                else -> "\u2753"//❓
            } else "<font color='#c3c3c3'>$type</font> "
        holder.title.text = if (t.trim().isNotEmpty() && pref.getString("ui_category", "icon") == "text")
            "[${fromHtml(t).trim()}] ${cur.title[position]}"
        else "${fromHtml(t)} ${cur.title[position]}"
        holder.desc.text = if (uiSettings.contains("size") && uiSettings.contains("desc"))
                    cur.size[position].trim() + " " + cur.description[position] + " " + cur.source[position]
                else if (uiSettings.contains("size")) cur.size[position] + " " + cur.source[position]
                else cur.description[position] + " " + cur.source[position]
        holder.sid.text = cur.sid[position]
        holder.lich.text = cur.lich[position]
        //set visibility
        holder.title.visibility = visibility(uiSettings.contains("title"))
        holder.desc.visibility = visibility(uiSettings.contains("desc") || uiSettings.contains("size"))
        holder.lineSidLich.visibility = visibility(uiSettings.contains("sid lich"))
        holder.torrent.visibility = visibility(uiSettings.contains("torrent"))
        holder.magnet.visibility =  visibility(uiSettings.contains("magnet"))

        //set enable and color for img buttons if error
        holder.torrent.setColorFilter( if (cur.linkTorrent[position] == "error")
            ContextCompat.getColor(context!!, R.color.colorPrimary)
            else ContextCompat.getColor(context!!, R.color.colorPink))
        holder.magnet.setColorFilter(if (cur.linkMagnet[position] == "error")
            ContextCompat.getColor(context, R.color.colorPrimary)
            else ContextCompat.getColor(context, R.color.colorPink))


        //set focusable for tv
        holder.lineView.isFocusable = true
        holder.torrent.isFocusable = !cur.linkTorrent[position].contains("error")
        holder.magnet.isFocusable = !cur.linkMagnet[position].contains("error")
        //if (position == 0) holder.mView.requestFocus()


        holder.lineView.setOnClickListener({
            val builder = AlertDialog.Builder(context, 2)
            val array =
                    if (cur.linkTorrent[position] != "error" && cur.linkMagnet[position] != "error")
                        arrayOf("Torrent", "Coppy torrent link", "Share torrent link",
                                "Magnet", "Coppy magnet link", "Share magnet link", "Open in browser")
                    else if (cur.linkTorrent[position] == "error")
                        arrayOf("Magnet", "Coppy magnet link", "Share magnet link", "Open in browser")
                    else if (cur.linkMagnet[position] == "error")
                        arrayOf("Torrent", "Coppy torrent link", "Share torrent link", "Open in browser")
                    else arrayOf("Open in browser")
            builder.setTitle(cur.title[position])
            builder.setItems(array,{_, i ->
                when (array[i]) {
                    "Torrent" -> {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(Uri.parse(cur.linkTorrent[position]),
                                    "application/x-bittorrent")
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            openUrl(cur.linkTorrent[position])
                        }

                    }
                    "Coppy torrent link" -> {copyText(cur.linkTorrent[position])}
                    "Share torrent link" -> {shareLink("Torrent $cur.title[position]", cur.linkTorrent[position])}
                    "Magnet" -> { openUrl(cur.linkMagnet[position]) }
                    "Coppy magnet link" -> {copyText(cur.linkMagnet[position])}
                    "Share magnet link" -> {"Magnet ${shareLink(cur.title[position], cur.linkMagnet[position])}"}
                    "Open in browser" -> { openUrl(cur.link[position]) }
                }
            }).create().show()
        })
        if (cur.linkTorrent[position] != "error")
            holder.torrent.setOnClickListener({
                try {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(cur.linkTorrent[position]),
                            "application/x-bittorrent")
                    context.startActivity(intent)
                } catch (e: Exception) {
                    openUrl(cur.linkTorrent[position])
                }
                Log.d(TAG, "torrent click + ${cur.linkTorrent[position]}")
            })
        if (cur.linkMagnet[position] != "error")
            holder.magnet.setOnClickListener({
                openUrl(cur.linkMagnet[position])
                Log.d(TAG, "magnet click + ${cur.linkMagnet[position]}")
            })

        holder.lineView.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            holder.selector.visibility = (if (!view.isSelected) View.VISIBLE
            else View.GONE)
            view.isSelected = b
        }
        holder.torrent.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (!cur.linkTorrent[position].contains("error")) {
                holder.torrent.setColorFilter(if (!view.isSelected)
                    ContextCompat.getColor(context, R.color.colorPrimaryLight)
                else ContextCompat.getColor(context, R.color.colorPink))
                view.isSelected = b
            }
        }
        holder.magnet.onFocusChangeListener = View.OnFocusChangeListener { view, b ->
            if (!cur.linkMagnet[position].contains("error")) {
                holder.magnet.setColorFilter(if (!view.isSelected)
                    ContextCompat.getColor(context, R.color.colorPrimaryLight)
                else ContextCompat.getColor(context, R.color.colorPink))
                view.isSelected = b
            }
        }

        //to stop the endless download
        if (position >= itemCount - 1)
            load()
    }

    private fun copyText (text : String) {
        Log.d(TAG, "copy $text")
        val clipboard = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("TAG", text)
        clipboard.primaryClip = clip
        snackbar("Скопировано")
    }

    private fun openUrl (url : String) {
        Log.d(TAG, "url $url")
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context!!.startActivity(intent)
        } catch (e: Exception) {
            snackbar("Не найдено подходящего приложения!")
        }
    }


    private fun shareLink (name : String, url : String) {
        Log.d(TAG, "share $name | $url")
        //val textToShare = "<a href=\"$url\">$name</a> from Torro."
        val intent = Intent(android.content.Intent.ACTION_SEND)

        intent.type = "text/html"
        intent.putExtra(Intent.EXTRA_SUBJECT, "sample")
        intent.putExtra(Intent.EXTRA_TEXT, "Torro app|$name|$url")

        context!!.startActivity(Intent.createChooser(intent, "Share"))
    }


    @Suppress("DEPRECATION")
    private fun fromHtml(source: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(source)
        }
    }


    private fun visibility(visibility: Boolean):Int{
        return if (visibility)
            View.VISIBLE
        else View.GONE
    }

    fun addItems(item: ItemTorrent) {
        if (this.item != null && this.item!!.size.isNotEmpty())
            this.item!!.addItems(item)
        else this.item = item
        Log.d(TAG, "item count : ${this.item!!.size.size}")
        complate(this.item!!.size.size)
    }

    override fun getItemCount(): Int {
        return if (item != null && item!!.size.isNotEmpty()) {
            complate(item!!.size.size)
            item!!.size.size
        } else { 0 }
    }

    abstract fun load()
    abstract fun complate(count: Int)
    abstract fun snackbar(string: String)

    class MainViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title = view.t_title_post!!
        var desc = view.t_desc_post!!
        var lich = view.t_lich_post!!
        var sid = view.t_sid_post!!
        var torrent = view.img_torrent!!
        var magnet = view.img_magnet!!
        var lineSidLich = view.l_sid_lich!!
        var lineView = view.lineView!!
        var selector = view.selector!!
        var mView = view
    }

}