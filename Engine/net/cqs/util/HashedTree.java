package net.cqs.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A tree class that uses a hashmap to speed up some operations.
 * Preliminary implementation that doesn't use a hashmap.
 * 
 * @author Ulf Ochsenfahrt
 */
public final class HashedTree<K,T> implements Serializable, Iterable<T>
{

private static final long serialVersionUID = 1L;

/** Convenience method to create a HashedTree instance of a particular key and value type. */
public static <K,T> HashedTree<K,T> of(Class<K> keyClass, Class<T> valueClass)
{ return new HashedTree<K,T>(keyClass, valueClass); }

	static class Node<K,T> implements Serializable
	{
		private static final long serialVersionUID = 1L;
		
		public final K key;
		public final T value;
		public Node<K,T> parent;
		public Node<K,T> child;
		public Node<K,T> prev, next;
		
		public Node(K key, T value)
		{
			this.key = key;
			this.value = value;
		}
		
		void appendChild(Node<K,T> n)
		{
			if (n == null) throw new NullPointerException();
			if (n.parent != null) throw new IllegalArgumentException();
			if (n.prev != null) throw new IllegalArgumentException();
			if (n.next != null) throw new IllegalArgumentException();
			n.parent = this;
			if (this.child == null)
				this.child = n;
			else
			{
				Node<K,T> temp = this.child;
				while (temp.next != null)
					temp = temp.next;
				n.prev = temp;
				temp.next = n;
			}
		}
		
		void prependChild(Node<K,T> n)
		{
			if (n == null) throw new NullPointerException();
			if (n.parent != null) throw new IllegalArgumentException();
			if (n.prev != null) throw new IllegalArgumentException();
			if (n.next != null) throw new IllegalArgumentException();
			n.parent = this;
			n.next = this.child;
			if (n.next != null) n.next.prev = n;
			this.child = n;
		}
		
		void remove()
		{
			// pull up all children one level
			if (child != null)
			{
				Node<K,T> n = child;
				Node<K,T> urchild = child;
				child = null;
				while (n.next != null)
				{
					n = n.next;
					n.parent = parent;
				}
				n.next = next;
				urchild.parent = parent;
				urchild.prev = this;
				if (next != null) next.prev = n;
				next = urchild;
			}
			
			// remove this node from next/prev chain
			if (prev != null)
			{
				if (parent.child == this) throw new IllegalStateException();
				prev.next = next;
			}
			else if (parent.child == this)
			{
				if (prev != null) throw new IllegalStateException();
				parent.child = next;
			}
			if (next != null)
				next.prev = prev;
		}
	}

private final Class<K> keyClass;
private final Class<T> valueClass;
private final Node<K,T> root = new Node<K,T>(null, null);
private transient HashMap<K,Node<K,T>> hashmap;

public HashedTree(Class<K> keyClass, Class<T> valueClass)
{
	this.keyClass = keyClass;
	this.valueClass = valueClass;
}

private void buildHashMap()
{
	if (hashmap != null) return;
	hashmap = new HashMap<K,Node<K,T>>();
	Node<K,T> n = root.child;
	while (n != null)
	{
		if (hashmap.containsKey(n.key))
			throw new Error("Internal representation is faulty!");
		hashmap.put(n.key, n);
		if (n.child != null)
			n = n.child;
		else if (n.next != null)
			n = n.next;
		else
		{
			while ((n != null) && (n.next != null))
				n = n.parent;
			if (n != null) n = n.next;
		}
	}
}

private void checkKey(K key)
{
	if (key == null) throw new NullPointerException();
	if (!keyClass.isInstance(key)) throw new ClassCastException();
}

private void checkPut(K key, T value)
{
	if (key == null) throw new NullPointerException();
	if (!keyClass.isInstance(key)) throw new ClassCastException();
	if ((value != null) && !valueClass.isInstance(value)) throw new ClassCastException();
	buildHashMap();
	if (findNode(key) != null) throw new IllegalArgumentException();
}

private Node<K,T> findNode(K key)
{
	checkKey(key);
	buildHashMap();
	return hashmap.get(key);
}


public void clear()
{
	root.child = null;
	if (hashmap != null)
		hashmap.clear();
}

public void put(K key, T value)
{
	checkPut(key, value);
	Node<K,T> n = new Node<K,T>(key, value);
	hashmap.put(key, n);
	root.appendChild(n);
}

public void putFirst(K key, T value)
{
	checkPut(key, value);
	Node<K,T> n = new Node<K,T>(key, value);
	hashmap.put(key, n);
	root.prependChild(n);
}

public boolean containsKey(K key)
{
	checkKey(key);
	return findNode(key) != null;
}

public T get(K key)
{
	checkKey(key);
	Node<K,T> n = findNode(key);
	return n == null ? null : n.value;
}

public int size()
{
	buildHashMap();
	return hashmap.size();
}

public boolean isEmpty()
{ return size() == 0; }

public T remove(K key)
{
	checkKey(key);
	Node<K,T> n = findNode(key);
	if (n != null)
	{
		n.remove();
		hashmap.remove(key);
		return n.value;
	}
	return null;
}

@Override
public Iterator<T> iterator()
{
	return new Iterator<T>()
		{
			private Node<K,T> current = root, next = root.child;
			@Override
      public boolean hasNext()
			{ return next != null; }
			@Override
      public T next()
			{
				if (next == null) throw new NoSuchElementException();
				current = next;
				if (next.child != null)
					next = next.child;
				else if (next.next != null)
					next = next.next;
				else
				{
					while ((next != null) && (next.next == null))
						next = next.parent;
					if (next != null) next = next.next;
				}
				return current.value;
			}
			@Override
      public void remove()
			{
				if (current == null) throw new IllegalStateException();
				current.remove();
				current = null;
			}
		};
}

}
