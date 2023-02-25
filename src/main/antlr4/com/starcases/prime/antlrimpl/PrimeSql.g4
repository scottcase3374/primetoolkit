grammar PrimeSql;

options
{
language=Java;
}

INT     : [0-9]+ ;
LT		: '<' ;
GT		: '>' ;
SEMI	: ';' ;

SELECT 	: 'select' ;
WHERE	: 'where' ;

ID		: [a-zA-Z]+[a-zA-Z0-9]* ;
WS 		: [ \t\r\n]+ -> skip ;

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
	SELECT sel_ret sel_where
	;

sel_ret :
	ID
	;

sel_where :
	WHERE expr
	;

expr :
		GT INT	# GreaterThan
	| 	LT INT	# LessThan
	;