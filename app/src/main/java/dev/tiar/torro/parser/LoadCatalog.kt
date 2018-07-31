package dev.tiar.torro.parser

import android.os.AsyncTask
import android.util.Log
import dev.tiar.torro.fragments.MainFragment
import dev.tiar.torro.items.ItemTorrent
import dev.tiar.torro.items.Statics
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * Created by Tiar on 06.2018.
 */
class LoadCatalog (private val url: String,
                   private val callback: MainFragment.OnItemCatListener<ItemTorrent>,
                   private var item: ItemTorrent) : AsyncTask<Void, Void, Void>() {
    companion object {
        private const val TAG = "LoadCatalog"
    }
    private var title = "error"
    private var description = "error"
    private var sid = "error"
    private var lich = "error"
    private var source = "error"
    private var type = "error"
    private var size = "error"
    private var link = "error"
    private var linkMagnet = "error"
    private var linkTorrent = "error"

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        callback.onSuccess(item)
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        when {
            url.startsWith(Statics.urlZooqle) -> parseZooqle(getData(url))
            url.startsWith(Statics.urlBitru) -> parseBitru(getData(url))
            url.startsWith(Statics.urlTpb) -> parseTpb(getData(url))
            url.startsWith(Statics.urlRutor) -> parseRutor(getData(url))
            url.startsWith(Statics.urlNnm) -> parseNnm(getData(url))
            url.startsWith(Statics.urlFileek) -> parseFileek(getData(url))
        }
        return null
    }

    private fun parseFileek(data: Document?) {
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("files_list")) {
                val tableList = data.select("#files_list.block")
                for (entry in tableList) {
                    defVar()
                    source = "Fileek"
                    if (entry.html().contains("ico")) {
                        type = entry.select(".ico img").attr("src")
                        if (type.contains("/"))
                            type = type.split("/").last()
                    }
                    if (entry.html().contains("name")) {
                        title = entry.select(".name").text()
                        link  = url
                    }
                    if (entry.html().contains("download-btn")){
                        var id = entry.select(".download-btn").attr("id")
                        if (id.contains("dw_"))
                            id = id.split("dw_")[1]
                        linkTorrent = Statics.urlFileek +
                                "/index.php?r=download/ajaxlinkcheck&id=$id&action=%2Fsearch&ismagnet=0"
                        val t = getData(linkTorrent)
                        if (t != null && t.text().contains("link\":\""))
                            linkTorrent = t.text().split("link\":\"")[1]
                        linkTorrent = linkTorrent.replace("\\", "")
                                .replace("\"", "").replace("}", "")
                        if (linkTorrent.trim().isEmpty())
                            linkTorrent = "error"

                        linkMagnet = "error"
                    }

                    if (entry.html().contains("count")) {
                        description = entry.select(".count").text()
                    }
                    if (entry.html().contains("size")) {
                        size = entry.select(".size").first().text()
                    }
                    sid = "x"
                    lich = "x"
                    if (title != "error" && sid != "error" && sid != "0") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun parseNnm(data: Document?) {
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("tablesorter")) {
                val tableList = data.select(".tablesorter tr")
                for (entry in tableList) {
                    defVar()
                    source = "NNM"
                    if (entry.html().contains("class=\"gen\""))
					    type = entry.select(".gen").text().replace("Трекер:", "").trim()

                    when {
                        type.contains("Книги") -> type = "книги"
                        type.contains("Apple") -> type = "apple"
                        type.contains("Android") -> type = "android"
                        type.contains("Мобильные устройства") -> type = "mobile"
                        type.contains("Архив") -> type = "архив"
                    }

                    if (entry.html().contains("genmed topictitle")) {
                        title = entry.select(".genmed.topictitle").text()
                        link  = Statics.urlNnm + "/" + entry.select(".genmed.topictitle").attr("href")
                    }
					if (entry.html().contains("download.php?id")){
                        linkTorrent = Statics.urlNnm + "/forum/" + entry.select("a[href^='download.php?id']").attr("href")
						//linkMagnet = "http://tparser.org/magnet.php?t=14" + linkTorrent.split("id=")[1]
					}
                    if (entry.html().contains("Рейтинг"))
                        description = "Рейтинг: " + entry.select("td[title='Рейтинг'] u").text()+" "
                    if (entry.html().contains("gensmall opened")) {
                        description += entry.select(".gensmall.opened").text()
                    }
                    if(description != "error") description = description.replace("error", "")
                    if (entry.html().contains("gensmall")) {
                        size = entry.select("td.gensmall").first().text()
						if(size.contains(" ")) {
                            size = if (size.split(" ").size > 1)
                                size.split(" ")[1] + " " + size.split(" ")[2]
                            else size.split(" ")[1]
                        }
                    }
                    sid = if (entry.html().contains("Seeders"))
                        entry.select("td[title='Seeders']").text() else "0"
                    lich = if (entry.html().contains("Leechers"))
                        entry.select("td[title='Leechers']").text() else "0"

                    if (title != "error" && sid != "error" && sid != "0") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun parseRutor(data: Document?) {
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("index")) {
                Log.d(TAG, "table : true")
                val tableList = data.select("#index tr")
                for (entry in tableList) {
                    defVar()
                    source = "Rutor"
                    if (entry.html().contains("/torrent/")) {
                        title = entry.select("a[href^='/torrent/']").text()
                        link  = Statics.urlRutor + entry.select("a[href^='/torrent/']").attr("href")
						}

                    if (entry.html().contains("align=\"right\"")) {
                        size = entry.select("td[align='right']").last().text()
                        description = entry.select("td").first().text()
                    }
                    if (entry.html().contains("downgif")) {
                        linkTorrent = entry.select(".downgif").attr("href")
                        if (!linkTorrent.contains(Statics.urlRutor))
                            linkTorrent = Statics.urlRutor + linkTorrent
                        //linkTorrent = linkTorrent.replace("http://d.rutor.info", Statics.urlRutor)
                    }
                    linkMagnet = if (entry.html().contains("magnet:")) {
                        entry.select("a[href^='magnet:']").attr("href")
                    } else "error"
                    //else "http://tparser.org/magnet.php?t=12" + link.split("torrent/")[1].split("/")[0]

                    sid = if (entry.html().contains("green"))
                        entry.select(".green").text().trim() else "0"
                    lich = if (entry.html().contains("red"))
                        entry.select(".red").text().trim() else "0"

                    Log.d(TAG, "Title: $title")
                    if (title != "error" && sid != "error" && sid != "0") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun parseTpb(data: Document?) {
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("searchResult")) {
                val tableList = data.select("#searchResult tr")
                for (entry in tableList) {
                    defVar()
                    source = "Tpb"
                    if (entry.html().contains("detName")) {
                        title = entry.select(".detName").text()
                        link  = Statics.urlTpb + entry.select(".detName a").attr("href")
                    }
                    if (entry.html().contains("detDesc")) {
                        description = entry.select(".detDesc").text()
                        if (description.contains(", Size ")) {
                            size = description.split(", Size ")[1].split(",")[0]
                            description = description.split(", Size ")[0]
                        }
                    }
                    if (entry.html().contains("magnet:?xt=")) {
                        linkMagnet = entry.select("a[href^='magnet:?xt=']").attr("href")
                    }
                    if (entry.html().contains("vertTh"))
                        type = entry.select(".vertTh a").first().text()

                    sid = if (entry.html().contains("td align=\"right\""))
                        entry.select("td[align='right']").first().text() else "0"
                    lich = if (entry.html().contains("td align=\"right\""))
                        entry.select("td[align='right']").last().text() else "0"

                    if (title != "error" && sid != "error" && sid != "0") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun parseBitru(data: Document?) {
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("zebra browse-list tabfix")) {
                val tableList = data.select(".zebra.browse-list.tabfix tr")
                for (entry in tableList) {
                    defVar()
                    source = "Bitru"
                    if (entry.html().contains("b-title")) {
                        title = entry.select(".b-title a").text()
                        link  = Statics.urlBitru + "/" + entry.select(".b-title a").attr("href")
                    }
                    if (entry.html().contains("ellips"))
                        description = entry.select(".ellips span").last().text()
                    if (entry.html().contains("b-tmp"))
                        type = entry.select(".b-tmp").attr("href")
                    if (entry.html().contains("title=\"Размер\""))
                        size = entry.select("td[title^='Размер']").text()
                    sid = if (entry.html().contains("b-seeders"))
                        entry.select(".b-seeders").text() else "0"
                    lich = if (entry.html().contains("b-leechers"))
                        entry.select(".b-leechers").text() else "0"
                    if (link.contains("?id="))
                        linkTorrent = Statics.urlBitru + "/download.php?id=" +
                                link.split("?id=")[1]
                    if (type.contains("?tmp="))
                        type = type.split("?tmp=")[1]

                    if (title != "error" && sid != "error" && sid != "0" && sid != "1") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun parseZooqle(data: Document?){
        if (data != null) {
            Log.d(TAG, "Start")
            if (data.html().contains("table-torrents")) {
                val tableList = data.select(".table-torrents tr")
                for (entry in tableList) {
                    defVar()
                    source = "Zooqle"
                    if (entry.html().contains("text-trunc"))
                        title = entry.select(".text-trunc a").text()

                    if (entry.html().contains("smallest trans90")) {
                        description = entry.select(".smallest.trans90").text()
                        if (entry.html().contains("smaller trans80"))
                            description = description + " " + entry.select(".smaller.trans80").text()
                    } else if (entry.html().contains("smaller trans80"))
                        description = entry.select(".smaller.trans80").text()

                    if (entry.html().contains("class=\"zqf zqf-"))
                        type = entry.html().split("class=\"zqf zqf-")[1]
                                .split(" ")[0]

                    if (entry.html().contains("prog-green prog-l"))
                        sid = entry.select(".prog-green.prog-l").text()
                    if (entry.html().contains("prog-r"))
                        lich = entry.select(".prog-r").text()
                    if (entry.html().contains("progress prog prog-narrow trans90"))
                        size = entry.select(".progress.prog.prog-narrow.trans90").text()
                    if (entry.html().contains("text-trunc"))
                        link = Statics.urlZooqle + entry.select(".text-trunc a")
                                .attr("href")
                    if (entry.html().contains("href=\"magnet:"))
                        linkMagnet = entry.select("a[href^='magnet']").attr("href")
                    if (entry.html().contains("href=\"/download/"))
                        linkTorrent = Statics.urlZooqle + entry.select("a[href^='/download/']")
                                .attr("href")

                    if (title != "error" && sid != "error" && sid != "0" && sid != "1") {
                        item.title.add(title)
                        item.description.add(description.replace("error", ""))
                        item.sid.add(sid)
                        item.lich.add(lich)
                        item.source.add(source)
                        item.size.add(size.replace("error", " "))
                        item.type.add(type)
                        item.link.add(link)
                        item.linkMagnet.add(linkMagnet)
                        item.linkTorrent.add(linkTorrent)
                    }
                }
            } else
                Statics.nextPage = false
        }else
            Statics.nextPage = false
    }

    private fun defVar () {
        title = "error"
        description = "error"
        sid = "error"
        lich = "error"
        source = "error"
        type = "error"
        size = "error"
        link = "error"
        linkMagnet = "error"
        linkTorrent = "error"
    }

    private fun getData(url: String): Document? {
        return try {
            var u = url
            if (url.startsWith("http://cameleo.xyz/r?url=")){
                u = "http://cameleo.xyz/r?url=" + url.split("/r?url=")[1]
                        .replace(":","%3A").replace("/","%2F")
            }
            Log.d(TAG, "getData: get connected to $u")
            Jsoup.connect(u).timeout(10000).ignoreContentType(true).get()
        } catch (e: Exception) {
            Statics.nextPage = false
            Log.d(TAG, "getData: connected false to $url")
            e.printStackTrace()
            null
        }

    }
}