package homework1;

public class HashTableLinearProbing<K, V> extends HashTableBase<K, V> {

	// This is the linear constant used in the linear probing, it can be
	// any positive number. The table capacity will be adjusted so that
	// the GCD(capacity, LINEAR_CONSTANT) = 1 so that all buckets can be probed.

	private static final int LINEAR_CONSTANT = 7;

	public HashTableLinearProbing(int capacity, double loadFactor, int hashFunctionMode) {
		super(capacity, loadFactor, hashFunctionMode);
	}

	@Override
	protected void setupProbing(K key, int mode) { // will used in Double hashing
	}

	@Override
	protected int probe(int x) {
		return LINEAR_CONSTANT * x;
	}

	// Adjust the capacity so that the linear constant and
	// the table capacity are relatively prime.
	@Override
	protected void adjustCapacity() {
		while (gcd(LINEAR_CONSTANT, capacity) != 1) {
			capacity++;
		}
	}
}
