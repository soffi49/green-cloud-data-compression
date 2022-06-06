package agents.greenenergy;

import domain.GreenSourceData;
import jade.core.behaviours.DataStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class WeatherWaiter {

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public Future<GreenSourceData> getWeather(DataStore dataStore, String jobId) {
        return executor.submit(() -> {
            if (!dataStore.containsKey(jobId)) {
                Thread.sleep(100);
            }
            var data = (GreenSourceData) dataStore.get(jobId);
            dataStore.remove(jobId);
            return data;
        });
    }
}
