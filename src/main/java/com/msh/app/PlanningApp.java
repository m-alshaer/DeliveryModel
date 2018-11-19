package com.msh.app;


import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.analysis.SolutionAnalyser;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.cost.TransportDistance;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.ManhattanCosts;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.algorithm.AlgorithmConfig;
import com.graphhopper.jsprit.io.algorithm.VehicleRoutingAlgorithms;
import com.msh.utils.MSHCosts;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.apache.commons.configuration.XMLConfiguration;
import java.util.Collection;

/**
 * Hello world!
 *
 */
public class PlanningApp
{

    public static void plan()
    {
        /*
         * get a vehicle type-builder and build a type with the typeId "vehicleType" and one capacity dimension, i.e. weight, and capacity dimension value of 2
         */
        final int WEIGHT_INDEX = 0;
        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
                .addCapacityDimension(WEIGHT_INDEX, 10).setCostPerWaitingTime(1.);
        VehicleType vehicleType = vehicleTypeBuilder.build();

        /*
         * get a vehicle-builder and build a vehicle located at (10,10) with type "vehicleType"
         */
        Builder vehicleBuilder = Builder.newInstance("vehicle");
        vehicleBuilder.setStartLocation(Location.newInstance(2.297473, 48.868265));
        vehicleBuilder.setType(vehicleType);
        VehicleImpl vehicle = vehicleBuilder.build();
        /*
         * build services at the required locations, each with a capacity-demand of 1.
         */
        Service service1 = Service.Builder.newInstance("1")
                .addTimeWindow(50,100)
                .addTimeWindow(20,35)
                .addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(2.302083, 48.865388)).build();

        Service service2 = Service.Builder.newInstance("2")
                .addSizeDimension(WEIGHT_INDEX, 1)
//            .setServiceTime(10)
                .setLocation(Location.newInstance(48.865048, 2.283415)).setServiceTime(10).build();

        Service service3 = Service.Builder.newInstance("3")
                .addTimeWindow(5, 10)
                .addTimeWindow(35, 50)
                .addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(2.237110,48.875790 )).build();

        Service service4 = Service.Builder.newInstance("4")
//            .addTimeWindow(5,10)
                .addTimeWindow(20, 40)
                .addTimeWindow(45, 80)
                .addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(2.210501, 48.939311 )).build();

        Service service5 = Service.Builder.newInstance("5")
                .addTimeWindow(5,10)
                .addTimeWindow(20, 40)
                .addTimeWindow(60,100)
                .addSizeDimension(WEIGHT_INDEX, 1).setLocation(Location.newInstance(2.200695, 48.933277 )).build();

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
        vrpBuilder.addVehicle(vehicle);
        vrpBuilder.addJob(service1).addJob(service2)
                .addJob(service3)
                .addJob(service4)
                .addJob(service5)
        ;
        vrpBuilder.setFleetSize(VehicleRoutingProblem.FleetSize.INFINITE); // to be tested with infinite fleet size!!
        vrpBuilder.setRoutingCost(new MSHCosts());

        VehicleRoutingProblem problem = vrpBuilder.build();

        /*
         * get the algorithm out-of-the-box.
         */
        AlgorithmConfig algorithmConfig = getAlgorithmConfig();
        VehicleRoutingAlgorithm algorithm = VehicleRoutingAlgorithms.createAlgorithm(problem, algorithmConfig);

        /*
         * and search a solution
         */
        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        /*
         * get the best
         */
        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

//        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);


        /*
         * plot
         */
        //new Plotter(problem,bestSolution).setLabel(Plotter.Label.ID).plot("output/plot", "mtw");

        //SolutionAnalyser a = new SolutionAnalyser(problem, bestSolution, (TransportDistance) problem.getTransportCosts());

       // System.out.println("distance: " + a.getDistance());
       // System.out.println("time: " + a.getTransportTime());
       // System.out.println("completion: " + a.getOperationTime());
        //System.out.println("waiting: " + a.getWaitingTime());
        /*
         * plot
         */
//        new Plotter(problem,bestSolution).setLabel(Plotter.Label.ID).plot("output/plot", "mtw");

//        new GraphStreamViewer(problem, bestSolution).labelWith(Label.ID).setRenderDelay(200).display();
    }

    private static AlgorithmConfig getAlgorithmConfig() {
        AlgorithmConfig config = new AlgorithmConfig();
        XMLConfiguration xmlConfig = config.getXMLConfiguration();
        xmlConfig.setProperty("iterations", "1");
        xmlConfig.setProperty("construction.insertion[@name]", "bestInsertion");

        xmlConfig.setProperty("strategy.memory", 1);
        String searchStrategy = "strategy.searchStrategies.searchStrategy";

        xmlConfig.setProperty(searchStrategy + "(0)[@name]", "random_best");
        xmlConfig.setProperty(searchStrategy + "(0).selector[@name]", "selectBest");
        xmlConfig.setProperty(searchStrategy + "(0).acceptor[@name]", "acceptNewRemoveWorst");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0)[@name]", "ruin_and_recreate");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).ruin[@name]", "randomRuin");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).ruin.share", "0.3");
        xmlConfig.setProperty(searchStrategy + "(0).modules.module(0).insertion[@name]", "bestInsertion");
        xmlConfig.setProperty(searchStrategy + "(0).probability", "0.5");

        xmlConfig.setProperty(searchStrategy + "(1)[@name]", "radial_best");
        xmlConfig.setProperty(searchStrategy + "(1).selector[@name]", "selectBest");
        xmlConfig.setProperty(searchStrategy + "(1).acceptor[@name]", "acceptNewRemoveWorst");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0)[@name]", "ruin_and_recreate");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).ruin[@name]", "radialRuin");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).ruin.share", "0.15");
        xmlConfig.setProperty(searchStrategy + "(1).modules.module(0).insertion[@name]", "bestInsertion");
        xmlConfig.setProperty(searchStrategy + "(1).probability", "0.5");

        return config;
    }
}
