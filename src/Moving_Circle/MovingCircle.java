package Moving_Circle;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridFile;
import Experiments.ListCheck;
import TestingAlgo.Main;
import TestingAlgo.Result;
import TestingAlgo.Values;
import edu.rice.hj.api.SuspendableException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;

import static TestingAlgo.Main.*;
import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module1.forasync;
import static edu.rice.hj.Module2.isolated;
import static edu.rice.hj.runtime.config.HjSystemProperty.numWorkers;

/**
 * Created by guroosh on 8/4/17.
 */
public class MovingCircle {
    static int runtime = Values.runtime;

    public static double runMovingCircleTesterJOMP(GridFile gridFile, ArrayList<Events> events) throws Exception {
        // System.out.println("Starting Moving Circle run with JOMP");
        long start = System.currentTimeMillis();
        JOMP.movingCircleJOMP movingCircleJOMP = new JOMP.movingCircleJOMP();
        Circle[] core_circles_array = movingCircleJOMP.movingCircleTesterJOMP(runtime, gridFile, minLon, minLat, maxLon, maxLat);
        long end = System.currentTimeMillis();
        Result.MovingTesterJOMP_time+=((double) (end - start)) / 1000 ;

        for (int i = 0; i < runtime; i++) {
            Circle temp_circle = core_circles_array[i];
            if (temp_circle == null) {
                continue;
            }
            core_circles.add(temp_circle);
        }
//        aftermovingcircal(gridFile, events);
        return ((double) (end - start)) / 1000;
    }

    public static double runMovingCircleTesterJvFP(GridFile gridFile, ArrayList<Events> events) {
        // System.out.println("Starting Moving Circle run with Java-Fork join pool");
        long start = System.currentTimeMillis();
        int threshold = runtime / curr_number_of_threads;
        MovingCircleRunnerFJP rootTask = new MovingCircleRunnerFJP(0, runtime, gridFile, threshold);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(rootTask);
        long end = System.currentTimeMillis();
//        // System.out.println(((double) (end - start)) / 1000);
        Result.MovingTesterFJP_time+=((double) (end - start)) / 1000;
//        aftermovingcircal(gridFile, events);
        return ((double) (end - start)) / 1000;
    }

    public static double runMovingCircleTesterHJ(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        // System.out.println("Starting Moving Circle run with Habanero-Java");
        long start = System.currentTimeMillis();
        finish(() -> {
            forasync(0, runtime, (i) -> {
                movingCircleTesterHJ(gridFile);
            });
        });
        long end = System.currentTimeMillis();
        Result.MovingTesterHJ_time+= ((double) (end - start)) / 1000;
//        System.out.println("Time HJ moving "+((double) (end - start)) / 1000+"s");
//        aftermovingcircal(gridFile, events);
        return ((double) (end - start)) / 1000;
    }

    public static void runMovingCircleTester(GridFile gridFile, ArrayList<Events> events) {
        // System.out.println("Starting Moving Circle run with single thread");
        long start = System.currentTimeMillis();

        for (int i = 0; i < runtime; i++) {
            movingCircleTester(gridFile);
        }

        long end = System.currentTimeMillis();
        Result.MovingTesterST_time+=((double) (end - start)) / 1000;
//        aftermovingcircal(gridFile, events);



    }

    private static void movingCircleTesterHJ(GridFile gridFile) {
        int circlecounter = Values.moving_circle_counter;
        double curr_radius = Values.lower_limit;
        double term_radius = Values.terminating_radius;
        double growth = Values.growth_rate;
        double upper_limit = Values.upper_limit;
        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);

