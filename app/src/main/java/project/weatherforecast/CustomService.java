package project.weatherforecast;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

public class CustomService extends Service {

    private final IBinder mBinder = new CustomBinder();
    private static final long PERIOD = 10800000L; //Three hours in ms
    private static final long PRESENTATION_PERIOD = 600000;//Three minutes in ms
    private Thread mThread;
    private HttpHelper mHttpHelper;
    private String mURL;
    private static DBHelper dbHelper = MainActivity.dbHelper;
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private boolean mThreadStarted = false;
    private String mCityName;
    private int mDan;

    public CustomService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    class CustomBinder extends Binder {
        CustomService getService() {
            return CustomService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mManager = (NotificationManager) CustomService.this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("project.weatherforecast", "channel", NotificationManager.IMPORTANCE_DEFAULT);
            mManager.createNotificationChannel(mChannel);
        }
        mHttpHelper = new HttpHelper();
        mThread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(PRESENTATION_PERIOD);
                        Log.d("AUTO CITY FETCHING:", "STARTED");

                        mURL = DetailsActivity.BASE_URL + CityWeatherInfo.WeatherFormater.formatCityNameForURL(mCityName) + DetailsActivity.API_ID;

                        Log.d("JSON OBJECT ACQUIRING:", "STARTED");
                        final JSONObject jsonObject = mHttpHelper.getJSONObjectFromURL(mURL);
                        final JSONObject mainObject = jsonObject.getJSONObject("main"); // used for temperature humidity and pressure
                        final JSONObject windObject = jsonObject.getJSONObject("wind");
                        final JSONObject sysObject = jsonObject.getJSONObject("sys"); //used for sunrise and sunset
                        final JSONArray weatherArray = jsonObject.getJSONArray("weather");
                        final JSONObject weatherObject = weatherArray.getJSONObject(0);
                        Log.d("JSON OBJECT ACQUIRING:", "FINISHED. FETCHED CITY: " + mCityName);

                        long dateUnix = Calendar.getInstance().getTime().getTime();
                        int day = CityWeatherInfo.WeatherFormater.extractDayFromUTCTime(dateUnix);
                        double temperature = Double.parseDouble(mainObject.getString("temp"));
                        double pressure = Double.parseDouble(mainObject.getString("pressure"));
                        double humidity = Double.parseDouble(mainObject.getString("humidity"));
                        double windDegrees;
                        if (windObject.has("deg")) {
                            windDegrees = Double.parseDouble(windObject.getString("deg"));
                        } else {
                            windDegrees = 10;
                        }
                        double windSpeed = Double.parseDouble(windObject.getString("speed"));
                        long sunrise = Long.parseLong(sysObject.getString("sunrise"));
                        long sunset = Long.parseLong(sysObject.getString("sunset"));
                        String weatherIconString = weatherObject.getString("icon");


                        CityWeatherInfo.CityWeather cityWeather = new CityWeatherInfo.CityWeather(dateUnix, day, mCityName, temperature, pressure,
                                humidity, sunrise, sunset, windSpeed, windDegrees, weatherIconString);
                        dbHelper.deleteCityWeather(mCityName, day);
                        dbHelper.insert(cityWeather);

                        mBuilder = new NotificationCompat.Builder(CustomService.this, NotificationChannel.DEFAULT_CHANNEL_ID);
                        mBuilder.setChannelId("project.weatherforecast");
                        mBuilder.setAutoCancel(true)
                                .setDefaults(NotificationCompat.DEFAULT_ALL)
                                .setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.notification_icon)
                                .setTicker("Weather Forecast")
                                .setContentTitle(mCityName + " weather updated")
                                .setContentText("New temperature: " + Double.toString(temperature))
                                .setContentInfo("INFO");
                                //.setContentIntent(mPendingIntent);

                        Log.d("AUTO CITY FETCHING:", "ENDED");
                        mManager.notify(1, mBuilder.build());

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
            try {
                mCityName = intent.getExtras().getString(getResources().getString(R.string.lokacija_str));
                mDan = intent.getExtras().getInt(getResources().getString(R.string.dan_str));
                mThread.start();
                mThreadStarted = true;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThreadStarted = false;
    }
}
