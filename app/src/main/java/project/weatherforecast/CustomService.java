package project.weatherforecast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
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
    private HttpHelper mHttpHelper;
    private Date mDate;
    private String mURL;
    private static DBHelper dbHelper = MainActivity.dbHelper;
    private NotificationManager mManager;
    private Notification mNotification;

    private boolean mThreadStarted = false;

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
        mNotification = new Notification.Builder(this)
                .setContentTitle("Weather Forecast")
                .setContentText("DataBase updated!")
                .setSmallIcon(R.drawable.w01d)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, DetailsActivity.class), 0))
                .setAutoCancel(true).build();
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mHttpHelper = new HttpHelper();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(PRESENTATION_PERIOD);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                while(true) {
                    try {
                        Log.d("AUTO CITY FETCHING:", "STARTED");
                        CityWeatherInfo.CityWeather[] cityWeathers = dbHelper.readAllCityWeathers();
                        for(int i = 0; i < cityWeathers.length; i++) {
                            Thread.sleep(5000);
                            String cityName = cityWeathers[i].getCityName();
                            mURL = DetailsActivity.BASE_URL + CityWeatherInfo.WeatherFormater.formatCityNameForURL(cityName) + DetailsActivity.API_ID;

                            Log.d("JSON OBJECT ACQUIRING:", "STARTED");
                            final JSONObject jsonObject = mHttpHelper.getJSONObjectFromURL(mURL);
                            final JSONObject mainObject = jsonObject.getJSONObject("main"); // used for temperature humidity and pressure
                            final JSONObject windObject = jsonObject.getJSONObject("wind");
                            final JSONObject sysObject = jsonObject.getJSONObject("sys"); //used for sunrise and sunset
                            final JSONArray weatherArray = jsonObject.getJSONArray("weather");
                            final JSONObject weatherObject = weatherArray.getJSONObject(0);
                            Log.d("JSON OBJECT ACQUIRING:", "FINISHED. FETCHED CITY: " + cityName);

                            long dateUnix = mDate.getTime();
                            double temperature = Double.parseDouble(mainObject.getString("temp"));
                            double pressure = Double.parseDouble(mainObject.getString("pressure"));
                            double humidity = Double.parseDouble(mainObject.getString("humidity"));
                            double windDegrees;
                            if(windObject.has("deg")) {
                                windDegrees = Double.parseDouble(windObject.getString("deg"));
                            } else {
                                windDegrees = 10;
                            }
                            double windSpeed = Double.parseDouble(windObject.getString("speed"));
                            long sunrise = Long.parseLong(sysObject.getString("sunrise"));
                            long sunset = Long.parseLong(sysObject.getString("sunset"));
                            String weatherIconString = weatherObject.getString("icon");


                            CityWeatherInfo.CityWeather cityWeather = new CityWeatherInfo.CityWeather(dateUnix, cityName, temperature, pressure,
                                    humidity, sunrise, sunset, windSpeed, windDegrees, weatherIconString);
                            dbHelper.deleteCityWeather(cityName);
                            dbHelper.insert(cityWeather);
                        }
                        mManager.notify(0, mNotification);
                        Log.d("AUTO CITY FETCHING:", "ENDED");
                        Thread.sleep(PRESENTATION_PERIOD);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!mThreadStarted) {
            mThread.start();
            mThreadStarted = true;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadStarted = false;
    }
}
