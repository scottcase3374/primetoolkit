grammar PrimeSql;

options
{
language=Java;
}

INT     	: [0-9]+ 	;
LT			: '<' 		;
GT			: '>' 		;
SEMI		: ';' 		;
GT_EQUAL 	: '>='		;
LT_EQUAL 	: '<='		;
SPLAT		: '*'		;
BOOL_NOT	: '!'		;
BOOL_OR		: '||'		;
BOOL_AND	: '&&'		;
LBRACKET	: '['		;
RBRACKET	: ']'		;
COMMA		: ','		;

ALL			: [aA][lL][lL];
BASES		: [bB][aA][sS][eE][sS];
BASE		: [bB][aA][sS][eE];
CONTAINS	: [cC][oO][nN][tT][aA][iI][nN][sS];
INDEX		: [iI][nN][dD][eE][xX];
MAX			: [mM][aA][xX];
PRIMES		: [pP][rR][iI][mM][eE][sS];
SELECT 		: [sS][eE][lL][eE][cC][tT];
WHERE		: [wW][hH][eE][rR][eE];

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
		SELECT select_scope WHERE idx_bounds base_match?
	;

select_scope :
		SPLAT
	|	PRIMES
	|	BASES
	;

idx_bounds :
		 INDEX idx_lobound? idx_hibound?
	;

idx_lobound :  // Index INT starts at 0, represents index of some prime
		GT INT			# GreaterThan
	| 	GT_EQUAL INT	# GreaterEqualThan
	;

idx_hibound : // Index INT starts at 0, represents index of some prime
		LT INT			# LessThan
	| 	LT_EQUAL INT	# LessEqualThan
	;

base_match :
		BASE ID CONTAINS array_clause # MatchArray
	;

array_clause : // Int represents a Prime Number
		LBRACKET INT (COMMA INT)* RBRACKET # Array
	;