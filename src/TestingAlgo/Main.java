package TestingAlgo;

import Algorithm_Ops.Circle;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridCell;
import Dataset.GridFile;
import Experiments.ListCheck;
import Visualize.VisualizeNaive;
import edu.rice.hj.api.SuspendableException;
import jsc.distributions.Poisson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static Dataset.GridFile.readDataFile;
import static Moving_Circle.MovingCircle.*;
import static Naive.Naive.*;

import jsc.distributions.Poisson;

/**
 * Created by Guroosh Chaudhary on 05-02-2017.
 */
public class Main {
    public static String fileName;
    public static double minLat = 360;
    public static double minLon = 360;
    public static double maxLat = -360;
    public static double maxLon = -360;
    public static double error_adjuster = 0.0000001;
    public static int bucket_number = 0;
    public static int bucket_size;
    public static String splitAxis = "horizontal";
    public static GridFile gridFile_global;
    public static ArrayList<Circle> core_circles = new ArrayList<>();
    public static ArrayList<Circle> list1 = new ArrayList<>(), list2 = new ArrayList<>();

    public static ArrayList<Events> event1 = new ArrayList<>();

    public static void main(String args[]) throws Exception {
        Scanner in = new Scanner(System.in);
//        fileName = "d.csv";
        fileName = "dWeapon_unlawful_discharge_of.csv";
//        fileName = "ny_robbery.csv";
//        bucket_size = Values.bucketSize;


        //data creation start
        ArrayList<Events> events = readDataFile(fileName);
        if (events.size() > 10000)
            bucket_size = 500;
        else
            bucket_size = 50;

        GridFile gridFile = new GridFile();
        GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
        gridCell = gridCell.getGridCell(gridFile);
        gridFile.make(events, gridCell);
        event1 = events;
        gridFile_global = gridFile;
        //data creation end

        System.out.println("\nStarting run with dataset " + fileName + "\n");
        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);


