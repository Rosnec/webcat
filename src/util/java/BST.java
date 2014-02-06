import java.util.Comparator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class
BST<K,V>
implements Set<K>
{
    protected Node<K,V> root;
    protected final Comparator<K> comp;

    public
    BST()
    {
	this(null);
    }

    public
    BST(Comparator<K> comp)
    {
	if (comp == null)
	    comp = new NaturalComparator<K>();
	this.root = null;
	this.comp = comp;
    }

    public boolean
    add(K key)
    {
	return add(key, null);
    }

    public boolean
    add(K key, V value)
    {
	return add(key, value, root) != null;
    }

    protected Node<K,V>
    add(K key, V value, Node<K,V> node)
    {
	/* reached end of recursion, insert new leaf node */
	if (node == null)
	    return new Node<K,V>(key, value);

	int c;
	c = comp.compare(key, node.key);

	if (c < 0)
	    node.left = add(key, value, node.left);
	else if (c > 0)
	    node.right = add(key, value, node.right);
	else /* c == 0 implied */
	    node.value = value;

	return node;
    }

    @Override
    public boolean
    addAll(Collection<? extends K> c)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public void
    clear()
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    public Comparator<? super K>
    comparator()
    {
	return comp;
    }

    @Override
    public boolean
    contains(Object o)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean
    containsAll(Collection<?> c)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean
    equals(Object o)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public int
    hashCode()
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    public boolean
    isEmpty()
    {
	return root == null;
    }

    @Override
    public Iterator<K>
    iterator()
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean
    remove(Object o)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean
    removeAll(Collection<?> c)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public boolean
    retainAll(Collection<?> c)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public int
    size()
    throws UnsupportedOperationException
    {
	return (root == null) ? 0 : size(root);
    }

    private int
    size(Node<K,V> n)
    {
	return 1 + ((n.left == null) ? 0 : size(n.left))
                 + ((n.right == null) ? 0 : size(n.right));
    }

    @Override
    public Object[]
    toArray()
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[]
    toArray(T[] a)
    throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException();
    }
}

class
Node<K,V>
{
    K key;
    V value;
    Node<K,V> left, right;

    Node(K key)
    {
	this(key, null, null, null);
    }

    Node(K key, V value)
    {
	this(key, value, null, null);
    }

    Node(K key, V value, Node<K,V> left, Node<K,V> right)
    {
	this.key = key;
	this.value = value;
	this.left = left;
	this.right = right;
    }
}
