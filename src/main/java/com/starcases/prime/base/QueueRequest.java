package com.starcases.prime.base;

import java.math.BigInteger;
import com.starcases.prime.intfc.PrimeRefIntfc;

public record QueueRequest(QueueOp qop, PrimeRefIntfc prime,  TripleIdx idx)
{
}
