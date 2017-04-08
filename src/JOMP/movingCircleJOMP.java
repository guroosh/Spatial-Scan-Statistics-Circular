package JOMP;

import Algorithm_Ops.Circle;
import Algorithm_Ops.CircleOps;
import Algorithm_Ops.ScanGeometry;
import Dataset.Events;
import Dataset.GridFile;



/**
 * Created by guroosh on 8/4/17.
 */
public class movingCircleJOMP {

    int added_circles = 0;
    public Circle[] movingCircleTesterJOMP(int runtime, GridFile gridFile, double minLon, double minLat, double maxLon, double maxLat) throws Exception {
        Circle[] core_circles = new Circle[runtime];
        int i;

// OMP PARALLEL BLOCK BEGINS
{
  __omp_Class0 __omp_Object0 = new __omp_Class0();
  // shared variables
  __omp_Object0.core_circles = core_circles;
  __omp_Object0.maxLat = maxLat;
  __omp_Object0.maxLon = maxLon;
  __omp_Object0.minLat = minLat;
  __omp_Object0.minLon = minLon;
  __omp_Object0.gridFile = gridFile;
  __omp_Object0.runtime = runtime;
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
  core_circles = __omp_Object0.core_circles;
  maxLat = __omp_Object0.maxLat;
  maxLon = __omp_Object0.maxLon;
  minLat = __omp_Object0.minLat;
  minLon = __omp_Object0.minLon;
  gridFile = __omp_Object0.gridFile;
  runtime = __omp_Object0.runtime;
}
// OMP PARALLEL BLOCK ENDS

         // OMP BARRIER BLOCK BEGINS
         jomp.runtime.OMP.doBarrier();
         // OMP BARRIER BLOCK ENDS

        return core_circles;
    }

    private void movingCircleTester3(Circle[] core_circles, GridFile gridFile, double minLon, double minLat, double maxLon, double maxLat) throws Exception {
        int moving_counter = 1;
        int circlecounter = 100;
        double curr_radius = 0.001;//1;

        double term_radius = 0.2, growth = 0.005;

        ScanGeometry area = new ScanGeometry(minLon, minLat, maxLon, maxLat);


        int count_limit = 1;
        while (count_limit-- > 0) {
            Circle curr_circle = new Circle("Random", curr_radius, area);//X: -80.9865 Y: 39.6339 r: 0.001(BAD Visualize)// X: -81.3279 Y: 39.6639 r: 0.001 (Good Case)
//            Circle curr_circle = new Circle(-81.2234, 39.7345, 0.001);   //-81.3773, 39.6293, .01)
            Circle next_circle;
            Circle temp_circle = new Circle();
            temp_circle.setRadius(curr_circle.getRadius());
            temp_circle.setX_coord(curr_circle.getX_coord());
            temp_circle.setY_coord(curr_circle.getY_coord());
            CircleOps controller = new CircleOps(curr_radius, term_radius, area, gridFile);

            double maxlikeli = 1;
            Circle fin_circle = null;
            while (controller.term(curr_circle) != 3) {


                next_circle = controller.checkanglepoints(curr_circle);

                Events[] points = controller.scanCircle(curr_circle).toArray(new Events[0]);
                Events[] points1 = controller.scanCircle(next_circle).toArray(new Events[0]);

                double curr_likeli = controller.likelihoodRatio(curr_circle, points);
                double next_likeli = controller.likelihoodRatio(next_circle, points1);

                if (curr_likeli < next_likeli) {
                    if (next_likeli > maxlikeli) {
                        maxlikeli = next_likeli;
                        fin_circle = new Circle(next_circle);
                    }
//                    core_circles.add(temp_circle);
                    curr_circle = new Circle(next_circle);
//                    System.out.println(moving_counter + "\t\t\tShifted circle: " + curr_circle.toString() + "\n\n");
                    moving_counter++;
                } else {
                    if (curr_likeli > maxlikeli) {
                        maxlikeli = curr_likeli;
                        fin_circle = new Circle(curr_circle);

                    }
                    curr_circle = controller.grow_radius(growth, curr_circle);
                    moving_counter++;
                }
                while (circlecounter <= 1) {

                    if (fin_circle != null) {
                        Circle finalFin_circle = fin_circle;
                         // OMP CRITICAL BLOCK BEGINS
                         synchronized (jomp.runtime.OMP.getLockByName("three")) {
                         // OMP USER CODE BEGINS

                        {
                            controller.removePoints(controller.scanCircle(finalFin_circle));
                            core_circles[added_circles] = finalFin_circle;
                            added_circles++;
                        }
                         // OMP USER CODE ENDS
                         }
                         // OMP CRITICAL BLOCK ENDS

                        fin_circle.lhr = maxlikeli;
                    }
                    return;
                }
                circlecounter--;
            }
        }

    }

// OMP PARALLEL REGION INNER CLASS DEFINITION BEGINS
private class __omp_Class0 extends jomp.runtime.BusyTask {
  // shared variables
  int i;
  Circle [ ] core_circles;
  double maxLat;
  double maxLon;
  double minLat;
  double minLon;
  GridFile gridFile;
  int runtime;
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
               __omp_WholeData2.stop = (long)( runtime);
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
                movingCircleTester3(core_circles, gridFile, minLon, minLat, maxLon, maxLat);

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

