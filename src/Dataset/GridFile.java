package Dataset;

import java.io.*;
import java.util.*;
import TestingAlgo.Main;

import static TestingAlgo.Main.*;

/**
 * Created by Guroosh Chaudhary on 05-02-2017.
 */
public class GridFile implements Serializable {

    private static final long serialVersionUID = 1L;

    public SortedLinkedList<Double> latScale = new SortedLinkedList<>();
    public SortedLinkedList<Double> lonScale = new SortedLinkedList<>();
    public HashMap<String, GridCell> gridCellObject = new HashMap<>();
    public HashMap<GridCell, Bucket> mapper = new HashMap<>();
    public int total_events;

    public GridFile() {
        this.latScale = new SortedLinkedList<>();
        this.lonScale = new SortedLinkedList<>();
        this.gridCellObject = new HashMap<>();
        this.mapper = new HashMap<>();
    }

    public void make(ArrayList<Events> events, GridCell gridCell) {
        init(gridCell);
        for (Events event : events) {
            insertEventOnGrid(event);
        }
        setTotalEvents();
    }

    private void setTotalEvents() {
        int counter = 0;
        HashSet<Bucket> bucketSet = new HashSet<>();
        HashSet<Events> eventSet = new HashSet<>();
        for (HashMap.Entry<String, GridCell> gcO : this.gridCellObject.entrySet()) {
            Bucket b = this.mapper.get(gcO.getValue());
            for (Events e : b.eventsInBucket) {
                eventSet.add(e);
            }
            bucketSet.add(b);
        }
        for (Bucket b : bucketSet) {
            counter += b.eventsInBucket.size();
        }
        if(counter == eventSet.size())
        {
            if(counter!=0)
            {
                this.total_events = counter;
            }
        }
    }

    private void insertEventOnGrid(Events event) {
        GridCell gridCell = getGridCell(event);
        Bucket bucket = this.mapper.get(gridCell);
        if(bucket== null)
            System.out.println(gridCell.getHashValue());
        boolean added = bucket.addEvent(event, gridCell);
        if (!added) {
            splitGrid(gridCell, event);
            //to check
//            int event_count_in_gridCell = gridCell.getCount();
//            int event_count_in_bucket = bucket.getCount();
//            if (event_count_in_gridCell == event_count_in_bucket) {
//                splitGrid(gridCell, event);
//            } else {
//                System.out.println("Error, point not inserted in bucket but unequal in cell and bucket");
//            }
        }
    }

    private void splitGrid(GridCell gridCell, Events event) {
        Events median = findMedianInGridCell(gridCell);
        if (gridCell.hasOnBoundary(median)) {
            String boundaryAxis = gridCell.getAxis(median);
            if (boundaryAxis.equals("hv")) {
                Bucket bucket = this.mapper.get(gridCell);
//                gridCell.addEvent(event);
                bucket.forceAddEvent(event, gridCell);
                return;
            } else if (boundaryAxis.equals("h")) {
                if (Main.splitAxis.equals("horizontal")) {
                    Bucket bucket = this.mapper.get(gridCell);
//                    gridCell.addEvent(event);
                    bucket.forceAddEvent(event, gridCell);
                    return;
                }
            } else if (boundaryAxis.equals("v")) {
                if (Main.splitAxis.equals("vertical")) {
                    Bucket bucket = this.mapper.get(gridCell);
//                    gridCell.addEvent(event);
                    bucket.forceAddEvent(event, gridCell);
                    return;
                }
            }
        }
        Bucket bucket = this.mapper.get(gridCell);
        bucket.forceAddEvent(event, gridCell);
        ArrayList<Replace> toReplace = new ArrayList<>();
        if (Main.splitAxis.equals("horizontal")) {
            for (HashMap.Entry<GridCell, Bucket> mapPointer : this.mapper.entrySet()) {
                GridCell thisGridCell = mapPointer.getKey();
                if (thisGridCell.minLat == gridCell.minLat && thisGridCell.maxLat == gridCell.maxLat) {

                    GridCell gc1 = new GridCell(this, thisGridCell.minLat, median.getLat(), thisGridCell.minLon, thisGridCell.maxLon);
                    GridCell gc2 = new GridCell(this, median.getLat(), thisGridCell.maxLat, thisGridCell.minLon, thisGridCell.maxLon);
                    gc1 = gc1.getGridCell(this);
                    gc2 = gc2.getGridCell(this);

                    Replace r = new Replace();
                    r.old = thisGridCell;
                    r.new1 = gc1;
                    r.new2 = gc2;
                    toReplace.add(r);
                }
            }
        } else {
            for (HashMap.Entry<GridCell, Bucket> mapPointer : this.mapper.entrySet()) {
                GridCell thisGridCell = mapPointer.getKey();
                if (thisGridCell.minLon == gridCell.minLon && thisGridCell.maxLon == gridCell.maxLon) {

                    GridCell gc1 = new GridCell(this, thisGridCell.minLat, thisGridCell.maxLat, thisGridCell.minLon, median.getLon());
                    GridCell gc2 = new GridCell(this, thisGridCell.minLat, thisGridCell.maxLat, median.getLon(), thisGridCell.maxLon);

                    gc1 = gc1.getGridCell(this);
                    gc2 = gc2.getGridCell(this);

                    Replace r = new Replace();
                    r.old = thisGridCell;
                    r.new1 = gc1;
                    r.new2 = gc2;
                    toReplace.add(r);
                }
            }
        }
        addToScales(median);
        replaceGridCells(toReplace, median, event);
        changeSplitAxis();
    }

