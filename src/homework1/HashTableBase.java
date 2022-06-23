package homework1;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

public abstract class HashTableBase<K, V> implements Iterable<K> {

	protected double loadFactor = 0.5;
	protected int capacity, threshold, modificationCount;
	int hashFunctionMode;

	protected int usedBuckets, keyCount;
	protected K[] keys;
	protected V[] values;
	protected static int collisions_count = 0;

	@SuppressWarnings("unchecked")
	protected final K TOMBSTONE = (K) (new Object());

	private static final int DEFAULT_CAPACITY = 7;

	@SuppressWarnings("unchecked")
	protected HashTableBase(int capacity, double loadFactor, int hashFunctionMode) {
		if (capacity <= 0)
			throw new IllegalArgumentException("Illegal capacity: " + capacity);

		if (loadFactor <= 0 || Double.isNaN(loadFactor) || Double.isInfinite(loadFactor))
			throw new IllegalArgumentException("Illegal loadFactor: " + loadFactor);

		this.loadFactor = loadFactor;
		this.capacity = Math.max(DEFAULT_CAPACITY, capacity);
		this.hashFunctionMode = hashFunctionMode;
		adjustCapacity();
		threshold = (int) (this.capacity * loadFactor);

		keys = (K[]) new Object[this.capacity];
		values = (V[]) new Object[this.capacity];
	}

	protected abstract void setupProbing(K key, int mode);

	protected abstract int probe(int x);

	protected abstract void adjustCapacity();

	protected void increaseCapacity() {
		capacity = (2 * capacity) + 1; // always will give prime number if initially capacity is a prime number
	}

	public void clear() {
		for (int i = 0; i < capacity; i++) {
			keys[i] = null;
			values[i] = null;
		}
		keyCount = usedBuckets = 0;
		modificationCount++;
	}

	public int size() {
		return keyCount;
	}

	public int getCapacity() {
		return capacity;
	}

	public boolean isEmpty() {
		return keyCount == 0;
	}

	public V add(K key, V value) {
		return insert(key, value);
	}

	public boolean containsKey(K key) {
		return hasKey(key);
	}

	public List<K> keys() {
		List<K> hashtableKeys = new ArrayList<>(size());
		for (int i = 0; i < capacity; i++)
			if (keys[i] != null && keys[i] != TOMBSTONE)
				hashtableKeys.add(keys[i]);
		return hashtableKeys;
	}

	public List<V> values() {
		List<V> hashtableValues = new ArrayList<>(size());
		for (int i = 0; i < capacity; i++)
			if (keys[i] != null && keys[i] != TOMBSTONE)
				hashtableValues.add(values[i]);
		return hashtableValues;
	}

	@SuppressWarnings("unchecked")
	protected void resizeTable() {
		increaseCapacity();
		adjustCapacity();

		threshold = (int) (capacity * loadFactor);

		K[] oldKeyTable = (K[]) new Object[capacity];
		V[] oldValueTable = (V[]) new Object[capacity];

		// Perform key table pointer swap
		K[] keyTableTmp = keys;
		keys = oldKeyTable;
		oldKeyTable = keyTableTmp;

		// Perform value table pointer swap
		V[] valueTableTmp = values;
		values = oldValueTable;
		oldValueTable = valueTableTmp;

		// Reset the key count and buckets used since we are about to
		// re-insert all the keys into the hash-table.
		keyCount = usedBuckets = 0;

		for (int i = 0; i < oldKeyTable.length; i++) {
			if (oldKeyTable[i] != null && oldKeyTable[i] != TOMBSTONE)
				insert(oldKeyTable[i], oldValueTable[i]);
			oldValueTable[i] = null;
			oldKeyTable[i] = null;
		}
	}

	// Converts a hash value to an index. Essentially, this strips the
	// negative sign and places the hash value in the domain [0, capacity)
	protected final int normalizeIndex(int keyHash) {
		return (keyHash & 0x7FFFFFFF) % capacity;
	}

	// Finds the greatest common denominator of a and b.
	protected static final int gcd(int a, int b) {
		if (b == 0)
			return a;
		return gcd(b, a % b);
	}

