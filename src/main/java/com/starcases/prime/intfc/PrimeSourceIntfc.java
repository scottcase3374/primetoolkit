package com.starcases.prime.intfc;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import javax.validation.constraints.Min;

import lombok.NonNull;

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
	
	// NOTE: if an interface is used then end-developers can provide new types.  Need to think about further.
	BaseTypes getActiveBaseId();
	void setActiveBaseId(@NonNull BaseTypes activeBaseId);
	boolean baseExist(@NonNull BaseTypes baseId);
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
	Optional<PrimeRefIntfc> getPrimeRef(@NonNull BigInteger val);
	
	/**
	 * Get prime ref nearest provided value.
	 *   
	 * @param val If positive then looking for 'next' value; if negative then looking for an earlier value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getNearPrimeRef(@NonNull BigInteger val);	

	
	/**
	 * Get prime ref nearest provided value. Typical use is locating a prime above/below some
	 * fractional value.
	 *   
	 * @param val If positive then looking for 'next' value; if negative then looking for an earlier value.

	 * @return
	 */
	Optional<PrimeRefIntfc> getNearPrimeRef(@NonNull BigDecimal val);

	/**
	 * Find/return first prime (if any exist) within the provided prime offset from the provided idx.
	 *  
	 * @param val Must be positive.
	 * @param maxPrimeOffset  If positive then looking for 'next' value; if negative then looking for an earlier value.
	
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@Min(0) int idx, @NonNull BigInteger targetOffset);
	
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
	Optional<PrimeRefIntfc> getPrimeRefWithinOffset(@Min(0) int idx, @NonNull BigDecimal targetOffset);
	
	
	/**
	 * Utility method for finding exact prime using an offset. logically equivalent to calling  getPrimeRef(val+ offset)  
	 * See @getPrime(BigInteger val) for details.
	 * 
	 * @param val Must be positive
	 * @param exactOffset Can be positive or negative.
	 * 
	 * @return
	 */
	Optional<PrimeRefIntfc> getPrimeRef(@NonNull BigInteger val, @NonNull BigInteger exactOffset);
		
	/**
	 * Returns    value of primeRef+1 minus value of primeref
	 * @param prime
	 * 
	 * @return
	 */
	Optional<BigInteger>    getDistToNextPrime(@NonNull PrimeRefIntfc prime);	
	
	
	Optional<BigInteger>    getDistToPrevPrime(@NonNull PrimeRefIntfc prime);

	/**
	 * Diff of primes at the 2 indexes
	 * @param idx1
	 * @param idx2
	 * @return
	 */
	Optional<BigInteger> getDistBetween(@Min(0) int idx1, @Min(1) int idx2);
	
	// Trying to move off of these index based calls.
	// Moving off the index based calls would simplify using alternative implementations for
	// which an 'index' value might not be a good fit for.
	
	// index based - relates to primerefintfc's
	int getMaxIdx();
	
	// index based oriented - index to primeref intfc
	int getNextLowPrimeIdx(@NonNull @Min(1) BigInteger val);
	int getNextHighPrimeIdx(@NonNull @Min(1) BigInteger val);
	
	int getNextLowPrimeIdx(@NonNull @Min(1) BigDecimal val);
	int getNextHighPrimeIdx(@NonNull @Min(1) BigDecimal val);
	
	// Index based
	Optional<PrimeRefIntfc> getPrimeRef(@Min(0) int primeIdx);	
	Optional<BigInteger> getPrime(@Min(0) int primeIdx);
	int getPrimeIdx(@NonNull @Min(1) BigInteger val);

	// index based - related to diff of the actual prime values.
	BigInteger getDistToNextPrime(@Min(0) int curIdx);
	BigInteger getDistToPrevPrime(@Min(0) int curIdx);	
	
	
	boolean distinct(@NonNull PrimeRefIntfc [] vals);
}
