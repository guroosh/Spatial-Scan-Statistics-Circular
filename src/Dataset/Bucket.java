package Dataset;

import java.util.ArrayList;
import TestingAlgo.Main;
/**
 * Created by Guroosh Chaudhary on 29-01-2017.
 */
public class Bucket {
    public ArrayList<Events> eventsInBucket = new ArrayList<>();
    int bucket_number;
//    int number_of_pointers = 1;

    Bucket() {
        this.bucket_number = Main.bucket_number;
        Main.bucket_number += 1;
    }

    public boolean addEvent(Events event, GridCell gridCell) {
        if (this.eventsInBucket.size() >= Main.bucket_size) {
            return false;
        }
//        gridCell.addEvent(event);
        this.eventsInBucket.add(event);
        return true;
    }

    public void forceAddEvent(Events event, GridCell gridCell) {
//        gridCell.addEvent(event);
        this.eventsInBucket.add(event);
    }

    public int getCount() {
        return eventsInBucket.size();
    }


}

