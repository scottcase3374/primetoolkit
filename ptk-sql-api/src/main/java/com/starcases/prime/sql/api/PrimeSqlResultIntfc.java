package com.starcases.prime.sql.api;

public interface PrimeSqlResultIntfc
{
	void setResult(final CharSequence result);
	CharSequence getResult();

	void setError(final CharSequence error);
	CharSequence getError();
}
