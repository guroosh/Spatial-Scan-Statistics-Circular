package Algorithm_Ops;


import Dataset.Events;

import javax.lang.model.element.ElementVisitor;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Guroosh Chaudhary on 14-03-2017.
 */
public class Circle {
    public double lhr;
    private double x_coord;
    private double y_coord;
    private double radius;
    public ArrayList<Events> points = new ArrayList<>();
    public double area;

    public Circle() {
        x_coord = 0;
        y_coord = 0;
        radius = 0;
    }

    public Circle(double x, double y, double r) {
        this.x_coord = x;
        this.y_coord = y;
        this.radius = r;
    }

    public Circle(Circle c2) {
        this.x_coord = c2.getX_coord();
        this.y_coord = c2.getY_coord();
        this.radius = c2.getRadius();
    }


    public Circle(String r, double curr_radius, ScanGeometry scanA) {
        if (r.equals("Random")) {
            this.setX_coord(ThreadLocalRandom.current().nextDouble(scanA.start_X, scanA.end_X));
            this.setY_coord(ThreadLocalRandom.current().nextDouble(scanA.start_Y, scanA.end_Y));
            this.setRadius(curr_radius);
        }
    }

    public static Comparator<Circle> sortByLHR() {
        return (o1, o2) -> {
            if (o1.lhr - o2.lhr > 0) {
                return -1;
            } else if (o1.lhr - o2.lhr < 0)
                return 1;
            else
                return 0;
//            return o1.lat - o2.lat;
        };
    }

    @Override
    public String toString() {
        return "X: " + String.format("%.4f", this.x_coord) + " Y: " + String.format("%.4f", this.y_coord) + " r: " + this.radius + " LR: " + this.lhr;
    }

    public double getX_coord() {
        return x_coord;
    }

    public void setX_coord(double x_coord) {
        this.x_coord = x_coord;
    }

    public double getY_coord() {
        return y_coord;
    }

    public void setY_coord(double y_coord) {
        this.y_coord = y_coord;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

}


