package project.weatherforecast;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

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
            mCityName.setText(getIntent().getExtras().getString(getResources().getString(R.string.lokacija_str)));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        mDani[Integer.parseInt(getIntent().getExtras().getString(getResources().getString(R.string.dan_str))) - 1].setTypeface(null, Typeface.BOLD);

        mCityWeathers = new ArrayList<>(Arrays.asList(MainActivity.dbHelper.readAllCityWeathers()));
        mCityWeathersCopy = new ArrayList<>(mCityWeathers);
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

    private CityWeatherInfo.CityWeather popRandomCity() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(mCityWeathersCopy.size());
        return mCityWeathersCopy.remove(randomNumber);
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
        if(mCityWeathersCopy.size() >= 7) {
            mWeekDaysCityWeathers = new CityWeatherInfo.CityWeather[] {
                    popRandomCity(),
                    popRandomCity(),
                    popRandomCity(),
                    popRandomCity(),
                    popRandomCity(),
                    popRandomCity(),
                    popRandomCity()
            };
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
