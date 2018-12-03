package com.deitel.weatherviewer;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
   private List<Weather> weatherList = new ArrayList<>();
   private WeatherArrayAdapter weatherAA;
   private ListView weatherLV;
   Spinner spinner;
   String city;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);

      spinner = (Spinner)findViewById(R.id.spinner);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ENcity, R.layout.spinner);
      adapter.setDropDownViewResource(R.layout.spinner);
      spinner.setAdapter(adapter);
      spinner.setOnItemSelectedListener(this);

      weatherLV = (ListView) findViewById(R.id.weatherLV);
      weatherAA = new WeatherArrayAdapter(this, weatherList);
      weatherLV.setAdapter(weatherAA);
      FloatingActionButton btn = (FloatingActionButton) findViewById(R.id.btn);

      btn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
//            EditText locationET = (EditText) findViewById(R.id.locationET);
            URL url = createURL(city);

            if (url != null) {
//               dismissKeyboard(locationET);
               GetWeatherTask getLocalWeatherTask = new GetWeatherTask();
               getLocalWeatherTask.execute(url);
            }
            else {
               Snackbar.make(findViewById(R.id.coordinatorLayout),
                  R.string.invalid_url, Snackbar.LENGTH_LONG).show();
            }
         }
      });
   }

   private void dismissKeyboard(View view) {
      InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
   }

   private URL createURL(String city) {
      String apiKey = getString(R.string.api_key);
      String baseUrl = getString(R.string.web_service_url);

      try {
         String urlString = baseUrl + URLEncoder.encode(city, "UTF-8") + "&units=metric&cnt=7&APPID=" + apiKey;
         return new URL(urlString);
      }
      catch (Exception e) {
         e.printStackTrace();
      }
      return null;
   }

   @Override
   public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
      switch(spinner.getSelectedItem().toString()){
         case "Minsk":
            city = "Minsk, Belarus";
            break;
         case "Brest":
            city = "Brest, Belarus";
            break;
         case "Vitebsk":
            city = "Vitebsk, Belarus";
            break;
         case "Gomel":
            city = "Gomel, Belarus";
            break;
         case "Grodno":
            city = "Grodno, Belarus";
            break;
         case "Mogilev":
            city = "Mogilev, Belarus";
            break;
      }
   }

   @Override
   public void onNothingSelected(AdapterView<?> parent) {

   }

   private class GetWeatherTask
      extends AsyncTask<URL, Void, JSONObject> {

      @Override
      protected JSONObject doInBackground(URL... params) {
         HttpURLConnection connection = null;

         try {
            connection = (HttpURLConnection) params[0].openConnection();
            int response = connection.getResponseCode();

            if (response == HttpURLConnection.HTTP_OK) {
               StringBuilder builder = new StringBuilder();
               try (BufferedReader reader = new BufferedReader(
                  new InputStreamReader(connection.getInputStream()))) {
                  String line;
                  while ((line = reader.readLine()) != null) {
                     builder.append(line);
                  }
               }catch (IOException e) {
                  e.printStackTrace();
               }
               return new JSONObject(builder.toString());
            }
         }catch (Exception e) {
            e.printStackTrace();
         }
         finally {
            connection.disconnect();
         }
         return null;
      }

      @Override
      protected void onPostExecute(JSONObject weather) {
         convertJSONtoArrayList(weather);
         weatherAA.notifyDataSetChanged();
         weatherLV.smoothScrollToPosition(0);
      }
   }

   private void convertJSONtoArrayList(JSONObject forecast) {
      weatherList.clear();
      try {
         JSONArray list = forecast.getJSONArray("list");

         for (int i = 0; i < list.length(); ++i) {
            JSONObject day = list.getJSONObject(i);
            JSONObject temperatures = day.getJSONObject("temp");
            JSONObject weather = day.getJSONArray("weather").getJSONObject(0);

            weatherList.add(new Weather(
               day.getLong("dt"),
               temperatures.getDouble("min"),
               temperatures.getDouble("max"),
               day.getDouble("humidity"),
               weather.getString("description"),
               day.getDouble("speed"),
               weather.getString("icon")));
         }
      }
      catch (JSONException e) {
         e.printStackTrace();
      }
   }
}
