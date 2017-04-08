package TestingAlgo;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Bucket;
import Dataset.Events;
import Dataset.GridCell;
import Dataset.GridFile;
import Naive.Naive;
import Visualize.*;
import edu.rice.hj.api.SuspendableException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;

import  Naive.Naive.*;

import static Dataset.GridFile.*;
import static Moving_Circle.MovingCircle.*;
import static Naive.Naive.*;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.forasync;
import static edu.rice.hj.Module2.isolated;

/**
 * Created by Guroosh Chaudhary on 05-02-2017.
 */
public class Main {
    public static int naive_counter = 1;
    public static double minLat = 360;
    public static double minLon = 360;
    public static double maxLat = -360;
    public static double maxLon = -360;
    public static double error_adjuster = 0.0000001;
    public static int bucket_number = 0;
    public static int bucket_size;
    public static String splitAxis = "horizontal";
    public static ArrayList<Circle> core_circles = new ArrayList<>();

    public static void main(String args[]) throws Exception {
        Scanner in = new Scanner(System.in);
        String fileName = "d.csv";  //in.nextLine();
        bucket_size = 100;
//        System.out.println("Enter file name: ");
//        while (true) {
//            System.out.println("Enter the bucket size: ");
//            bucket_size = in.nextInt();
//            if (bucket_size < 1) {
//                System.out.println("Bucket number should be a natural number");
//            }
//            break;
//        }

        //data creation start
        ArrayList<Events> events = readDataFile(fileName);

        GridFile gridFile = new GridFile();
        GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
        gridCell = gridCell.getGridCell(gridFile);
        gridFile.make(events, gridCell);
        //data creation end

        System.out.println("Starting run with dataset "+fileName+"\n");

        runNaiveTester(gridFile, events);
        runNaiveTesterHJ(gridFile, events);
        runNaiveTesterFJP(gridFile, events);
        runNaiveTesterJOMP(gridFile, events);

        runMovingCircleTester(gridFile, events);
        runMovingCircleTesterHJ(gridFile, events);
        runMovingCircleTesterJvFP(gridFile, events);
        runMovingCircleTesterJOMP(gridFile, events);
        System.out.println("Complete");

    }













}











