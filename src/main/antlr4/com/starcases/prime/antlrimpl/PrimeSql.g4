grammar PrimeSql;

options
{
language=Java;
}

BOOL_NOT	: '!'		;
BOOL_OR		: '||'		;
BOOL_AND	: '&&'		;

COMMA		: ','		;

LT			: '<' 		;
GT			: '>' 		;
SEMI		: ';' 		;
GT_EQUAL 	: '>='		;
LT_EQUAL 	: '<='		;

SPLAT		: '*'		;

LBRACE		: '{'		;
RBRACE		: '}'		;
LBRACKET	: '['		;
RBRACKET	: ']'		;

ALL			: [aA][lL][lL];
ANY			: [aA][nN][yY];
BASES		: [bB][aA][sS][eE][sS];
BASE		: [bB][aA][sS][eE];
CONTAINS	: [cC][oO][nN][tT][aA][iI][nN][sS];
INDEX		: [iI][nN][dD][eE][xX];
MATCHED		: [mM][aA][tT][cC][hH][eE][dD];
NO			: [nN][oO];
MAX			: [mM][aA][xX];
ONLY		: [oO][nN][lL][yY];
PARALLEL	: [pP][aA][rR][aA][lL][lL][eE][lL];
PRIMES		: [pP][rR][iI][mM][eE][sS];
SELECT 		: [sS][eE][lL][eE][cC][tT];
USING		: [uU][sS][iI][nN][gG];
WHERE		: [wW][hH][eE][rR][eE];
WITH		: [wW][iI][tT][hH];

INT     	: [0-9]+;
ID			: [a-zA-Z]+[a-zA-Z0-9_]*;
WS 			: [ \t\r\n]+ -> skip;

root :
		stmts EOF
	;

stmts :
		(stmt SEMI)*
	;

stmt :
		select
	;

select :
		SELECT sel_opts select_field WHERE idx_bounds base_match?
	;

sel_opts :
		(USING PARALLEL)?
	;

// For ALL/MATCHED - MATCHED is default
// For each prime
//   ALL means return ALL tuples where any of the tuples matched (if multiple exist)
//   MATCHED means only return the matched tuples (if multiple exist)
select_field :
		sel=SPLAT 	all_or_matched? (idx_sel=NO INDEX)?
	|	sel=PRIMES 	all_or_matched? (WITH idx_sel=INDEX)?
	|	sel=BASES 	all_or_matched? (WITH idx_sel=INDEX)?
	;

all_or_matched :
		sel=ALL MATCHED
	|	sel=ONLY MATCHED
	;

idx_bounds :
		 INDEX (opG=(GT | GT_EQUAL) gval=INT)? (opL=(LT | LT_EQUAL) lval=INT)?
	;

base_match :
		BASE ID CONTAINS array_top_clause # BaseMatch
	;

array_top_clause :
		LBRACKET array_subclause RBRACKET
	;

array_subclause :
 		array_items (COMMA array_items)*
	;

array_items :  // INT represents a Prime Number
		LBRACKET INT (COMMA INT)* RBRACKET # SubArray
	|	INT  # ArrayItem
	;
