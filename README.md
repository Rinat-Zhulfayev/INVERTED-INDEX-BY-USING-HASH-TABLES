# INVERTED-INDEX-BY-USING-HASH-TABLES
An inverted index is an index data structure, which is used to map all documents with their content.

1. Main Functionalities
• put(Key k, Value v)
If a word (k) is already present, then add a reference of the document (v) to the index; otherwise
create a new entry. You should store the frequency of each word with the document identifier.

• Value get(Key k)
Search the given word (k) in the hash table. If the word is available in the table, then return an
output as shown below, otherwise return a “not found” message to the user

• remove(Key k)
Remove the given word (k) and the associated value from the inverted index.

• resize(int capacity)
Make the hash table dynamically growable. Put method should double the current table size if
the hash table reach the maximum load factor.

2. Hash Function
• Simple Summation Function (SSF)
• Polynomial Accumulation Function (PAF)

3. Collision Handling
• Linear Probing (LP)
• Double Hashing (DH)

