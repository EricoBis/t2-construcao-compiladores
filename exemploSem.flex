%%

%byaccj

%{
  private Parser yyparser;
  public int yyline = 1;
  
  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

NUM = [0-9]+
NL  = \n|\r|\r\n

%%

"$TRACE_ON"  { yyparser.setDebug(true); }
"$TRACE_OFF" { yyparser.setDebug(false); }
"$MOSTRA_TS" { yyparser.listarTS(); }

[+\-*/=><;,(){}[\]\.] { return (int) yycharat(0); }

"&&" { return Parser.AND; }
"||" { return Parser.OR; }
"==" { return Parser.EQUALS; }
"!=" { return Parser.DIFF; }

{NUM}  { 
    yyparser.yylval = new ParserVal(Integer.parseInt(yytext())); 
    return Parser.NUM; 
}

int    { return Parser.INT; }
float  { return Parser.FLOAT; }
bool   { return Parser.BOOL; }
string { return Parser.STRING; }
void   { return Parser.VOID; }
if     { return Parser.IF; }
struct { return Parser.STRUCT; }
func   { return Parser.FUNC; }
return { return Parser.RETURN; }

[a-zA-Z][a-zA-Z0-9_]* { 
    yyparser.yylval = new ParserVal(yytext());
    return Parser.IDENT; 
}

\"[^\"]*\" { 
    yyparser.yylval = new ParserVal(yytext());
    return Parser.LITERAL; 
}

{NL}   { yyline++; }
[ \t]+ { }

. { 
    System.err.println("Erro léxico: '" + yytext() + "' na linha " + yyline); 
    return YYEOF; 
}

%%