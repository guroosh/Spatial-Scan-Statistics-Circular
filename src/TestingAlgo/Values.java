package TestingAlgo;

/**
 * Created by guroosh on 14/4/17.
 */
public class Values {
    public static int bucketSize = 50;

    public static double lh_threshold = 0;
    public static double lower_limit = 0.001;
    public static double upper_limit = 0.02;
    public static double terminating_radius = 0.2;
    public static double growth_rate = 0.001;
    public static int moving_circle_counter = 50;
    public static int runtime = 1000;
    public static int number_of_radius_naive = (int) (((upper_limit - lower_limit) / lower_limit) + 1);
    public static int top_circles_for_print = 0;
    public static int top_circles_for_visualize = top_circles_for_print;
    public static int top_circles_naive_final = runtime;           //equivalent to runtime in moving circle
    public static int  nruns=1;
    public static int  p2runs=1000;
    public static double ji_threshold = 0.3;
    public static int average_for_multi_core = 1;

    public static void setGrowth_rate(double growth_rate) {
        Values.growth_rate = growth_rate;
    }
}
