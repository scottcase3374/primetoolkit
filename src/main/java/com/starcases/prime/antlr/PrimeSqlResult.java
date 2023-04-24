package com.starcases.prime.antlr;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class PrimeSqlResult
{
	@Getter
	@Setter
	private String result = "<empty>";

	@Getter
	@Setter
	private String error = null;
}
