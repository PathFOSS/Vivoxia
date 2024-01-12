package com.pathfoss.vivoxia.general;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pathfoss.vivoxia.R;
import com.pathfoss.vivoxia.nutrition.Food;
import com.pathfoss.vivoxia.nutrition.FoodDataBase;
import com.pathfoss.vivoxia.nutrition.FoodImport;
import com.pathfoss.vivoxia.nutrition.FoodImportNutrients;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.net.ssl.HttpsURLConnection;

public class DownloadService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(@NonNull Intent intent, int flags, int startId) {

        // Initialize variables
        String[] files = intent.getStringArrayExtra("files");
        String baseURL = intent.getStringExtra("baseURL");
        Context context = this;

        // Set up notification
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        assert files != null;
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, NotificationBuilder.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle("Downloading foods")
                .setContentText("0%")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setProgress(files.length, 0, false);

        // Create background thread to download data. NB! Currently only used for food data!
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Thread thread = new Thread() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "Please enable notifications to see download progress", Toast.LENGTH_SHORT).show();
                } else {
                    startForeground(1, notification.build());
                    for (int i = 0; i < files.length; i++) {
                        Future<Integer> integerFuture;
                        try {
                            integerFuture = executorService.submit(new DownloadJSON(baseURL + files[i]));
                            notification.setProgress(files.length, i + integerFuture.get(), false);
                            int percent = (int) (i + 1) * 100 / files.length;
                            notification.setContentText(percent + "%");
                            notificationManagerCompat.notify(1, notification.build());
                        } catch (MalformedURLException | ExecutionException | InterruptedException e) {
                            notification.setContentText("Error");
                            throw new RuntimeException(e);
                        }
                    }
                    notification.setProgress(0, 0, false);
                    notification.setContentText("Offline migration completed");
                    notificationManagerCompat.notify(1, notification.build());
                }
            }
        };
        thread.start();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Create class to process data in the background with callables
    private static class DownloadJSON implements Callable<Integer> {

        private final FoodDataBase foodDataBase = Controller.getFoodDataBase();
        private final URL fullURL;

        // Pass the full url for download
        private DownloadJSON (String fullURL) throws MalformedURLException {
            this.fullURL = new URL(fullURL);
        }

        @NonNull
        @Override
        public Integer call() {
            // Connect to GitHub via HTTPS, store data in a variable, and enter into database one by one
            try {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) fullURL.openConnection();
                InputStream inputStream = httpsURLConnection.getInputStream();

                ObjectMapper objectMapper = new ObjectMapper();
                FoodImport[] foodImport = objectMapper.readValue(inputStream, FoodImport[].class);
                try {
                    for (FoodImport f : foodImport) {
                        FoodImportNutrients foodImportNutrients = f.getNutrients();
                        String name = f.getName();
                        Food food = new Food(name, 100, "g", foodImportNutrients.getCalories(), foodImportNutrients.getProtein(), foodImportNutrients.getFat(), foodImportNutrients.getCarbs(), 0, 0);
                        foodDataBase.addEntry(food);
                    }

                } catch (Exception ignored){}
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
            return 1;
        }
    }
}