    private void changeSplitAxis() {
        if (Main.splitAxis.equals("horizontal"))
            Main.splitAxis = "vertical";
        else
            Main.splitAxis = "horizontal";
    }

    private void replaceGridCells(ArrayList<Replace> toReplace, Events median, Events currentEvent) {
        for (Replace r : toReplace) {
            Bucket oldBucket = this.mapper.get(r.old);
            Bucket newBucket1 = new Bucket();
            Bucket newBucket2 = new Bucket();
            for (Events event : oldBucket.eventsInBucket) {
                if (Main.splitAxis.equals("horizontal")) {
                    if (event.getLat() <= median.getLat()) {
                        newBucket1.forceAddEvent(event, r.new1);
                    } else if (event.getLat() > median.getLat()) {
                        newBucket2.forceAddEvent(event, r.new2);
                    }
                } else {
                    if (event.getLon() <= median.getLon()) {
                        newBucket1.forceAddEvent(event, r.new1);
                    } else if (event.getLon() > median.getLon()) {
                        newBucket2.forceAddEvent(event, r.new2);
                    }
                }
            }
            this.mapper.put(r.new1, newBucket1);
            this.mapper.put(r.new2, newBucket2);
            this.mapper.remove(r.old);
            this.gridCellObject.remove(r.old.getHashValue());
        }
    }

    private void addToScales(Events median) {
        if (Main.splitAxis.equals("horizontal"))
            this.latScale.add(median.getLat());
        else
            this.lonScale.add(median.getLon());
    }

    private Events findMedianInGridCell(GridCell gridCell) {
        Bucket bucket = mapper.get(gridCell);
        ArrayList<Events> events = bucket.eventsInBucket;
        if (Main.splitAxis.equals("horizontal"))
            events.sort(Events.sortByY());
        else
            events.sort(Events.sortByX());
        return events.get((events.size() - 1) / 2);
    }

