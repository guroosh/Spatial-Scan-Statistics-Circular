package TestingAlgo;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Bucket;
import Dataset.Events;
import Dataset.GridCell;
import Dataset.GridFile;
import edu.rice.hj.api.SuspendableException;

import java.io.*;
import java.util.*;

import static edu.rice.hj.Module0.finish;
import static edu.rice.hj.Module0.launchHabaneroApp;
import static edu.rice.hj.Module1.forasync;
import static edu.rice.hj.Module2.isolated;

/**
 * Created by Guroosh Chaudhary on 05-02-2017.
 */
public class Main {
    public static int naive_counter = 1;
    public static int counter1 = 0;
    public static double minLat = 360;
    public static double minLon = 360;
    public static double maxLat = -360;
    public static double maxLon = -360;
    public static double error_adjuster = 0.0000001;
    public static int bucket_number = 0;
    public static int bucket_size;
    public static String splitAxis = "horizontal";
    public static ArrayList<Circle> core_circles = new ArrayList<>();

    public static void main(String args[]) throws IOException, SuspendableException {
        Scanner in = new Scanner(System.in);
        String fileName = "d.csv";//in.nextLine();
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
//        printScaledData(gridFile);
        //data creation end
//        System.out.println("Total points with geo-encoding: " + events.size());
//        System.out.println("Points without geo-encoding (lost data): " + counter1);
//        System.out.println("Longitude: from " + minLon + " to " + maxLon);
//        System.out.println("Latitude: from " + minLat + " to " + maxLat);
//        System.out.println("Entering checking procedure:");
//        checkData(gridFile);
//        System.out.println();
//        System.out.println("Entering object serializing: ");
//        serialize(gridFile);

        System.out.println();
//        runNaiveTester(gridFile, events);
        runMovingCircleTester(gridFile, events);
        runMovingCircleTesterHJ(gridFile, events);
//            runNaiveTesterHJ(gridFile, events);
        System.out.println("Complete");

    }

    private static void runMovingCircleTesterHJ(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        int runtime = 100;
        Visualize vis = new Visualize();
        long start=System.currentTimeMillis();
        finish(() -> {
            forasync(0, runtime, (i) -> {
                movingCircleTester1(gridFile);

            });
        });
        long end=System.currentTimeMillis();
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);

        core_circles.add(c1);
        core_circles.add(c2);
        core_circles.add(c3);
        core_circles.add(c4);
        vis.drawCircles(events, core_circles);
        System.out.println("Complete");
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

