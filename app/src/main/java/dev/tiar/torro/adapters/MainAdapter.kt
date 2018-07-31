package dev.tiar.torro.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import dev.tiar.torro.R
import dev.tiar.torro.items.ItemTorrent
import kotlinx.android.synthetic.main.item_catalog_line.view.*
import kotlinx.android.synthetic.main.item_catalog_swipe.view.*
import java.util.*















/**
 * Created by Tiar on 03.2018.
 */
abstract class MainAdapter(private val context: Context?) : RecyclerSwipeAdapter<MainAdapter.MainViewHolder>() {
    private var item: ItemTorrent? = null
    private val TAG = "MainAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catalog_swipe, parent, false)
        return MainViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MainViewHolder, pos: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(context)
        val defSettings = HashSet(Arrays.asList("title", "desc", "size", "sid lich", "torrent", "magnet"))
        val uiSettings = pref.getStringSet("catalog_ui", defSettings)
        val position = holder.adapterPosition
        val cur = item!!
        var type = cur.type[position].replace("error", "").trim().toLowerCase()
        if (type.contains(",")) type = type.split(",")[0].trim()
        //set value
        val t = if (pref.getString("ui_category", "icon") == "icon")
            when (type.trim()) {
                "tv", "serial", "video", "сериалы" -> "\uD83D\uDCFA "//📺
                "movies", "movie", "видео", "документалистика" -> "\uD83C\uDFAC "//🎬
                "music", "audio", "музыка" -> "\uD83C\uDFA7 "//🎧
                "game", "games", "игры" -> "\uD83C\uDFAE "//🎮
                "book", "literature","книги" -> "\uD83D\uDCD6 "//📖
                "soft", "app", "applications","программы" -> "\uD83D\uDCBB "//💻
                "anime", "аниме" -> "\uD83D\uDC79 "//👹
                "files" -> "\uD83D\uDCC1 "//📁
                "audiobook" -> "\uD83C\uDFA7\uD83D\uDCD6 "//🎧📖
                "xxx", "porn" -> "\uD83D\uDD1E "//🔞
                "image", "мультимедиа" -> "\uD83C\uDFA8"//🎨
                "apple", "android" -> "\uD83D\uDCF1"//📱
                else -> "\u2753"//❓
            } else type
        //"<font color='#c3c3c3'>$type</font> "
        holder.title.text = if (t.trim().isNotEmpty() && pref.getString("ui_category", "icon") == "text")
            "[${t.trim()}] ${cur.title[position]}"
        else "$t ${cur.title[position]}"
        holder.desc.text = if (uiSettings.contains("size") && uiSettings.contains("desc"))
                    cur.size[position].trim() + " " + cur.description[position] + " " + cur.source[position]
                else if (uiSettings.contains("size")) cur.size[position] + " " + cur.source[position]
                else cur.description[position] + " " + cur.source[position]
        holder.sid.text = cur.sid[position]
        holder.lich.text = cur.lich[position]
        holder.sid2.text = cur.sid[position]
        holder.lich2.text = cur.lich[position]
        //set visibility
        holder.title.visibility = visibility(uiSettings.contains("title"))
        holder.desc.visibility = visibility(uiSettings.contains("desc") || uiSettings.contains("size"))
        holder.lineSidLich.visibility = visibility(uiSettings.contains("sid lich"))
        holder.torrent.visibility = visibility(uiSettings.contains("torrent"))
        holder.magnet.visibility =  visibility(uiSettings.contains("magnet"))
        holder.torrent2.visibility = visibility(!cur.linkTorrent[position].contains("error"))
        holder.magnet2.visibility =  visibility(!cur.linkMagnet[position].contains("error"))
        holder.rightBlock.visibility = visibility(uiSettings.contains("magnet") && uiSettings.contains("torrent"))
        holder.lineSidLich2.visibility = visibility(!uiSettings.contains("magnet") &&
                !uiSettings.contains("torrent") && uiSettings.contains("sid lich"))

        if(!uiSettings.contains("magnet") && !uiSettings.contains("torrent"))
            holder.swipeLayout.showMode = SwipeLayout.ShowMode.LayDown
        else holder.swipeLayout.isRightSwipeEnabled = false

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

        holder.torrent.setOnClickListener({
            onTorrentClick(cur.linkTorrent[position])
        })
        holder.magnet.setOnClickListener({
            openUrl(cur.linkMagnet[position])
            Log.d(TAG, "magnet click + ${cur.linkMagnet[position]}")
        })
        holder.torrent2.setOnClickListener({
            onTorrentClick(cur.linkTorrent[position])
        })
        holder.magnet2.setOnClickListener({
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

    private fun onTorrentClick(torrent : String) {
        if (torrent != "error") {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(torrent),
                        "application/x-bittorrent")
                context!!.startActivity(intent)
            } catch (e: Exception) {
                openUrl(torrent)
            }
            Log.d(TAG, "torrent click + $torrent")
        }
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
        var swipeLayout = view.swipe!!

        var rightBlock = view.right_block!!
        var title = view.t_title_post!!
        var desc = view.t_desc_post!!
        var torrent = view.img_torrent!!
        var magnet = view.img_magnet!!
        var torrent2 = view.img_torrentSwp!!
        var magnet2 = view.img_magnetSwp!!

        var lich = view.t_lich_post!!
        var sid = view.t_sid_post!!
        var lich2 = view.t_lich_post2!!
        var sid2 = view.t_sid_post2!!
        var lineSidLich = view.l_sid_lich!!
        var lineSidLich2 = view.l_sid_lich2!!

        var lineView = view.lineView!!
        var selector = view.selector!!
    }

}