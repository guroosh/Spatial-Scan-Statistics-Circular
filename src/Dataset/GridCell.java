package Dataset;

/**
 * Created by Guroosh Chaudhary on 29-01-2017.
 */
public class GridCell {
    double minY;
    double maxY;
    double minX;
    double maxX;

    String hashValue;

//    ArrayList<Events> eventsInGridCell = new ArrayList<>();

    public GridCell(GridFile gridFile, double minLat, double maxLat, double minLon, double maxLon) {
        this.minY = minLat;
        this.maxY = maxLat;
        this.minX = minLon;
        this.maxX = maxLon;

        String hashValue = String.valueOf(minLat) + "_" +
                String.valueOf(maxLat) + "_" +
                String.valueOf(minLon) + "_" +
                String.valueOf(maxLon);
        this.hashValue = hashValue;

        //testing start
        GridCell gridCell = gridFile.gridCellObject.get(hashValue);
        if (gridCell != null) {
            System.out.println("Error: Duplicate grid cell created");
        }
        //testing end

        gridFile.gridCellObject.putIfAbsent(hashValue, this);
    }


    public String getHashValue() {
        return this.hashValue;
    }

//    public void addEvent(Events event) {
//        this.eventsInGridCell.add(event);
//    }
//
//    public int getCount() {
//        return eventsInGridCell.size();
//    }


    public GridCell getGridCell(GridFile gridFile) {
        return gridFile.gridCellObject.get(this.getHashValue());    //helps if grid cell already in hashMap
    }

    public boolean hasOnBoundary(Events event) {
        if (event.getY() == this.minY || event.getY() == this.maxY)
            return true;
        else if (event.getX() == this.minX || event.getX() == this.maxX)
            return true;
        return false;
    }

    public String getAxis(Events event) {
        String axis = "";
        if (event.getY() == this.minY || event.getY() == this.maxY)
            axis += "h";
        if (event.getX() == this.minX || event.getX() == this.maxX)
            axis += "v";
        return axis;
    }
}
