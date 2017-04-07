package JOMP;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by guroosh on 8/4/17.
 */
public class naiveJOMP {

    public Circle circles[];
    public long circle_count;
    public int added_circles = 0;

    public void naiveTesterJOMP(GridFile gridFile, double minLon, double minLat, double maxLon, double maxLat) throws Exception {
        double likelihood_threshold = 0;
        double curr_radius = 0.001;
        double initial_radius = curr_radius;
        double term_radius = 0.01;
        double shift_radius = 0.001;
        double growth_radius = 0.001;
        int number_of_radius = 10;       //By formula: number  <=  ((t-c)/g) + 1

        int lat_circles = (int) ((maxLat - minLat) / shift_radius) + 1;
        int lon_circles = (int) ((maxLon - minLon) / shift_radius) + 1;
        int assumed_number_of_circles = lat_circles * lon_circles * number_of_radius;

        circles = new Circle[assumed_number_of_circles];

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);
        CircleOps controller = new CircleOps(initial_radius, term_radius, area, gridFile);
//        System.out.println(curr_circle.toString());

        int i;

// OMP PARALLEL BLOCK BEGINS
{
  __omp_Class0 __omp_Object0 = new __omp_Class0();
  // shared variables
  __omp_Object0.controller = controller;
  __omp_Object0.area = area;
  __omp_Object0.assumed_number_of_circles = assumed_number_of_circles;
  __omp_Object0.lon_circles = lon_circles;
  __omp_Object0.lat_circles = lat_circles;
  __omp_Object0.number_of_radius = number_of_radius;
  __omp_Object0.growth_radius = growth_radius;
  __omp_Object0.shift_radius = shift_radius;
  __omp_Object0.term_radius = term_radius;
  __omp_Object0.initial_radius = initial_radius;
  __omp_Object0.curr_radius = curr_radius;
  __omp_Object0.likelihood_threshold = likelihood_threshold;
  __omp_Object0.maxLat = maxLat;
  __omp_Object0.maxLon = maxLon;
  __omp_Object0.minLat = minLat;
  __omp_Object0.minLon = minLon;
  __omp_Object0.gridFile = gridFile;
  // firstprivate variables
  try {
    jomp.runtime.OMP.doParallel(__omp_Object0);
  } catch(Throwable __omp_exception) {
    System.err.println("OMP Warning: Illegal thread exception ignored!");
    System.err.println(__omp_exception);
  }
  // reduction variables
  // shared variables
  i = __omp_Object0.i;
  controller = __omp_Object0.controller;
  area = __omp_Object0.area;
  assumed_number_of_circles = __omp_Object0.assumed_number_of_circles;
  lon_circles = __omp_Object0.lon_circles;
  lat_circles = __omp_Object0.lat_circles;
  number_of_radius = __omp_Object0.number_of_radius;
  growth_radius = __omp_Object0.growth_radius;
  shift_radius = __omp_Object0.shift_radius;
  term_radius = __omp_Object0.term_radius;
  initial_radius = __omp_Object0.initial_radius;
  curr_radius = __omp_Object0.curr_radius;
  likelihood_threshold = __omp_Object0.likelihood_threshold;
  maxLat = __omp_Object0.maxLat;
  maxLon = __omp_Object0.maxLon;
  minLat = __omp_Object0.minLat;
  minLon = __omp_Object0.minLon;
  gridFile = __omp_Object0.gridFile;
}
// OMP PARALLEL BLOCK ENDS

         // OMP BARRIER BLOCK BEGINS
         jomp.runtime.OMP.doBarrier();
         // OMP BARRIER BLOCK ENDS

    }

// OMP PARALLEL REGION INNER CLASS DEFINITION BEGINS
private class __omp_Class0 extends jomp.runtime.BusyTask {
  // shared variables
  int i;
  CircleOps controller;
  ScanGeometry area;
  int assumed_number_of_circles;
  int lon_circles;
  int lat_circles;
  int number_of_radius;
  double growth_radius;
  double shift_radius;
  double term_radius;
  double initial_radius;
  double curr_radius;
  double likelihood_threshold;
  double maxLat;
  double maxLon;
  double minLat;
  double minLon;
  GridFile gridFile;
  // firstprivate variables
  // variables to hold results of reduction

