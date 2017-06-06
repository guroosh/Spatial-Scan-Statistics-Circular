package Dataset;

import java.util.Comparator;

/**
 * Created by Guroosh Chaudhary on 29-01-2017.
 */
public class Events {
    double y;
    double x;
    public boolean marked = false;

    public void setY(double y) {
        this.y = y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public double getX() {
        return this.x;
    }

    @Override
    public String toString() {
        return "Lat: " + getY() + ", Lon: " + getX();
    }

    public static Comparator<Events> sortByX() {
        return (o1, o2) -> {
            if (o1.x - o2.x > 0) {
                return 1;
            } else if (o1.x - o2.x < 0)
                return -1;
            else
                return 0;
//            return o1.x - o2.x;
        };
    }

    public static Comparator<Events> sortByY() {
        return (o1, o2) -> {
            if (o1.y - o2.y > 0) {
                return 1;
            } else if (o1.y - o2.y < 0)
                return -1;
            else
                return 0;
//            return o1.y - o2.y;
        };
    }
}