        Circle curr_circle = new Circle("Random", curr_radius, area);//X: -80.9865 Y: 39.6339 r: 0.001(BAD Visualize)// X: -81.3279 Y: 39.6639 r: 0.001 (Good Case)
//            Circle curr_circle = new Circle(-81.2234, 39.7345, 0.001);   //-81.3773, 39.6293, .01)
        Circle next_circle;
        Circle temp_circle = new Circle(curr_circle);

        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);

        double maxlikeli = -1;
        Circle fin_circle = null;
        ArrayList<Events> points = controller.scanCircle(curr_circle);
        while (controller.term(curr_circle) != 3) {


            next_circle = controller.checkanglepoints(curr_circle, points);

            ArrayList<Events> points1 = controller.scanCircle(next_circle);

            double curr_likeli = controller.likelihoodRatio(curr_circle, points);
            double next_likeli = controller.likelihoodRatio(next_circle, points1);
            if (curr_likeli < next_likeli) {
                if (next_likeli > maxlikeli) {
                    maxlikeli = next_likeli;
                    fin_circle = new Circle(next_circle);
                }
//                temp_circle = new Circle(curr_circle);
//                core_circles.add(temp_circle);
                curr_circle = new Circle(next_circle);
                points = points1;
            } else {
                if (curr_likeli > maxlikeli) {
                    if (curr_circle.getRadius() <= upper_limit) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);
                    }
                }
                curr_circle = controller.grow_radius(growth, curr_circle);
                points = controller.scanCircle(curr_circle);
//                temp_circle = new Circle(curr_circle);
//                core_circles.add(temp_circle);
            }
            if (circlecounter == 1) {

                if (fin_circle != null) {
                    Circle finalFin_circle = fin_circle;
                    finalFin_circle.lhr = maxlikeli;
                    isolated(() -> {
//                        controller.removePoints(controller.scanCircle(finalFin_circle));
                        core_circles.add(finalFin_circle);
                    });
                }
                return;
            }
            circlecounter--;
        }
    }

    private static void movingCircleTesterFJP(GridFile gridFile) {
        double curr_radius = Values.lower_limit;
        double term_radius = Values.terminating_radius;
        double growth = Values.growth_rate;
        double upper_limit = Values.upper_limit;
        int circlecounter = Values.moving_circle_counter;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        Circle curr_circle = new Circle("Random", curr_radius, area);
        Circle next_circle;
        Circle temp_circle;         //used to add in core circles, if using visualize
        ArrayList<Events> points = new ArrayList<>();
        ArrayList<Events> next_points = new ArrayList<>();
        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);
        double maxlikeli = -1;
        Circle fin_circle = null;
        points = controller.scanCircle(curr_circle);

        while (controller.term(curr_circle) != 3) {
            next_circle = controller.checkanglepoints(curr_circle, points);
            ArrayList<Events> points1 = controller.scanCircle(next_circle);
            double curr_likeli = controller.likelihoodRatio(curr_circle, points);
            double next_likeli = controller.likelihoodRatio(next_circle, points1);
            if (curr_likeli < next_likeli) {
                if (next_likeli > maxlikeli) {
                    maxlikeli = next_likeli;
                    fin_circle = new Circle(next_circle);
                }
                temp_circle = new Circle(curr_circle);
//                core_circles.add(temp_circle);
                curr_circle = new Circle(next_circle);
                points = points1;
            } else {
                if (curr_likeli > maxlikeli) {
                    if (curr_circle.getRadius() <= upper_limit) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);
                    }
                }
                curr_circle = controller.grow_radius(growth, curr_circle);
                points = controller.scanCircle(curr_circle);
