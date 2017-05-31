package Naive;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridFile;
import TestingAlgo.Main;
import TestingAlgo.Result;
import TestingAlgo.Values;
import Visualize.VisualizeNaive;
import edu.rice.hj.api.SuspendableException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import static Algorithm_Ops.CircleOps.checkintersection;
import static TestingAlgo.Main.*;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.forasync;
import static edu.rice.hj.Module2.isolated;
import static edu.rice.hj.runtime.config.HjSystemProperty.numWorkers;

/**
 * Created by guroosh on 8/4/17.
 */
public class Naive {


    public static Long count_naive_circles_for_single_thread = 0L;
    public static ArrayList<Circle> top_likelihood_circles_for_single_thread = new ArrayList<>();
    public static Long count_naive_circles_for_HJ = 0L;
    public static ArrayList<Circle> top_likelihood_circles_for_HJ = new ArrayList<>();
    public static Long count_naive_circles_for_JOMP = 0L;
    public static ArrayList<Circle> top_likelihood_circles_for_JOMP = new ArrayList<>();
    public static Long count_naive_circles_for_FJP = 0L;
    public static  List<Circle> top_likelihood_circles_for_FJP = Collections.synchronizedList(new ArrayList<Circle>());


    public static void runNaiveTester(GridFile gridFile, ArrayList<Events> events) {
        // System.out.println("\nStarting Naive run");
        long startTime = System.currentTimeMillis();
        naiveTester(gridFile);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Result.NaiveTesterST_time +=totalTime/(double)1000;
//        System.out.println("Total time for naive with single thread: " + ((double) totalTime / (double) 1000) + "s");

//        Collections.sort(top_likelihood_circles_for_single_thread, Circle.sortByLHR());
//        naiveWithoutIntersectingCircles(top_likelihood_circles_for_single_thread, count_naive_circles_for_single_thread);
//        afterNaive(events, gridFile);
    }

    public static double runNaiveTesterHJ(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        // System.out.println("Starting Naive run with HABANERO");
        long startTime1 = System.currentTimeMillis();
        launchHabaneroApp(() -> {
            naiveTesterHJ(gridFile);
        });
        long endTime1 = System.currentTimeMillis();
        long totalTime = endTime1 - startTime1;
        Result.NaiveTesterHJ_time+=totalTime/(double)1000;
//        System.out.println("Total time for naive with HABANERO: " + ((double) totalTime / (double) 1000) + "s");
//
//        Collections.sort(top_likelihood_circles_for_HJ, Circle.sortByLHR());
//        naiveWithoutIntersectingCircles(top_likelihood_circles_for_HJ, count_naive_circles_for_HJ);
//        afterNaive(events, gridFile);
        return ((double) totalTime / (double) 1000);
    }

    public static double runNaiveTesterJOMP(GridFile gridFile, ArrayList<Events> events) throws Exception {
        // System.out.println("Starting Naive run with JOMP");
        long startTime = System.currentTimeMillis();
        naiveTesterJOMP(gridFile);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Result.NaiveTesterJOMP_time+=totalTime/(double)1000;
//        System.out.println("Total time for naive with JOMP: " + ((double) totalTime / (double) 1000) + "s");

//        Collections.sort(top_likelihood_circles_for_JOMP, Circle.sortByLHR());
//        naiveWithoutIntersectingCircles(top_likelihood_circles_for_JOMP, count_naive_circles_for_FJP);
//        afterNaive(events, gridFile);
        return ((double) totalTime / (double) 1000);
    }

    public static double runNaiveTesterFJP(GridFile gridFile, ArrayList<Events> events) {
        // System.out.println("Starting Naive run with Fork Join Pool");
        long startTime = System.currentTimeMillis();
        int runtime = Values.number_of_radius_naive;
        int threshold = runtime / curr_number_of_threads;
        NaiveRunnerFJP rootTask = new NaiveRunnerFJP(0, runtime, gridFile, threshold);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(rootTask);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        Result.NaiveTesterFJP_time+=totalTime/(double)1000;
//         System.out.println("Total time for naive with Fork Join Pool: " + ((double) totalTime / (double) 1000) + "s");
//
//        Collections.sort(top_likelihood_circles_for_FJP, Circle.sortByLHR());
//        naiveWithoutIntersectingCircles(top_likelihood_circles_for_FJP, count_naive_circles_for_FJP);
//        afterNaive(events, gridFile);
        return ((double) totalTime / (double) 1000);
    }

