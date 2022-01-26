package com.starcases.prime.cli;

/**
 *
 * enumerations naming the available log production types.
 *
 */
enum LogOper
{
	/**
	 * Logs the default node structure of the dataset including default bases
	 */
	NODESTRUCT,

	/**
	 * Logs prefixes when multiple bases exist which may have a prefix of items; i.e. default bases
	 */
	PREFIX,

	/**
	 * logs the dataset from the graph structure used in visualizations
	 */
	GRAPHSTRUCT,

	/**
	 * Logs bases if nprime or triples generation is used.
	 */
	BASES
}