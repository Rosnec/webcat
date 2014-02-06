import java.util.Comparator;

public class
NaturalComparator<T>
implements Comparator<T>
{
    public int compare(T o1, T o2)
    {
	return ((Comparable) o1).compareTo((Comparable) o2);
    }
}
