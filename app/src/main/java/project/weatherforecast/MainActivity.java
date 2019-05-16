package project.weatherforecast;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button mButtonPrikazi;
    EditText mEditDodajGrad;
    ListView mListGradovi;
    TextView mTextWeatherForecast;
    static public TextView mTextEmpty;
    static public DBHelper dbHelper;
    ItemAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonPrikazi= findViewById(R.id.buttonDodajGrad);
        mEditDodajGrad = findViewById(R.id.textEditGradIliLokacija);
        mListGradovi = findViewById(R.id.cityList);
        mTextWeatherForecast = findViewById(R.id.textWeatherForecast);
        mTextEmpty = findViewById(R.id.textViewEmpty);

        mEditDodajGrad.setOnClickListener(this);
        mButtonPrikazi.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        mListAdapter = new ItemAdapter(this);
        mListGradovi.setAdapter(mListAdapter);

        CityWeatherInfo.CityWeather[] mCityWeathers = dbHelper.readAllCityWeathers();
        try {
            for(int i = 0; i < mCityWeathers.length; i++) {
                mListAdapter.addCity(new CityListItem(mCityWeathers [i]));
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            mTextWeatherForecast.setVisibility(View.VISIBLE);
            mListGradovi.setVisibility(View.VISIBLE);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonDodajGrad:
                CityListItem city = new CityListItem(mEditDodajGrad.getText().toString());
                mListAdapter.addCity(city);
                mTextWeatherForecast.setVisibility(View.VISIBLE);
                mListGradovi.setVisibility(View.VISIBLE);
                mTextEmpty.setVisibility(View.GONE);
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                break;
            case R.id.textEditGradIliLokacija:
                mTextWeatherForecast.setVisibility(View.GONE);
                mListGradovi.setVisibility(View.GONE);
                mTextEmpty.setVisibility(View.VISIBLE);
                break;
            default:
        }
    }

}
