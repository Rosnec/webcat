public class
BSTCounter<K>
extends BST<K,Integer>
{
    @Override
    public boolean
    add(K key)
    {
	return add(key, root) != null;
    }

    protected Node<K,Integer>
    add(K key, Node<K,Integer> node)
    {
	/* reached end of recursion, insert new leaf node */
	if (node == null)
	    return new Node<K,Integer>(key, new Integer(1));

	int c;
	/* no Comparator provided. Use keys' intrinsic order */
	if (comp == null)
	    c = ((Comparable)key).compareTo((Comparable)node.key);
	/* use provided Comparator */
	else
	    c = comp.compare(key, node.key);

	if (c < 0)
	    node.left = add(key, node.left);
	else if (c > 0)
	    node.right = add(key, node.right);
	else /* c == 0 implied */
	    ++node.value;

	return node;
    }

}