            System.out.println(c.toString() + " LHR: " + c.lhr);

        }
        System.out.println("Amount of circles found : " + core_circles.size() + " " + count);
        System.out.println("Time for Habanero implementation :"+(end-start));

    }

    private static void runNaiveTester(GridFile gridFile, ArrayList<Events> events) {
        System.out.println("Entering circle scanning: ");
        System.out.println("Starting Naive run");
        long startTime = System.currentTimeMillis();
        naiveTester(gridFile);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time for naive with 1 thread: " + totalTime);
        afterNaive(events);
    }

    private static void runNaiveTesterHJ(GridFile gridFile, ArrayList<Events> events) throws SuspendableException {
        System.out.println("Entering circle scanning: ");
        System.out.println("Starting Naive run with HABANERO");
        long startTime = System.currentTimeMillis();
        launchHabaneroApp(() -> {
            naiveTesterHJ(gridFile);
        });
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Total time for naive with HJ: " + totalTime);
        afterNaive(events);
    }

    private static void runMovingCircleTester(GridFile gridFile, ArrayList<Events> events) {
        System.out.println("Starting Moving Circle run");
        int runtime = 100;

        Visualize vis = new Visualize();
        long start=System.currentTimeMillis();
        for (int i = 0; i < runtime; i++) {
            movingCircleTester(gridFile);

        }
        long end=System.currentTimeMillis();
        Circle c1 = new Circle(minLon, minLat, 0.0001);
        Circle c2 = new Circle(minLon, maxLat, 0.0001);
        Circle c3 = new Circle(maxLon, maxLat, 0.0001);
        Circle c4 = new Circle(maxLon, minLat, 0.0001);

        core_circles.add(c1);
        core_circles.add(c2);
        core_circles.add(c3);
        core_circles.add(c4);
        vis.drawCircles(events, core_circles);
        System.out.println("Complete");
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

            System.out.println(c.toString() + " LHR: " + c.lhr);

        }
        System.out.println("Amount of circles found : " + core_circles.size() + " " + count);
        System.out.println("Time for naive moving circle implementation:"+(end-start));
    }
    private static void movingCircleTester1(GridFile gridFile) {
        int moving_counter = 1;
        int circlecounter = 100;
        double curr_radius = 0.001;//1;

        double term_radius = 0.2, growth = 0.005;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);


        int count_limit = 1;
        while (count_limit-- > 0) {
            Circle curr_circle = new Circle("Random", curr_radius, area);//X: -80.9865 Y: 39.6339 r: 0.001(BAD Visualize)// X: -81.3279 Y: 39.6639 r: 0.001 (Good Case)
//            Circle curr_circle = new Circle(-81.2234, 39.7345, 0.001);   //-81.3773, 39.6293, .01)
            Circle next_circle;
            Circle temp_circle = new Circle();
            temp_circle.setRadius(curr_circle.getRadius());
            temp_circle.setX_coord(curr_circle.getX_coord());
            temp_circle.setY_coord(curr_circle.getY_coord());
            CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);

            double maxlikeli = 1;
            Circle fin_circle = null;
            while (controller.term(curr_circle) != 3) {


                next_circle = controller.checkanglepoints(curr_circle);

                ArrayList<Events> points = controller.scanCircle(curr_circle);
                ArrayList<Events> points1 = controller.scanCircle(next_circle);

                double curr_likeli = controller.likelihoodRatio(curr_circle, points);
                double next_likeli = controller.likelihoodRatio(next_circle, points1);

                if (curr_likeli < next_likeli) {
                    if (next_likeli > maxlikeli) {
                        maxlikeli = next_likeli;
                        fin_circle = new Circle(next_circle);
                    }
//                    core_circles.add(temp_circle);
                    curr_circle = new Circle(next_circle);
//                    System.out.println(moving_counter + "\t\t\tShifted circle: " + curr_circle.toString() + "\n\n");
                    moving_counter++;
                } else {
                    if (curr_likeli > maxlikeli) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);

                    }
                    curr_circle = controller.grow_radius(growth, curr_circle);
                    moving_counter++;
                }
                while (circlecounter <= 1) {

                    if (fin_circle != null) {
                        Circle finalFin_circle = fin_circle;
                        isolated(()->{
                            controller.removePoints(controller.scanCircle(finalFin_circle));
                            core_circles.add(finalFin_circle);
                        });
                        fin_circle.lhr = maxlikeli;
                    }
                    return;
                }
                circlecounter--;
            }
        }
    }

    private static void movingCircleTester(GridFile gridFile) {
        int moving_counter = 1;
        int circlecounter = 100;
        double curr_radius = 0.001;//1;

        double term_radius = 0.2, growth = 0.005;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);


        int count_limit = 1;
        while (count_limit-- > 0) {
            Circle curr_circle = new Circle("Random", curr_radius, area);//X: -80.9865 Y: 39.6339 r: 0.001(BAD Visualize)// X: -81.3279 Y: 39.6639 r: 0.001 (Good Case)
//            Circle curr_circle = new Circle(-81.2234, 39.7345, 0.001);   //-81.3773, 39.6293, .01)
            Circle next_circle;
            Circle temp_circle = new Circle();
            temp_circle.setRadius(curr_circle.getRadius());
            temp_circle.setX_coord(curr_circle.getX_coord());
            temp_circle.setY_coord(curr_circle.getY_coord());
            CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);

            double maxlikeli = 1;
            Circle fin_circle = null;
            while (controller.term(curr_circle) != 3) {


                next_circle = controller.checkanglepoints(curr_circle);

                ArrayList<Events> points = controller.scanCircle(curr_circle);
                ArrayList<Events> points1 = controller.scanCircle(next_circle);

                double curr_likeli = controller.likelihoodRatio(curr_circle, points);
                double next_likeli = controller.likelihoodRatio(next_circle, points1);

                if (curr_likeli < next_likeli) {
                    if (next_likeli > maxlikeli) {
                        maxlikeli = next_likeli;
                        fin_circle = new Circle(next_circle);
                    }
//                    core_circles.add(temp_circle);
                    curr_circle = new Circle(next_circle);
//                    System.out.println(moving_counter + "\t\t\tShifted circle: " + curr_circle.toString() + "\n\n");
                    moving_counter++;
                } else {
                    if (curr_likeli > maxlikeli) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);

                    }
                    curr_circle = controller.grow_radius(growth, curr_circle);
                    moving_counter++;
                }
                while (circlecounter <= 1) {

                    if (fin_circle != null) {

                        controller.removePoints(controller.scanCircle(fin_circle));
                        core_circles.add(fin_circle);

                        fin_circle.lhr = maxlikeli;
                    }
                    return;
                }
                circlecounter--;
            }
        }
    }

    private static void naiveTesterHJ(GridFile gridFile) throws SuspendableException {
        final double likelihood_threshold = 0;
        final double[] curr_radius = {0.001};
        final double initial_radius = curr_radius[0];
        final double term_radius = 0.01;
        final double shift_radius = 0.001;
        final double growth_radius = 0.001;
        int number_of_radius = 10;       //By formula: number  <=  ((t-c)/g) + 1

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        final Circle[] curr_circle = new Circle[1];
        CircleOps controller = new CircleOps(initial_radius, term_radius, area, gridFile);
//        System.out.println(curr_circle.toString());

        final long[] count_naive_circles = {0};
        ArrayList<Circle> top_likelihood_circles = new ArrayList<>();

        finish(() -> {
//            for (int i = 0; i < number_of_radius; i++) {
            forasync(0, number_of_radius - 1, (i) -> {
                int finalI = i;
//                    async(() -> {
                final double[] curr_local_radius = new double[1];
                final Circle[] curr_local_circle = new Circle[1];
                isolated(() -> {
                    curr_radius[0] = controller.increase_radius(curr_radius[0], growth_radius);
                    curr_local_radius[0] = curr_radius[0] - initial_radius;
                    curr_circle[0] = new Circle(minLon, minLat, curr_local_radius[0]);
                    curr_local_circle[0] = curr_circle[0];
                });
//                    System.out.println(curr_local_radius[0]);
//                    System.out.println(curr_local_circle[0].toString());
//                    System.out.println();
                while (controller.term(curr_local_circle[0]) != -1) {
                    while (controller.term(curr_local_circle[0]) == 0) {
                        isolated(() -> {
                            count_naive_circles[0]++;
                        });
//                            System.out.print(finalI);
//                            System.out.print(naive_counter + " : " + curr_circle.toString());
                        ArrayList<Events> points = controller.scanCircle(curr_local_circle[0]);
                        double likelihood_ratio = controller.likelihoodRatio(curr_local_circle[0], points);
                        if (likelihood_ratio > likelihood_threshold) {            //(likelihood_ratio > 2000) {
                            //Starting for visualization
                            Circle temp_circle = new Circle(curr_local_circle[0].getX_coord(), curr_local_circle[0].getY_coord(), curr_local_circle[0].getRadius());
                            temp_circle.lhr = likelihood_ratio;
                            //Ending for visualization
                            isolated(() -> {
                                top_likelihood_circles.add(temp_circle);
                            });
                        }
                        curr_local_circle[0] = controller.grow_x(shift_radius, curr_local_circle[0]);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                    }
                    curr_local_circle[0] = controller.shift(curr_local_circle[0], -1, shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
                }
                System.out.println("DONE for radius: " + curr_local_radius[0]);
//                    });
            });
//            }
        });
        System.out.println("SIZE A: " + top_likelihood_circles.size() + " " + count_naive_circles[0]);
        Collections.sort(top_likelihood_circles, Circle.sortByLHR());
        System.out.println("SIZE B");
        naiveWithoutIntersectingCircles(top_likelihood_circles, count_naive_circles[0]);
    }

    private static void naiveTester(GridFile gridFile) {
        // TODO: 21-03-2017 what if we do a circle area to a threshold ratio kind of thing
        //TEST case 1:      (Slow case)
        double likelihood_threshold = 0;
        double curr_radius = 0.001;
        double term_radius = 0.01;
        double shift_radius = 0.001;
        double growth_radius = 0.001;

        //TEST case 2:
//        double likelihood_threshold = 9000;
//        double curr_radius = 0.005;
//        double term_radius = 0.05;
//        double shift_radius = 0.005;
//        double growth_radius = .005;

        //TEST case 3:  (Good case)
//        double likelihood_threshold = 8000;
//        double curr_radius = 0.005;
//        double term_radius = 0.03;
//        double shift_radius = 0.005;
//        double growth_radius = .005;

        //TEST case 4:      (NaN why? because no circles)
//        double likelihood_threshold = 10000;
//        double curr_radius = 0.005;
//        double term_radius = 0.03;
//        double shift_radius = 0.005;
//        double growth_radius = .005;

        //TEST case 5:          Very small circles, AWESOME visualisation
//        double likelihood_threshold = 100;
//        double curr_radius = 0.001;
//        double term_radius = 0.01;
//        double shift_radius = 0.001;
//        double growth_radius = .001;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        Circle curr_circle = new Circle(minLon, minLat, curr_radius);
        //atomic
//        CircleOps controller = new CircleOps(curr_radius,term_radius);
//        curr_radius = curr_radius + growth;
        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);
//        System.out.println(curr_circle.toString());

        long count_naive_circles = 0;
        ArrayList<Circle> top_likelihood_circles = new ArrayList<>();

        while (controller.term(curr_circle) != 3) {
            // TODO: 21-03-2017 printing one loop less, why?
            // TODO: 21-03-2017 ANS: because 0.1 is stored as 0.1000000000001 sometimes
            while (controller.term(curr_circle) != -1) {
                while (controller.term(curr_circle) == 0) {
                    count_naive_circles++;
                    naive_counter++;
//                System.out.print(naive_counter + " : " + curr_circle.toString());
                    ArrayList<Events> points = controller.scanCircle(curr_circle);
                    double likelihood_ratio = controller.likelihoodRatio(curr_circle, points);
                    if (likelihood_ratio > likelihood_threshold) {            //(likelihood_ratio > 2000) {
                        //Starting for visualization
                        Circle temp_circle = new Circle();
                        temp_circle.setRadius(curr_circle.getRadius());
                        temp_circle.setX_coord(curr_circle.getX_coord());
                        temp_circle.setY_coord(curr_circle.getY_coord());
                        temp_circle.lhr = likelihood_ratio;
//                        circles.add(temp_circle);
                        //Ending for visualization
                        top_likelihood_circles.add(temp_circle);
                    }
                    curr_circle = controller.grow_x(shift_radius, curr_circle);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                }
                curr_circle = controller.shift(curr_circle, -1, shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
            }
            System.out.println("DONE for radius: " + curr_radius);
            curr_radius = controller.increase_radius(curr_radius, growth_radius);
            curr_circle = new Circle(minLon, minLat, curr_radius);

            // Code segment for showing top n circles of each radius
//            Collections.sort(top_likelihood_circles, Circle.sortByLHR());
//            for (int i = 0; i < 10; i++) {
////                System.out.println(c.lhr + " ,   circle: " + c.toString());
//                Circle c = top_likelihood_circles.get(i);
//                controller.removePoints(controller.scanCircle(c));
//                core_circles.add(c);
//            }
//            top_likelihood_circles = new ArrayList<>();
            //ending code segment
        }

        System.out.println("SIZE A: " + top_likelihood_circles.size() + " " + count_naive_circles);
        Collections.sort(top_likelihood_circles, Circle.sortByLHR());
        System.out.println("SIZE B");
        naiveWithoutIntersectingCircles(top_likelihood_circles, count_naive_circles);
    }


    private static void naiveWithoutIntersectingCircles(ArrayList<Circle> top_likelihood_circles, long count_naive_circles) {
        int j = 0;
        int intersecting_flg = 0;
//        Circle prev_cl = new Circle();
        int top_number_circles = 1000;
        for (int i = 0; j < top_number_circles; i++) {
            if (i >= top_likelihood_circles.size()) {
                break;
            }
            Circle cl = top_likelihood_circles.get(i);
            if (i == 0) {
                System.out.println(cl.lhr + " ,   circle: " + cl.toString());
                core_circles.add(cl);
                j++;
            }
            for (Circle core_circle : core_circles) {
                if (intersectingOnlyOne(core_circle, cl)) {
                    intersecting_flg++;
                }
            }
            if (intersecting_flg != 0) {
                // TODO: 25-03-2017 implement this if for including circles having good LHRatio and intersecting multiple core circles
//                if (intersecting_flg != 1) {
//                    System.out.println(cl.lhr + " ,   circle: " + cl.toString());
//                    core_circles.add(cl);
//                    j++;
//                }
                intersecting_flg = 0;
            } else {
                System.out.println(cl.lhr + " ,   circle: " + cl.toString());
                core_circles.add(cl);
                j++;
            }
//            prev_cl = cl;
        }
        System.out.println("Total circles: " + count_naive_circles);
    }

    private static boolean intersectingOnlyOne(Circle c1, Circle c2) {
        double x1 = c1.getX_coord(), x2 = c2.getX_coord(), y1 = c1.getY_coord(), y2 = c2.getY_coord();
        double dist = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        //if intersecting once return true, except if more than 50% of smaller circle is in larger circle
        //to remove exception divide larger radius by 2 in the first 2 ifs when comparing with distances
        if (c1.getRadius() > c2.getRadius() && dist < c1.getRadius() / 2) {
            return true;           //returns if c1 overlapps c2
        }
        if (c1.getRadius() < c2.getRadius() && dist < c2.getRadius() / 2) {
            return true;           //returns if c2 overlapps c1
        }
        if (dist > (c1.getRadius() + c2.getRadius())) {
            return false;           //returns if c1 and c2 are seperate
        }
        return true;                //returns if c1 and c2 are intersecting
    }

    private static void serialize(GridFile gridFile) {

        FileOutputStream fout = null;
        ObjectOutputStream oos = null;

        try {
            fout = new FileOutputStream("gridFile.ser");
            oos = new ObjectOutputStream(fout);
//            oos.writeObject(gridFile);
            oos.close();
            fout.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void checkData(GridFile gridFile) {
        //checking all values
        int counter = 0;
        System.out.println("Number of grids: " + gridFile.gridCellObject.size());
        System.out.println("Number of mappings:" + gridFile.mapper.size());
        System.out.println("Number of lat:" + gridFile.latScale.size());
        System.out.println("Number of lon:" + gridFile.lonScale.size());
        HashSet<Bucket> bucketSet = new HashSet<>();
        HashSet<Events> eventSet = new HashSet<>();
        for (HashMap.Entry<String, GridCell> gcO : gridFile.gridCellObject.entrySet()) {
            Bucket b = gridFile.mapper.get(gcO.getValue());
            for (Events e : b.eventsInBucket) {
                eventSet.add(e);
            }
            bucketSet.add(b);
        }
        for (Bucket b : bucketSet) {
            counter += b.eventsInBucket.size();
        }
        System.out.println("Total points (check 1): " + counter);
        System.out.println("Total points (check 2): " + eventSet.size());
//        Events subject = new Events();
//        int s_counter=0, loop_counter=0;
//        for(Events e : test1)
//        {
//            loop_counter++;
//            if(!eventSet.contains(e))
//            {
//                s_counter++;
//                subject = e;
//                break;
//            }
//        }
//        if(s_counter!=1)
//        {
//            System.out.println("Multiple subjects: WARNING!!!");
//        }
//        else
//            System.out.println(loop_counter+"SUBJECT: "+subject.lat+" "+subject.lon);
//        System.out.println();
    }

    private static void afterNaive(ArrayList<Events> events) {
        VisualizeNaive vis = new VisualizeNaive();
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

    private static void printScaledData(GridFile gridFile) {
        double lat, lon;
        double minLat = 360;
        double minLon = 360;
        double maxLat = -360;
        double maxLon = -360;
        for (HashMap.Entry<GridCell, Bucket> entry : gridFile.mapper.entrySet()) {
            Bucket b = entry.getValue();
            for (Events e : b.eventsInBucket) {
                System.out.println(e.toString());
                lat = e.getLat();
                lon = e.getLon() * (111.320 / 110.574) * Math.cos(e.getLat());
                e.setLat(lat);
                e.setLon(lon);
                System.out.println(e.toString());
                // TODO: 03-03-2017 fix this
                if (lon > maxLon) {
                    maxLon = lon;
                } else if (lon < minLon) {
                    minLon = lon;
                }
                if (lat > maxLat) {
                    maxLat = lat;
                } else if (lat < minLat) {
                    minLat = lat;
                }
            }
        }
    }

    private static ArrayList<Events> readDataFile(String fileName) throws IOException {
//        int latInCSV = 19;//13;
//        int lonInCSV = 20;//12;
        double lat, lon;
        int latInCSV = 13;
        int lonInCSV = 12;
        String line;
        String lines[];
        ArrayList<Events> events = new ArrayList<>();
        InputStream is = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        boolean isHeader = true;
//        int stopper = 0;

        while ((line = br.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
//            if(stopper==100000)
//            {
//                break;
//            }
//            stopper++;
            line = modifyLine(line);
            lines = line.split(",");
            try {
                double longitude, latitude;
                longitude = Double.parseDouble(lines[lonInCSV]);
                latitude = Double.parseDouble(lines[latInCSV]);
                Events position = new Events();
                position.setLat(latitude);
                position.setLon(longitude);
//                System.out.print(position.toString()+"      ");

                //starting conversion from degrees to length
                lat = position.getLat();
                lon = position.getLon() * (111.320 / 110.574) * Math.cos(Math.toRadians(position.getLat()));
                if (lon > maxLon) {
                    maxLon = lon;
                } else if (lon < minLon) {
                    minLon = lon;
                }
                if (lat > maxLat) {
                    maxLat = lat;
                } else if (lat < minLat) {
                    minLat = lat;
                }
                position.setLat(lat);
                position.setLon(lon);
//                System.out.println(position.toString());
                //ending conversion from degrees to length

                events.add(position);
            } catch (ArrayIndexOutOfBoundsException e) {
                counter1++;
            }
        }
        return events;
    }

    private static String modifyLine(String line) {
        int counter = 0;
        StringBuilder newString = new StringBuilder(line);
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (counter == 0)
                    counter++;
                else
                    counter--;
            }
            if (ch == ',') {
                if (counter == 1) {
                    newString.setCharAt(i, '-');
                }
            }
        }

        String ans = newString.toString();
//        ans = ans.replace("-","\"\"");
        return ans;
    }

}