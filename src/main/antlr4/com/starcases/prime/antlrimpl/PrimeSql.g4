grammar PrimeSql;

options
{
language=Java;
}

INT     : [0-9]+ ;
LT		: '<' ;
GT		: '>' ;
SEMI	: ';' ;

SELECT 	: [sS][eE][lL][eE][cC][tT] ;
WHERE	: [wW][hH][eE][rR][eE] ;
INDEX	: [iI][nN][dD][eE][xX] ;

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
	SELECT sel_where_index
	;

sel_where_index :
	WHERE INDEX expr
	;

expr :
	lobound? hibound?
	;

lobound :
	GT INT	# GreaterThan // Index INT starts at 0
	;
hibound :
	LT INT	# LessThan // Index INT starts at 0
	;

