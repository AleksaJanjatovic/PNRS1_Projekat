package project.weatherforecast;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


final class CityWeatherInfo {

    private CityWeatherInfo() {

    }

    public enum TemperatureUnit {
        CELSIUS, FAHRENHEIT
    }

    final static long time = Calendar.getInstance().getTime().getTime();
    public static final CityWeather[] weatherSamples = {
            new CityWeather(time, 1, "Sombor", 20, 998, 50, time, time + 36000000, 30, 20, "01d"),
            new CityWeather(time, 2, "Sombor", 23, 1023, 70, time, time + 36000000, 50, 10, "02d"),
            new CityWeather(time, 3, "Sombor", 7, 1020, 60, time, time + 36000000, 60, 50, "03d"),
            new CityWeather(time, 4, "Sombor", 14, 1010, 70, time, time + 36000000, 70, 70, "04d"),
            new CityWeather(time, 5, "Sombor", 5, 995, 80, time, time + 36000000, 20, 80, "09d"),
            new CityWeather(time, 6, "Sombor", 17, 987, 70, time, time + 36000000, 5, 90, "010d"),
            new CityWeather(time, 7, "Sombor", 26, 1055, 60, time, time + 36000000, 10, 120, "011d")
    };

    private static final WindDirection[] windDirections = {
            new WindDirection("SEVERNI", 348.75, 11.25),
            new WindDirection("SEVERO-SEVEROISTOČNI", 11.25, 33.75),
            new WindDirection("SEVEROISTOČNI", 33.75, 56.25),
            new WindDirection("ISTOČNI-SEVEROISTOČNI", 56.25, 78.75),
            new WindDirection("ISTOČNI", 78.75, 101.25),
            new WindDirection("ISTOČNI-JUGOISTOČNI", 101.25, 123.75),
            new WindDirection("JUGOISTOČNI", 123.75, 146.25),
            new WindDirection("JUGO-JUGOISTOČNI", 146.25, 168.75),
            new WindDirection("JUŽNI", 168.75, 191.25),
            new WindDirection("JUGO-JUGOZAPADNI", 191.25, 213.75),
            new WindDirection("JUGOZAPADNI", 213.75, 236.25),
            new WindDirection("ZAPADNI-JUGOZAPADNI", 236.25, 258.75),
            new WindDirection("ZAPADNI", 258.75, 281.25),
            new WindDirection("ZAPADNI-SEVEROZAPADNI", 281.25, 303.75),
            new WindDirection("SEVEROZAPADNI", 303.75, 326.25),
            new WindDirection("SEVERNI-SEVEROZAPADNI", 326.25, 348.75),
    };

    public static class CityWeather {
        long mDateAndTime;
        int mDay;
        String mCityName;
        double mTemperature;
        double mPressure;
        double mHumidity;
        long mSunrise;
        long mSunset;
        double mWindSpeed;
        double mWindDirection;
        String mWeatherIconString;

        CityWeather(long mDateAndTime,
                    int day,
                    String mCityName,
                    double mTemperature,
                    double mPressure,
                    double mHumidity,
                    long mSunrise,
                    long mSunset,
                    double mWindSpeed,
                    double mWindDirection, String mWeatherIcon) {
            this.mDateAndTime = mDateAndTime;
            this.mDay = day;
            this.mCityName = mCityName;
            this.mTemperature = mTemperature;
            this.mPressure = mPressure;
            this.mHumidity = mHumidity;
            this.mSunrise = mSunrise;
            this.mSunset = mSunset;
            this.mWindSpeed = mWindSpeed;
            this.mWindDirection = mWindDirection;
            this.mWeatherIconString = mWeatherIcon;
        }

        int getDay() { return mDay; }

        long getDateAndTime() {
            return mDateAndTime;
        }

        String getCityName() {
            return mCityName;
        }

        double getTemperature() {
            return mTemperature;
        }

        double getPressure() {
            return mPressure;
        }

        double getHumidity() {
            return mHumidity;
        }

        long getSunrise() {
            return mSunrise;
        }

        long getSunset() {
            return mSunset;
        }

        double getWindSpeed() {
            return mWindSpeed;
        }

        double getWindDirection() {
            return mWindDirection;
        }

        String getWeatherIconString() {
            return mWeatherIconString;
        }

        public void setDateAndTime(long mDateAndTime) {
            this.mDateAndTime = mDateAndTime;
        }

        public void setCityName(String mCityName) {
            this.mCityName = mCityName;
        }

        public void setTemperature(double mTemperature) {
            this.mTemperature = mTemperature;
        }

        public void setPressure(double mPressure) {
            this.mPressure = mPressure;
        }

        public void setHumidity(double mHumidity) {
            this.mHumidity = mHumidity;
        }

        public void setSunrise(long mSunrise) {
            this.mSunrise = mSunrise;
        }

        public void setSunset(long mSunset) {
            this.mSunset = mSunset;
        }

        public void setWindSpeed(double mWindSpeed) {
            this.mWindSpeed = mWindSpeed;
        }

        public void setWindDirection(double mWindDirection) {
            this.mWindDirection = mWindDirection;
        }

        public void setWeatherIconString(String mWeatherIcon) {
            this.mWeatherIconString = mWeatherIcon;
        }
    }

    public static class WeatherFormater {

        public static  String formatWindDirection(double windDirection) {

            if(windDirection > windDirections[0].lowerBound || windDirection < windDirections[0].upperBound) {
                return windDirections[0].windDirectionString;
            }

            for(int i = 1; i < windDirections.length; i++)  {
                if(windDirection >= windDirections[i].lowerBound && windDirection <= windDirections[i].upperBound) {
                    return windDirections[i].windDirectionString;
                }
            }

            return null;
        }

        static String formatWindSpeed(double windSpeed) {
            return windSpeed + " m/s";
        }

        static  String formatTemperature(double temperature, String unit) {
            Log.d("PASSED TEMPERATURE",  Double.toString(temperature));
            temperature = Math.round(temperature*100)/100.0;
            return temperature + " " + unit + "°";
        }

        static String formatUTCTime(long time) {
            Date date = new Date(time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
            return simpleDateFormat.format(date);
        }

        static int extractDayFromUTCTime(long time) {
            Date date = new Date(time);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("u");
            return Integer.parseInt(simpleDateFormat.format(date));
        }

        static String formatPressure(double pressure) { return pressure + " hPa"; }

        static String formatHumidity(double humidity) {
            return humidity + " %";
        }

        static String formatWeatherPNG(String png) { return "w" + png; }

        static String formatCityNameForURL(String cityName) {
            return cityName.replaceAll(" ", "%20");
        }

    }

    private static class WindDirection {
        String windDirectionString;
        double lowerBound, upperBound;

        private WindDirection(String windDirectionString, double lowerbound, double upperBound){
            this.lowerBound = lowerbound;
            this.upperBound = upperBound;
            this.windDirectionString = windDirectionString;
        }
    }
}
