package Moving_Circle;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridFile;
import TestingAlgo.Main;
import Visualize.Visualize;
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

/**
 * Created by guroosh on 8/4/17.
 */
public class MovingCircle {
    static int runtime = 100;

    public static void runMovingCircleTesterJOMP(GridFile gridFile, ArrayList<Events> events) throws Exception {
        System.out.println("Starting Moving Circle run with JOMP");
        int runtime = 100;
        long start = System.currentTimeMillis();
        JOMP.movingCircleJOMP movingCircleJOMP = new JOMP.movingCircleJOMP();
        Circle[] core_circles_array = movingCircleJOMP.movingCircleTesterJOMP(runtime, gridFile, minLon, minLat, maxLon, maxLat);
        long end = System.currentTimeMillis();
        System.out.println("Time :" + ((double) (end - start)) / 1000 + "s");

        for (int i = 0; i < runtime; i++) {
            Circle temp_circle = core_circles_array[i];
            if (temp_circle == null) {
                continue;
            }
            core_circles.add(temp_circle);
        }
        aftermovingcircal(gridFile, events);
    }

    public static void runMovingCircleTesterJvFP(GridFile gridFile, ArrayList<Events> events) {
        System.out.println("Starting Moving Circle run with Java-Fork join pool");
        int runtime = 100;
        long start = System.currentTimeMillis();
        int threshold = runtime / Runtime.getRuntime().availableProcessors();
        MovingCircleRunnerFJP rootTask = new MovingCircleRunnerFJP(0, runtime, gridFile, threshold);
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(rootTask);
        long end = System.currentTimeMillis();
        System.out.println("Time :" + ((double) (end - start)) / 1000 + "s");
        aftermovingcircal(gridFile, events);

    }

    public static void runMovingCircleTesterHJ(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        System.out.println("Starting Moving Circle run with Habanero-Java");
        int runtime = 100;
        long start = System.currentTimeMillis();
        finish(() -> {
            forasync(0, runtime, (i) -> {
                movingCircleTesterHJ(gridFile);
            });
        });
        long end = System.currentTimeMillis();
        System.out.println("Time :" + ((double) (end - start)) / 1000 + "s");
        aftermovingcircal(gridFile, events);
    }

    public static void runMovingCircleTester(GridFile gridFile, ArrayList<Events> events) {
        System.out.println("Starting Moving Circle run with single thread");


        long start = System.currentTimeMillis();

        for (int i = 0; i < runtime; i++) {
            movingCircleTester(gridFile);
        }
        long end = System.currentTimeMillis();
        System.out.println("Time :" + ((double) (end - start)) / 1000 + "s");
        aftermovingcircal(gridFile, events);
    }

    private static void movingCircleTesterHJ(GridFile gridFile) {
        int circlecounter = 100;
        double curr_radius = 0.001;
        double term_radius = 0.2;
        double growth = 0.003;
        double upper_limit = 0.01;
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
                        controller.removePoints(controller.scanCircle(finalFin_circle));
                        core_circles.add(finalFin_circle);
                    });
                }
                return;
            }
            circlecounter--;
        }
    }

    private static void movingCircleTesterjvhp(GridFile gridFile) {
        double curr_radius = 0.001;
        double term_radius = 0.2;
        double growth = 0.003;
        double upper_limit = 0.01;
        int circlecounter = 100;

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
                        controller.removePoints(controller.scanCircle(finalFin_circle));
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
        int circlecounter = 100;
        double curr_radius = 0.001;
        double term_radius = 0.2;
        double growth = 0.003;
        double upper_limit = 0.01;
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
                    fin_circle.lhr = maxlikeli;
                    core_circles.add(fin_circle);
                    controller.removePoints(controller.scanCircle(fin_circle));
                }
                return;
            }
            circlecounter--;
        }
    }

    private static void aftermovingcircal(GridFile gridFile, ArrayList<Events> events) {
        visualizedata(events);
        Collections.sort(core_circles, Circle.sortByLHR());
        Circle a = new Circle(core_circles.get(0));
        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        CircleOps controller = new CircleOps(1, 2, area, gridFile);
        int count = 1;
        for (Circle c : core_circles) {
            if (!controller.equals(a, c)) {
                count++;
                a = new Circle(c);
            }
        }
        int number = 10;
        drawtop(number);
        System.out.println("\tAmount of circles found : " + core_circles.size() + " Unique : " + count + "\n");
        Main.list2.addAll(core_circles);
        core_circles = new ArrayList<>();
        CircleOps.resetPointsVisibility(gridFile);
    }

    private static void drawtop(int number) {
        if (number == 0)
            number = core_circles.size();
        for (int i = 0; i < number; i++) {
            System.out.println("\t" + core_circles.get(i).toString());
        }

    }

    private static void visualizedata(ArrayList<Events> events) {
        Visualize vis = new Visualize();
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);

        core_circles.add(c1);
        core_circles.add(c2);
        core_circles.add(c3);
        core_circles.add(c4);
        vis.drawCircles(events, core_circles);
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
                movingCircleTesterjvhp(gridFile);
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
