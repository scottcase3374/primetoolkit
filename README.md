# PrimeToolKit

## Personal Project involving prime numbers

## Goals

1. Experimentation with various graph libraries for data visualization
2. Experiment with Picocli command line interface library
3. Experiment with Java features in versions > Java 11
4. Research properties of prime numbers
5. Research representations of prime numbers
	- Sets of 3 unique primes that sum to a prime
	- Multiples of a few low primes such as 1,2,3
4. Research data structures for use as primes numbers become very large
	- BitSets, Lists, BigInteger are the main starting points
	- Further work into other items including
		- Greater than Base-10 representations
		- Run length encoding (of binary probably)
		- Predefined patterns (repeating)
		- Trie style structure
		- Additive method (P-1000 = P-999 + n)
		- Possible combinations of subset of the above

## Reasoning
Given a reasonably powerful desktop computer with 64Gb of memory; Some of the above goals can be done with several million primes and the related data *in memory*. A question is - can a dynamic mix of data representations allow processing significantly more data in memory efficiently? In the process of determining that, some interesting properties of primes may also be found.  Minimum goal would be 15 million primes + associated data with a stretch goal of probably 50-70 million.

Current processing on my i7 with 64Gb RAM reaches about 3-5 million (for a simple base generation) before system speed / stability / etc start to suffer. Graph visualizations slow once you get into the low thousands. Exporting and using other external tools (GML format) performed better for visualizations.