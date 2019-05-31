package project.weatherforecast;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView mCityName;
    TextView[] mDani;
    TextView[] mTemperature;
    TextView[] mPritisci;
    TextView[] mVlaznost;

    TextView mMinTemperaturaDan, mMaxTemperaturaDan, mMinTemperaturaVrednost, mMaxTemperaturaVrednost;
    ImageButton mColdImageButton, mWarmImageButton;
    ArrayList<CityWeatherInfo.CityWeather> mCityWeathers;
    ArrayList<CityWeatherInfo.CityWeather> mCityWeathersCopy;
    CityWeatherInfo.CityWeather[] mWeekDaysCityWeathers;
    boolean coldCitiesToogle = false, warmCitiesToogle = false;
    String mCityNameString;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        mCityName = findViewById(R.id.textViewCityName);

        mDani = new TextView[] {
                findViewById(R.id.textViewPonedeljak),
                findViewById(R.id.textViewUtorak),
                findViewById(R.id.textViewSreda),
                findViewById(R.id.textViewCetvrtak),
                findViewById(R.id.textViewPetak),
                findViewById(R.id.textViewSubota),
                findViewById(R.id.textViewNedelja)};

        mTemperature = new TextView[] {
                findViewById(R.id.textViewPonedeljakTemperatura),
                findViewById(R.id.textViewUtorakTemperatura),
                findViewById(R.id.textViewSredaTemperatura),
                findViewById(R.id.textViewCetvrtakTemperatura),
                findViewById(R.id.textViewPetakTemperatura),
                findViewById(R.id.textViewSubotaTemperatura),
                findViewById(R.id.textViewNedeljaTemperatura)};

        mPritisci = new TextView[] {
                findViewById(R.id.textViewPonedeljakPritisak),
                findViewById(R.id.textViewUtorakPritisak),
                findViewById(R.id.textViewSredaPritisak),
                findViewById(R.id.textViewCetvrtakPritisak),
                findViewById(R.id.textViewPetakPritisak),
                findViewById(R.id.textViewSubotaPritisak),
                findViewById(R.id.textViewNedeljaPritisak)};

        mVlaznost = new TextView[] {
                findViewById(R.id.textViewPonedeljakVlaznost),
                findViewById(R.id.textViewUtorakVlaznost),
                findViewById(R.id.textViewSredaVlaznost),
                findViewById(R.id.textViewCetvrtakVlaznost),
                findViewById(R.id.textViewPetakVlaznost),
                findViewById(R.id.textViewSubotaVlaznost),
                findViewById(R.id.textViewNedeljaVlaznost)};


        mMinTemperaturaDan = findViewById(R.id.textViewMinTemperaturaDan);
        mMaxTemperaturaDan = findViewById(R.id.textViewMaxTemperaturaDan);
        mMinTemperaturaVrednost = findViewById(R.id.textViewMinTemperaturaVrednost);
        mMaxTemperaturaVrednost = findViewById(R.id.textViewMaxTemperaturaVrednost);

        mColdImageButton = findViewById(R.id.imageButtonCold);
        mWarmImageButton = findViewById(R.id.imageButtonWarm);
        mColdImageButton.setOnClickListener(this);
        mWarmImageButton.setOnClickListener(this);
        try {
            mCityNameString = getIntent().getExtras().getString(getResources().getString(R.string.lokacija_str));
            mCityName.setText(mCityNameString);
            mDani[Integer.parseInt(getIntent().getExtras().getString(getResources().getString(R.string.dan_str))) - 1].setTypeface(null, Typeface.BOLD);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        mCityWeathers = new ArrayList<>(Arrays.asList(MainActivity.dbHelper.readAllCityWeathers()));
        fillTheWeek();
        setMaxTemperatureView();
        setMinTemperatureView();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButtonCold:
                coldCitiesToogle = !coldCitiesToogle;
                warmCitiesToogle = false;
                if(coldCitiesToogle) {
                    for(int i = 0; i < 7; i++) {
                        if(mWeekDaysCityWeathers[i].getTemperature() < 10) {
                            mDani[i].setVisibility(View.VISIBLE);
                            mTemperature[i].setVisibility(View.VISIBLE);
                            mPritisci[i].setVisibility(View.VISIBLE);
                            mVlaznost[i].setVisibility(View.VISIBLE);
                        } else {
                            mDani[i].setVisibility(View.INVISIBLE);
                            mTemperature[i].setVisibility(View.INVISIBLE);
                            mPritisci[i].setVisibility(View.INVISIBLE);
                            mVlaznost[i].setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    for(int i = 0; i < 7; i++) {
                        mDani[i].setVisibility(View.VISIBLE);
                        mTemperature[i].setVisibility(View.VISIBLE);
                        mPritisci[i].setVisibility(View.VISIBLE);
                        mVlaznost[i].setVisibility(View.VISIBLE);
                    }
                }
                break;

            case R.id.imageButtonWarm:
                warmCitiesToogle = !warmCitiesToogle;
                coldCitiesToogle = false;
                if(warmCitiesToogle) {
                    for(int i = 0; i < 7; i++) {
                        if(mWeekDaysCityWeathers[i].getTemperature() > 10) {
                            mDani[i].setVisibility(View.VISIBLE);
                            mTemperature[i].setVisibility(View.VISIBLE);
                            mPritisci[i].setVisibility(View.VISIBLE);
                            mVlaznost[i].setVisibility(View.VISIBLE);
                        } else {
                            mDani[i].setVisibility(View.INVISIBLE);
                            mTemperature[i].setVisibility(View.INVISIBLE);
                            mPritisci[i].setVisibility(View.INVISIBLE);
                            mVlaznost[i].setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    for(int i = 0; i < 7; i++) {
                        mDani[i].setVisibility(View.VISIBLE);
                        mTemperature[i].setVisibility(View.VISIBLE);
                        mPritisci[i].setVisibility(View.VISIBLE);
                        mVlaznost[i].setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void setMaxTemperatureView() {
        double extreme = -200, newValue;
        int index = 0;
        for(int i = 0; i < 7; i++) {
            newValue = mWeekDaysCityWeathers[i].getTemperature();
            if(newValue > extreme) {
                extreme = newValue;
                index = i;
            }
        }
        String[] days = getResources().getStringArray(R.array.dan_srp_str_arr);
        mMaxTemperaturaDan.setText(days[index]);
        mMaxTemperaturaVrednost.setText(Double.toString(mWeekDaysCityWeathers[index].getTemperature()));
    }

    private void setMinTemperatureView() {
        double extreme = 200, newValue;
        int index = 0;
        for(int i = 0; i < 7; i++) {
            newValue = mWeekDaysCityWeathers[i].getTemperature();
            if(newValue < extreme) {
                extreme = newValue;
                index = i;
            }
        }
        String[] days = getResources().getStringArray(R.array.dan_srp_str_arr);
        mMinTemperaturaDan.setText(days[index]);
        mMinTemperaturaVrednost.setText(Double.toString(mWeekDaysCityWeathers[index].getTemperature()));
    }



    private void fillTheWeek() {
        mWeekDaysCityWeathers = new CityWeatherInfo.CityWeather[7];
        CityWeatherInfo.CityWeather pom;
        for(int i = 1; i <= 7; i++) {
            if((pom = MainActivity.dbHelper.readCityWeather(mCityNameString, i)) == null) {
                pom = CityWeatherInfo.weatherSamples[i - 1];
            }
            mWeekDaysCityWeathers[i - 1] = pom;
        }
        for(int i = 0; i < 7; i++) {
            fillADay(mTemperature[i], mPritisci[i], mVlaznost[i], mWeekDaysCityWeathers[i]);
        }
    }

    private void fillADay(TextView temperature, TextView pressure, TextView humidity, CityWeatherInfo.CityWeather cityWeather) {
        if(cityWeather != null) {
            temperature.setText(Double.toString(cityWeather.getTemperature()));
            pressure.setText(Double.toString(cityWeather.getPressure()));
            humidity.setText(Double.toString(cityWeather.getHumidity()));
        } else {
            temperature.setText("");
            pressure.setText("");
            humidity.setText("");
        }
    }
}
