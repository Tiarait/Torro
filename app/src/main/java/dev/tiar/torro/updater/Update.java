package dev.tiar.torro.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import dev.tiar.torro.BuildConfig;

/**
 * Created by Tiar on 01.07.2017.
 */

public class Update extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    //private final String GITHUB_RELEASES_URL = "https://github.com/Tiarait/KinoTor/releases/latest";
    private final String PDA_RELEASES_URL = "https://4pda.ru/forum/index.php?showtopic=909965";
    private static float curr_ver = Float.parseFloat(BuildConfig.VERSION_NAME);
    private boolean newVer = false;

    public Update(Activity a) {
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... params) {
        //UpdaterGit(Getdata(GITHUB_RELEASES_URL));
        Updater4pda(Getdata(PDA_RELEASES_URL));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!newVer)
            Toast.makeText(activity, "Обновлений нет", Toast.LENGTH_SHORT).show();
    }

//    private void UpdaterGit(Document getdata) {
//        if (getdata != null) {
//            float latest_ver = Float.parseFloat(getdata.select("span.css-truncate-target").first().text());
//            if (curr_ver < latest_ver && getdata.html().contains(".apk")) {
//                final String download_url ="https://github.com" + getdata.select("a[href$='.apk']").first().attr("href");
//                update_d = new UpdateDialog();
//                update_d.show(activity.getFragmentManager(), download_url);
//            } else {
//                new_ver = false;
//                Log.d("mydebug", "version: " + curr_ver + " git: " + latest_ver);
//            }
//        }
//    }

    private void Updater4pda(Document getdata) {
        if (getdata != null) {
            Element l = getdata.select("#post-75167179").first();
            float latest_ver = Float.parseFloat(l.text().split("версия: ")[1].split(" ")[0]);
            if (curr_ver < latest_ver) {
                newVer = true;
                DialogFragment update_d = new UpdateDialog();
                update_d.show(activity.getFragmentManager(), PDA_RELEASES_URL);
            } else {
                Log.d("mydebug", "version: " + curr_ver + " 4pda: " + latest_ver);
            }
        }
    }

    private Document Getdata(String url){
        try {
            return Jsoup.connect(url)
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

