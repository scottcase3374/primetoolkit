package com.starcases.prime.base.triples.impl;

import com.starcases.prime.kern.api.BaseTypesIntfc;

public enum TripleBaseType implements BaseTypesIntfc
{

	/**
	 * Base entries are a total of exactly 3 values; handle ALL combinations by default.
	 *
	 * List of 3 values makes 1 base.  There are X bases (combinations) per prime.
	 *
	 * Example for Prime 773; each triple below sums to 773.
	 * There are actually 796 bases with only 4 shown here.
	 *
	 * [241,263,269], [239,263,271], [233,269,271], [239,257,277]
	 */
	TRIPLE ;
}
