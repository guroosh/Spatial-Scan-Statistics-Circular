//package Moving_Circle;
//
//import Algorithm_Ops.CircleOps;
//import Algorithm_Ops.ScanGeometry;
//
//
///**
// * Created by LakshayD on 2/8/2017.
// */
//
//public class Main {
//
//    public static void main(String args[]) {
//        System.out.println("Task runner for one Moving  Circle,Implementing one random circle");
//        runtypeA();
//
//    }
//
//    private static void runtypeA() {
//        System.out.println("RunTypeA");
//        double curr_radius = 1;
//        System.out.println("Need to know terminating radius,step growth amount,starting x,y ending x,y ");
//        double term_radius = 10, growth = 0.5, start_x = 0, start_y = 0, end_x = 0, end_y = 0, shift_x = 0, shift_y = 0;
//        System.out.println("Start thread");
//        ScanGeometry area = new ScanGeometry(start_x, start_y, end_x, end_y);
////      Thread Start
//        ScanGeometry.Circle curr_circle = new ScanGeometry.Circle("Random", curr_radius, area);
//        CircleOps controller = new CircleOps(curr_radius, term_radius, area);
//        while (controller.term(curr_circle) != 3) {
//            ScanGeometry.Circle fin_circle = controller.scan(curr_circle);
//            if (fin_circle != null)
////                Add to hashmap for processing
//                ;
//            curr_circle = controller.grow_radius(growth, curr_circle);
//            ScanGeometry.Circle new_circle = controller.checkquadpoints(curr_circle);
//            if (new_circle != null)
////                Add to hashmap for processing
//                ;
//        }
//        if (taskinqueue) {
//            dotask();
//        } else
//            sleep thread;
//
//    }
//
//}
