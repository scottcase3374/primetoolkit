package com.starcases.prime.intfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.Iterator;

/**
 * 
 * Main interface for needs related to prime / base generation and alternative
 * algs.
 *
 */
public interface PrimeSourceIntfc
{
	//
	// initialization
	//
	
	void init();
	
	//
	// non-navigation related / base selection
	// 
	
	// @TODO note that if an interface is used then end-developers can provide new types.  Need to think about further.
	BaseTypes getActiveBaseId();
	void setActiveBaseId(BaseTypes activeBaseId);
	boolean baseExist(BaseTypes baseId);
	//
	// Navigation - non-index based api
	//
	
	/**
	 * Iterator to PrimeRefIntfc instances.
	 * 
	 * @return
	 */
	Iterator<PrimeRefIntfc> getPrimeRefIter();

	
	// 
	// Search and retrieve - non-index apis
	//
	
	/**
	 * Get exact prime ref instance for value if it is a prime and was previously identified and a prime ref was created.
	 * 
	 * @param val Must be positive value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(BigInteger val);
	
	/**
	 * Get prime ref nearest provided value.
	 *   
	 * @param val If positive then looking for 'next' value; if negative then looking for an earlier value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getNearPrimeRef(BigInteger val);	

	
	/**
	 * Get prime ref nearest provided value. Typical use is locating a prime above/below some
	 * fractional value.
	 *   
	 * @param val If positive then looking for 'next' value; if negative then looking for an earlier value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getNearPrimeRef(BigDecimal val);

	/**
	 * Find/return first prime (if any exist) that is within the provided prime offset from the provided value.
	 *  
	 * @param val Must be positive.
	 * @param maxPrimeOffset  If positive then looking for 'next' value; if negative then looking for an earlier value.
	
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(BigInteger val, BigInteger maxPrimeOffset);
	
	/**
	 * Find/return first prime (if any exist) that is within the provided prime offset from the provided value.
	 *  
	 * Typical use is locating a prime above/below some
	 * fractional value.
	 * 
	 * @param val Must be positive.
	 * @param maxPrimeOffset  If positive then looking for 'next' value; if negative then looking for an earlier value.
	
	 * @return
	 */	
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(BigDecimal val, BigDecimal maxPrimeOffset);
	
	
	/**
	 * Utility method for finding exact prime using an offset. logically equivalent to calling  getPrimeRef(val+ offset)  
	 * See @getPrime(BigInteger val) for details.
	 * 
	 * @param val Must be positive
	 * @param exactOffset Can be positive or negative.
	 * 
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(BigInteger val, BigInteger exactOffset);
		
	/**
	 * Returns    value of primeRef+1 minus value of primeref
	 * @param prime
	 * 
	 * @return
	 */
	Optional<BigInteger>    getDistToNextPrime(PrimeRefIntfc prime);	
	
	
	Optional<BigInteger>    getDistToPrevPrime(PrimeRefIntfc prime);

	// Trying to move off of these index based calls.
	// Moving off the index based calls would simplify using alternative implementations for
	// which an 'index' value might not be a good fit for.
	
	// index based - relates to primerefintfc's
	int getMaxIdx();
	
	// index based oriented - index to primeref intfc
	int getNextLowPrimeIdx(BigInteger val);
	int getNextHighPrimeIdx(BigInteger val);
	
	int getNextLowPrimeIdx(BigDecimal val);
	int getNextHighPrimeIdx(BigDecimal val);
	
	// Index based
	Optional<PrimeRefIntfc> getPrimeRef(int primeIdx);	
	BigInteger getPrime(int primeIdx);
	int getPrimeIdx(BigInteger val);

	// index based - related to diff of the actual prime values.
	BigInteger getDistToNextPrime(int curIdx);
	BigInteger getDistToPrevPrime(int curIdx);	
	
	
	boolean distinct(PrimeRefIntfc [] vals);
	
}
