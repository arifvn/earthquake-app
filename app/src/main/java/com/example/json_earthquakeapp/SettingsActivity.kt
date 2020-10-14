package com.example.json_earthquakeapp

import android.os.Bundle
import android.os.PersistableBundle
import android.text.InputType
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*

class SettingsActivity : AppCompatActivity() {

    companion object {
        private val TAB_TITLE = SettingsActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_settings, SettingsFragment())
                .commit()
        } else {
            title = savedInstanceState.getCharSequence(TAB_TITLE)
        }

        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.settings_title)
            }
        }

        setupToolbar()
    }

    private fun setupToolbar() {
        supportActionBar?.setTitle(R.string.settings_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putCharSequence(TAB_TITLE, title)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val minMagnitude: Preference? =
                findPreference(getString(R.string.settings_min_magnitude_key))
            bindPreferenceSummaryValue(minMagnitude)

            val orderBy: Preference? =
                findPreference(getString(R.string.settings_order_by_key))
            bindPreferenceSummaryValue(orderBy)

            val editTextPreference =
                preferenceManager.findPreference<EditTextPreference>(getString(R.string.settings_min_magnitude_key))
            editTextPreference!!.setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
            }
        }

        override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
            val stringValue: String = newValue.toString()
            if (preference is ListPreference) {
                val listPreference: ListPreference = preference
                val prefIndex: Int = listPreference.findIndexOfValue(stringValue)
                if (prefIndex >= 0) {
                    val labels: Array<CharSequence> = listPreference.entries
                    preference.summary = labels[prefIndex]
                }
            } else {
                preference?.summary = stringValue
            }
            return true
        }

        private fun bindPreferenceSummaryValue(preference: Preference?) {
            preference?.onPreferenceChangeListener = this
            val prefs = PreferenceManager.getDefaultSharedPreferences(preference?.context)
            val prefString = prefs.getString(preference?.key, "")
            onPreferenceChange(preference, prefString)
        }
    }
}