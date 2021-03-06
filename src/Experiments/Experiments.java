package Experiments;

import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridCell;
import Dataset.GridFile;

import TestingAlgo.Values;
import edu.rice.hj.api.SuspendableException;
import jsc.distributions.Poisson;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static Moving_Circle.MovingCircle.*;
import static Naive.Naive.*;
import static TestingAlgo.Main.*;

/**
 * Created by ld on 29/5/17.
 */
//Contains experiments on various parameters
public class Experiments {
    //Serial version of Naive algorithm versues Parallel versions
    public static void exp1_phase1(GridFile gridFile, ArrayList<Events> events) throws Exception {
        String res_text = "Running Experiment 1 on Phase 1";
        for (int i = 1; i <= Values.nruns; i++) {
            if (i % 5 == 0)
                System.out.println("Run :" + i);
            runNaiveTester(gridFile, events);
            clear();
            runNaiveTesterHJ(gridFile, events);
            clear();
            runNaiveTesterFJP(gridFile, events);
            clear();
//            runNaiveTesterJOMP(gridFile, events);
//            Main.clear();
        }
        result(res_text, Values.nruns);

    }

    //Serial version of Moving Circle algorithm versues Parallel versions
    public static void exp2_phase1(GridFile gridFile, ArrayList<Events> events) throws Exception {
        String res_text = "Running Experiment 2 on Phase 1";
        for (int i = 1; i <= Values.nruns; i++) {
            if (i % 5 == 0)
                System.out.println("Run :" + i);
            runMovingCircleTester(gridFile, events);
            clear();
            runMovingCircleTesterHJ(gridFile, events);
            clear();
            runMovingCircleTesterJvFP(gridFile, events);
            clear();
//            runMovingCircleTesterJOMP(gridFile, events);
//            clear();
        }
        result(res_text, Values.nruns);

    }

    //Monte Carlo experiment on Naive all frameworks
    public static void exp1_phase2(int size, ScanGeometry area) throws Exception {
        String res_text = "Starting phase 2 of exp 1 using a poison distribution with mean : " + size + " Runs :" + Values.p2runs;
        double mean = size;
        Poisson object = new Poisson(mean);
        for (int i = 0; i < Values.p2runs; i++) {
            if (i % 50 == 0)
                System.out.println("Run :" + i);

            double val = object.random();
            ArrayList<Events> poisson_data = new ArrayList<>();

            for (int j = 0; j < val; j++) {
                Events event = new Events();
                double x = ThreadLocalRandom.current().nextDouble(area.start_X, area.end_X);
                double y = ThreadLocalRandom.current().nextDouble(area.start_Y, area.end_Y);
                event.setX(x);
                event.setY(y);
                poisson_data.add(event);
            }

            GridFile gridFile = new GridFile();
            GridCell gridCell = new GridCell(gridFile, area.start_Y, area.end_Y, area.start_X, area.end_X);
            gridCell = gridCell.getGridCell(gridFile);
            gridFile.make(poisson_data, gridCell);


            runNaiveTester(gridFile, poisson_data);
            clear();
            runNaiveTesterHJ(gridFile, poisson_data);
            clear();
            runNaiveTesterFJP(gridFile, poisson_data);
            clear();
//            runNaiveTesterJOMP(gridFile, poisson_data);
//            clear();

        }
        result(res_text, Values.p2runs);
    }
//Monte Carlo experiment on Moving Circle all frameworks

    public static void exp2_phase2(int size, ScanGeometry area) throws Exception {
        String res_text = "Starting phase 2 of exp 1 using a poison distribution with mean : " + size + " Runs :" + Values.p2runs;
        double mean = size;
        Poisson object = new Poisson(mean);
        for (int i = 0; i < Values.p2runs; i++) {
            if (i % 50 == 0)
                System.out.println("Run :" + i);

            double val = object.random();
            ArrayList<Events> poisson_data = new ArrayList<>();

            for (int j = 0; j < val; j++) {
                Events event = new Events();
                double x = ThreadLocalRandom.current().nextDouble(area.start_X, area.end_X);
                double y = ThreadLocalRandom.current().nextDouble(area.start_Y, area.end_Y);
                event.setX(x);
                event.setY(y);
                poisson_data.add(event);
            }

            GridFile gridFile = new GridFile();
            GridCell gridCell = new GridCell(gridFile, area.start_Y, area.end_Y, area.start_X, area.end_X);
            gridCell = gridCell.getGridCell(gridFile);
            gridFile.make(poisson_data, gridCell);


            runMovingCircleTester(gridFile, poisson_data);
            clear();
            runMovingCircleTesterHJ(gridFile, poisson_data);
            clear();
            runMovingCircleTesterJvFP(gridFile, poisson_data);
            clear();
//            runMovingCircleTesterJOMP(gridFile, poisson_data);
//            clear();

        }
        result(res_text, Values.p2runs);
    }

    //Experiment on time taken by varying the growth rate
    public static void exp3(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        double growth = Values.growth_rate / 2;
        for (double j = growth; j < growth * 20; j += (growth)) {

            String res_text = "Running Experiment 3" + " on growth rate " + j;
            Values.setGrowth_rate(j);
            for (int i = 1; i <= Values.nruns; i++) {
                if (i % 5 == 0)
                    System.out.println("Run :" + i);

                runNaiveTester(gridFile, events);
                clear();
                runMovingCircleTester(gridFile, events);
                clear();

            }
            result(res_text, Values.nruns);
        }

    }


}