	// Place a key-value pair into the hash-table. If the value already
	// exists inside the hash-table then the value is updated
	public int SSP_hashFunction(K key, int size) {
		char ch[];
		ch = ((String) key).toCharArray();
		int length = ((String) key).length();
		int sum = 0;
		for (int i = 0; i < length; i++) {
			sum += ch[i] - 96;
		}
		return sum % size;
	}

	public int PAF_hashFunction(K key, int size) {
		char ch[];
		int length = ((String) key).length();
		int z = 33;
		int polynom[] = new int[length];
		ch = ((String) key).toCharArray();
		for (int i = 0; i < ch.length; i++) {
			polynom[i] = ch[i] - 96;
		}
		int result = polynom[0];
		for (int i = 1; i < length; i++) {
			result = (result * z + polynom[i]) % size;
		}
		return result;
	}

	static boolean isPrime(int num) {
		if (num <= 1)
			return false;
		if (num <= 3)
			return true;

		if (num % 2 == 0 || num % 3 == 0)
			return false;

		for (int i = 5; i * i <= num; i = i + 6)
			if (num % i == 0 || num % (i + 2) == 0)
				return false;

		return true;
	}

	static int getNextPrime(int num) {// Get next prime number including input number
		boolean flag = false;
		int number = num;
		while (!flag) {
			if (isPrime(number))
				flag = true;
			else
				number++;
		}

		return number;
	}

	public V insert(K key, V val) {
		if (key == null)
			throw new IllegalArgumentException("Null key");
		if (usedBuckets >= threshold)
			resizeTable();

		setupProbing(key, hashFunctionMode);
		int index;
		if (hashFunctionMode == 1)
			index = SSP_hashFunction(key, capacity);
		else
			index = PAF_hashFunction(key, capacity);

		final int offset = normalizeIndex(index);
		for (int i = offset, j = -1, x = 1;; i = normalizeIndex(offset + probe(x++))) {

			// The current slot was previously deleted
			if (keys[i] == TOMBSTONE) {
				if (j == -1)
					j = i;

				// The current cell already contains a key
			} else if (keys[i] != null) {
				// The key we're trying to insert already exists in the hash-table,
				// so update its value with the most recent value
				if (keys[i].equals(key)) {

					V oldValue = values[i];
					if (j == -1) {
						values[i] = val;
					} else {
						keys[i] = TOMBSTONE;
						values[i] = null;
						keys[j] = key;
						values[j] = val;
					}
					modificationCount++;
					return oldValue;
				}

				// Current cell is null so an insertion/update can occur
			} else {
				// No previously encountered deleted buckets
				if (j == -1) {
					usedBuckets++;
					keyCount++;
					keys[i] = key;
					values[i] = val;

					// Previously seen deleted bucket. Instead of inserting
					// the new element at i where the null element is insert
					// it where the deleted token was found.
				} else {
					keyCount++;
					keys[j] = key;
					values[j] = val;
				}

				modificationCount++;
				collisions_count += x;
				return null;
			}
		}
	}

	public V get(K key) {
		if (key == null)
			throw new IllegalArgumentException("Null key");

		setupProbing(key, hashFunctionMode);
		int index;
		if (hashFunctionMode == 1)
			index = SSP_hashFunction(key, capacity);
		else
			index = PAF_hashFunction(key, capacity);
		final int offset = normalizeIndex(index);

		// Starting at the original hash linearly probe until we find a spot where
		// our key is or we hit a null element in which case our element does not exist.
		for (int i = offset, j = -1, x = 1;; i = normalizeIndex(offset + probe(x++))) {

			// Ignore deleted cells, but record where the first index
			// of a deleted cell is found to perform lazy relocation later.
			if (keys[i] == TOMBSTONE) {

				if (j == -1)
					j = i;

				// We hit a non-null key, perhaps it's the one we're looking for.
			} else if (keys[i] != null) {

				// The key we want is in the hash-table!
				if (keys[i].equals(key)) {

					// If j != -1 this means we previously encountered a deleted cell.
					// We can perform an optimization by swapping the entries in cells
					// i and j so that the next time we search for this key it will be
					// found faster. This is called lazy deletion/relocation.
					if (j != -1) {
						// Swap key-values pairs at indexes i and j.
						keys[j] = keys[i];
						values[j] = values[i];
						keys[i] = TOMBSTONE;
						values[i] = null;
						return values[j];
					} else {
						return values[i];
					}
				}

				// Element was not found in the hash-table :/
			} else
				return null;
		}
	}

