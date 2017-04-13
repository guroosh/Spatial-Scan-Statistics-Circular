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
public class CircleOps {
    private double term_radius;
    private double start_radius;
    private ScanGeometry area;
    private GridFile gridFile;

    public CircleOps(double c_radius, double e_radius, ScanGeometry arr, GridFile gridFile) {
        this.term_radius = e_radius;
        this.start_radius = c_radius;
        this.area = arr;
        this.gridFile = gridFile;

    }

    public Circle init(double x, double y) {
        Circle circle = new Circle();
        circle.setX_coord(x);
        circle.setY_coord(y);
        circle.setRadius(start_radius);
        return circle;
    }

    public Circle grow_radius(double shift, Circle circle) {
        circle.setRadius(circle.getRadius() + shift);
        return circle;

    }

    public Circle grow_x(double shift, Circle circle) {
        circle.setX_coord(circle.getX_coord() + shift);
        return circle;

    }

    public Circle grow_y(double shift, Circle circle) {
        circle.setY_coord(circle.getY_coord() + shift);
        return circle;

    }

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
//        return -1 if circle doesnt exist;
//        return 1 if x is out of bounds;
//        return 2 if y is out of bounds;
//        return 3 if radius is out of bounds;
//        return 0 if all ok;
    }


    public Circle grow_xy(double growth, Circle circle) {
        circle = grow_x(growth, circle);
        circle = grow_y(growth, circle);
        return circle;
    }

    public Circle shift(Circle circle, double shift_x, double shift_y) {
//        circle.setRadius(curr_radius);
//        circle.setX_coord(circle.getX_coord() + shift_x);
        int chk_bound = term(circle);
        if (chk_bound == 1) {
            circle.setY_coord(circle.getY_coord() + shift_y);
//            put in scan geometry
            circle.setX_coord(area.start_X);
        }
        chk_bound = term(circle);
        if (chk_bound == 2) {
            return null;
        }

        return circle;
    }

    public double likelihoodRatio(Circle circle, Events[] points) {
        return this.likelihoodRatio(circle, new ArrayList<>(Arrays.asList(points)));
    }

    public double likelihoodRatio(Circle circle, ArrayList<Events> points) {
        double circle_area = Math.PI * circle.getRadius() * circle.getRadius() * 1000;
        double total_area = (this.area.end_X - this.area.start_X) * (this.area.end_Y - this.area.start_Y) * 1000;
        int total_points = gridFile.total_events;
        int circle_points = points.size();
        if (circle_points == 0) {
//            System.out.println("Indicator: " + indicator);
//            System.out.println("Answer: 0 (0 points)");
            return 0;
        }
        double baseline = (total_points * circle_area) / total_area;
        int indicator = circle_points > baseline ? 1 : 0;
        if (indicator == 0) {
//            System.out.println("Circle_area: " + circle_area);
//            System.out.println("Total_area: " + total_area);
//            System.out.println("Circle_points: " + circle_points);
//            System.out.println("Total_points: " + total_points);
//            System.out.println("Baseline: " + baseline);
//            System.out.println("Indicator: " + indicator);
//            System.out.println("Answer: 0 (less than baseline)");
            return 0;
        }
        double k1 = circle_points / baseline;
        double k2 = circle_points;
        double k3 = (total_points - circle_points) / (total_points - baseline);
        double k4 = total_points - circle_points;

        double p1 = Math.pow((circle_points / baseline), circle_points);
        double p2 = Math.pow(((total_points - circle_points) / (total_points - baseline)), total_points - circle_points);
//        System.out.println("Circle_area: " + circle_area);
//        System.out.println("Total_area: " + total_area);
//        System.out.println("Circle_points: " + circle_points);
//        System.out.println("Total_points: " + total_points);
//        System.out.println("Baseline: " + baseline);
//        System.out.println("Indicator: " + indicator);
        double numerator = (k2 * Math.log(k1)) + (k4 * Math.log(k3));
//        System.out.println("Answer: " + answer + "   " +k2+ " log(" + k1 + ") + " + k4+ " log(" + k3 + ")");
        return numerator;
    }
