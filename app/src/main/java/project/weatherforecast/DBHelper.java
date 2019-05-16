package project.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "weather.dp";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "Weather";
    public static final String COLUMN_DATE_AND_TIME = "DateAndTime";
    public static final String COLUMN_CITY_NAME = "CityName";
    public static final String COLUMN_TEMPERATURE = "Temperature";
    public static final String COLUMN_PRESSURE = "Pressure";
    public static final String COLUMN_HUMIDITY = "Humidity";
    public static final String COLUMN_SUNRISE = "Sunrise";
    public static final String COLUMN_SUNSET = "Sunset";
    public static final String COLUMN_WINDSPEED = "WindSpeed";
    public static final String COLUMN_WINDDIRECTION = "WindDirection";
    public static final String COLUMN_WEATHER_ICON_STRING = "WeatherIcon";

    private SQLiteDatabase mSQLDB = null;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_CITY_NAME + " TEXT, " +
                COLUMN_DATE_AND_TIME + " INTEGER, " +
                COLUMN_TEMPERATURE + " REAL, " +
                COLUMN_PRESSURE + " REAL, " +
                COLUMN_HUMIDITY + " REAL, " +
                COLUMN_SUNRISE + " INTEGER, " +
                COLUMN_SUNSET + " INTEGER, " +
                COLUMN_WINDSPEED + " REAL, " +
                COLUMN_WINDDIRECTION + " INTEGER, " +
                COLUMN_WEATHER_ICON_STRING + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insert(CityWeatherInfo.CityWeather cityWeather) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY_NAME, cityWeather.getCityName());
        values.put(COLUMN_DATE_AND_TIME, cityWeather.getDateAndTime());
        values.put(COLUMN_TEMPERATURE, cityWeather.getTemperature());
        values.put(COLUMN_PRESSURE, cityWeather.getPressure());
        values.put(COLUMN_HUMIDITY, cityWeather.getHumidity());
        values.put(COLUMN_SUNRISE, cityWeather.getSunrise());
        values.put(COLUMN_SUNSET, cityWeather.getSunset());
        values.put(COLUMN_WINDSPEED, cityWeather.getWindSpeed());
        values.put(COLUMN_WINDDIRECTION, cityWeather.getWindDirection());
        values.put(COLUMN_WEATHER_ICON_STRING, cityWeather.getWeatherIconString());

        db.insert(TABLE_NAME, null, values);
        close();
    }

    public CityWeatherInfo.CityWeather readCityWeather(String cityNameKey) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null,
                COLUMN_CITY_NAME + "=?", new String[] {cityNameKey},
                null, null, null);
        cursor.moveToFirst();
        if(cursor.getCount() <= 0) {
            return null;
        }
        CityWeatherInfo.CityWeather cityWeather = createCityWeather(cursor);

        close();
        return cityWeather;
    }

    public CityWeatherInfo.CityWeather[] readAllCityWeathers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);

        if(cursor.getCount() <= 0) {
            return null;
        }

        CityWeatherInfo.CityWeather[] allCityWeathers = new CityWeatherInfo.CityWeather[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            allCityWeathers[i++] = createCityWeather(cursor);
        }
        close();
        return allCityWeathers;
    }

    public void deleteCityWeather(String cityName) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_CITY_NAME + "=?", new String[] {cityName});
        close();
    }

    private CityWeatherInfo.CityWeather createCityWeather(Cursor cursor) {
        String cityName = cursor.getString(cursor.getColumnIndex(COLUMN_CITY_NAME));
        long dateAndTime = cursor.getLong(cursor.getColumnIndex(COLUMN_DATE_AND_TIME));
        double temperature = cursor.getDouble(cursor.getColumnIndex(COLUMN_TEMPERATURE));
        double pressure = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRESSURE));
        double humidity = cursor.getDouble(cursor.getColumnIndex(COLUMN_HUMIDITY));
        long sunrise = cursor.getLong(cursor.getColumnIndex(COLUMN_SUNRISE));
        long sunset = cursor.getLong(cursor.getColumnIndex(COLUMN_SUNSET));
        double windSpeed = cursor.getDouble(cursor.getColumnIndex(COLUMN_WINDSPEED));
        int windDirection = cursor.getInt(cursor.getColumnIndex(COLUMN_WINDDIRECTION));
        String weatherIconString = cursor.getString(cursor.getColumnIndex(COLUMN_WEATHER_ICON_STRING));

        return new CityWeatherInfo.CityWeather(dateAndTime, cityName, temperature, pressure,
                humidity, sunrise, sunset, windSpeed, windDirection, weatherIconString);
    }

}