  public void go(int __omp_me) throws Throwable {
  // firstprivate variables + init
  // private variables
  // reduction variables, init to default
    // OMP USER CODE BEGINS

        {
             { // OMP FOR BLOCK BEGINS
             // copy of firstprivate variables, initialized
             // copy of lastprivate variables
             // variables to hold result of reduction
             boolean amLast=false;
             {
               // firstprivate variables + init
               // [last]private variables
               // reduction variables + init to default
               // -------------------------------------
               jomp.runtime.LoopData __omp_WholeData2 = new jomp.runtime.LoopData();
               jomp.runtime.LoopData __omp_ChunkData1 = new jomp.runtime.LoopData();
               __omp_WholeData2.start = (long)( 0);
               __omp_WholeData2.stop = (long)( number_of_radius);
               __omp_WholeData2.step = (long)(1);
               jomp.runtime.OMP.setChunkStatic(__omp_WholeData2);
               while(!__omp_ChunkData1.isLast && jomp.runtime.OMP.getLoopStatic(__omp_me, __omp_WholeData2, __omp_ChunkData1)) {
               for(;;) {
                 if(__omp_WholeData2.step > 0) {
                    if(__omp_ChunkData1.stop > __omp_WholeData2.stop) __omp_ChunkData1.stop = __omp_WholeData2.stop;
                    if(__omp_ChunkData1.start >= __omp_WholeData2.stop) break;
                 } else {
                    if(__omp_ChunkData1.stop < __omp_WholeData2.stop) __omp_ChunkData1.stop = __omp_WholeData2.stop;
                    if(__omp_ChunkData1.start > __omp_WholeData2.stop) break;
                 }
                 for(int i = (int)__omp_ChunkData1.start; i < __omp_ChunkData1.stop; i += __omp_ChunkData1.step) {
                   // OMP USER CODE BEGINS
 {

                double[] curr_local_radius = {initial_radius + (growth_radius * i)};
                Circle[] curr_local_circle = {new Circle(minLon, minLat, curr_local_radius[0])};

//                    System.out.println(curr_local_radius[0]);
//                    System.out.println(curr_local_circle[0].toString());
//                    System.out.println();
                while (controller.term(curr_local_circle[0]) != -1) {
                    while (controller.term(curr_local_circle[0]) == 0) {
                         // OMP CRITICAL BLOCK BEGINS
                         synchronized (jomp.runtime.OMP.getLockByName("one")) {
                         // OMP USER CODE BEGINS

                        {
                            circle_count++;
                        }
                         // OMP USER CODE ENDS
                         }
                         // OMP CRITICAL BLOCK ENDS

//                            System.out.print(finalI);
//                            System.out.print(naive_counter + " : " + curr_circle.toString());
                        Events points[] = controller.scanCircle(curr_local_circle[0]).toArray(new Events[0]);

                        double likelihood_ratio = controller.likelihoodRatio(curr_local_circle[0], points);
                        if (likelihood_ratio > likelihood_threshold) {            //(likelihood_ratio > 2000) {
                            //Starting for visualization
                            Circle temp_circle = new Circle(curr_local_circle[0].getX_coord(), curr_local_circle[0].getY_coord(), curr_local_circle[0].getRadius());
                            temp_circle.lhr = likelihood_ratio;
                             // OMP CRITICAL BLOCK BEGINS
                             synchronized (jomp.runtime.OMP.getLockByName("two")) {
                             // OMP USER CODE BEGINS

                            {
                                circles[added_circles] = temp_circle;
                                added_circles++;
                            }
                             // OMP USER CODE ENDS
                             }
                             // OMP CRITICAL BLOCK ENDS

                        }
                        curr_local_circle[0] = controller.grow_x(shift_radius, curr_local_circle[0]);           // TODO: 21-03-2017 change grow_x to shift_x (just the name)
                    }
                    curr_local_circle[0] = controller.shift(curr_local_circle[0], -1, shift_radius);               // TODO: 21-03-2017 change shift to shift_y and change returning null to something else
                }
                System.out.println("DONE for radius: " + curr_local_radius[0]);
            }
                   // OMP USER CODE ENDS
                   if (i == (__omp_WholeData2.stop-1)) amLast = true;
                 } // of for 
                 if(__omp_ChunkData1.startStep == 0)
                   break;
                 __omp_ChunkData1.start += __omp_ChunkData1.startStep;
                 __omp_ChunkData1.stop += __omp_ChunkData1.startStep;
               } // of for(;;)
               } // of while
               // call reducer
               jomp.runtime.OMP.doBarrier(__omp_me);
               // copy lastprivate variables out
               if (amLast) {
               }
             }
             // set global from lastprivate variables
             if (amLast) {
             }
             // set global from reduction variables
             if (jomp.runtime.OMP.getThreadNum(__omp_me) == 0) {
             }
             } // OMP FOR BLOCK ENDS

        }
    // OMP USER CODE ENDS
  // call reducer
  // output to _rd_ copy
  if (jomp.runtime.OMP.getThreadNum(__omp_me) == 0) {
  }
  }
}
// OMP PARALLEL REGION INNER CLASS DEFINITION ENDS

}

