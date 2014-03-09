package webcat.util.java;

import java.util.*;
import clojure.lang.*;

public class PersistentBST
    extends APersistentMap
    implements IObj, Reversible, Sorted {

    public final Comparator comp;
    public final Node tree;
    public final int _count;
    final IPersistentMap _meta;

    final static public PersistentBST EMPTY = new PersistentBST();

    static public IPersistentMap create(Map other) {
        IPersistentMap ret = EMPTY;
        for (Object o : other.entrySet()) {
            Map.Entry e = (Entry) o;
            ret = ret.assoc(e.getKey(), e.getValue());
        }
        return ret;
    }

    public PersistentBST() {
        this(RT.DEFAULT_COMPARATOR);
    }

    public PersistentBST withMeta(IPersistentMap meta) {
        return new PersistentBST(meta, comp, tree, _count);
    }

    private PersistentBST(Comparator comp) {
        this(null, comp);
    }

    public PersistentBST(IPersistentMap meta, Comparator comp,
			 Node tree, int _count) {
	this.comp = comp;
	this._meta = meta;
	tree = null;
	_count = 0;
    }

    PersistentBST(IPersistentMap meta, Comparator comp,
		      Node tree, int _count) {
	this._meta = meta;
	this.comp = comp;
	this.tree = tree;
	this._count = _count;
    }

    static public PersistentBST create(ISeq items) {
	IPersistentMap ret = EMPTY;
	for(; items != null; items = items.next().next()) {
	    if (items.next() == null)
		throw new IllegalArgumentException(String.format(
		    "No value supplied for key: %s", items.first()
		));
	    ret = ret.assoc(items.first(), RT.second(items));
	}
	return (PersistentBST) ret;
    }

    static public PersistentBST create(Comparator comp, ISeq items) {
	IPersistentMap ret = new PersistentBST(comp);
	for(; items != null; items = items.next().next()) {
	    if (items.next() == null)
		throw new IllegalArgumentException(String.format(
		    "No value supplied for key: %s", items.first()
	        ));
	    ret = ret.assoc(items.first(), RT.second(items));
	}
	return (PersistentBST) ret;
    }

    public boolean containsKey(Object key) {
	return entryAt(key) != null;
    }

    // line 97 of PersistentTreeMap.java //
    public PersistentBST assocEx(Object key, Object val) {
	Box found = new Box(null);
	Node t = add(tree, key, val, found);
	if (t == null) // null == already contains key
	    throw clojure.lang.Util.runtimeException("Key already present");
	// PersistentTreeMap passes t.blacken(), but that's for a RBT.
	// I'm going to assume that just balances the tree, which I'm not too
	// worried about
	return new PersistentBST(comp, t, _count + 1, meta());
    }

    public PersistentBST assoc(Object key, Object val) {
	Box found = new Box(null);
	Node t = add(tree, key, val, found);
	if (t == null) { // null == already contains key
	    Node foundNode = (Node) found.val;
	    // note only get same collection on identity of val, not equals()
	    if (foundNode.val() == val)
		return this;
	    return new PersistentBST(comp, replace(tree, key, val),
				     _count, meta());
	}
	return new PersistentBST(comp, t, _count + 1, meta());
    }

    public PersistentBST without(Object key) {
	Box found = new Box(null);
	Node t = remove(tree, key, found);
	if (t == null) {
	    // doesn't contain key
	    if (found.val == null)
		return this;
	    // tree is empty
	    return new PersistentBST(meta(), comp);
	}
	// PersistentTreeMap uses t.blacken()
	return new PersistentBST(comp, t, _count - 1, meta());
    }

    // line 134 of PersistentTreeMap
    public ISeq seq() {
	if (_count > 0)
	    return Seq.create(tree, false, _count);
	return null;
    }

    public IPersistentCollection empty() {
	return new PersistentBST(meta(), comp);
    }

    public ISeq rseq() {
	if (_count > 0)
	    return Seq.create(tree, false, _count);
	return null;
    }

    public Comparator comparator() {
	return comp;
    }

    public Object entryKey(Object entry) {
	return ((IMapEntry) entry).key();
    }

    public ISeq seq(boolean ascending) {
	if (_count > 0)
	    return Seq.create(tree, ascending, _count);
	return null;
    }

    public ISeq seqFrom(Object key, boolean ascending) {
	if (_count > 0) {
	    ISeq stack = null;
	    Node t = tree;
	    while (t != null) {
		int c = doCompare(key, t.key);
		if (c == 0) {
		    stack = RT.cons(t, stack);
		    return new Seq(stack, ascending);
		}
		else if (ascending) {
		    if (c < 0) {
			stack = RT.cons(t, stack);
			t = t.left();
		    }
		    else
			t = t.right();
		}
		else {
		    if (c > 0) {
			stack = RT.cons(t, stack);
			t = t.right();
		    }
		    else
			t = t.left();
		}
	    }
	    if (stack != null)
		return new Seq(stack, ascending);
	}
	return null;
    }

    public NodeIterator iterator() {
	return new NodeIterator(tree, true);
    }

    public Object kvreduce(IFn f, Object init) {
	if (tree != null)
	    init = tree.kvreduce(f, init);
	if (RT.isReduced(init))
	    init = ((IDeref) init).deref();
	return init;
    }

    public NodeIterator reverseIterator() {
	return new NodeIterator(tree, false);
    }

    public Iterator keys() {
	return keys(iterator());
    }

    public Iterator vals() {
	return vals(iterator());
    }

    public Iterator keys(NodeIterator it) {
	return new KeyIterator(it);
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
	if (t != null)
	    while(t.left() != null)
		t = t.left();
	return t;
    }

    public Object maxKey() {
	Node t = max();
	return (t != null) ? t.key : null;
    }

    public Node max() {
	Node t = tree;
	if (t != null)
	    while(t.right() != null)
		t = t.right();
	return t;
    }

    public int depth() {
	return depth(tree);
    }

    int depth(Node t) {
	if (t == null)
	    return 0;
	return 1 + Math.max(depth(t.left()), depth(t.right()));
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

    public int doCompare(Object k1, Object k2) {
	return comp.compare(k1, k2);
    }

    Node add(Node t, Object key, Object val, Box found) {
	if (t == null)
	    if (val == null)
		return new Node(key);
	    else
		return new Node(key, val);
	int c = doCompare(key, t.key);
	if (c == 0) {
	    found.val = t;
	    return null;
	}
	Node ins = c < 0 ? add(t.left(),  key, val, found)
                         : add(t.right(), key, val, found);
	if (ins == null) // found below
	    return null;
	if (c < 0)
	    return t.addLeft(ins);
	else
	    return t.addRight(ins);
    }

    Node remove(Node t, Object key, Box found) {
	if (t == null)
	    return null; // not found signal
	int c = doCompare(key, t.key);
	if (c == 0) {
	    found.val = t;
	    return append(t.left(), t.right());
	}
	Node del = c < 0 ? remove(t.left(),  key, found)
	                 : remove(t.right(), key, found);
	if (del == null && found.val == null) // not found below
	    return null;
	if (c < 0)
	    return t.removeLeft(del);
	else
	    return t.removeRight(del);
    }
    //362
    static Node append(Node left, Node right) {
	if (left == null)
	    return right;
	else if (right == null)
	    return left;
	
}
