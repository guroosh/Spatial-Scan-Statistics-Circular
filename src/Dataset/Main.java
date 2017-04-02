//package Dataset;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Scanner;
//
///**
// * Created by Guroosh Chaudhary on 05-02-2017.
// */
//public class Main {
//
//    static int counter1=0;
//    static double minLat = 360;
//    static double minLon = 360;
//    static double maxLat = -360;
//    static double maxLon = -360;
//    static int bucket_number = 0;
//    static int bucket_size;
////    static SortedLinkedList<Double> latScale;
////    static SortedLinkedList<Double> lonScale;
////    static HashMap<String, GridCell> gridCellObject;
////    static HashMap<GridCell, Bucket> mapper;
//    static String splitAxis = "horizontal";
//
//    public static void main(String args[]) throws IOException {
//        Scanner in = new Scanner(System.in);
//        System.out.println("Enter file name: ");
//        String fileName = in.nextLine();
//        while (true) {
//            System.out.println("Enter the bucket size: ");
//            bucket_size = in.nextInt();
//            if (bucket_size < 1) {
//                System.out.println("Bucket number should be a natural number");
//            }
//            break;
//        }
//        ArrayList<Events> events = readDataFile(fileName);
////        System.out.println("Total given points: " + events.size());
//        System.out.println("Points without geo-encoding (lost data): " +counter1);
//        System.out.println("Longitude: from " + minLon + " to " + maxLon);
//        System.out.println("Latitude: from " + minLat + " to " + maxLat);
////        for(int i=1; i<events.size(); i=i+99)
////        {
////            System.out.println("Staring for bucket size: "+i);
////            bucket_size = i;
////        latScale = new SortedLinkedList<>();
////        lonScale = new SortedLinkedList<>();
////        gridCellObject = new HashMap<>();
////        mapper = new HashMap<>();
//        GridFile gridFile = new GridFile();
////        GridFile gridFile = new GridFile(latScale, lonScale, gridCellObject, mapper);
//        GridCell gridCell = new GridCell(gridFile, minLat, maxLat, minLon, maxLon);
//        gridCell = gridCell.getGridCell(gridFile);
//        gridFile.make(events, gridCell);
//        System.out.println("Entering checking procedure");
//        checkData(gridFile);
//        serialize(gridFile);
//        System.out.println("Complete");
//    }
//
//    private static void serialize(GridFile gridFile)
//    {
//
//        FileOutputStream fout = null;
//        ObjectOutputStream oos = null;
//
//        try {
//            fout = new FileOutputStream("gridFile.ser");
//            oos = new ObjectOutputStream(fout);
////            oos.writeObject(gridFile);
//            oos.close();
//            fout.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        } finally {
//            if (fout != null) {
//                try {
//                    fout.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (oos != null) {
//                try {
//                    oos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
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
//    private static ArrayList<Events> readDataFile(String fileName) throws IOException {
////        int latInCSV = 19;//13;
////        int lonInCSV = 20;//12;
//        int latInCSV = 13;
//        int lonInCSV = 12;
//        String line;
//        String lines[];
//        ArrayList<Events> events = new ArrayList<>();
//        InputStream is = new FileInputStream(fileName);
//        InputStreamReader isr = new InputStreamReader(is);
//        BufferedReader br = new BufferedReader(isr);
//        boolean isHeader = true;
////        int stopper = 0;
//
//        while ((line = br.readLine()) != null) {
//            if (isHeader) {
//                isHeader = false;
//                continue;
//            }
////            if(stopper==100000)
////            {
////                break;
////            }
////            stopper++;
//            line = modifyLine(line);
//            lines = line.split(",");
//            try {
//                double longitude, latitude;
//                longitude = Double.parseDouble(lines[lonInCSV]);
//                latitude = Double.parseDouble(lines[latInCSV]);
//
////                int original_lon = lonInCSV, original_lat = latInCSV;
////                while (true) {
////                    longitude = Double.parseDouble(lines[lonInCSV]);
////                    latitude = Double.parseDouble(lines[latInCSV]);
////                    if (latitude > 90 || longitude > 180 || latitude < -90 || longitude < -180) {
////                        lonInCSV++;
////                        latInCSV++;
////                        System.out.println("Shifting lat, lon");
////                    }
////                    else if()
////                    else {
////                        lonInCSV = original_lon;
////                        latInCSV = original_lat;
////                        break;
////                    }
////                }
//                if (longitude > maxLon) {
//                    maxLon = longitude;
//                } else if (longitude < minLon) {
//                    minLon = longitude;
//                }
//                if (latitude > maxLat) {
//                    maxLat = latitude;
//                } else if (latitude < minLat) {
//                    minLat = latitude;
//                }
//                Events position = new Events();
//                position.setLat(latitude);
//                position.setLon(longitude);
//                events.add(position);
//            } catch (ArrayIndexOutOfBoundsException e) {
////                System.out.println(line);
////                System.out.println(latInCSV+" "+ lonInCSV+ " "+ lines.length);
////                e.printStackTrace();
//                counter1++;
//            }
//        }
//        return events;
//    }
//
//    private static String modifyLine(String line) {
//        int counter=0;
//        StringBuilder newString = new StringBuilder(line);
//        for(int i=0;i<line.length();i++)
//        {
//            char ch = line.charAt(i);
//            if(ch == '"')
//            {
//                if(counter == 0)
//                    counter++;
//                else
//                    counter--;
//            }
//            if(ch == ',')
//            {
//                if(counter == 1)
//                {
//                    newString.setCharAt(i,'-');
//                }
//            }
//        }
//
//        String ans = newString.toString();
////        ans = ans.replace("-","\"\"");
//        return ans;
//    }
//}
//
