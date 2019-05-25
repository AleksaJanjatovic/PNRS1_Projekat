package project.weatherforecast;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class CustomService extends Service {

    private final IBinder mBinder = new CustomBinder();
    private static final long PERIOD = 10800000L; //Three hours in ms
    private static final long PRESENTATION_PERIOD = 10000;//Three minutes in ms
    private Thread mThread;
    private Handler mHandler;
    private HttpHelper mHttpHelper;
    private Date mDate;
    private String mURL;

    public CustomService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class CustomBinder extends Binder {
        CustomService getService() {
            return CustomService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDate = Calendar.getInstance().getTime();
        //mHandler = new Handler();
        mHttpHelper = new HttpHelper();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    CityWeatherInfo.CityWeather[] cityWeathers = MainActivity.dbHelper.readAllCityWeathers();
                    for(int i = 0; i < cityWeathers.length; i++) {
                        Thread.sleep(5000);
                        String cityName = cityWeathers[i].getCityName();
                        mURL = DetailsActivity.BASE_URL + CityWeatherInfo.WeatherFormater.formatCityNameForURL(cityWeathers[i].getCityName()) + DetailsActivity.API_ID;

                        Log.d("JSON OBJECT ACQUIRING:", "Zaba");
                        final JSONObject jsonObject = mHttpHelper.getJSONObjectFromURL(mURL);
                        Log.d("JSON OBJECT ACQUIRING:", "full_url");
                        final JSONObject mainObject = jsonObject.getJSONObject("main"); // used for temperature humidity and pressure
                        Log.d("JSON OBJECT ACQUIRING:", "main");
                        final JSONObject windObject = jsonObject.getJSONObject("wind");
                        Log.d("JSON OBJECT ACQUIRING:", "wind");
                        final JSONObject sysObject = jsonObject.getJSONObject("sys"); //used for sunrise and sunset
                        Log.d("JSON OBJECT ACQUIRING:", "sys");
                        final JSONArray weatherArray = jsonObject.getJSONArray("weather");
                        Log.d("JSON OBJECT ACQUIRING:", "weather");
                        final JSONObject weatherObject = weatherArray.getJSONObject(0);
                        Log.d("JSON OBJECT ACQUIRING:", "weatherObject");

                        long dateUnix = mDate.getTime();
                        double temperature = Double.parseDouble(mainObject.getString("temp"));
                        double pressure = Double.parseDouble(mainObject.getString("pressure"));
                        double humidity = Double.parseDouble(mainObject.getString("humidity"));
                        int windDegrees;
                        if(windObject.has("deg")) {
                            windDegrees = Integer.parseInt(windObject.getString("deg"));
                        } else {
                            windDegrees = 10;
                        }
                        double windSpeed = Double.parseDouble(windObject.getString("speed"));
                        long sunrise = Long.parseLong(sysObject.getString("sunrise"));
                        long sunset = Long.parseLong(sysObject.getString("sunset"));
                        String weatherIconString = weatherObject.getString("icon");
                        Log.d("JSON OBJECT ACQUIRING:", "Over");

                        CityWeatherInfo.CityWeather cityWeather = new CityWeatherInfo.CityWeather(dateUnix, cityName, temperature, pressure,
                                humidity, sunrise, sunset, windSpeed, windDegrees, weatherIconString);
                        MainActivity.dbHelper.deleteCityWeather(cityName);
                        MainActivity.dbHelper.insert(cityWeather);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(PRESENTATION_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mThread.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
