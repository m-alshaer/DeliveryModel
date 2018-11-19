package com.msh.utils;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.cost.AbstractForwardVehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.driver.Driver;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.util.Coordinate;
import com.graphhopper.jsprit.core.util.Locations;

public class MSHCosts extends AbstractForwardVehicleRoutingTransportCosts {
    public double speed = 2;
    private Locations locations;

    public MSHCosts(Locations locations) {
        super();
        this.locations = locations;
    }

    public MSHCosts() {

    }

    @Override
    public double getTransportTime(Location from, Location to, double time, Driver driver, Vehicle vehicle) {
        return calculateDistance(from, to) / speed;
    }

    @Override
    public double getTransportCost(Location from, Location to, double time, Driver driver, Vehicle vehicle) {
        double distance;
        try {
            distance = calculateDistance(from, to);
        } catch (NullPointerException e) {
            throw new NullPointerException("cannot calculate euclidean distance. coordinates are missing. either add coordinates or use another transport-cost-calculator.");
        }
        double costs = distance;
        if (vehicle != null) {
            if (vehicle.getType() != null) {
                costs = distance * vehicle.getType().getVehicleCostParams().perDistanceUnit;
            }
        }
        return costs;
    }

    private double calculateDistance(Location fromLocation, Location toLocation) {
        Coordinate from = null;
        Coordinate to = null;
        if (fromLocation.getCoordinate() != null & toLocation.getCoordinate() != null) {
            from = fromLocation.getCoordinate();
            to = toLocation.getCoordinate();
        } else if (locations != null) {
            from = locations.getCoord(fromLocation.getId());
            to = locations.getCoord(toLocation.getId());
        }
        if (from == null || to == null) throw new NullPointerException();
        return calculateDistance(from, to);
    }

    private double calculateDistance(Coordinate from, Coordinate to) {
        //return MapQuestAPI.getDistanceFromSingleRoute(from,to);//GoogleAPIServices.getDistance(from,to);
        MapQuestAPI api = new MapQuestAPI();
        return api.calculateDistanceFromMultipleRoutes(from, to);
       // System.out.println( Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY()) );
        //return Math.abs(from.getX() - to.getX()) + Math.abs(from.getY() - to.getY());
    }
}
