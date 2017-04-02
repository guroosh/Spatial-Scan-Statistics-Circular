//package Dataset;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.util.HashMap;
//import java.util.HashSet;
//
///**
// * Created by Guroosh Chaudhary on 20-02-2017.
// */
//public class TestDeSer {
//    public static void main(String args[])
//    {
//        TestDeSer obj = new TestDeSer();
//        GridFile gridFile = obj.deserialize("gridFile.ser");
//        checkData(gridFile);
//        System.out.println("Complete");
//    }
//
//    private static void checkData(GridFile gridFile) {
//        //checking all values
//        int counter = 0;
//        System.out.println("Number of grids: " + gridFile.gridCellObject.size());
//        System.out.println("Number of mappings:" + gridFile.mapper.size());
//        System.out.println("Number of lat:" + gridFile.latScale.size());
//        System.out.println("Number of lon:" + gridFile.lonScale.size());
//        HashSet<Bucket> bucketSet = new HashSet<>();
//        HashSet<Events> eventSet = new HashSet<>();
//        for (HashMap.Entry<String, GridCell> gcO : gridFile.gridCellObject.entrySet()) {
//            Bucket b = gridFile.mapper.get(gcO.getValue());
//            for (Events e : b.eventsInBucket) {
//                eventSet.add(e);
//            }
//            for (Events e : b.extraEventsInBucket) {
//                eventSet.add(e);
//            }
//            bucketSet.add(b);
//        }
//        for (Bucket b : bucketSet) {
//            counter += b.extraEventsInBucket.size() + b.eventsInBucket.size();
//        }
//        System.out.println("Total points (check 1): " + counter);
//        System.out.println("Total points (check 2): " + eventSet.size());
//        System.out.println();
//    }
//
//    private GridFile deserialize(String filename) {
//        GridFile gridFile = null;
//        FileInputStream fin = null;
//        ObjectInputStream ois = null;
//
//        try {
//            fin = new FileInputStream(filename);
//            ois = new ObjectInputStream(fin);
//            gridFile = (GridFile) ois.readObject();
//            ois.close();
//            fin.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (fin != null) {
//                try {
//                    fin.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (ois != null) {
//                try {
//                    ois.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return gridFile;
//    }
//}