//                temp_circle = new Circle(curr_circle);
//                core_circles.add(temp_circle);
            }
            if (circlecounter == 1) {

                if (fin_circle != null) {
                    Circle finalFin_circle = fin_circle;
                    final ReentrantLock rl = new ReentrantLock();
                    rl.lock();
                    try {
//                        controller.removePoints(controller.scanCircle(finalFin_circle));
                        core_circles.add(finalFin_circle);
                    } finally {
                        rl.unlock();
                    }
                    fin_circle.lhr = maxlikeli;
                }
                return;
            }
            circlecounter--;
        }
    }

    private static void movingCircleTester(GridFile gridFile) {
        int circlecounter = Values.moving_circle_counter;
        double curr_radius = Values.lower_limit;
        double term_radius = Values.terminating_radius;
        double growth = Values.growth_rate;
        double upper_limit = Values.upper_limit;


        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        Circle curr_circle = new Circle("Random", curr_radius, area);

        Circle next_circle;
        Circle temp_circle;         //used to add in core circles, if using visualize

        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);
        double maxlikeli = -1;
        Circle fin_circle = null;
        ArrayList<Events> points = controller.scanCircle(curr_circle);
        while (controller.term(curr_circle) != 3) {

            next_circle = controller.checkanglepoints(curr_circle, points);
            ArrayList<Events> points1 = controller.scanCircle(next_circle);
            double curr_likeli = controller.likelihoodRatio(curr_circle, points);
            double next_likeli = controller.likelihoodRatio(next_circle, points1);
            if (curr_likeli < next_likeli) {
                if (next_likeli > maxlikeli) {
                    maxlikeli = next_likeli;
                    fin_circle = new Circle(next_circle);
                }
                temp_circle = new Circle(curr_circle);

                curr_circle = new Circle(next_circle);
                points = points1;
            } else {
                if (curr_likeli > maxlikeli) {
                    if (curr_circle.getRadius() <= upper_limit) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);
                    }
                }
                curr_circle = controller.grow_radius(growth, curr_circle);
                points = controller.scanCircle(curr_circle);
                temp_circle = new Circle(curr_circle);

            }
            if (circlecounter == 1) {
                if (fin_circle != null) {
                    fin_circle.lhr = maxlikeli;
                    core_circles.add(fin_circle);
//                    controller.removePoints(controller.scanCircle(fin_circle));
                }
                return;
            }
            circlecounter--;
        }
    }

    private static void aftermovingcircal(GridFile gridFile, ArrayList<Events> events) {
        Collections.sort(core_circles, Circle.sortByLHR());
        ArrayList<Circle> non_intersecting_core_circles = new ArrayList<>();
        boolean intersecting_flag = false;
        int i = 0;
        for (Circle c : core_circles) {
            if (i == 0) {
                non_intersecting_core_circles.add(c);
                i = 1;
                continue;
            }
            for (Circle d : non_intersecting_core_circles) {
                if (new ListCheck().checkOverlapping(c, d) > 0) {
                    intersecting_flag = true;
                }
            }
            if(intersecting_flag)
            {
                intersecting_flag = false;
            }
            else
            {
                non_intersecting_core_circles.add(c);
            }
        }

        ArrayList<Circle> moving_circles_for_visualize = new ArrayList<>();
        int top_circles_for_visualize = Values.top_circles_for_visualize;

        if (top_circles_for_visualize > non_intersecting_core_circles.size()) {
            moving_circles_for_visualize.addAll(non_intersecting_core_circles);
        } else {
            moving_circles_for_visualize.addAll(non_intersecting_core_circles.subList(0, top_circles_for_visualize));
        }

        visualizedata(events, moving_circles_for_visualize);

        int number = Values.top_circles_for_print;
        drawtop(number, non_intersecting_core_circles);
        Main.list2.addAll(non_intersecting_core_circles);
        core_circles = new ArrayList<>();
//        CircleOps.resetPointsVisibility(gridFile);
    }

    private static void drawtop(int number, ArrayList<Circle> non_intersecting_core_circles) {
        if (number == -1)
            number = non_intersecting_core_circles.size();
        for (int i = 0; i < number; i++) {
            System.out.println("\t" + non_intersecting_core_circles.get(i).toString());
        }
    }

    private static void visualizedata(ArrayList<Events> events, ArrayList<Circle> moving_circles_for_visualize) {
        Visualize.VisualizeNaive vis = new Visualize.VisualizeNaive();
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);

        moving_circles_for_visualize.add(c1);
        moving_circles_for_visualize.add(c2);
        moving_circles_for_visualize.add(c3);
        moving_circles_for_visualize.add(c4);
//        int top_circles_for_visualize = Values.top_circles_for_visualize;

        vis.drawCircles(events, moving_circles_for_visualize, "title");
    }

    private static class MovingCircleRunnerFJP extends RecursiveAction {
        private GridFile gridFile;
        private int runtime;
        private int start;
        private int end;

        public MovingCircleRunnerFJP(int start, int end, GridFile gridFile, int runtime) {
            this.gridFile = gridFile;
            this.runtime = runtime;
            this.start = start;
            this.end = end;
        }

        public void run1(int runtime) {
            for (int i = 0; i < runtime; i++) {
                movingCircleTesterFJP(gridFile);
            }
        }

        @Override
        protected void compute() {
            if (end - start <= runtime) {
                run1(runtime);
                return;
            }
            int mid = start + (end - start) / 2;
            invokeAll(
                    new MovingCircleRunnerFJP(start, mid, gridFile, runtime),
                    new MovingCircleRunnerFJP(mid, end, gridFile, runtime)

            );

        }

    }


}
