package homework1;

import java.security.InvalidKeyException;
import java.util.Iterator;

public interface DictionaryInterface<K, V> {
	public V add(K key, V value) throws InvalidKeyException;

	public V remove(K key);

	public V getValue(K key) throws InvalidKeyException;

	public boolean containsK(K key);

	public boolean containsV(V value);

	public Iterator<K> getKeyIterator();

	public Iterator<V> getValueIterator();

	public boolean isEmpty();

	public int getSize();

	public void clear();

	public void displayHashTable();

	public int getNumberOfProbes();

	public int findEntry(K key) throws InvalidKeyException;
}
