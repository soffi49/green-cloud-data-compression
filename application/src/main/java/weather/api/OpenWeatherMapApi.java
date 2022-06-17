package weather.api;

import static java.lang.String.format;
import static mapper.JsonMapper.getMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.location.Location;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Properties;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weather.domain.OpenWeatherMap;

public class OpenWeatherMapApi {

    private static final Logger logger = LoggerFactory.getLogger(OpenWeatherMapApi.class);

    private static final String URL =
        "https://api.openweathermap.org/data/2.5/weather?lat=%s&lon=%s&appid=%s&units=metric";
    private static final ObjectMapper MAPPER = getMapper();

    private final String apiKey;
    private final OkHttpClient client;

    public OpenWeatherMapApi() {
        Properties properties = new Properties();
        URL res = getClass().getClassLoader().getResource("config.properties");

        try (FileInputStream fileInputStream = new FileInputStream(Paths.get(res.toURI()).toFile())) {
            properties.load(fileInputStream);
        } catch (FileNotFoundException fileNotFoundException) {
            logger.error("Could not find the properties file", fileNotFoundException);
        } catch (Exception exception) {
            logger.error("Could not load properties file {}", exception.toString());
        }

        this.apiKey = properties.getProperty("weather_api_key");
        this.client = new OkHttpClient();
    }

    public OpenWeatherMap getWeather(Location location) {
        var url = format(URL, location.getLatitude(), location.getLongitude(), apiKey);
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            return MAPPER.readValue(response.body().string(), OpenWeatherMap.class);
        } catch (IOException | NullPointerException e) {
            logger.error("Network error fetching weather", e);
        }

        return null;
    }
}
