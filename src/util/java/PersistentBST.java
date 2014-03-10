package util.java;

import java.util.*;
import clojure.lang.*;

@SuppressWarnings("unchecked")
public class PersistentBST extends APersistentMap implements Sorted {

public final Comparator comp;
public Node tree;
public int _count;
final IPersistentMap _meta;

private PersistentBST(Comparator comp) {
    this(null, comp);
}

public PersistentBST(IPersistentMap meta, Comparator comp) {
    this._meta = meta;
    this.comp = comp;
    tree = null;
    _count = 0;
}

PersistentBST(IPersistentMap meta, Comparator comp, Node tree, int _count) {
    this._meta = meta;
    this.comp = comp;
    this.tree = tree;
    this._count = _count;
}

static public IPersistentMap create(Map other) {
    PersistentBST ret = new PersistentBST(null, RT.DEFAULT_COMPARATOR, null, 0);
    Map.Entry e;

    for(Object o : other.entrySet()) {
	e = (Entry) o;
	append(ret, new Node(e.getKey(), e.getValue()));
    }
    return ret;
}

static PersistentBST append(PersistentBST root, Node leaf) {
    if (root.tree == null)
	root.tree = leaf;
    else
	append(root.tree, leaf, root.comparator());
    root._count++;
    return root;
}

static Node append(Node tree, Node leaf, Comparator comp) {
    int c = comp.compare(tree, leaf);
    if (c > 0)
	// we have reached the end of the left-chain. insert here
	if (tree.left() == null)
	    return tree.addLeft(leaf);
        // node already exists here. recur
	else
	    return append(tree.left(), leaf, comp);
    // leaf is greater than tree, needs to go right
    else if (c < 0)
	// we have reached the end of the right-chain. insert here
	if (tree.right() == null)
	    return tree.addRight(leaf);
        // node already exists here. recur
	else
	    return append(tree.right(), leaf, comp);
    else
	throw new IllegalArgumentException(String.format("Duplicate key: %d",
							 leaf.val()));
}

public PersistentBST assoc(Object key, Object val) {
    throw new UnsupportedOperationException();
}

public PersistentBST assocEx(Object key, Object val) {
    throw new UnsupportedOperationException();
}

public PersistentBST without(Object o) {
    throw new UnsupportedOperationException();
}

public PersistentBST empty() {
    throw new UnsupportedOperationException();
}

public Object entryKey(Object entry) {
    throw new UnsupportedOperationException();
}

public NodeIterator iterator() {
    return new NodeIterator(tree, true);
}

public NodeIterator reverseIterator() {
    return new NodeIterator(tree, false);
}

public ISeq seq() {
    return (_count > 0) ? Seq.create(tree, true, _count) : null;
}

public ISeq seq(boolean ascending) {
    return (_count > 0) ? Seq.create(tree, ascending, _count) : null;
}

public ISeq rseq() {
    return (_count > 0) ? Seq.create(tree, false, _count) : null;
}

public ISeq seqFrom(Object o, boolean ascending) {
    throw new UnsupportedOperationException();
}

public Comparator comparator() {
    return comp;
}

public Iterator keys() {
    return keys(iterator());
}

public Iterator keys(NodeIterator it) {
    return new KeyIterator(it);
}

public Iterator vals() {
    return vals(iterator());
}

public Iterator vals(NodeIterator it) {
    return new ValIterator(it);
}

public Object minKey() {
    Node t = min();
    return (t != null) ? t.key : null;
}

public Node min() {
    Node t = tree;
    if(t != null)
	while(t.left() != null)
	    t = t.left();
    return t;
}

public Object maxKey() {
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
    return (n != null) ? n.val() : notFound;
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

public boolean containsKey(Object key) {
    return entryAt(key) != null;
}

public int doCompare(Object k1, Object k2) {
    return comp.compare(k1, k2);
}


static class Node extends AMapEntry {
    final Object key;
    final Object val;

    Node left;
    Node right;

    Node(Object key, Object val) {
	this.key = key;
	this.val = val;
    }

    public Object key() {
	return key;
    }

    public Object val() {
	return val;
    }

    /* need to implement Entry's abstract methods getKey and getValue,
       although we'll never use them */
    public Object getKey() {
	return key();
    }

    public Object getValue() {
	return val();
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

static public class Seq extends ASeq {
    final ISeq stack;
    final boolean ascending;
    final int count;

    public Seq(ISeq stack, boolean ascending) {
	this.stack = stack;
	this.ascending = ascending;
	this.count = -1;
    }

    public Seq(ISeq stack, boolean ascending, int count) {
	this.stack = stack;
	this.ascending = ascending;
	this.count = -1;
    }

    Seq(IPersistentMap meta, ISeq stack, boolean ascending, int count) {
	super(meta);
	this.stack = stack;
	this.ascending = ascending;
	this.count = count;
    }

    static Seq create(Node tree, boolean ascending, int count) {
	return new Seq(push(tree, null, ascending), ascending, count);
    }

    static ISeq push(Node tree, ISeq stack, boolean ascending) {
	if (tree == null)
	    return stack;
	else {
	    stack = RT.cons(tree, stack);
	    tree = ascending ? tree.left() : tree.right();
	    return push(tree, stack, ascending);
	}
    }

    public Object first() {
	return stack.first();
    }

    public ISeq next() {
	Node tree = (Node) stack.first();
	ISeq nextstack = push(ascending ? tree.right() : tree.left(),
			      stack.next(), ascending);
	return (nextstack != null) ? new Seq(nextstack, ascending, count-1)
                                   : null;
    }

    public int count() {
	return (count < 0) ? super.count() : count;
    }

    public Obj withMeta(IPersistentMap meta) {
	return new Seq(meta, stack, ascending, count);
    }
}

static public class NodeIterator implements Iterator {
    Stack parents = new Stack();
    boolean ascending;

    NodeIterator(Node t, boolean ascending) {
	this.ascending = ascending;
	pushBranch(t);
    }

    void pushBranch(Node t) {
	while (t != null) {
	    parents.push(t);
	    t = ascending ? t.left() : t.right();
	}
    }

    public boolean hasNext() {
	return !(parents.isEmpty());
    }

    public Object next() {
	Node t = (Node) parents.pop();
	pushBranch(ascending ? t.right() : t.left());
	return t;
    }

    public void remove() {
	throw new UnsupportedOperationException();
    }
}

static abstract class ASpecificIterator implements Iterator {
    NodeIterator it;

    public boolean hasNext() {
	return it.hasNext();
    }

    public abstract Object next();

    public void remove() {
	throw new UnsupportedOperationException();
    }
}

static class KeyIterator extends ASpecificIterator {
    KeyIterator(NodeIterator it) {
	this.it = it;
    }

    public Object next() {
	return ((Node) it.next()).key;
    }
}

static class ValIterator extends ASpecificIterator {
    ValIterator(NodeIterator it) {
	this.it = it;
    }
    public Object next() {
	return ((Node) it.next()).val;
    }
}

}
