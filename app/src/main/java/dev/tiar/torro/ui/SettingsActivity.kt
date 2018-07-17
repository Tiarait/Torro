package dev.tiar.torro.ui

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.*
import android.support.design.widget.Snackbar
import android.view.MenuItem
import dev.tiar.torro.R
import dev.tiar.torro.items.Statics



/**
 * A [PreferenceActivity] that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 *
 * See [Android Design: Settings](http://developer.android.com/design/patterns/settings.html)
 * for design guidelines and the [Settings API Guide](http://developer.android.com/guide/topics/ui/settings.html)
 * for more information on developing a Settings UI.
 */
class SettingsActivity : AppCompatPreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupActionBar()
    }

    /**
     * Set up the [android.app.ActionBar], if the API is available.
     */
    private fun setupActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_check)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            this.onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * {@inheritDoc}
     */
    override fun onIsMultiPane(): Boolean {
        return isXLargeTablet(this)
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    override fun onBuildHeaders(target: List<PreferenceActivity.Header>) {
        loadHeadersFromResource(R.xml.pref_headers, target)
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    override fun isValidFragment(fragmentName: String): Boolean {
        return PreferenceFragment::class.java.name == fragmentName
                || InfoPreferenceFragment::class.java.name == fragmentName
                || SettingsPreferenceFragment::class.java.name == fragmentName
                || WebPreferenceFragment::class.java.name == fragmentName
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class InfoPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_information)
            setHasOptionsMenu(true)

            val btnUpdate = findPreference("update")
            btnUpdate.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                //                val update = Update(activity)
//                update.execute()
                true
            }

            val btnEmail = findPreference("email")
            btnEmail.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val emailIntent = Intent(android.content.Intent.ACTION_SEND)
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Torro")
                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("tiarait.dev@gmail.com"))
                emailIntent.putExtra(Intent.EXTRA_TEXT, "")
                emailIntent.type = "message/rfc822"
                emailIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(Intent.createChooser(emailIntent, "Выберите email клиент :"))
                true
            }

            val pb24 = findPreference("pb24")
            pb24.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                copiedText(pb24.title.toString())
                true
            }

            val qiwi = findPreference("qiwi")
            qiwi.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                copiedText(qiwi.title.toString())
                true
            }

            val btnWmr = findPreference("wmr")
            btnWmr.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                copiedText(btnWmr.title.toString())
                true
            }

            val btnWmu = findPreference("wmu")
            btnWmu.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                copiedText(btnWmu.title.toString())
                true
            }

            val btnWmz = findPreference("wmz")
            btnWmz.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                copiedText(btnWmz.title.toString())
                true
            }
        }

        @Suppress("DEPRECATION")
        private fun copiedText(copiedText: String) {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as android.text.ClipboardManager
            clipboard.text = copiedText
            Snackbar.make(view, "Скопировано", Snackbar.LENGTH_SHORT).show()
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class SettingsPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_settings)
            setHasOptionsMenu(true)


        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    class WebPreferenceFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.pref_domen)
            setHasOptionsMenu(true)

            val preference = PreferenceManager.getDefaultSharedPreferences(activity)

            val freerutorUrl = findPreference("rutor_furl") as EditTextPreference
            freerutorUrl.summary = preference.getString("rutor_furl", Statics.urlRutor)
            val zooqleUrl = findPreference("zooqle_furl") as EditTextPreference
            zooqleUrl.summary = preference.getString("zooqle_furl", Statics.urlZooqle)
            val tpbUrl = findPreference("tpb_furl") as EditTextPreference
            tpbUrl.summary = preference.getString("tpb_furl", Statics.urlTpb)
            val bitruUrl = findPreference("bitru_furl") as EditTextPreference
            bitruUrl.summary = preference.getString("bitru_furl", Statics.urlBitru)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            val id = item.itemId
            if (id == android.R.id.home) {
                activity.onBackPressed()
                return true
            }
            return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private fun isXLargeTablet(context: Context): Boolean {
            return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_XLARGE
        }
    }
}
