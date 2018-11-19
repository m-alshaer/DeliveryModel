package com.msh.utils;


import com.graphhopper.jsprit.core.util.Coordinate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class MapQuestAPI {

    private List<Route> routes = new ArrayList<Route>();
    public static List<Coordinate> lstHardPoints = new ArrayList<>();
    public static List<Coordinate> lstSoftPoints = new ArrayList<>();

    public static Double getDistanceFromSingleRoute(Coordinate from, Coordinate to) {
        double distance = 999999999;
        try {
            //String path2="http://www.mapquestapi.com/directions/v2/route?key=&from=48.868265,2.297473&to=48.868265,2.297473";
            String path = "http://www.mapquestapi.com/directions/v2/route?key&from=";
            String btw = "&to=";
            String full_path = path + from.getY() + "," + from.getX() + btw + to.getY() + "," + to.getX();
            URL url = new URL(full_path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            String object = "";
            System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                object += output;
                //System.out.println(output);
            }
            JSONObject result = new JSONObject(object);
            System.out.println(result);
            JSONObject route = (JSONObject) result.get("route");

            distance = (double) route.getDouble("distance");
            System.out.println(distance);
            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return distance;
    }

    public void fillMultipleRoutes(Coordinate from, Coordinate to) {
        double distance = 999999999;
        try {
            //String path2="http://www.mapquestapi.com/directions/v2/route?key=&from=48.868265,2.297473&to=48.868265,2.297473";
            String path = "http://www.mapquestapi.com/directions/v2/alternateroutes?key=&from=";
            String btw = "&to=";
            String full_path = path + from.getY() + "," + from.getX() + btw + to.getY() + "," + to.getX() + "&maxRoutes=3";
            URL url = new URL(full_path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            String object = "";
            //System.out.println("Output from Server .... \n");

            while ((output = br.readLine()) != null) {
                object += output;
                //System.out.println(output);
            }
            Route fullroute = new Route();
            JSONObject result = new JSONObject(object);
           // System.out.println(result);
            JSONObject route = (JSONObject) result.get("route");
            fullroute.distance = (double) route.getDouble("distance");
            if(route == null){
                conn.disconnect();
                return;
            }
            // Check Bounding Box Elements
            BoudingBox bb = new BoudingBox();
            JSONObject boudingBox = route.getJSONObject("boundingBox");
            JSONObject lr = boudingBox.getJSONObject("lr");
            double lr_lng = lr.getDouble("lng");
            double lr_lat = lr.getDouble("lat");
            JSONObject ul = boudingBox.getJSONObject("ul");
            double ul_lng = ul.getDouble("lng");
            double ul_lat = ul.getDouble("lat");
            Coordinate lr_point = new Coordinate(lr_lng, lr_lat);
            Coordinate ul_point = new Coordinate(ul_lng, ul_lat);
            bb.lowerRight = lr_point;
            bb.upperLeft = ul_point;
            fullroute.boudingBox = bb;

            //Check the coordinates on the route
            JSONObject shape = route.getJSONObject("shape");
            JSONArray shapePoints = shape.getJSONArray("shapePoints");
            ArrayList<Coordinate> lstPointsRoute = new ArrayList<>();
            for (int j = 0; j < shapePoints.length() - 1; j = j + 2) {
                if (j % 2 == 0) {
                    double y = shapePoints.getDouble(j);
                    double x = shapePoints.getDouble(j + 1);
                    Coordinate shapePointRoute = new Coordinate(x, y);
                    lstPointsRoute.add(shapePointRoute);
                }
            }
            fullroute.lstPoints = lstPointsRoute;
            routes.add(fullroute);


            JSONArray alternativesRoutes = (JSONArray) route.get("alternateRoutes");
            for (int i = 0; i < alternativesRoutes.length(); i++) {
                fullroute = new Route();
                JSONObject altroute = alternativesRoutes.getJSONObject(i);
                JSONObject routeObj = altroute.getJSONObject("route");
                fullroute.distance = (double) routeObj.getDouble("distance");

                // Check Bounding Box Elements
                bb = new BoudingBox();
                boudingBox = routeObj.getJSONObject("boundingBox");
                lr = boudingBox.getJSONObject("lr");
                lr_lng = lr.getDouble("lng");
                lr_lat = lr.getDouble("lat");
                ul = boudingBox.getJSONObject("ul");
                ul_lng = ul.getDouble("lng");
                ul_lat = ul.getDouble("lat");
                lr_point = new Coordinate(lr_lng, lr_lat);
                ul_point = new Coordinate(ul_lng, ul_lat);
                bb.lowerRight = lr_point;
                bb.upperLeft = ul_point;
                fullroute.boudingBox = bb;

                        //Check the coordinates on the route
                        shape = routeObj.getJSONObject("shape");
                        shapePoints = shape.getJSONArray("shapePoints");
                        lstPointsRoute = new ArrayList<>();
                        for (int j = 0; j < shapePoints.length() - 1; j = j + 2) {
                            if (j % 2 == 0) {
                                double y = shapePoints.getDouble(j);
                        double x = shapePoints.getDouble(j + 1);
                        Coordinate shapePointRoute = new Coordinate(x, y);
                        lstPointsRoute.add(shapePointRoute);
                    }
                }
                fullroute.lstPoints = lstPointsRoute;
                routes.add(fullroute);
                /*for (Coordinate x : lstPointsRoute) {
                    System.out.println("X: " + x.getX() + " & Y: " + x.getY());
                }*/
                //Coordinate p = new Coordinate( 4.770646, 45.778074 );
                //Coordinate p = new Coordinate(2.235421,48.901456 );
                //boolean test = checkInTheBoudingBox(p,lr_point,ul_point);
                //System.out.println(test);

            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        } catch(Exception e){
        //    e.printStackTrace();
        }
    }

    public  boolean checkInTheBoudingBox(Coordinate p, Coordinate lr, Coordinate ul) {
        if (ul.getX() <= p.getX() && p.getX() <= lr.getX() && lr.getY() <= p.getY() && p.getY() <= ul.getY()) {
            // Point is in bounding box
            return true;
        }
        return false;
    }

    public  void printRoutes() {
        for (Route r : routes) {
            System.out.println(r.distance);
            System.out.println(r.boudingBox.toString());
            System.out.println(r.lstPoints.toString());
        }
    }

    public Route selectBestRouteWithConstraints() {
        List<Coordinate> lstHardPointsAffects = new ArrayList<>();
        List<Coordinate> lstSoftPointsAffects = new ArrayList<>();
        //check if the bouding box to fill the only affected points
        boolean inBoudingBox = false;
        for (Route r : routes) {
            for (Coordinate p : lstHardPoints) {
                inBoudingBox = checkInTheBoudingBox(p, r.boudingBox.lowerRight, r.boudingBox.upperLeft);
                if (inBoudingBox) {
                    lstHardPointsAffects.add(p);
                }
            }
            for (Coordinate p : lstSoftPoints) {
                inBoudingBox = checkInTheBoudingBox(p, r.boudingBox.lowerRight, r.boudingBox.upperLeft);
                if (inBoudingBox) {
                    lstSoftPointsAffects.add(p);
                }
            }
        }
        //check for first working route that is not affected by hard constraints and remove the affected ones from routes
        Route selectedRoute = null;
        boolean firstRoute = false;
        List<Route> lstRoutes_not_selected = new ArrayList<>();
        for (Route r : routes) {
            boolean infectedHardRoute = false;
            for (Coordinate p : lstHardPointsAffects) {
                if (r.lstPoints.contains(p)) {
                    infectedHardRoute = true;
                    lstRoutes_not_selected.add(r);
                    break;
                }
            }
            if (!infectedHardRoute && !firstRoute) {
                selectedRoute = r;
                firstRoute = true;
            }
        }

        if (selectedRoute == null)
            return null;
        //test if selected route completely safe from soft constraints side
        boolean notsuitable = false;
        for (Coordinate p : lstSoftPointsAffects) {
            if (selectedRoute.lstPoints.contains(p)) {
                notsuitable = true;
                break;
            }
        }

        Route selected_final_route = selectedRoute;

        //try to find another route
        routes.removeAll(lstRoutes_not_selected);
        Map<Route, Integer> mapCounts = new HashMap<Route, Integer>();
        if (notsuitable) {
            for (Route r : routes) {
                mapCounts.put(r, Integer.parseInt("0"));
                for (Coordinate p : lstSoftPointsAffects) {
                    if (r.lstPoints.contains(p)) {
                        Integer c = mapCounts.get(r);
                        c = c + 1;
                        mapCounts.put(r, c);
                    }
                }

            }

            Iterator it = mapCounts.entrySet().iterator();
            int maximum = -1;

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                System.out.println(pair.getKey() + " = " + pair.getValue());
                if (Integer.parseInt(pair.getValue().toString()) > maximum) {
                    maximum = Integer.parseInt(pair.getValue().toString());
                    selected_final_route = (Route) pair.getKey();
                }
                it.remove(); // avoids a ConcurrentModificationException
            }
        }
        return selected_final_route;

    }


        /*if(!inBoudingBox){
            System.out.println("Events are out of the scope of the routes");
        }*/

    public static double getDistanceFromMultipleRoutes(Route selectedRoute) {
        if(selectedRoute == null) {
            return 9999999;
        }
        return selectedRoute.distance;
    }

    public double calculateDistanceFromMultipleRoutes(Coordinate from, Coordinate to){
        lstHardPoints.add(new Coordinate(2.250254,48.8881));
        //lstSoftPoints.add(new Coordinate(2.242546,48.878101));
        fillMultipleRoutes(from, to);
        printRoutes();
        Route selectedRoute = selectBestRouteWithConstraints();
        return getDistanceFromMultipleRoutes(selectedRoute);
       // System.out.println(getDistanceFromMultipleRoutes(selectedRoute));
    }

    public static void main(String[] args) {

        Coordinate x = new Coordinate(2.237110, 48.875790);
        Coordinate y = new Coordinate(2.210501, 48.939311);
        //getDistanceFromSingleRoute(x,y);
       // lstHardPoints.add(new Coordinate(2.250254,48.8881));
        //lstSoftPoints.add(new Coordinate(2.242546,48.878101));
       // fillMultipleRoutes(x, y);
        //printRoutes();
       // Route selectedRoute = selectBestRouteWithConstraints();
       // System.out.println(getDistanceFromMultipleRoutes(selectedRoute));
    }
}
