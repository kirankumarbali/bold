package com.rec.bolt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by robot on 9/17/15.
 */
public class WeatherDataArrayAdapter extends ArrayAdapter<WeatherData> {

    public WeatherDataArrayAdapter(Context context, ArrayList<WeatherData> data) {
        super(context, 0, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WeatherData item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.weather_list_item, parent, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);

        viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(
                item.weatherId));


        // Read date from cursor
        long dateInMillis = item.getDateTime();
        // Find TextView and set formatted date on it
        viewHolder.dateView.setText(Utility.getFriendlyDayString(getContext(), dateInMillis));

        // Read weather forecast from cursor
        String description = item.getDescription();
        // Find TextView and set weather forecast on it
        viewHolder.descriptionView.setText(description);

        // For accessibility, add a content description to the icon field
        viewHolder.iconView.setContentDescription(description);

        // Read high temperature from cursor
        double high = item.getHigh();
        viewHolder.highTempView.setText(Utility.formatTemperature(getContext(), high));

        // Read low temperature from cursor
        double low = item.getLow();
        viewHolder.lowTempView.setText(Utility.formatTemperature(getContext(), low));

        return convertView;
    }


    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
        }
    }
}
