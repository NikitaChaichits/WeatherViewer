package com.deitel.weatherviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class WeatherArrayAdapter extends ArrayAdapter<Weather> {
   private static class ViewHolder {
      ImageView conditionIV;
      TextView dayTV, minTempTV, maxTempTV;
      TextView humidityTV, speedTV;
   }

   // stores already downloaded Bitmaps for reuse
   private Map<String, Bitmap> bitmaps = new HashMap<>();

   // constructor to initialize superclass inherited members
   public WeatherArrayAdapter(Context context, List<Weather> forecast) {
      super(context, -1, forecast);
   }

   // creates the custom views for the ListView's items
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      Weather day = getItem(position);
      ViewHolder viewHolder;

      if (convertView == null) {
         viewHolder = new ViewHolder();
         LayoutInflater inflater = LayoutInflater.from(getContext());
         convertView = inflater.inflate(R.layout.list_item, parent, false);
         viewHolder.conditionIV = (ImageView) convertView.findViewById(R.id.conditionIV);
         viewHolder.dayTV = (TextView) convertView.findViewById(R.id.dayTV);
         viewHolder.minTempTV = (TextView) convertView.findViewById(R.id.minTV);
         viewHolder.maxTempTV= (TextView) convertView.findViewById(R.id.maxTV);
         viewHolder.humidityTV = (TextView) convertView.findViewById(R.id.humidityTV);
         viewHolder.speedTV = (TextView) convertView.findViewById(R.id.speedTV);
         convertView.setTag(viewHolder);
      }
      else { // reuse existing ViewHolder stored as the list item's tag
         viewHolder = (ViewHolder) convertView.getTag();
      }

      // if weather condition icon already downloaded, use it;
      // otherwise, download icon in a separate thread
      if (bitmaps.containsKey(day.iconURL)) {
         viewHolder.conditionIV.setImageBitmap(
            bitmaps.get(day.iconURL));
      }
      else {
         new LoadImageTask(viewHolder.conditionIV).execute(
            day.iconURL);
      }

      Context context = getContext();
      viewHolder.dayTV.setText(context.getString(R.string.day_description, day.dayOfWeek, day.description));
      viewHolder.minTempTV.setText(context.getString(R.string.min_temp, day.minTemp));
      viewHolder.maxTempTV.setText(context.getString(R.string.max_temp, day.maxTemp));
      viewHolder.humidityTV.setText(context.getString(R.string.humidity, day.humidity));
      viewHolder.speedTV.setText(context.getString(R.string.speed, day.speed));
      return convertView; // return completed list item to display
   }

   // AsyncTask to load weather condition icons in a separate thread
   private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
      private ImageView imageView; // displays the thumbnail

      // store ImageView on which to set the downloaded Bitmap
      public LoadImageTask(ImageView imageView) {
         this.imageView = imageView;
      }

      // load image; params[0] is the String URL representing the image
      @Override
      protected Bitmap doInBackground(String... params) {
         Bitmap bitmap = null;
         HttpURLConnection connection = null;

         try {
            URL url = new URL(params[0]); // create URL for image

            // open an HttpURLConnection, get its InputStream
            // and download the image
            connection = (HttpURLConnection) url.openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
               bitmap = BitmapFactory.decodeStream(inputStream);
               bitmaps.put(params[0], bitmap); // cache for later use
            }
            catch (Exception e) {
               e.printStackTrace();
            }
         }
         catch (Exception e) {
            e.printStackTrace();
         }
         finally {
            connection.disconnect(); // close the HttpURLConnection
         }

         return bitmap;
      }

      // set weather condition image in list item
      @Override
      protected void onPostExecute(Bitmap bitmap) {
         imageView.setImageBitmap(bitmap);
      }
   }
}

