package com.msh.utils;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TrafficModel;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;
import com.graphhopper.jsprit.core.util.Coordinate;
import org.joda.time.DateTime;
import org.joda.time.Duration;

public class GoogleAPIServices {

    private static GeoApiContext getContext(){
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyAzpf8SGnEQzv-dKOU2BGs7dAis-li6wMg")
                .build();

        return context;
    }

    public static Double getDistance(Coordinate from, Coordinate to) {
        double distance = 999999999;
      try {
          //DirectionsResult result =
          //        DirectionsApi.getDirections(getContext(), "Sydney, AU", "Melbourne, AU").await();
          //System.out.println(result.routes[0].legs.length);
          DirectionsResult result = DirectionsApi.newRequest(getContext())
                  .departureTime(new DateTime().plus(Duration.standardMinutes(2)))
                  .trafficModel(TrafficModel.BEST_GUESS)
                  .mode(TravelMode.DRIVING)
                  .units(Unit.METRIC)
                  .region("fr")
                  .origin(from.getX() + "," + from.getY())
                  .destination(to.getX() + "," + to.getY())
                  .await();
          System.out.println("Number of routes: " + result.routes.length );
          if(result.routes.length != 0) {
              System.out.println("Number of legs in route 1: " + result.routes[0].legs.length);
              if(result.routes[0].legs.length!=0) {
                  System.out.println(result.routes[0].legs[0].duration);
                  System.out.println(result.routes[0].legs[0].durationInTraffic);
                  System.out.println(result.routes[0].legs[0].distance);
                  System.out.println(result.routes[0].legs[0].distance.inMeters);
                  distance = result.routes[0].legs[0].distance.inMeters;
              }
          }
      }catch(Exception ex) {
          ex.printStackTrace();
      }
      return distance;
    }

    public static void main(String[] args){
        Coordinate x = new Coordinate(12,3);
        Coordinate y = new Coordinate(12,3);
        getDistance(x,y);

    }
}