        experiment_montecarlo_value(events.size(), area);
        System.out.println("Complete");

//        experiment_naive_vs_moving(events, list1, list2);
//        experiment_p_value(events.size(), area);
    }

    private static void experiment_montecarlo_value(int size, ScanGeometry area) throws Exception {
        System.out.println("Starting runtime test of " + Values.pval_nruns + " Monte Carlo simulations showing  a poison distribution with mean : " + size);
        double mean = size;
        Poisson object = new Poisson(mean);
        for (int i = 0; i < Values.pval_nruns; i++) {
            if (i % 50 == 0)
                System.out.println("Run :" + i);

            double val = object.random();
//        ArrayList<Events> poisson_data = new ArrayList<>();
            ArrayList<Events> poisson_data = event1;
//        for (int j = 0; j < val; j++) {
//            Events event = new Events();
//            double x = ThreadLocalRandom.current().nextDouble(area.start_X, area.end_X);
//            double y = ThreadLocalRandom.current().nextDouble(area.start_Y, area.end_Y);
//            event.setLon(x);
//            event.setLat(y);
//            poisson_data.add(event);
//        }

            GridFile gridFile = new GridFile();
            GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
            gridCell = gridCell.getGridCell(gridFile);
            gridFile.make(poisson_data, gridCell);
            gridFile_global = gridFile;

//            runMovingCircleTester(gridFile, poisson_data);
//            clear();
//            runMovingCircleTesterHJ(gridFile, poisson_data);
//            clear();
//            runMovingCircleTesterJvFP(gridFile, poisson_data);
            clear();
            runMovingCircleTesterJOMP(gridFile, poisson_data);


//            runNaiveTester(gridFile, poisson_data);
//            clear();
//            runNaiveTesterHJ(gridFile, poisson_data);
//            clear();
//            runNaiveTesterFJP(gridFile, poisson_data);
            clear();
            runNaiveTesterJOMP(gridFile, poisson_data);



        }
        result();
    }

    private static void clear() {
        core_circles = new ArrayList<>();
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        count_naive_circles_for_single_thread = 0L;
        top_likelihood_circles_for_single_thread = new ArrayList<>();
        count_naive_circles_for_HJ = 0L;
        top_likelihood_circles_for_HJ = new ArrayList<>();
        count_naive_circles_for_JOMP = 0L;
        top_likelihood_circles_for_JOMP = new ArrayList<>();
        count_naive_circles_for_FJP = 0L;
        top_likelihood_circles_for_FJP = Collections.synchronizedList(new ArrayList<Circle>());

    }

    private static void result() {
        System.out.println("Result is as follows:" +
                "\n\tNaive" +
                "\n\t\tST " + (Result.NaiveTesterST_time / 1000) +
                "s\n\t\tHJ " + (Result.NaiveTesterHJ_time / 1000) +
                "s\n\t\tFJP " + (Result.NaiveTesterFJP_time / 1000) +
                "s\n\t\tJOMP " + (Result.NaiveTesterJOMP_time / 1000) +
                "s\n\tMoving " +
                "\n\t\tST " + (Result.MovingTesterST_time) +
                "s\n\t\tHJ " + (Result.MovingTesterHJ_time) +
                "s\n\t\tFJP " + (Result.MovingTesterFJP_time) +
                "s\n\t\tJOMP " + (Result.MovingTesterJOMP_time)
                + "s");
    }

    private static void experiment_p_value(int size, ScanGeometry scanA) {
        System.out.println("Starting experiment of statistical significance on a poison distribution with mean : " + size);
        double mean = size;
        Poisson object = new Poisson(mean);
//        for (int i = 0; i < Values.pval_nruns ; i++) {

        double val = object.random();
        ArrayList<Events> poisson_data = new ArrayList<>();
        for (int j = 0; j < val; j++) {
            Events event = new Events();
            double x = ThreadLocalRandom.current().nextDouble(scanA.start_X, scanA.end_X);
            double y = ThreadLocalRandom.current().nextDouble(scanA.start_Y, scanA.end_Y);
            event.setLon(x);
            event.setLat(y);
            poisson_data.add(event);
        }

        GridFile gridFile = new GridFile();
        GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
        gridCell = gridCell.getGridCell(gridFile);
        gridFile.make(poisson_data, gridCell);
        gridFile_global = gridFile;

        VisualizeNaive visualize = new VisualizeNaive();
        ArrayList<Circle> four_circles = new ArrayList<>();
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);
        four_circles.add(c1);
        four_circles.add(c2);
        four_circles.add(c3);
        four_circles.add(c4);
        visualize.drawCircles(poisson_data, four_circles, "Dataset");


//        }
    }

    private static void experiment_naive_vs_moving(ArrayList<Events> events, ArrayList<Circle> list1, ArrayList<Circle> list2) {
        double jaccardI;
        double threshold = Values.ji_threshold;
        int array[] = {3, 5, 10, 15};
        for (int i : array) {
            try {
                jaccardI = new ListCheck().jaccardIndex(list1.subList(0, i), list2.subList(0, i), threshold);
                System.out.println("For top: " + i + " JI: " + jaccardI * 100 + "%");
//                System.out.println("Our calc " + jaccardI+"%");
            } catch (IndexOutOfBoundsException e) {
                //do nothing;
            }
        }

        //starting visualize_in_one
        ArrayList<Circle> temp1 = new ArrayList<>(list1.subList(0, (Values.top_circles_for_visualize > list1.size()) ? list1.size() : Values.top_circles_for_visualize));
        ArrayList<Circle> temp2 = new ArrayList<>(list2.subList(0, (Values.top_circles_for_visualize > list2.size()) ? list2.size() : Values.top_circles_for_visualize));
        temp1.add(new Circle(0, 0, -7));
        temp1.addAll(temp2);
        temp1.add(new Circle(minLon, minLat, 0.0001));
        temp1.add(new Circle(minLon, maxLat, 0.0001));
        temp1.add(new Circle(maxLon, maxLat, 0.0001));
        temp1.add(new Circle(maxLon, minLat, 0.0001));
        VisualizeNaive visualize = new VisualizeNaive();
        String title = "Experiment";
        visualize.drawCircles(events, temp1, title);
    }
}









