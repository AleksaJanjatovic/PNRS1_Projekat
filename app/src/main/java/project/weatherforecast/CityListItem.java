package project.weatherforecast;

public class CityListItem {
    private boolean mRadioCity = false;
    private String mTextCity;

    public CityListItem(String s) {
        mTextCity = s;
    }

    public CityListItem(CityWeatherInfo.CityWeather cityWeather) {
        mTextCity = cityWeather.getCityName();
    }

    String getTextCity() {return mTextCity;}
    boolean getRadioCity() {return mRadioCity;}

    public void setRadioCity(boolean m_radioCity) {
        this.mRadioCity = m_radioCity;
    }

    public void setTextCity(String m_textCity) {
        this.mTextCity = m_textCity;
    }
}