//    public double likelihoodRatio(ScanGeometry.Circle circle, ArrayList<Events> points) {
//        double L0, L1, lambda;
//        double ug, uz, ng, nz;
//        double total_area, circle_area;
//        circle_area = Math.PI * circle.getRadius() * circle.getRadius();
//        total_area = (this.area.end_X - this.area.start_X) * (this.area.end_Y - this.area.start_Y);
//
//        ng = gridFile.total_events;
//        nz = points.size();
//        if (nz == 0)
//            return 0;
//        ug = ng;
//        uz = (ug * circle_area) / total_area;
//        System.out.println(circle_area);
//        System.out.println(total_area);
//        System.out.println();
//        System.out.println(ng);
//        System.out.println(nz);
//        System.out.println(ug);
//        System.out.println(uz);
//        double a, b, c, d, e, f, g;
//        d = uz - nz;
//        e = ug - ng;
//        f = ng - nz;
//        a = nz / uz;
//        b = f / (ug-uz);
//        c = ng / ug;
//        g = e - d;
//        System.out.println();
//        System.out.println(a);
//        System.out.println(b);
//        System.out.println(c);
//        System.out.println(d);
//        System.out.println(e);
//        System.out.println(f);
//        System.out.println(g);
//        double p1, p2, p3, p4, p5, p6;
//        p1 = Math.pow(a, nz);
//        p2 = Math.pow((1 - a), (d));
//        p3 = Math.pow(b, f);
//        p4 = Math.pow(1 - b, g);
//        p5 = Math.pow(c, ng);
//        p6 = Math.pow((1 - c), e);
//        System.out.println();
//        System.out.println(p1);
//        System.out.println(p2);
//        System.out.println(p3);
//        System.out.println(p4);
//        System.out.println(p5);
//        System.out.println(p6);
//        L1 = p1 * p2 * p3 * p4;
//        L0 = p5 * p6;
//        System.out.println();
//        System.out.println(L1);
//        System.out.println(L0);
//        lambda = L1 / L0;
//        return lambda;
//    }

    // TODO: 2/9/2017 Complete implementation
    //this needs to return a circle encompassing the circle surrounding new points
    //For now returns center of quad where points are found


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

    public static boolean checkintersection(Circle c1, Circle c2) {
        //circles just touching are considered as not intersecting
        double x1 = c1.getX_coord(), x2 = c2.getX_coord(), y1 = c1.getY_coord(), y2 = c2.getY_coord();
        double dist = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        if (c1.getRadius() > c2.getRadius() && dist < c1.getRadius() - c2.getRadius()) {     // / 2) {
            return true;           //returns if c1 overlaps c2
        }
        if (c1.getRadius() < c2.getRadius() && dist < c2.getRadius() - c1.getRadius()) {     // / 2) {
            return true;           //returns if c2 overlaps c1
        }
        if (dist > (c1.getRadius() + c2.getRadius())) {
            return false;           //return if c1 and c2 are separate
        }
        return true;                //returns if intersecting circles
    }

    public boolean equals(Circle c1, Circle c2) {
        return ((c1.getX_coord() == c2.getX_coord()) && (c1.getY_coord() == c2.getY_coord()) && (c1.getRadius() == c2.getRadius()));
    }

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
//        System.out.println("INNER: " + scanCircle(c_inner).size() + ", OUTER: " + answer.size() + ", DIFFERENCE: " + answer1.size());
        return answer1;
    }

    public void removePoints(ArrayList<Events> points) {
        for (Events e : points) {
            e.marked = true;
        }
    }

    public ArrayList<Events> getDifferencePoint(Circle c_inner, double growth) {
        Circle c_outer = new Circle(c_inner.getX_coord(), c_inner.getY_coord(), c_inner.getRadius() + growth);
        double lat, lon, r1, r2;
        lat = c_inner.getY_coord();
        lon = c_inner.getX_coord();
        r1 = c_inner.getRadius();
        r2 = c_outer.getRadius();

        double lat1 = c_outer.getY_coord() - c_outer.getRadius();
        double lat2 = c_inner.getY_coord() - (c_inner.getRadius() / Math.sqrt(2));
        double lat3 = c_inner.getY_coord() + (c_inner.getRadius() / Math.sqrt(2));
        double lat4 = c_outer.getY_coord() + c_outer.getRadius();

        double lon1 = c_outer.getX_coord() - c_outer.getRadius();
        double lon2 = c_inner.getX_coord() - (c_inner.getRadius() / Math.sqrt(2));
        double lon3 = c_inner.getX_coord() + (c_inner.getRadius() / Math.sqrt(2));
        double lon4 = c_outer.getX_coord() + c_outer.getRadius();

        ArrayList<Events> answer = new ArrayList<>();
        System.out.print(answer.size() + "   ");
        answer.addAll(getDifferencePointPart(lat1, lat3 - 1, lon1, lon2 + 1, "OIOI"));
        System.out.print(answer.size() + "   ");
        answer.addAll(getDifferencePointPart(lat3 - 1, lat4, lon1, lon3 - 1, "IOOI"));
        System.out.print(answer.size() + "   ");
        answer.addAll(getDifferencePointPart(lat1, lat2 + 1, lon2 + 1, lon4, "OIIO"));
        System.out.print(answer.size() + "   ");
        answer.addAll(getDifferencePointPart(lat2 + 1, lat4, lon3 - 1, lon4, "IOIO"));
        System.out.print(answer.size() + "   ");


        ArrayList<Events> answer1 = new ArrayList<>();
        for (Events e : answer) {
            double x1 = e.getLon() - lon;
            double y1 = e.getLat() - lat;
            double dist = Math.sqrt((x1 * x1) + (y1 * y1));
            if (dist > r1 && dist <= r2)
                answer1.add(e);
        }
//        System.out.println("\t\tPoints in buckets: " + answer.size());
//        System.out.println("\t\tPoints in circle: " + answer1.size());
        System.out.println("INNER: " + scanCircle(c_inner).size() + ", OUTER: " + scanCircle(c_outer).size() + ", DIFFERENCE: " + answer1.size());
        return answer1;
    }

    private ArrayList<Events> getDifferencePointPart(double lat_min, double lat_max, double lon_min, double lon_max, String code) {
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
        ArrayList<Events> answer = new ArrayList<>();
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
                    answer.add(e);
                }
            }
        }
        return answer;
    }

    public ArrayList<Events> scanCircle(Circle circle) {
        double lon = circle.getX_coord();
        double lat = circle.getY_coord();
        double radius = circle.getRadius();
//        System.out.println("Lat, Lon, Radius: " + lat + " " + lon + " " + radius);
        ArrayList<Events> answer = new ArrayList<>();
        double lat_min = lat - radius, lat_max = lat + radius, lon_min = lon - radius, lon_max = lon + radius;
//        System.out.println("Lat circle, Lon circle: " + lat_min + " " + lat_max + " " + lon_min + " " + lon_max);
//        System.out.println("Lat scale: "+gridFile.latScale.toString());
//        System.out.println("Lon scale: "+gridFile.lonScale.toString());
        int lat_min_id, lat_max_id, lon_min_id, lon_max_id;
        Range r_lat;
        Range r_lon;
        r_lat = search(lat_min, lat_max, gridFile.latScale);
        r_lon = search(lon_min, lon_max, gridFile.lonScale);
        lat_min_id = r_lat.min;
        lat_max_id = r_lat.max;
        lon_min_id = r_lon.min;
        lon_max_id = r_lon.max;
//        System.out.println(lat_min_id+" "+lat_max_id+" "+lon_min_id+" "+lon_max_id);
        if (lat_min_id == -1)
            lat_min_id = 0;
        if (lon_min_id == -1)
            lon_min_id = 0;
        if (lat_max_id == -2)
            lat_max_id = gridFile.latScale.size() - 1;
        if (lon_max_id == -2)
            lon_max_id = gridFile.lonScale.size() - 1;
//        System.out.println(lat_min_id+" "+lat_max_id+" "+lon_min_id+" "+lon_max_id);
        int total_buckets_loaded = 0;
//        System.out.println(lat_min_id + " " + lat_max_id + " " + lon_min_id + " " + lon_max_id);
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
//                        e.marked = true;
                    }
                }
            }
        }
//        System.out.println("lat range: " + lat_min_id + " " + lat_max_id);
//        System.out.println("lon range: " + lon_min_id + " " + lon_max_id);
//        System.out.println("\t\tTotal buckets loaded: " + total_buckets_loaded);
        ArrayList<Events> answer1 = new ArrayList<>();
        for (Events e : answer) {
            double x1 = e.getLon() - lon;
            double y1 = e.getLat() - lat;
            double dist = Math.sqrt((x1 * x1) + (y1 * y1));
            if (dist <= radius)
                answer1.add(e);
        }
//        System.out.println("\t\tPoints in buckets: " + answer.size());
//        System.out.println("\t\tPoints in circle: " + answer1.size());
        return answer1;
    }

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

    public double increase_radius(double radius, double growth) {
        return radius + growth;
    }
}

class Range {
    int min, max;
}