package project.weatherforecast;

public class CustomJNI {

    static {
        System.loadLibrary("CustomJNI");
    }

    static public native double convert_temperature(double temperature, int unit);

}
