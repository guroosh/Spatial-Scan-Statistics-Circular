package Algorithm_Ops;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by LakshayD on 2/8/2017.
 */
public class ScanGeometry {

    double start_X;
    double start_Y;
    double end_X;
    double end_Y;

    public ScanGeometry(double x, double y, double x1, double y1) {
        this.start_X = x;
        this.start_Y = y;
        this.end_X = x1;
        this.end_Y = y1;
    }
}
