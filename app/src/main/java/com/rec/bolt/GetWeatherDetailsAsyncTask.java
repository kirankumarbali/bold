package com.rec.bolt;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by robot on 9/17/15.
 */
public class GetWeatherDetailsAsyncTask extends AsyncTask<Void, Void, String> implements AdapterView.OnItemClickListener {
    Activity activity;
    View rootView;
    ArrayList<WeatherData> weatherDataArrayList;

    public GetWeatherDetailsAsyncTask(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
    }

    @Override
    protected String doInBackground(Void... voids) {
        ServiceHandler service = new ServiceHandler();
        return service.makeServiceCall("http://api.openweathermap.org/data/2.5/forecast/daily?q=Visakhapatnam&cnt=14&units=metric",ServiceHandler.GET);
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.i("Testing","inside onPostExecute");
        try {
            getWeatherDataFromJson(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getWeatherDataFromJson(String forecastJsonStr)
            throws JSONException {
        Log.i("Testing","inside getWeatherDataFromJson");
        // Now we have a String representing the complete forecast in JSON Format.
        // Fortunately parsing is easy:  constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        // These are the names of the JSON objects that need to be extracted.

        // Location information
        final String OWM_CITY = "city";
        final String OWM_CITY_NAME = "name";
        final String OWM_COORD = "coord";

        // Location coordinate
        final String OWM_LATITUDE = "lat";
        final String OWM_LONGITUDE = "lon";

        // Weather information.  Each day's forecast info is an element of the "list" array.
        final String OWM_LIST = "list";

        final String OWM_PRESSURE = "pressure";
        final String OWM_HUMIDITY = "humidity";
        final String OWM_WINDSPEED = "speed";
        final String OWM_WIND_DIRECTION = "deg";

        // All temperatures are children of the "temp" object.
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";

        final String OWM_WEATHER = "weather";
        final String OWM_DESCRIPTION = "main";
        final String OWM_WEATHER_ID = "id";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
            String cityName = cityJson.getString(OWM_CITY_NAME);

            JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
            double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);
            double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);

            //##long locationId = addLocation(locationSetting, cityName, cityLatitude, cityLongitude);

            // Insert the new weather information into the database
             weatherDataArrayList = new ArrayList<WeatherData>();

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();

            for(int i = 0; i < weatherArray.length(); i++) {
                // These are the values that will be collected.
                long dateTime;
                double pressure;
                int humidity;
                double windSpeed;
                double windDirection;

                double high;
                double low;

                String description;
                int weatherId;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);

                // Cheating to convert this to UTC time, which is what we want anyhow
                dateTime = dayTime.setJulianDay(julianStartDay+i);

                pressure = dayForecast.getDouble(OWM_PRESSURE);
                humidity = dayForecast.getInt(OWM_HUMIDITY);
                windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);

                // Description is in a child array called "weather", which is 1 element long.
                // That element also contains a weather code.
                JSONObject weatherObject =
                        dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);
                weatherId = weatherObject.getInt(OWM_WEATHER_ID);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                high = temperatureObject.getDouble(OWM_MAX);
                low = temperatureObject.getDouble(OWM_MIN);

                WeatherData weatherInfo = new WeatherData();

                weatherInfo.setDateTime(dateTime);
                weatherInfo.setHumidity(humidity);
                weatherInfo.setPressure(pressure);
                weatherInfo.setWindSpeed(windSpeed);
                weatherInfo.setWindDirection(windDirection);
                weatherInfo.setHigh(high);
                weatherInfo.setLow(low);
                weatherInfo.setDescription(description);
                weatherInfo.setWeatherId(weatherId);

                weatherDataArrayList.add(weatherInfo);
            }


            ListView forecastList = (ListView)rootView.findViewById(R.id.listview_forecast);

            WeatherDataArrayAdapter mForecastAdapter = new WeatherDataArrayAdapter(
                    activity, // The current context (this activity)
                    weatherDataArrayList);

            forecastList.setOnItemClickListener(this);

            forecastList.setAdapter(mForecastAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Intent intent = new Intent(activity,DetailActivity.class);
        intent.putExtra("weatherData",weatherDataArrayList.get(i));
        activity.startActivity(intent);
    }
}
