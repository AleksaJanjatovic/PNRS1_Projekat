package project.weatherforecast;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;


public final class CityWeatherInfo {

    private CityWeatherInfo() {

    }

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
        String mCityName;
        double mTemperature;
        double mPressure;
        double mHumidity;
        long mSunrise;
        long mSunset;
        double mWindSpeed;
        double mWindDirection;
        String mWeatherIconString;

        public CityWeather(long mDateAndTime,
                            String mCityName,
                            double mTemperature,
                            double mPressure,
                            double mHumidity,
                            long mSunrise,
                            long mSunset,
                            double mWindSpeed,
                            double mWindDirection, String mWeatherIcon) {
            this.mDateAndTime = mDateAndTime;
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

        long getDateAndTime() {
            return mDateAndTime;
        }

        String getCityName() {
            return mCityName;
        }

        public double getTemperature() {
            return mTemperature;
        }

        public double getPressure() {
            return mPressure;
        }

        public double getHumidity() {
            return mHumidity;
        }

        public long getSunrise() {
            return mSunrise;
        }

        public long getSunset() {
            return mSunset;
        }

        public double getWindSpeed() {
            return mWindSpeed;
        }

        public double getWindDirection() {
            return mWindDirection;
        }

        public String getWeatherIconString() {
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

        public static String formatWindSpeed(double windSpeed) {
            return windSpeed + " m/s";
        }

        public static  String formatTemperature(double temperature, String unit, boolean convert) {
            Log.d("PASSED TEMPERATURE",  Double.toString(temperature));
            if(convert) {
                if(unit.equals("F")) {
                    temperature = temperature*9/5 + 32;
                } else if (unit.equals("C")) {
                    temperature = (temperature - 32) * 5/9;
                }
            }
            temperature = Math.round(temperature*100)/100;
            return Double.toString(temperature) + " " + unit + "°";
        }

        public static String formatUTCTime(long time) {
            Date date = new Date(time);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss");
            return simpleDateFormat.format(date);
        }

        public static String formatPressure(double pressure) { return pressure + " hPa"; }

        public static String formatHumidity(double humidity) {
            return humidity + " %";
        }

        public static String formatWeatherPNG(String png) { return "w" + png; }

        public static String formatCityNameForURL(String cityName) {
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
