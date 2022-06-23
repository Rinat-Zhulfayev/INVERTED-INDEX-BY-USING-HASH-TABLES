package homework1;

import java.math.BigInteger;

public class HashTableDoubleHashing<K, V> extends HashTableBase<K, V> {

	private int hash;

	private int hash2(K k) {
		char ch[];
		ch = ((String) k).toCharArray();
		int length = ((String) k).length();
		int sum = 0;
		for (int i = 0; i < length; i++) {
			sum += ch[i] - 96;
		}

		return 7 - sum % 7;
	}

	// Designated constructor
	public HashTableDoubleHashing(int capacity, double loadFactor, int hashFunctionMode) {
		super(capacity, loadFactor, hashFunctionMode);
	}

	@Override
	protected void setupProbing(K key, int mode) {
		// Cache second hash value.
		hash = normalizeIndex(hash2(key));

		// Fail safe to avoid infinite loop.
		if (hash == 0)
			hash = 1;
	}

	@Override
	protected int probe(int x) {
		return x * hash;
	}

	// Adjust the capacity until it is a prime number. The reason for
	// doing this is to help ensure that the GCD(hash, capacity) = 1 when
	// probing so that all the cells can be reached.
	@Override
	protected void adjustCapacity() {
		while (!(new BigInteger(String.valueOf(capacity)).isProbablePrime(20))) {
			capacity++;
		}
	}
}