	public V remove(K key) {
		if (key == null)
			throw new IllegalArgumentException("Null key");

		setupProbing(key, hashFunctionMode);
		int index;
		if (hashFunctionMode == 1)
			index = SSP_hashFunction(key, capacity);
		else
			index = PAF_hashFunction(key, capacity);
		final int offset = normalizeIndex(index);

		// Starting at the hash linearly probe until we find a spot where
		// our key is or we hit a null element in which case our element does not exist
		for (int i = offset, x = 1;; i = normalizeIndex(offset + probe(x++))) {

			// Ignore deleted cells
			if (keys[i] == TOMBSTONE)
				continue;

			// Key was not found in hash-table.
			if (keys[i] == null)
				return null;

			// The key we want to remove is in the hash-table!
			if (keys[i].equals(key)) {
				keyCount--;
				modificationCount++;
				V oldValue = values[i];
				keys[i] = TOMBSTONE;
				values[i] = null;
				return oldValue;
			}
		}
	}

	public boolean hasKey(K key) {
		if (key == null)
			throw new IllegalArgumentException("Null key");

		setupProbing(key, hashFunctionMode);
		int index;
		if (hashFunctionMode == 1)
			index = SSP_hashFunction(key, capacity);
		else
			index = PAF_hashFunction(key, capacity);
		final int offset = normalizeIndex(index);

		// Starting at the original hash linearly probe until we find a spot where
		// our key is or we hit a null element in which case our element does not exist.
		for (int i = offset, j = -1, x = 1;; i = normalizeIndex(offset + probe(x++))) {

			// Ignore deleted cells, but record where the first index
			// of a deleted cell is found to perform lazy relocation later.
			if (keys[i] == TOMBSTONE) {

				if (j == -1)
					j = i;

				// We hit a non-null key, perhaps it's the one we're looking for.
			} else if (keys[i] != null) {

				// The key we want is in the hash-table!
				if (keys[i].equals(key)) {

					// If j != -1 this means we previously encountered a deleted cell.
					// We can perform an optimization by swapping the entries in cells
					// i and j so that the next time we search for this key it will be
					// found faster. This is called lazy deletion/relocation.
					if (j != -1) {
						// Swap the key-value pairs of positions i and j.
						keys[j] = keys[i];
						values[j] = values[i];
						keys[i] = TOMBSTONE;
						values[i] = null;
					}
					return true;
				}

				// Key was not found in the hash-table :/
			} else
				return false;
		}
	}

	public static int getNumberOfTheCollisions() {
		return collisions_count;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		for (int i = 0; i < capacity; i++)
			if (keys[i] != null && keys[i] != TOMBSTONE)
				sb.append(values[i] + " => " + keys[i] + ", ");
		sb.append("}");

		return sb.toString();
	}

	@Override
	public Iterator<K> iterator() {
		// Before the iteration begins record the number of modifications
		// done to the hash-table. This value should not change as we iterate
		// otherwise a concurrent modification has occurred :0
		final int MODIFICATION_COUNT = modificationCount;

		return new Iterator<K>() {
			int index, keysLeft = keyCount;

			@Override
			public boolean hasNext() {
				// The contents of the table have been altered
				if (MODIFICATION_COUNT != modificationCount)
					throw new ConcurrentModificationException();
				return keysLeft != 0;
			}

			// Find the next element and return it
			@Override
			public K next() {
				while (keys[index] == null || keys[index] == TOMBSTONE)
					index++;
				keysLeft--;
				return keys[index++];
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

}
