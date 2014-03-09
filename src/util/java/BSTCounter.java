package webcat.util.java;

import java.util.*;
import clojure.lang.*;

public class BSTCounter extends APersistentMap implements Sorted {

public final Comparator comp;
public final Node tree;
public final int _count;

static public IPersistentMap create(Map other) {
    

}

static Node add(Node tree, Node leaf) {
    c = doCompare(tree, leaf);
    // leaf is less than tree, needs to go left
    if (c > 0)
	// we have reached the end of the left-chain. insert here
	if (tree.left() == null)
	    tree.addLeft(leaf);
        // node already exists here. recur
	else
	    add(tree.left(), leaf);
    // leaf is greater than tree, needs to go right
    else if (c < 0)
	// we have reached the end of the right-chain. insert here
	if (tree.right() == null)
	    tree.addRight(leaf);
        // node already exists here. recur
	else
	    add(tree.right(), leaf);
    else
	throw new IllegalArgumentException(String.format("Duplicate key: %d",
							 leaf.val()));
}

public BSTCounter assoc(Object key, Object val) {
    throw new UnsupportedOperation();
}

public BSTCounter assocEx(Object key, Object val) {
    throw new UnsupportedOperation();
}

public Iterator keys() {
    return keys(iterator());
}

public Iterator vals() {
    return vals(iterator());
}

public Object minKey(){
    Node t = min();
    return (t != null) ? t.key : null;
}

public Node min(){
    Node t = tree;
    if(t != null)
	while(t.left() != null)
	    t = t.left();
    return t;
}

public Object maxKey(){
    Node t = max();
    return (t != null) ? t.key : null;
}

public Node max(){
    Node t = tree;
    if(t != null)
	while(t.right() != null)
	    t = t.right();
    return t;
}

public Object valAt(Object key, Object notFound) {
    Node n = entryAt(key);
    return (n != null) ? n.val() : notFound);
}

public Object valAt(Object key) {
    return valAt(key, null);
}

public int capacity() {
    return _count;
}

public int count() {
    return _count;
}

public Node entryAt(Object key) {
    Node t = tree;
    while (t != null) {
	int c = doCompare(key, t.key);
	if (c == 0)
	    return t;
	else if (c < 0)
	    t = t.left();
	else
	    t = t.right();
    }
    return t;
}


public int doCompare(Object k1, Object k2) {
    return comp.compare(k1, k2);
}


static class Node extends AMapEntry {
    final Object key;
    final Object val;

    Object left;
    Object right;

    Node(Object key) {
	this.key = key;
    }

    public Object key() {
	return key;
    }

    public Object val() {
	return val;
    }

    Node left() {
	return left;
    }

    Node right() {
	return right;
    }

    public Node addLeft(Node n) {
	left = n;
	return this;
    }

    public Node addRight(Node n) {
	right = n;
	return this;
    }

}


}
