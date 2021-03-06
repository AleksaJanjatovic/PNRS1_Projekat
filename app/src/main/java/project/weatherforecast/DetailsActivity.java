package project.weatherforecast;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity implements View.OnClickListener {

    Button bTemperatura, bIzlazakIZalazak, bVetar, bStatistika, bStopService;
    LinearLayout lTemperatura, lIzlazakIZalazak, lVetar;
    Spinner spinnerJedinica;
    TextView tLokacija, tDan, tTemperatura, tPritisak,
            tVlaznostVazduha, tIzlazakSunca, tZalazakSunca, tBrzinaVetra, tPravac;
    ImageView imageWeatherPNG;
    ImageButton bRefreshButton;

    public static final String BASE_URL = "https://openweathermap.org/data/2.5/weather?q=";
    public static final String API_ID = "&units=metric&appid=b6907d289e10d714a6e88b30761fae22";
    String mCityName;
    String full_url = BASE_URL;
    String prev_jedinica = "C";
    int mDay;

    Resources resources;
    HttpHelper httpHelper;
    double temperature;
    final Context mContext = this;
    CityWeatherInfo.CityWeather cityWeather;
    ServiceConnection connection;
    boolean mBound = false;
    boolean mApplicationRun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        resources = getResources();

        /* VIEW INITIALIZATION BEGIN */
        tTemperatura = findViewById(R.id.textViewTemperatura);
        tPritisak = findViewById(R.id.textViewPritisak);
        tVlaznostVazduha = findViewById(R.id.textViewVlaznostVazduha);
        tIzlazakSunca = findViewById(R.id.textViewIzlazakSunca);
        tZalazakSunca= findViewById(R.id.textViewZalazakSunca);
        tPravac = findViewById(R.id.textViewPravac);
        tBrzinaVetra = findViewById(R.id.textViewBrzinaVetra);
        tLokacija = findViewById(R.id.textViewLokacija);
        tDan = findViewById(R.id.textViewDan);
        bTemperatura = findViewById(R.id.buttonTemperatura);
        bIzlazakIZalazak = findViewById(R.id.buttonIzlazakIZalazak);
        bVetar = findViewById(R.id.buttonVetar);
        lTemperatura = findViewById(R.id.linearLayoutTemperatura);
        lIzlazakIZalazak = findViewById(R.id.linearLayoutIzlazakIZalazak);
        lVetar = findViewById(R.id.linearLayoutVetar);
        spinnerJedinica = findViewById(R.id.spinnerJedinica);
        imageWeatherPNG = findViewById(R.id.imageViewWeatherPNG);
        bStatistika = findViewById(R.id.buttonStatistika);
        bRefreshButton = findViewById(R.id.imageButtonRefresh);
        bStopService = findViewById(R.id.buttonStopService);
        /* VIEW INITIALIZATION END */

        /* INTERFACE BEGIN */
        bTemperatura.setOnClickListener(this);
        bIzlazakIZalazak.setOnClickListener(this);
        bVetar.setOnClickListener(this);
        bStatistika.setOnClickListener(this);
        bRefreshButton.setOnClickListener(this);
        bStopService.setOnClickListener(this);
        /* INTERFACE END */

        /* CONFIGURATING VIEW BEGIN */
        bTemperatura.setBackgroundColor(resources.getColor(R.color.colorGray));
        bIzlazakIZalazak.setBackgroundColor(resources.getColor(R.color.colorGray));
        bVetar.setBackgroundColor(resources.getColor(R.color.colorGray));
        lTemperatura.setVisibility(View.GONE);
        lIzlazakIZalazak.setVisibility(View.GONE);
        lVetar.setVisibility(View.GONE);
        /* CONFIGURATING VIEW END */

        /* LOCATION CONFIGURATION BEGIN */
        String location = resources.getString(R.string.lokacija_str);
        mCityName = "";
        try {
            mCityName = getIntent().getExtras().getString(location);
            full_url += CityWeatherInfo.WeatherFormater.formatCityNameForURL(mCityName) + API_ID;
            location += mCityName;
        } catch (Exception e) {
            e.printStackTrace();
            tLokacija.setText(resources.getString(R.string.erorr_location));
        }
        tLokacija.setText(location);
        /* LOCATION CONFIGURATION END */

        /* UTILITIES SETUP BEGIN */
        httpHelper = new HttpHelper();
        mDay = CityWeatherInfo.WeatherFormater.extractDayFromUTCTime(Calendar.getInstance().getTime().getTime());
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                CustomService.CustomBinder binder = (CustomService.CustomBinder) service;
                //bindService(new Intent(mContext, CustomService.class), connection, Context.BIND_AUTO_CREATE);
                mBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                //unbindService(connection);
                mBound = false;
            }
        };
        /* UTILITIES SETUP END */

        /* SPINNER INIT BEGIN */
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.jedinica_str_arr, android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJedinica.setAdapter(spinnerAdapter);
        /* SPINNER INIT END */

        /* READING WEATHER FROM DATABASE BEGIN */
        if((cityWeather = MainActivity.dbHelper.readCityWeather(mCityName, CityWeatherInfo.WeatherFormater.extractDayFromUTCTime(Calendar.getInstance().getTime().getTime()))) == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        fetchCityWeather();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshWeatherView();
                            }
                        });
                        MainActivity.dbHelper.insert(cityWeather);
                    } catch (NullPointerException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "No internet connection.\nWeather could not be fetched.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            refreshWeatherView();
            temperature = cityWeather.getTemperature();
        }
        /* READING WEATHER FROM DATABASE END */

        SystemClock.sleep(1000);

        /* SPINNER INTERFACE INIT BEGIN */
        spinnerJedinica.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String jedinica = spinnerJedinica.getItemAtPosition(position).toString();
                if(!jedinica.equals(prev_jedinica)) {
                    prev_jedinica = jedinica;

                    if(jedinica.equals("F")) {
                        temperature = CustomJNI.convert_temperature(temperature, 1);
                    } else {
                        temperature = CustomJNI.convert_temperature(temperature, 0);;
                    }
                    tTemperatura.setText(resources.getString(R.string.temperatura_str) + CityWeatherInfo.WeatherFormater.formatTemperature(temperature, jedinica));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        /* SPINNER INTERFACE INIT END */
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            MainActivity.mTextEmpty.setVisibility(View.GONE);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonTemperatura:
                lTemperatura.setVisibility(View.VISIBLE);
                lIzlazakIZalazak.setVisibility(View.GONE);
                lVetar.setVisibility(View.GONE);
                bTemperatura.setBackgroundColor(resources.getColor(R.color.colorCyan));
                bTemperatura.setTextColor(resources.getColor(R.color.colorBlack));
                bTemperatura.setEnabled(false);
                bIzlazakIZalazak.setBackgroundColor(resources.getColor(R.color.colorGray));
                bIzlazakIZalazak.setEnabled(true);
                bVetar.setBackgroundColor(resources.getColor(R.color.colorGray));
                bVetar.setEnabled(true);
                break;

            case R.id.buttonIzlazakIZalazak:
                lTemperatura.setVisibility(View.GONE);
                lIzlazakIZalazak.setVisibility(View.VISIBLE);
                lVetar.setVisibility(View.GONE);
                bTemperatura.setBackgroundColor(resources.getColor(R.color.colorGray));
                bTemperatura.setEnabled(true);
                bIzlazakIZalazak.setBackgroundColor(resources.getColor(R.color.colorCyan));
                bIzlazakIZalazak.setTextColor(resources.getColor(R.color.colorBlack));
                bIzlazakIZalazak.setEnabled(false);
                bVetar.setBackgroundColor(resources.getColor(R.color.colorGray));
                bVetar.setEnabled(true);
                break;

            case R.id.buttonVetar:
                lTemperatura.setVisibility(View.GONE);
                lIzlazakIZalazak.setVisibility(View.GONE);
                lVetar.setVisibility(View.VISIBLE);
                bTemperatura.setBackgroundColor(resources.getColor(R.color.colorGray));
                bTemperatura.setEnabled(true);
                bIzlazakIZalazak.setBackgroundColor(resources.getColor(R.color.colorGray));
                bIzlazakIZalazak.setEnabled(true);
                bVetar.setBackgroundColor(resources.getColor(R.color.colorCyan));
                bVetar.setTextColor(resources.getColor(R.color.colorBlack));
                bVetar.setEnabled(false);
                break;

            case R.id.buttonStopService:
                stopService(new Intent(this, CustomService.class));
                break;

            case R.id.buttonStatistika:
                Intent intent = new Intent(this, StatisticsActivity.class);
                intent.putExtra(resources.getString(R.string.lokacija_str), mCityName);
                SimpleDateFormat pom = new SimpleDateFormat("u", Locale.US);
                intent.putExtra(resources.getString(R.string.dan_str), pom.format(cityWeather.getDateAndTime()));
                startActivity(intent);
                break;

            case R.id.imageButtonRefresh:
                MainActivity.dbHelper.deleteCityWeather(mCityName, CityWeatherInfo.WeatherFormater.extractDayFromUTCTime(Calendar.getInstance().getTime().getTime()));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            fetchCityWeather();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    refreshWeatherView();
                                }
                            });
                            MainActivity.dbHelper.insert(cityWeather);
                        } catch (NullPointerException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "No internet connection.\nWeather could not be fetched.", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
        }
    }

    private void refreshWeatherView() {
        String pom;
        pom = resources.getString(R.string.temperatura_str) +
                CityWeatherInfo.WeatherFormater.formatTemperature(cityWeather.getTemperature(), spinnerJedinica.getSelectedItem().toString());
        tTemperatura.setText(pom);

        pom = resources.getString(R.string.vlaznost_str) +
                CityWeatherInfo.WeatherFormater.formatHumidity(cityWeather.getHumidity());
        tVlaznostVazduha.setText(pom);

        pom = resources.getString(R.string.pritisak_str) +
                CityWeatherInfo.WeatherFormater.formatPressure(cityWeather.getPressure());
        tPritisak.setText(pom);

        pom = resources.getString(R.string.pravac_str) +
                CityWeatherInfo.WeatherFormater.formatWindDirection(cityWeather.getWindDirection());
        tPravac.setText(pom);

        pom = resources.getString(R.string.brzina_str) +
                CityWeatherInfo.WeatherFormater.formatWindSpeed(cityWeather.getWindSpeed());
        tBrzinaVetra.setText(pom);

        pom = resources.getString(R.string.izlazak_str) +
                CityWeatherInfo.WeatherFormater.formatUTCTime(cityWeather.getSunrise());
        tIzlazakSunca.setText(pom);

        pom = resources.getString(R.string.zalazak_str) +
                CityWeatherInfo.WeatherFormater.formatUTCTime(cityWeather.getSunset());
        tZalazakSunca.setText(pom);

        int imageId = resources.getIdentifier(
                CityWeatherInfo.WeatherFormater.formatWeatherPNG(cityWeather.getWeatherIconString()),
                "drawable",
                getApplicationContext().getPackageName());
        imageWeatherPNG.setImageResource(imageId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM 'u' hh:mm", Locale.US);
        String dan = resources.getString(R.string.dan_str) + simpleDateFormat.format(cityWeather.getDateAndTime());
        tDan.setText(dan);
    }

    private void fetchCityWeather() throws NullPointerException, JSONException, IOException{
        Log.d("JSON OBJECT ACQUIRING:", "STARTED");
        final JSONObject jsonObject = httpHelper.getJSONObjectFromURL(full_url);
        final JSONObject mainObject = jsonObject.getJSONObject("main"); // used for temperature humidity and pressure
        final JSONObject windObject = jsonObject.getJSONObject("wind");
        final JSONObject sysObject = jsonObject.getJSONObject("sys"); //used for sunrise and sunset
        final JSONArray weatherArray = jsonObject.getJSONArray("weather");
        final JSONObject weatherObject = weatherArray.getJSONObject(0);
        Log.d("JSON OBJECT ACQUIRING:", "FINISHED. FETCHED CITY: " + mCityName);

        long dateUnix = Calendar.getInstance().getTime().getTime();
        int day = CityWeatherInfo.WeatherFormater.extractDayFromUTCTime(dateUnix);
        Log.d("FUN", "FACT");
        temperature = Double.parseDouble(mainObject.getString("temp"));
        Log.d("TEMPERATURA: ", "FETCHOVANA" + temperature);
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
        Log.d("JSON OBJECT ACQUIRING:", "Over");

        cityWeather = new CityWeatherInfo.CityWeather(dateUnix, day, mCityName, temperature, pressure, humidity, sunrise, sunset, windSpeed, windDegrees, weatherIconString);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startService(new Intent(this, CustomService.class).putExtra(resources.getString(R.string.lokacija_str), mCityName).putExtra(resources.getString(R.string.dan_str), mDay));
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, CustomService.class));
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mApplicationRun) {
            refreshWeatherView();
        }
        mApplicationRun = true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshWeatherView();
    }


}