    private static void naiveTesterJOMP(GridFile gridFile) throws Exception {
        JOMP.naiveJOMP naiveJOMP = new JOMP.naiveJOMP();
        naiveJOMP.naiveTesterJOMP(gridFile, minLon, minLat, maxLon, maxLat);
        Circle top_likelihood_circles_a[] = naiveJOMP.circles;
        int circles_added = naiveJOMP.added_circles;
        count_naive_circles_for_JOMP = naiveJOMP.circle_count;
        for (int i = 0; i < circles_added; i++) {
            top_likelihood_circles_for_JOMP.add(top_likelihood_circles_a[i]);
        }
    }

    private static void naiveTesterHJ(GridFile gridFile) throws SuspendableException {
        final double likelihood_threshold = Values.lh_threshold;
        final double[] curr_radius = {Values.lower_limit};
        final double initial_radius = curr_radius[0];
        final double term_radius = Values.upper_limit;
        final double shift_radius = curr_radius[0];
        final double growth_radius = curr_radius[0];
        int number_of_radius = Values.number_of_radius_naive;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        CircleOps controller = new CircleOps(initial_radius, term_radius, area, gridFile);
        finish(() -> {
            forasync(0, number_of_radius - 1, (i) -> {
                final double[] curr_local_radius = {initial_radius + (growth_radius * i)};
                final Circle[] curr_local_circle = {new Circle(minLon, minLat, curr_local_radius[0])};
                while (controller.term(curr_local_circle[0]) != -1) {
                    while (controller.term(curr_local_circle[0]) == 0) {
                        isolated(() -> {
                            count_naive_circles_for_HJ++;
                        });
                        ArrayList<Events> points = controller.scanCircle(curr_local_circle[0]);
                        double likelihood_ratio = controller.likelihoodRatio(curr_local_circle[0], points);
                        if (likelihood_ratio > likelihood_threshold) {
                            Circle temp_circle = new Circle(curr_local_circle[0].getX_coord(), curr_local_circle[0].getY_coord(), curr_local_circle[0].getRadius());
                            temp_circle.lhr = likelihood_ratio;
                            isolated(() -> {
                                top_likelihood_circles_for_HJ.add(temp_circle);
                            });
                        }
                        curr_local_circle[0] = controller.grow_x(shift_radius, curr_local_circle[0]);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                    }
                    curr_local_circle[0] = controller.shifty_startx(curr_local_circle[0], shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
                }
            });
        });
    }

    private static void naiveTesterFJP(GridFile gridFile, int start, int end) {

        final double likelihood_threshold = Values.lh_threshold;
        final double[] curr_radius = {Values.lower_limit};
        final double initial_radius = curr_radius[0];
        final double term_radius = Values.upper_limit;
        final double shift_radius = curr_radius[0];
        final double growth_radius = curr_radius[0];

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        CircleOps controller = new CircleOps(initial_radius, term_radius, area, gridFile);
        for (int i = start; i < end; i++) {
            final double[] curr_local_radius = {initial_radius + (growth_radius * i)};
            final Circle[] curr_local_circle = {new Circle(minLon, minLat, curr_local_radius[0])};
            while (controller.term(curr_local_circle[0]) != -1) {
                while (controller.term(curr_local_circle[0]) == 0) {
                    synchronized (Main.class) {
                        count_naive_circles_for_FJP++;
                    }
                    ArrayList<Events> points = controller.scanCircle(curr_local_circle[0]);
                    double likelihood_ratio = controller.likelihoodRatio(curr_local_circle[0], points);
                    if (likelihood_ratio > likelihood_threshold) {
                        Circle temp_circle = new Circle(curr_local_circle[0].getX_coord(), curr_local_circle[0].getY_coord(), curr_local_circle[0].getRadius());
                        temp_circle.lhr = likelihood_ratio;
                        synchronized (top_likelihood_circles_for_FJP) {
                            top_likelihood_circles_for_FJP.add(temp_circle);
                        }
                    }
                    curr_local_circle[0] = controller.grow_x(shift_radius, curr_local_circle[0]);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                }
                curr_local_circle[0] = controller.shifty_startx(curr_local_circle[0], shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
            }
//            // System.out.println("Radius: "+curr_local_radius[0]);
        }
    }

    private static void naiveTester(GridFile gridFile) {
        double likelihood_threshold = Values.lh_threshold;
        double curr_radius = Values.lower_limit;
        double term_radius = Values.upper_limit;
        double shift_radius = curr_radius;
        double growth_radius =Values.growth_rate;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        Circle curr_circle = new Circle(minLon, minLat, curr_radius);
        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);


        while (controller.term(curr_circle) != 3) {
            //printing one loop less, before
            //ANS: because 0.1 is stored as 0.1000000000001 sometimes, used error adjuster, done
            while (controller.term(curr_circle) != -1) {
                while (controller.term(curr_circle) == 0) {
                    count_naive_circles_for_single_thread++;
                    ArrayList<Events> points = controller.scanCircle(curr_circle);
                    double likelihood_ratio = controller.likelihoodRatio(curr_circle, points);
                    if (likelihood_ratio > likelihood_threshold) {

                        Circle temp_circle = new Circle(curr_circle);
                        temp_circle.lhr = likelihood_ratio;
                        top_likelihood_circles_for_single_thread.add(temp_circle);
                    }
                    curr_circle = controller.grow_x(shift_radius, curr_circle);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                }
                curr_circle = controller.shifty_startx(curr_circle, shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
            }
//            // System.out.println("Radius: " + curr_radius);
            curr_radius = controller.increase_radius(curr_radius, growth_radius);
            curr_circle = new Circle(minLon, minLat, curr_radius);
        }
    }

    private static void naiveWithoutIntersectingCircles(List<Circle> top_likelihood_circles, long count_naive_circles) {
        int j = 0;
        int intersecting_flg = 0;
        int top_number_circles = Values.top_circles_naive_final;
        for (int i = 0; j < top_number_circles; i++) {
            if (i >= top_likelihood_circles.size()) {
                break;
            }
            Circle cl = top_likelihood_circles.get(i);
            if (i == 0) {
//                // System.out.println(cl.lhr + " ,   circle: " + cl.toString());
                core_circles.add(cl);
                j++;
                continue;
            }
            for (Circle core_circle : core_circles) {
                if (checkintersection(core_circle, cl)) {
                    intersecting_flg++;
                }
            }
            if (intersecting_flg != 0) {
                intersecting_flg = 0;
            } else {
//                // System.out.println(cl.lhr + " ,   circle: " + cl.toString());
                core_circles.add(cl);
                j++;
            }
        }

    }

    private static void afterNaive(ArrayList<Events> events, GridFile gridFile) {
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);
        core_circles.add(c1);
        core_circles.add(c2);
        core_circles.add(c3);
        core_circles.add(c4);
        // System.out.println();
        VisualizeNaive vis = new VisualizeNaive();
        vis.drawCircles(events, core_circles,"Naive HJ");
        int number = Values.top_circles_for_print;
        drawtop(number, gridFile);
        Main.list1.addAll(core_circles);
        core_circles = new ArrayList<>();
    }

    private static void drawtop(int number, GridFile gridFile) {
        if (number == -1)
            number = core_circles.size();

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        for (int i = 0; i < number; i++) {
            // System.out.println("\t" + core_circles.get(i).toString());
        }
        // System.out.println();
    }

    private static class NaiveRunnerFJP extends RecursiveAction {
        private GridFile gridFile;
        private int runtime;
        private int start;
        private int end;

        public NaiveRunnerFJP(int start, int end, GridFile gridFile, int runtime) {
            this.gridFile = gridFile;
            this.runtime = runtime;
            this.start = start;
            this.end = end;
        }

        public void run1(int start, int end) {
            naiveTesterFJP(gridFile, start, end);
        }

        @Override
        protected void compute() {
            if (end - start <= runtime) {
                run1(start, end);
                return;
            }
            int mid = start + (end - start) / 2;
            invokeAll(
                    new NaiveRunnerFJP(start, mid, gridFile, runtime),
                    new NaiveRunnerFJP(mid, end, gridFile, runtime)
            );
        }
    }
}
