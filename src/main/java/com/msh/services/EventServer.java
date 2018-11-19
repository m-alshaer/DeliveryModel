package com.msh.services;

import com.graphhopper.jsprit.core.util.Coordinate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class EventServer extends Thread {
    private List<Coordinate> lstHardPoints;
    private List<Coordinate> lstSoftPoints;

    public EventServer(List<Coordinate> lstHardPoints, List<Coordinate> lstSoftPoints){
        this.lstHardPoints = lstHardPoints;
        this.lstSoftPoints = lstSoftPoints;
    }

    @Override
    public void run() {

        try {
            ServerSocket _serverSocket = new ServerSocket(6799);
            String messsgeReaded = "";

            while (true) {

                Socket connectionSocket = _serverSocket.accept();
                BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                messsgeReaded = inFromClient.readLine();
                String[] arrReaded = messsgeReaded.split(";");
                String longitude=arrReaded[0];
                String latitude = arrReaded[1];
                boolean hard = arrReaded[2].equals("1");
                Coordinate p = new Coordinate(Double.parseDouble(longitude),Double.parseDouble(latitude));

                if(hard){
                    this.lstHardPoints.add(p);
                }else{
                    this.lstSoftPoints.add(p);
                }

                System.out.println("Received: " + messsgeReaded);

                Thread.sleep(200);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }


    }
}
