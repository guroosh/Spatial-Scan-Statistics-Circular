package Experiments;

import Algorithm_Ops.Circle;
import TestingAlgo.Main;

import java.util.List;

/**
 * Created by guroosh on 13/4/17.
 */
public class ListCheck {
    public double checkOverlapping(Circle c1, Circle c2) {

        double R1 = c1.getRadius();
        double R2 = c2.getRadius();
        double x1 = c1.getX_coord(), x2 = c2.getX_coord(), y1 = c1.getY_coord(), y2 = c2.getY_coord();
        double dist = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        double areaCircle1 = Math.PI * R1 * R1;
        double areaCircle2 = Math.PI * R2 * R2;
        if (dist <= Math.abs(R1 - R2)) {
            if (R1 > R2) {
                return areaCircle2 / areaCircle1;
            } else if (R2 >= R1) {
                return areaCircle1 / areaCircle2;
            }
        }
        if (dist >= R1 + R2 - Main.error_adjuster) {
            return 0;
        }
        double area1 = R1 * R1 * Math.acos(((dist * dist) + (R1 * R1) - (R2 * R2)) / (2 * dist * R1));
        double area2 = R2 * R2 * Math.acos(((dist * dist) + (R2 * R2) - (R1 * R1)) / (2 * dist * R2));
        double area3 = (Math.sqrt((-dist + R1 + R2) * (dist + R1 - R2) * (dist - R1 + R2) * (dist + R1 + R2))) / 2;
        double num = area1 + area2 - area3;
        double den = areaCircle1 + areaCircle2;
        return num / (den - num);
    }

    public double jaccardIndex(List<Circle> list1, List<Circle> list2, double threshold) {
        int count = 0;

        int size1 = list1.size();
        int size2 = list2.size();

        for (Circle c1 : list1) {
            for (Circle c2 : list2) {
                double equality = checkOverlapping(c1, c2);
//                System.out.print("equality: "+equality+",  ");
                if (equality > threshold) {
                    count++;
                }
            }
//            System.out.println();
        }
        double ans;
        int intersection, union;
        intersection = count;
        union = size1 + size2 - count;
//        System.out.print("I: " + intersection + ", U: " + union);
        System.out.print("Matched: "+intersection+" Calc Percentage: "+((double)intersection/(double)size1)*100+"%\t");
        ans = (intersection * 1.0) / union;
        return ans;
    }
}
