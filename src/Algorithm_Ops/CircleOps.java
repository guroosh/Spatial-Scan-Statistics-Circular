package Algorithm_Ops;

import Dataset.*;
import TestingAlgo.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by LakshayD on 2/8/2017.
 */
//Has functions related to the Circles used by algorithm
public class CircleOps {
    private double term_radius;
    private double start_radius;
    private ScanGeometry area;
    private GridFile gridFile;
//Sets the operator to an initial and final radius and a bound area.Also passes a gridfile built from the dataset to the operator
    public CircleOps(double c_radius, double e_radius, ScanGeometry arr, GridFile gridFile) {
        this.term_radius = e_radius;
        this.start_radius = c_radius;
        this.area = arr;
        this.gridFile = gridFile;

    }
//Initialize a circle with a given x,y coordinate and start radius
    public Circle init(double x, double y) {
        Circle circle = new Circle();
        circle.setX_coord(x);
        circle.setY_coord(y);
        circle.setRadius(start_radius);
        return circle;
    }
//Grows the radius of the given circle by a given amount
    public Circle grow_radius(double shift, Circle circle) {
        circle.setRadius(circle.getRadius() + shift);
        return circle;

    }
//Shifts the x coordinate of the given circle by a given amount
    public Circle grow_x(double shift, Circle circle) {
        circle.setX_coord(circle.getX_coord() + shift);
        return circle;

    }
//Shifts the y coordinate of the given circle by a given amount
    public Circle grow_y(double shift, Circle circle) {
        circle.setY_coord(circle.getY_coord() + shift);
        return circle;

    }
/*Check whether the circle provided is a valid circle. -1 is returned when the circle isn't valid(null),
2 when the circle is out of y
1 when the circle is out of x bound
3 when radius of the circle is more than the terminating radius
0 when all is fine
*/
public int term(Circle circle) {
        if (circle == null)
            return -1;
        else if (circle.getY_coord() > area.end_Y || circle.getY_coord() < area.start_Y)
            return 2;
        else if (circle.getX_coord() > area.end_X || circle.getX_coord() < area.start_X)
            return 1;
        else if (circle.getRadius() - Main.error_adjuster > term_radius)
            return 3;
        else
            return 0;

    }

//Shift the given circle's x and y by given amount
    public Circle grow_xy(double growth, Circle circle) {
        circle = grow_x(growth, circle);
        circle = grow_y(growth, circle);
        return circle;
    }
/*Shifts y coordinate by given value and x coordinate to start of the area if x co-ordinate crosses given area.
 Sends termination command if circle crosses y bound too*/
    public Circle shifty_startx(Circle circle,  double shift_y) {

        int chk_bound = term(circle);
        if (chk_bound == 1) {
            circle.setY_coord(circle.getY_coord() + shift_y);
            circle.setX_coord(area.start_X);
        }
        chk_bound = term(circle);
        if (chk_bound == 2) {
            return null;
        }

        return circle;
    }
// Returns likelihood ratio of given circle given points in the circle
    public double likelihoodRatio(Circle circle, Events[] points) {
        return this.likelihoodRatio(circle, new ArrayList<>(Arrays.asList(points)));
    }
// Auxiliary function for calculating likelihood ratio
    public double likelihoodRatio(Circle circle, ArrayList<Events> points) {
        double circle_area = Math.PI * circle.getRadius() * circle.getRadius() * 1000;
        double total_area = (this.area.end_X - this.area.start_X) * (this.area.end_Y - this.area.start_Y) * 1000;
        int total_points = gridFile.total_events;
        int circle_points = points.size();
        if (circle_points == 0) {

            return 0;
        }
        double baseline = (total_points * circle_area) / total_area;
        int indicator = circle_points > baseline ? 1 : 0;
        if (indicator == 0) {
            return 0;
        }
        double k1 = circle_points / baseline;
        double k2 = circle_points;
        double k3 = (total_points - circle_points) / (total_points - baseline);
        double k4 = total_points - circle_points;

        double p1 = Math.pow((circle_points / baseline), circle_points);
        double p2 = Math.pow(((total_points - circle_points) / (total_points - baseline)), total_points - circle_points);
        double numerator = (k2 * Math.log(k1)) + (k4 * Math.log(k3));
        return numerator;
    }


// Auxiliary function that given a circle, scans the point in it and returns a circle centered in the quadrant with the highest density of points
// and starting radius
    public Circle checkquadpoints(Circle curr_circle) {
        ArrayList<Events> points = scanCircle(curr_circle);
        int quadreg = 0, posi_lat = 0, posi_lon = 0;
        double x = curr_circle.getX_coord(), y = curr_circle.getY_coord(), r = curr_circle.getRadius();
        Circle new_circle = new Circle();
        quadreg = getquad(points, curr_circle.getX_coord(), curr_circle.getY_coord());


        switch (quadreg) {//anticlockwise from 2' o clock on graph
            case 1: {
                x = x + r / 2;
                y = y + r / 2;
                r = start_radius;
                new_circle.setX_coord(x);
                new_circle.setY_coord(y);
                new_circle.setRadius(r);
                break;
            }
            case 2: {
                x = x - r / 2;
                y = y + r / 2;
                r = start_radius;
                new_circle.setX_coord(x);
                new_circle.setY_coord(y);
                new_circle.setRadius(r);
                break;
            }
            case 3: {
                x = x - r / 2;
                y = y - r / 2;
                r = start_radius;
                new_circle.setX_coord(x);
                new_circle.setY_coord(y);
                new_circle.setRadius(r);
                break;
            }
            case 4: {
                x = x + r / 2;
                y = y - r / 2;
                r = start_radius;
                new_circle.setX_coord(x);
                new_circle.setY_coord(y);
                new_circle.setRadius(r);
                break;
            }
            default:
                return curr_circle;

        }
        r = curr_circle.getRadius() / 2;
        new_circle.setRadius(r);
        return new_circle;
    }
// Auxiliary function to check mean of points
    public Circle checkanglepoints(Circle curr_circle, Events curr_points[]) {
        return checkanglepoints(curr_circle, new ArrayList<>(Arrays.asList(curr_points)));
    }
// Function finds out density in each given quad and returns a circle at the mean of the most dense quad with radius equal to start radius
    public Circle checkanglepoints(Circle curr_circle, ArrayList<Events> curr_points) {

        ArrayList<Events> quadpoints = new ArrayList<>();
        int quadreg = 0;
        Circle new_circle = new Circle();
        double x = curr_circle.getX_coord(), y = curr_circle.getY_coord();
        quadreg = getquad(curr_points, x, y);

        if (quadreg == 1) {
            for (Events e : curr_points
                    ) {
                if (e.getLat() > y && e.getLon() > x) {
                    quadpoints.add(e);
                }

            }
        }
        if (quadreg == 2) {
            for (Events e : curr_points
                    ) {
                if (e.getLat() > y && e.getLon() < x) {
                    quadpoints.add(e);
                }

            }
        }
        if (quadreg == 3) {
            for (Events e : curr_points
                    ) {
                if (e.getLat() < y && e.getLon() < x) {
                    quadpoints.add(e);
                }

            }
        }
        if (quadreg == 4) {
            for (Events e : curr_points
                    ) {
                if (e.getLat() < y && e.getLon() > x) {
                    quadpoints.add(e);
                }

            }
        }
        double x1 = 0, y1 = 0;
        for (Events point : quadpoints
                ) {
            x1 += point.getLon();
            y1 += point.getLat();
        }
        x1 = x1 / quadpoints.size();
        y1 = y1 / quadpoints.size();
        new_circle.setX_coord(x1);
        new_circle.setY_coord(y1);

        new_circle.setRadius(start_radius);

        return new_circle;

    }
//    Reset the points that are marked
    public static void resetPointsVisibility(GridFile gridFile) {
        HashSet<Bucket> bucketSet = new HashSet<>();
        HashSet<Events> eventSet = new HashSet<>();
        for (HashMap.Entry<String, GridCell> gcO : gridFile.gridCellObject.entrySet()) {
            Bucket b = gridFile.mapper.get(gcO.getValue());
            eventSet.addAll(b.eventsInBucket);
            bucketSet.add(b);
        }
        for (Events events : eventSet) {
            events.marked = false;
        }
    }
// Function checks whether two circles are intersecting (note not touching each other)
    public static boolean checkintersection(Circle c1, Circle c2) {

        double x1 = c1.getX_coord(), x2 = c2.getX_coord(), y1 = c1.getY_coord(), y2 = c2.getY_coord();
        double dist = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        if (c1.getRadius() > c2.getRadius() && dist < c1.getRadius() - c2.getRadius()) {     // / 2) {
            return true;
        }
        if (c1.getRadius() < c2.getRadius() && dist < c2.getRadius() - c1.getRadius()) {     // / 2) {
            return true;
        }
        return !(dist > (c1.getRadius() + c2.getRadius()));
    }
//    Function to check whether two circles are equal or not
    public boolean equals(Circle c1, Circle c2) {
        return ((c1.getX_coord() == c2.getX_coord()) && (c1.getY_coord() == c2.getY_coord()) && (c1.getRadius() == c2.getRadius()));
    }
// Auxiliary function to return most dense quad of the circle
    private int getquad(ArrayList<Events> points, double x, double y) {
        int quadreg = 0;
        int[] quad = new int[5];
        for (int i = 0; i < 5; i++) {
            quad[i] = 0;
        }
        for (Events e : points
                ) {
            if (e.getLon() > x && e.getLat() > y)
                quad[1]++;
            if (e.getLon() > x && e.getLat() < y)
                quad[4]++;
            if (e.getLon() < x && e.getLat() > y)
                quad[2]++;
            if (e.getLon() < x && e.getLat() < y)
                quad[3]++;
        }
        int max = 0;
        for (int i = 1; i < 5; i++) {
            if (max < quad[i]) {
                max = quad[i];
                quadreg = i;
            }

        }
        return quadreg;
    }
// Gives points new in the circle when grown by growth amount
    public ArrayList<Events> difference(Circle c_inner, double growth) {
        Circle c_outer = new Circle(c_inner.getX_coord(), c_inner.getY_coord(), c_inner.getRadius() + growth);
        ArrayList<Events> answer = scanCircle(c_outer);
        ArrayList<Events> answer1 = new ArrayList<>();
        for (Events e : answer) {
            double x1 = e.getLon() - c_inner.getX_coord();
            double y1 = e.getLat() - c_inner.getY_coord();
            double dist = Math.sqrt((x1 * x1) + (y1 * y1));
            if (dist > c_inner.getRadius())
                answer1.add(e);
        }
        return answer1;
    }
// Marks a list of points i.e points that shouldn't be considered again
    public void removePoints(ArrayList<Events> points) {
        for (Events e : points) {
            e.marked = true;
        }
    }
// Returns points found in the given circle
    public ArrayList<Events> scanCircle(Circle circle) {
        double lon = circle.getX_coord();
        double lat = circle.getY_coord();
        double radius = circle.getRadius();
        ArrayList<Events> answer = new ArrayList<>();
        double lat_min = lat - radius, lat_max = lat + radius, lon_min = lon - radius, lon_max = lon + radius;
      int lat_min_id, lat_max_id, lon_min_id, lon_max_id;
        Range r_lat;
        Range r_lon;
        r_lat = search(lat_min, lat_max, gridFile.latScale);
        r_lon = search(lon_min, lon_max, gridFile.lonScale);
        lat_min_id = r_lat.min;
        lat_max_id = r_lat.max;
        lon_min_id = r_lon.min;
        lon_max_id = r_lon.max;
        if (lat_min_id == -1)
            lat_min_id = 0;
        if (lon_min_id == -1)
            lon_min_id = 0;
        if (lat_max_id == -2)
            lat_max_id = gridFile.latScale.size() - 1;
        if (lon_max_id == -2)
            lon_max_id = gridFile.lonScale.size() - 1;
        int total_buckets_loaded = 0;
        for (int i = lat_min_id; i < lat_max_id; i++) {
            for (int j = lon_min_id; j < lon_max_id; j++) {
                total_buckets_loaded++;
                String hashValue = gridFile.latScale.get(i) + "_"
                        + gridFile.latScale.get(i + 1) + "_"
                        + gridFile.lonScale.get(j) + "_"
                        + gridFile.lonScale.get(j + 1);
                GridCell gridCell = gridFile.gridCellObject.get(hashValue);
                Bucket bucket = gridFile.mapper.get(gridCell);
                for (Events e : bucket.eventsInBucket) {
                    if (!e.marked) {
                        answer.add(e);

                    }
                }
            }
        }

        ArrayList<Events> answer1 = new ArrayList<>();
        for (Events e : answer) {
            double x1 = e.getLon() - lon;
            double y1 = e.getLat() - lat;
            double dist = Math.sqrt((x1 * x1) + (y1 * y1));
            if (dist <= radius)
                answer1.add(e);
        }

        return answer1;
    }
// Returns a min and max for scaling
    private static Range search(double min, double max, SortedLinkedList<Double> scale) {
        Range range = new Range();
        range.max = scale.size() - 1;
        range.min = 0;
        boolean found_min = false;
        for (int i = 0; i < scale.size(); i++) {
            if (min <= scale.get(i) && !found_min) {
                found_min = true;
                if (min < scale.get(i)) {
                    range.min = i - 1;
                } else
                    range.max = i;
            }
            if (max <= scale.get(i) && found_min) {
                range.max = i;
                break;
            }
        }
        return range;
    }
//Increases radius of a circle by given amount
    public double increase_radius(double radius, double growth) {
        return radius + growth;
    }
}

class Range {
    int min, max;
}