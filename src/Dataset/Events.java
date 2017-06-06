package Dataset;

import java.util.Comparator;

/**
 * Created by Guroosh Chaudhary on 29-01-2017.
 */
public class Events {
    double lat;
    double lon;
    public boolean marked = false;

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    @Override
    public String toString() {
        return "Lat: " + getLat() + ", Lon: " + getLon();
    }

    public static Comparator<Events> sortByX() {
        return (o1, o2) -> {
            if (o1.lon - o2.lon > 0) {
                return 1;
            } else if (o1.lon - o2.lon < 0)
                return -1;
            else
                return 0;
//            return o1.lon - o2.lon;
        };
    }

    public static Comparator<Events> sortByY() {
        return (o1, o2) -> {
            if (o1.lat - o2.lat > 0) {
                return 1;
            } else if (o1.lat - o2.lat < 0)
                return -1;
            else
                return 0;
//            return o1.lat - o2.lat;
        };
    }
}
