public class BSTCounter<Comparable> extends AbstractSet<E>
{
    private BSTCounterNode root;

    public BSTCounter()
    {
	this.root = null;
    }

    @Override
    public boolean add(Comparable e)
    {
	add(e, root);
	return true;
    }

    /* I think this doesn't need to be implemented
    public boolean addAll(Collection<Comparable> c)
    {
	for (e : c) {
	    
    }
    */

    @Override
    public boolean contains(Comparable e)
    {
	return contains(e, this.root);
    }

    public int count(Comparable e)
    {
	return count(e, this.root);
    }

    @Override
    public boolean isEmpty()
    {
	return this.root == null;
    }	

    @Override
    public int size()
    {
	return size(this.root);
    }

    protected BSTCounterNode add(Comparable e, BSTCounterNode n)
    {
	if (n == null)
	    n = new BSTCounterNode(e);
	else if (e.compareTo(n.element) < 0)
	    n.left = add(e, n.left);
	else if (e.compareTo(n.element) > 0)
	    n.right = add(e, n.right);
	else
	    n.count++;
	return n;
    }

    protected boolean contains(Comparable e, BSTCounterNode n)
    {
	if (n == null)
	    return false;
	if (n.element == e)
	    return true;
	return contains(e, n.left) || contains(e, n.right);
    }

    protected int count(Comparable e, BSTCounterNode n)
    {
	if (n == null)
	    return 0;
	if (n.element == e)
	    return n.count;
	return count(n.left) + count(n.right);
    }

    protected int size(BSTCounterNode n)
    {
	return (n == null) ? 0
	                   : 1 + size(n.left) + size(n.right);
    }
}

class BSTCounterNode
{
    Comparable element;
    int        count;
    BSTNode    left;
    BSTNode    right;

    BSTNode(Comparable e)
    {
	this.element = e;
	this.count = 1;
	this.right = this.left = null;
    }
}
