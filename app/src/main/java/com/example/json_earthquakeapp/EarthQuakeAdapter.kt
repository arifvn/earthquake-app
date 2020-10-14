package com.example.json_earthquakeapp

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.earthquake_list_item.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor

class EarthQuakeAdapter(
    private val context: Context,
    private val listItem: ArrayList<EarthQuake>,
    private val callback: (EarthQuake) -> Unit
) :
    RecyclerView.Adapter<EarthQuakeAdapter.EarthQuakeHolder>() {

    companion object {
        private const val LOCATION_SEPARATOR = " of "
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EarthQuakeHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.earthquake_list_item, parent, false)
        return EarthQuakeHolder(itemView)
    }

    override fun getItemCount(): Int = listItem.size

    override fun onBindViewHolder(holder: EarthQuakeHolder, position: Int) {
        holder.bindItem(listItem[position])
    }

    inner class EarthQuakeHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bindItem(earthQuake: EarthQuake) {
            with(view) {
                tv_magnitude.text = formatMagnitude(earthQuake.mag)

                val magnitudeCircle: GradientDrawable = tv_magnitude.background as GradientDrawable
                val magnitudeColor = getMagnitudeColor(earthQuake.mag)
                magnitudeCircle.color = ColorStateList.valueOf(magnitudeColor)

                val originalLocation = earthQuake.place
                var locationOffset: String = ""
                var primaryLocation: String = ""
                if (originalLocation.contains(LOCATION_SEPARATOR)) {
                    val parts = originalLocation.split(LOCATION_SEPARATOR)
                    locationOffset = parts[0] + LOCATION_SEPARATOR
                    primaryLocation = parts[1]
                } else {
                    locationOffset = context.getString(R.string.near_the)
                    primaryLocation = originalLocation
                }
                tv_location_offset.text = locationOffset
                tv_primary_location.text = primaryLocation

                val date = Date(earthQuake.time)
                tv_date.text = formatDate(date)
                tv_time.text = formatTime(date)

                setOnClickListener {
                    callback(earthQuake)
                }
            }
        }
    }

    private fun getMagnitudeColor(magnitude: Double): Int {
        val magnitudeColorResourceId: Int
        val magnitudeFloor = floor(magnitude).toInt()
        magnitudeColorResourceId = when (magnitudeFloor) {
            0, 1 -> R.color.magnitude1
            2 -> R.color.magnitude2
            3 -> R.color.magnitude3
            4 -> R.color.magnitude4
            5 -> R.color.magnitude5
            6 -> R.color.magnitude6
            7 -> R.color.magnitude7
            8 -> R.color.magnitude8
            9 -> R.color.magnitude9
            else -> R.color.magnitude10plus
        }
        return getColor(context, magnitudeColorResourceId)
    }

    private fun formatMagnitude(magnitude: Double): String {
        val magnitudeFormat = DecimalFormat("0.0")
        return magnitudeFormat.format(magnitude)
    }

    private fun formatDate(dateObject: Date): String? {
        val dateFormat = SimpleDateFormat("LLL dd, yyyy", Locale.getDefault())
        return dateFormat.format(dateObject)
    }

    private fun formatTime(dateObject: Date): String? {
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return timeFormat.format(dateObject)
    }
}