package Dataset;

import java.util.*;

/**
 * Created by Guroosh Chaudhary on 29-01-2017.
 */

public class SortedLinkedList<T> extends LinkedList<T> {
    private static final long serialVersionUID = 1L;
    private Comparator<? super T> comparator = null;

    public SortedLinkedList() {
    }

    @Override
    public boolean add(T paramT) {
        int insertionPoint = Collections.binarySearch(this, paramT, comparator);
        super.add((insertionPoint > -1) ? insertionPoint : (-insertionPoint) - 1, paramT);
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends T> paramCollection) {
        boolean result = false;
        if (paramCollection.size() > 4) {
            result = super.addAll(paramCollection);
            Collections.sort(this, comparator);
        } else {
            for (T paramT : paramCollection) {
                result |= add(paramT);
            }
        }
        return result;
    }


}
