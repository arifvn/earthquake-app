package com.example.json_earthquakeapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.earthquake_activity.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class EarthQuakeActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var listEarthQuake = ArrayList<EarthQuake>()
    private lateinit var call: Call<Result>
    private lateinit var orderBy: String
    private lateinit var minMagnitude: String
    private lateinit var sharePrefs: SharedPreferences

    companion object {
        private const val LIST_EARTHQUAKE = "list_earthquake"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.earthquake_activity)

        sharePrefs = PreferenceManager.getDefaultSharedPreferences(this)
        sharePrefs.registerOnSharedPreferenceChangeListener(this)
        getDefaultValue(sharePrefs)

        rv_main.adapter = EarthQuakeAdapter(this, listEarthQuake) {
            onClickListener(it)
        }
        rv_main.layoutManager = LinearLayoutManager(this)

        checkSaveInstanceState(savedInstanceState)
    }

    private fun checkSaveInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            getData()
        }
        if (savedInstanceState != null) {
            listEarthQuake.addAll(
                savedInstanceState.getParcelableArrayList<EarthQuake>(
                    LIST_EARTHQUAKE
                ) as ArrayList<EarthQuake>
            )
        }
    }

    private fun checkConnecting(): Boolean {
        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val netInfo = connMgr.activeNetworkInfo
            if (netInfo != null) {
                return netInfo.isConnected
            }
        } else {
            val netInfo = connMgr.activeNetwork
            if (netInfo != null) {
                val netCapabilities = connMgr.getNetworkCapabilities(netInfo);
                return (netCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || netCapabilities.hasTransport(
                    NetworkCapabilities.TRANSPORT_WIFI
                ))
            }
        }
        return false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(LIST_EARTHQUAKE, listEarthQuake)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> true
        }
    }

    private fun getData() {
        if (!checkConnecting()) {
            tv_status.text = getString(R.string.no_internet_connection)
            tv_status.visibility = View.VISIBLE
            loading_indicator.visibility = View.GONE
            return
        }
        loading_indicator.visibility = View.VISIBLE
        call = ApiClient.getClient.getEarthQuake(orderBy, minMagnitude)
        call.enqueue(object : Callback<Result> {
            override fun onFailure(call: Call<Result>, t: Throwable) {
                loading_indicator.visibility = View.GONE
                tv_status.text = t.message
                tv_status.visibility = View.VISIBLE
            }

            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                parsingData(response)
                loading_indicator.visibility = View.GONE

                if (listEarthQuake.size == 0) {
                    tv_status.visibility = View.VISIBLE
                    tv_status.text = getString(R.string.no_earthquakes)
                } else {
                    tv_status.visibility = View.GONE
                }
            }
        })
    }

    private fun parsingData(response: Response<Result>) {
        val result = response.body()?.features
        if (result != null) {
            for (i in result.indices) {
                val earthQuake = result[i].properties
                listEarthQuake.add(earthQuake)
                rv_main.adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == null) {
            return
        }
        getDefaultValue(sharePrefs)
        listEarthQuake.clear()
        rv_main.adapter?.notifyDataSetChanged()
        getData()
    }

    private fun getDefaultValue(sharePrefs: SharedPreferences) {
        val minKey = getString(R.string.settings_min_magnitude_key)
        val minDefault = getString(R.string.settings_min_magnitude_default)
        minMagnitude = sharePrefs.getString(minKey, minDefault).toString()

        val orderMin = getString(R.string.settings_order_by_key)
        val orderDefault = getString(R.string.settings_order_by_default)
        orderBy = sharePrefs.getString(orderMin, orderDefault).toString()
    }

    private fun onClickListener(earthQuake: EarthQuake) {
        val url = Uri.parse(earthQuake.url)
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }
}