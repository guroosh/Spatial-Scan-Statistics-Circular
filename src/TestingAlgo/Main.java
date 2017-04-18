package TestingAlgo;

import Algorithm_Ops.Circle;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridCell;
import Dataset.GridFile;
import Experiments.ListCheck;
import Visualize.VisualizeNaive;
import jsc.distributions.Poisson;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static Dataset.GridFile.readDataFile;

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
    public static ArrayList<Circle> list1JOMP = new ArrayList<>(), list2JOMP = new ArrayList<>();
    public static ArrayList<Circle> list1FJP = new ArrayList<>(), list2FJP = new ArrayList<>();
    public static ArrayList<Circle> list1HJ = new ArrayList<>(), list2HJ = new ArrayList<>();

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
        gridFile_global = gridFile;
        //data creation end

        System.out.println("\nStarting run with dataset " + fileName + "\n");
        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
//        runNaiveTester(gridFile, events);
//        runNaiveTesterHJ(gridFile, events);
//        runNaiveTesterFJP(gridFile, events);
//        runNaiveTesterJOMP(gridFile, events);

//        runMovingCircleTester(gridFile, events);
//        runMovingCircleTesterHJ(gridFile, events);
//        runMovingCircleTesterJvFP(gridFile, events);
//        runMovingCircleTesterJOMP(gridFile, events);

        System.out.println("Complete");

//        experiment_naive_vs_moving(events, list1, list2);
        experiment_p_value(events.size(), area);
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









