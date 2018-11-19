package com.msh.app;

import com.msh.services.EventServer;
import com.msh.utils.MapQuestAPI;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProgramLauncher {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        boolean quit = false;
        EventServer e_server = new EventServer(MapQuestAPI.lstHardPoints,MapQuestAPI.lstSoftPoints);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        while(true) {
            if(quit)
                break;
            System.out.println("Please select an option: ");
            System.out.println("1: start the server");
            System.out.println("2: stop the server");
            System.out.println("3: launch the routing optimization algorithm");
            System.out.println("4: quit the program");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.println("Starting server...");
                    executor = Executors.newFixedThreadPool(1);
                    executor.submit(e_server);

                    break;
                case 2:
                    System.out.println("Server is shutting down...");
                    executor.shutdownNow();
                    break;
                case 3:
                    System.out.println("Routing optimization in progress...");
                    PlanningApp.plan();
                    break;
                case 4:
                    // Perform "quit" case.
                    System.out.println("Thank you for testing, Bye...");
                    quit = true;
                    break;
                default:
                    System.out.println("Option not valid!");
                    break;
                    // The user input an unexpected choice.
            }
        }
    }
}
