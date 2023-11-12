package com.starcases.prime.sql.api;

public interface PrimeResultIntfc
{
	void setResult(final CharSequence result);
	CharSequence getResult();

	void setError(final CharSequence error);
	CharSequence getError();
}