    private GridCell getGridCell(Events event) {
        for (int i = 0; i < this.lonScale.size() - 1; i++) {
            if (this.lonScale.get(i) > this.lonScale.get(i + 1)) {
                System.out.println("SORT LON SCALE");
                return null;
            }
        }
        for (int i = 0; i < this.latScale.size() - 1; i++) {
            if (this.latScale.get(i) > this.latScale.get(i + 1)) {
                System.out.println("SORT LAT SCALE");
                return null;
            }
        }
        double lat1, lat2, lon1, lon2;
        int latSize = this.latScale.size();
        int lonSize = this.lonScale.size();

        lat1 = this.latScale.get(latSize - 2);
        lat2 = this.latScale.get(latSize - 1);
        lon1 = this.lonScale.get(lonSize - 2);
        lon2 = this.lonScale.get(lonSize - 1);

        for (int i = 0; i < lonSize; i++) {
            if (event.lon <= this.lonScale.get(i)) {
                if (i == 0) {
                    lon1 = this.lonScale.get(i);
                    lon2 = this.lonScale.get(i + 1);
                    break;
                }
                lon1 = this.lonScale.get(i - 1);
                lon2 = this.lonScale.get(i);
                break;
            }
        }
        for (int i = 0; i < latSize; i++) {
            if (event.lat <= this.latScale.get(i)) {
                if (i == 0) {
                    lat1 = this.latScale.get(i);
                    lat2 = this.latScale.get(i + 1);
                    break;
                }
                lat1 = this.latScale.get(i - 1);
                lat2 = this.latScale.get(i);
                break;
            }
        }
        GridCell gridCell = this.gridCellObject.get(String.valueOf(lat1) + "_" + String.valueOf(lat2) + "_" + String.valueOf(lon1) + "_" + String.valueOf(lon2));
        return gridCell;
    }

    private void init(GridCell gridCell) {
        Bucket bucket = new Bucket();
        this.mapper.put(gridCell, bucket);
        this.latScale.add(gridCell.minLat);
        this.latScale.add(gridCell.maxLat);
        this.lonScale.add(gridCell.minLon);
        this.lonScale.add(gridCell.maxLon);
    }
    private static void checkData(GridFile gridFile) {
        int counter = 0;
        System.out.println("Number of grids: " + gridFile.gridCellObject.size());
        System.out.println("Number of mappings:" + gridFile.mapper.size());
        System.out.println("Number of lat:" + gridFile.latScale.size());
        System.out.println("Number of lon:" + gridFile.lonScale.size());
        HashSet<Bucket> bucketSet = new HashSet<>();
        HashSet<Events> eventSet = new HashSet<>();
        for (HashMap.Entry<String, GridCell> gcO : gridFile.gridCellObject.entrySet()) {
            Bucket b = gridFile.mapper.get(gcO.getValue());
            eventSet.addAll(b.eventsInBucket);
            bucketSet.add(b);
        }
        for (Bucket b : bucketSet) {
            counter += b.eventsInBucket.size();
        }
        System.out.println("Total points (check 1): " + counter);
        System.out.println("Total points (check 2): " + eventSet.size());
    }
    public static ArrayList<Events> readDataFile(String fileName) throws IOException {
//        int latInCSV = 21;
//        int lonInCSV = 22;
        double lat, lon;
        int latInCSV = 13;
        int lonInCSV = 12;
        String line;
        String lines[];
        ArrayList<Events> events = new ArrayList<>();
        InputStream is = new FileInputStream(fileName);
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        boolean isHeader = true;
        int counter1 = 0;
        while ((line = br.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            line = modifyLine(line);
            lines = line.split(",");
            try {
                double longitude, latitude;
                longitude = Double.parseDouble(lines[lonInCSV]);
                latitude = Double.parseDouble(lines[latInCSV]);
                Events position = new Events();
                position.setLat(latitude);
                position.setLon(longitude);
                lat = position.getLat();
                lon = position.getLon();// * (111.320 / 110.574) * Math.cos(Math.toRadians(position.getLat()));
                if (lon > maxLon) {
                    maxLon = lon;
                } else if (lon < minLon) {
                    minLon = lon;
                }
                if (lat > maxLat) {
                    maxLat = lat;
                } else if (lat < minLat) {
                    minLat = lat;
                }
                position.setLat(lat);
                position.setLon(lon);
                events.add(position);
            } catch (ArrayIndexOutOfBoundsException e) {
                counter1++;                     //to check amount of corrupted data: if lat lon is empty after geo locating
            }
        }
        return events;
    }
    private static String modifyLine(String line) {
        int counter = 0;
        StringBuilder newString = new StringBuilder(line);
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (counter == 0)
                    counter++;
                else
                    counter--;
            }
            if (ch == ',') {
                if (counter == 1) {
                    newString.setCharAt(i, '#');
                }
            }
        }
        String ans = newString.toString();
//        ans = ans.replace("-","\"\"");            //important: if data also contains a dash
        return ans;
    }


}

class Replace {
    GridCell old, new1, new2;
}