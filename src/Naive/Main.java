//package Naive;
//
//import Algorithm_Ops.*;
//import Dataset.GridFile;
//
//
///**
// * Created by LakshayD on 2/8/2017.
// */
//
//public class Main {
//
//    public static void main(String args[]) {
//        System.out.println("Task runner for one Naive Circle,Implementing all circles of one radius");
//        GridFile gridFile = new GridFile();
//        runtypeA(gridFile);
//
//    }
//
//    private static void runtypeA(GridFile gridFile) {
//        System.out.println("RunTypeA");
//        double curr_radius = 1;
//        System.out.println("Need to know terminating radius,step growth amount,starting x,y ending x,y and shifting of center");
//        double term_radius = 10, growth = 0.5, start_x = 0, start_y = 0, end_x = 0, end_y = 0, shift_x = 0, shift_y = 0;
//        System.out.println("Start thread");
//        ScanGeometry area = new ScanGeometry(start_x, start_y, end_x, end_y);
//        ScanGeometry.Circle curr_circle = new ScanGeometry.Circle(start_x, start_y, curr_radius);
//        //atomic
////        CircleOps controller = new CircleOps(curr_radius,term_radius);
////        curr_radius = curr_radius + growth;

//        CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);
//        System.out.println(curr_circle.toString());
//        while (controller.term(curr_circle) != -1) {
//            while (controller.term(curr_circle) == 0) {
//                ScanGeometry.Circle fin_circle = controller.scan(curr_circle);
//                if (fin_circle != null)
////                Add to hashmap for processing
//                {
//                    ;
//                }
//                curr_circle = controller.grow_x(growth, curr_circle);
////
////
//            }
//            curr_circle = controller.shift(curr_circle, shift_x, shift_y);
//        }
//    }
//
//}
