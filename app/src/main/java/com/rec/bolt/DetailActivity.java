package com.rec.bolt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        WeatherData data = (WeatherData) intent.getSerializableExtra("weatherData");

        mIconView = (ImageView) findViewById(R.id.detail_icon);
        mDateView = (TextView) findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) findViewById(R.id.detail_pressure_textview);


        int weatherId = data.getWeatherId();

        // Use weather art image
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        // Read date from cursor and update views for day of week and date
        long date = data.getDateTime();
        String friendlyDateText = Utility.getDayName(this, date);
        String dateText = Utility.getFormattedMonthDay(this, date);
        mFriendlyDateView.setText(friendlyDateText);
        mDateView.setText(dateText);

        // Read description from cursor and update view
        String description = data.getDescription();
        mDescriptionView.setText(description);

        // For accessibility, add a content description to the icon field
        mIconView.setContentDescription(description);


        double high = data.getHigh();
        String highString = Utility.formatTemperature(this, high);
        mHighTempView.setText(highString);

        // Read low temperature from cursor and update view
        double low = data.getLow();
        String lowString = Utility.formatTemperature(this, low);
        mLowTempView.setText(lowString);

        // Read humidity from cursor and update view
        float humidity = data.getHumidity();
        mHumidityView.setText(this.getString(R.string.format_humidity, humidity));

        // Read wind speed and direction from cursor and update view
        double windSpeedStr = data.getWindSpeed();
        double windDirStr = data.getWindDirection();
        mWindView.setText(Utility.getFormattedWind(this, windSpeedStr, windDirStr));

        // Read pressure from cursor and update view
        double pressure = data.getPressure();
        mPressureView.setText(this.getString(R.string.format_pressure, pressure));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
