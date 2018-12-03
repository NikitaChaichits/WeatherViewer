package com.deitel.weatherviewer;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

class Weather {
   public final String dayOfWeek;
   public final String minTemp;
   public final String maxTemp;
   public final String humidity;
   public final String description;
   public final String speed;
   public final String iconURL;

   public Weather(long timeStamp, double minTemp, double maxTemp,
                  double humidity, String description, double speed, String iconName) {
      NumberFormat numberFormat = NumberFormat.getInstance();
      numberFormat.setMaximumFractionDigits(0);

      this.dayOfWeek = convertTimeStampToDay(timeStamp);
      this.minTemp = numberFormat.format(minTemp) + "\u00B0C";
      this.maxTemp = numberFormat.format(maxTemp) + "\u00B0C";
      this.humidity = NumberFormat.getPercentInstance().format(humidity / 100.0);
      this.description = description;
      numberFormat.setMaximumFractionDigits(1);
      this.speed = numberFormat.format((speed));
      this.iconURL ="http://openweathermap.org/img/w/" + iconName + ".png";
   }

   private static String convertTimeStampToDay(long timeStamp) {
      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(timeStamp * 1000);
      SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE");
      return dateFormatter.format(calendar.getTime());
   }
}
