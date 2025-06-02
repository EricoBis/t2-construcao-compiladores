//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "exemploSem.y"
  import java.io.*;
  import java.util.*;
  import java.util.stream.*;
//#line 21 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short IDENT=257;
public final static short INT=258;
public final static short FLOAT=259;
public final static short BOOL=260;
public final static short NUM=261;
public final static short STRING=262;
public final static short LITERAL=263;
public final static short AND=264;
public final static short VOID=265;
public final static short MAIN=266;
public final static short IF=267;
public final static short RETURN=268;
public final static short STRUCT=269;
public final static short FUNC=270;
public final static short OR=271;
public final static short EQUALS=272;
public final static short DIFF=273;
public final static short ELSE=274;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    6,    0,    5,    5,    8,    8,    8,   10,   11,    9,
    1,    1,    1,    1,    1,    7,    7,   13,   16,   12,
    3,    3,   14,   14,   17,   17,   18,   15,   19,   19,
   20,   21,   20,   20,   20,   20,   22,    4,   23,   23,
   24,   24,    2,    2,    2,    2,    2,    2,    2,    2,
    2,    2,    2,    2,    2,    2,    2,    2,    2,    2,
};
final static short yylen[] = {                            2,
    0,    3,    2,    0,    1,    3,    6,    0,    0,    8,
    1,    1,    1,    1,    1,    2,    0,    0,    0,    9,
    1,    1,    1,    0,    3,    1,    2,    3,    2,    0,
    2,    0,    6,    3,    5,    3,    0,    5,    1,    0,
    3,    3,    3,    3,    3,    3,    3,    3,    3,    3,
    3,    3,    1,    1,    3,    1,    1,    4,    3,    3,
};
final static short yydefred[] = {                         1,
    0,    0,   15,   11,   12,   13,   14,    0,    0,    0,
    0,    5,    8,    0,    0,    0,    2,    0,    3,    0,
    6,    0,   22,   21,    0,   16,    0,    0,   18,    0,
    0,    0,    9,    7,    0,    0,    0,    0,   23,    0,
   10,   27,   19,    0,    0,   25,   30,   20,    0,    0,
   54,   56,    0,    0,   28,    0,    0,    0,   53,   29,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   31,
    0,    0,    0,   36,   55,    0,   34,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,   60,   58,
    0,    0,   39,   32,    0,    0,   38,    0,   35,   41,
   33,
};
final static short yydgoto[] = {                          1,
    9,   58,   25,   59,   10,    2,   17,   11,   12,   20,
   36,   18,   32,   38,   48,   45,   39,   40,   49,   60,
  108,   62,  102,  103,
};
final static short yysindex[] = {                         0,
    0,   21,    0,    0,    0,    0,    0, -257,  -63, -259,
   21,    0,    0,  -42, -212,   65,    0, -259,    0,  -81,
    0,  -41,    0,    0, -206,    0,   21, -204,    0,  -71,
   -2,   18,    0,    0, -219,   14, -196,   33,    0,   32,
    0,    0,    0, -219,  -46,    0,    0,    0,  201,  -12,
    0,    0,   40,   31,    0,   31, -176,  113,    0,    0,
   31,   43,   31,  -12,  124,   91,  -47,   31,   31,   31,
   31,   31,   31,   31,   31,   31,   31,   31, -160,    0,
  135,   31,  103,    0,    0,   31,    0,   52,   52,   52,
   52,  178,  -16,  -16,   13,   13,  -10,  -10,    0,    0,
  145,   58,    0,    0,  156,   31,    0,   42,    0,    0,
    0,
};
final static short yyrindex[] = {                         0,
    0,    2,    0,    0,    0,    0,    0,    0,    0,  100,
    1,    0,    0,    0,    0,    0,    0,  100,    0,    0,
    0,    0,    0,    0,    0,    0,  -24,    0,    0,    0,
    0,    0,    0,    0,   62,    0,    0,    0,    0,   63,
    0,    0,    0,    0,    0,    0,    0,    0,    0,  -27,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,  -37,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   74,    0,    0,    0,    0,    0,    3,   25,   47,
   69,  167,   34,   61,  434,  439,  393,  429,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,
};
final static short yygindex[] = {                         0,
  198,  275,    0,    0,   10,    0,   99,    0,    0,    0,
    0,    0,    0,    0,    0,    0,   75,    0,    0,   15,
    0,    0,    0,   19,
};
final static int YYTABLESIZE=532;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         13,
    4,    4,   37,   57,   57,   57,   57,   57,   57,   57,
   16,   87,   37,   86,   57,   57,   21,   57,   57,   57,
   19,   57,   57,   57,   57,   77,   75,   15,   76,   79,
   78,   57,   57,   57,   57,   79,   30,    3,    4,    5,
    6,   27,    7,   48,   48,   48,   48,   48,   22,   48,
   29,   28,   31,   33,   77,   57,   34,   35,   79,   78,
   42,   48,   48,   48,   48,   50,   50,   50,   50,   50,
   56,   50,   41,   43,   47,   44,   47,   47,   61,   63,
   67,   56,   82,   50,   50,   50,   50,   51,   51,   51,
   51,   51,   47,   51,   47,   48,   99,   79,  107,   17,
    4,   49,   24,   26,   49,   51,   51,   51,   51,   52,
   52,   52,   52,   52,   40,   52,   26,   50,   46,   49,
    0,   49,  111,    0,  110,    4,   47,   52,   52,   52,
   52,   85,   77,   75,    0,   76,   79,   78,    0,   51,
    0,    0,    0,  104,   77,   75,    0,   76,   79,   78,
   74,   72,   73,   49,   77,   75,    0,   76,   79,   78,
    0,   52,   74,   72,   73,   77,   75,    0,   76,   79,
   78,   80,   74,   72,   73,    0,   77,   75,    0,   76,
   79,   78,   84,   74,   72,   73,   77,   75,  106,   76,
   79,   78,    0,   14,   74,   72,   73,   77,   75,    0,
   76,   79,   78,    0,   74,   72,   73,   59,    0,    0,
   59,    0,    0,   24,  109,   74,   72,   73,    0,   77,
   75,    0,   76,   79,   78,   59,   57,  100,    0,   15,
    0,    0,   37,   57,   57,   57,   57,   74,   72,   73,
   56,   37,    0,   57,   57,   57,   57,   68,    0,    0,
    0,    0,    0,   68,   69,   70,   71,    0,    0,   59,
   69,   70,   71,    0,    0,    0,   48,    0,    0,    0,
    4,    4,    0,   48,   48,   48,   68,    3,    4,    5,
    6,    0,    7,   69,   70,   71,    0,   64,   50,    8,
    0,   51,    0,   52,    0,   50,   50,   50,   50,    4,
    5,    6,   51,    7,   52,   57,    0,    0,   53,   54,
   51,    0,    0,    0,    0,    0,    0,   51,   51,   51,
    0,    3,    4,    5,    6,   55,    7,    0,   65,   23,
   66,    0,   52,    0,    0,   81,    0,   83,    0,   52,
   52,   52,   88,   89,   90,   91,   92,   93,   94,   95,
   96,   97,   98,    0,   68,    0,  101,    0,    0,    0,
  105,   69,   70,   71,    0,    0,   68,    0,    0,    0,
    0,    0,    0,   69,   70,   71,   68,    0,    0,    0,
  101,    0,    0,   69,   70,   71,    0,   68,    0,    0,
    0,    0,    0,    0,   69,   70,   71,    0,   68,    0,
    0,    0,    0,    0,    0,   69,   70,   71,   68,    0,
    0,    0,    0,    0,    0,   69,   70,   71,    0,   68,
    0,    0,    0,    0,    0,    0,   69,   70,   71,    0,
    0,    0,    0,   45,   45,   45,   45,   45,    0,   45,
    0,   68,    0,    0,    0,    0,    0,    0,   69,   70,
   71,   45,   45,   45,   45,    0,    0,   50,    4,    5,
    6,   51,    7,   52,    0,    0,    0,   53,   54,   46,
   46,   46,   46,   46,   43,   46,   43,   43,   43,   44,
    0,   44,   44,   44,    0,   45,    0,   46,   46,   46,
   46,    0,   43,   43,   43,   43,    0,   44,   44,   44,
   44,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   46,    0,    0,    0,    0,   43,    0,    0,    0,
    0,   44,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                        257,
    0,    0,   40,   41,   42,   43,   44,   45,   46,   47,
  270,   59,   40,   61,   42,   43,   59,   45,   46,   47,
   11,   59,   60,   61,   62,   42,   43,   91,   45,   46,
   47,   59,   60,   61,   62,   46,   27,  257,  258,  259,
  260,  123,  262,   41,   42,   43,   44,   45,  261,   47,
  257,   93,  257,  125,   42,   93,   59,   40,   46,   47,
  257,   59,   60,   61,   62,   41,   42,   43,   44,   45,
   40,   47,   59,   41,   41,   44,  123,   44,   91,   40,
  257,   40,   40,   59,   60,   61,   62,   41,   42,   43,
   44,   45,   59,   47,   61,   93,  257,   46,   41,    0,
  125,   41,   41,   41,   44,   59,   60,   61,   62,   41,
   42,   43,   44,   45,   41,   47,   18,   93,   44,   59,
   -1,   61,  108,   -1,  106,  125,   93,   59,   60,   61,
   62,   41,   42,   43,   -1,   45,   46,   47,   -1,   93,
   -1,   -1,   -1,   41,   42,   43,   -1,   45,   46,   47,
   60,   61,   62,   93,   42,   43,   -1,   45,   46,   47,
   -1,   93,   60,   61,   62,   42,   43,   -1,   45,   46,
   47,   59,   60,   61,   62,   -1,   42,   43,   -1,   45,
   46,   47,   59,   60,   61,   62,   42,   43,   44,   45,
   46,   47,   -1,  257,   60,   61,   62,   42,   43,   -1,
   45,   46,   47,   -1,   60,   61,   62,   41,   -1,   -1,
   44,   -1,   -1,   16,   59,   60,   61,   62,   -1,   42,
   43,   -1,   45,   46,   47,   59,  264,   93,   -1,  257,
   -1,   -1,   35,  271,  272,  273,  264,   60,   61,   62,
   40,   44,   -1,  271,  272,  273,   49,  264,   -1,   -1,
   -1,   -1,   -1,  264,  271,  272,  273,   -1,   -1,   93,
  271,  272,  273,   -1,   -1,   -1,  264,   -1,   -1,   -1,
  270,  270,   -1,  271,  272,  273,  264,  257,  258,  259,
  260,   -1,  262,  271,  272,  273,   -1,  257,  264,  269,
   -1,  261,   -1,  263,   -1,  271,  272,  273,  257,  258,
  259,  260,  261,  262,  263,  108,   -1,   -1,  267,  268,
  264,   -1,   -1,   -1,   -1,   -1,   -1,  271,  272,  273,
   -1,  257,  258,  259,  260,  125,  262,   -1,   54,  265,
   56,   -1,  264,   -1,   -1,   61,   -1,   63,   -1,  271,
  272,  273,   68,   69,   70,   71,   72,   73,   74,   75,
   76,   77,   78,   -1,  264,   -1,   82,   -1,   -1,   -1,
   86,  271,  272,  273,   -1,   -1,  264,   -1,   -1,   -1,
   -1,   -1,   -1,  271,  272,  273,  264,   -1,   -1,   -1,
  106,   -1,   -1,  271,  272,  273,   -1,  264,   -1,   -1,
   -1,   -1,   -1,   -1,  271,  272,  273,   -1,  264,   -1,
   -1,   -1,   -1,   -1,   -1,  271,  272,  273,  264,   -1,
   -1,   -1,   -1,   -1,   -1,  271,  272,  273,   -1,  264,
   -1,   -1,   -1,   -1,   -1,   -1,  271,  272,  273,   -1,
   -1,   -1,   -1,   41,   42,   43,   44,   45,   -1,   47,
   -1,  264,   -1,   -1,   -1,   -1,   -1,   -1,  271,  272,
  273,   59,   60,   61,   62,   -1,   -1,  257,  258,  259,
  260,  261,  262,  263,   -1,   -1,   -1,  267,  268,   41,
   42,   43,   44,   45,   41,   47,   43,   44,   45,   41,
   -1,   43,   44,   45,   -1,   93,   -1,   59,   60,   61,
   62,   -1,   59,   60,   61,   62,   -1,   59,   60,   61,
   62,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   93,   -1,   -1,   -1,   -1,   93,   -1,   -1,   -1,
   -1,   93,
};
}
final static short YYFINAL=1;
final static short YYMAXTOKEN=274;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,"'('","')'","'*'","'+'","','",
"'-'","'.'","'/'",null,null,null,null,null,null,null,null,null,null,null,"';'",
"'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
"'['",null,"']'",null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,"'{'",null,"'}'",null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,"IDENT","INT","FLOAT","BOOL","NUM",
"STRING","LITERAL","AND","VOID","MAIN","IF","RETURN","STRUCT","FUNC","OR",
"EQUALS","DIFF","ELSE",
};
final static String yyrule[] = {
"$accept : prog",
"$$1 :",
"prog : $$1 dList mList",
"dList : decl dList",
"dList :",
"decl : declStruct",
"decl : type IDENT ';'",
"decl : type '[' NUM ']' IDENT ';'",
"$$2 :",
"$$3 :",
"declStruct : STRUCT IDENT $$2 '{' dList '}' $$3 ';'",
"type : INT",
"type : FLOAT",
"type : BOOL",
"type : STRING",
"type : IDENT",
"mList : metodo mList",
"mList :",
"$$4 :",
"$$5 :",
"metodo : FUNC typeFunc IDENT $$4 '(' parametros ')' $$5 bloco",
"typeFunc : type",
"typeFunc : VOID",
"parametros : listaParam",
"parametros :",
"listaParam : param ',' listaParam",
"listaParam : param",
"param : type IDENT",
"bloco : '{' listacmd '}'",
"listacmd : listacmd cmd",
"listacmd :",
"cmd : exp ';'",
"$$6 :",
"cmd : IF '(' exp ')' $$6 cmd",
"cmd : type IDENT ';'",
"cmd : type IDENT '=' exp ';'",
"cmd : RETURN exp ';'",
"$$7 :",
"callFunc : IDENT $$7 '(' argsOuSemArgs ')'",
"argsOuSemArgs : args",
"argsOuSemArgs :",
"args : exp ',' args",
"args : exp ',' args",
"exp : exp '+' exp",
"exp : exp '-' exp",
"exp : exp '*' exp",
"exp : exp '/' exp",
"exp : exp '>' exp",
"exp : exp AND exp",
"exp : exp '<' exp",
"exp : exp OR exp",
"exp : exp EQUALS exp",
"exp : exp DIFF exp",
"exp : callFunc",
"exp : NUM",
"exp : '(' exp ')'",
"exp : LITERAL",
"exp : IDENT",
"exp : IDENT '[' exp ']'",
"exp : exp '=' exp",
"exp : exp '.' IDENT",
};

//#line 239 "exemploSem.y"

  private Yylex lexer;

  private TabSimb ts;

  public static TS_entry Tp_INT =  new TS_entry("int", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_FLOAT = new TS_entry("float", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_BOOL = new TS_entry("bool", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_STRING = new TS_entry("string", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_ARRAY = new TS_entry("array", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_STRUCT = new TS_entry("struct", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_ERRO = new TS_entry("_erro_", null, null, ClasseID.TipoBase);
  public static TS_entry Tp_VOID = new TS_entry("void", null, null, ClasseID.TipoBase);

  public static final int ARRAY = 1500;
  public static final int ATRIB = 1600;

  private TS_entry currEscopo;
  private ClasseID currClass;
  private List<TS_entry> paramTypes;
  private int expectedParams;
  private int paramCounter;
  private TS_entry funcEntry;

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }


  public void yyerror (String error) {
    System.err.println ("Erro (linha: "+ lexer.getLine() + ")\tMensagem: "+error);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);

    ts = new TabSimb();

    ts.insert(Tp_ERRO);
    ts.insert(Tp_INT);
    ts.insert(Tp_FLOAT);
    ts.insert(Tp_BOOL);
    ts.insert(Tp_STRING);
    ts.insert(Tp_ARRAY);
    ts.insert(Tp_STRUCT);
    

  }  

  public void setDebug(boolean debug) {
    yydebug = debug;
  }

  public void listarTS() { ts.listar();}

  public static void main(String args[]) throws IOException {
    System.out.println("\n\nVerificador semantico simples\n");
    

    Parser yyparser;
    if ( args.length > 0 ) {
      yyparser = new Parser(new FileReader(args[0]));
    }
    else {
      System.out.println("[Quit with CTRL-D]");
      System.out.print("Programa de entrada:\n");
	  yyparser = new Parser(new InputStreamReader(System.in));
    }

    yyparser.yyparse();

  	yyparser.listarTS();

	  System.out.print("\n\nFeito!\n");
    
  }

   TS_entry validaTipo(int operador, TS_entry A, TS_entry B) {
       
         switch ( operador ) {
              case ATRIB:
                    if ( (A == Tp_INT && B == Tp_INT)                        ||
                         ((A == Tp_FLOAT && (B == Tp_INT || B == Tp_FLOAT))) ||
                         (A ==Tp_STRING)                                     ||
                         (A == B) )
                         return A;
                     else
                         yyerror("(sem) tipos incomp. para atribuicao: "+ A.getTipoStr() + " = "+B.getTipoStr());
                    break;


              case '+' :
                    if ( A == Tp_INT && B == Tp_INT)
                          return Tp_INT;
                    else if ( (A == Tp_FLOAT && (B == Tp_INT || B == Tp_FLOAT)) ||
				              		      (B == Tp_FLOAT && (A == Tp_INT || A == Tp_FLOAT)) ) 
                         return Tp_FLOAT;
                    else if (A==Tp_STRING || B==Tp_STRING)
                        return Tp_STRING;
                    else
                        yyerror("(sem) tipos incomp. para soma: "+ A.getTipoStr() + " + "+B.getTipoStr());
                    break;
              case '-' :
                      if ( A == Tp_INT && B == Tp_INT)
                            return Tp_INT;
                      else if ( (A == Tp_FLOAT && (B == Tp_INT || B == Tp_FLOAT)) ||
                                  (B == Tp_FLOAT && (A == Tp_INT || A == Tp_FLOAT)) ) 
                          return Tp_FLOAT;
                      else
                          yyerror("(sem) tipos incomp. para subtr: "+ A.getTipoStr() + " + "+B.getTipoStr());
                      break;
              case '*' :
                      if ( A == Tp_INT && B == Tp_INT)
                            return Tp_INT;
                      else if ( (A == Tp_FLOAT && (B == Tp_INT || B == Tp_FLOAT)) ||
                                  (B == Tp_FLOAT && (A == Tp_INT || A == Tp_FLOAT)) ) 
                          return Tp_FLOAT;
                      else
                          yyerror("(sem) tipos incomp. para multip: "+ A.getTipoStr() + " + "+B.getTipoStr());
                      break;
              case '/' :
                      if ( A == Tp_INT && B == Tp_INT)
                            return Tp_INT;
                      else if ( (A == Tp_FLOAT && (B == Tp_INT || B == Tp_FLOAT)) ||
                                  (B == Tp_FLOAT && (A == Tp_INT || A == Tp_FLOAT)) ) 
                          return Tp_FLOAT;
                      else
                          yyerror("(sem) tipos incomp. para div: "+ A.getTipoStr() + " + "+B.getTipoStr());
                      break;
             case '>' :
   	              if ((A == Tp_INT || A == Tp_FLOAT) && (B == Tp_INT || B == Tp_FLOAT))
                         return Tp_BOOL;
					        else
                        yyerror("(sem) tipos incomp. para op relacional: "+ A.getTipoStr() + " > "+B.getTipoStr());
			            break;
            case '<' :
   	              if ((A == Tp_INT || A == Tp_FLOAT) && (B == Tp_INT || B == Tp_FLOAT))
                         return Tp_BOOL;
					        else
                        yyerror("(sem) tipos incomp. para op relacional: "+ A.getTipoStr() + " < "+B.getTipoStr());
			            break;
             case AND:
 	                if (A == Tp_BOOL && B == Tp_BOOL)
                         return Tp_BOOL;
					       else
                        yyerror("(sem) tipos incomp. para op lógica: "+ A.getTipoStr() + " && "+B.getTipoStr());
                 break;
            case OR:
 	                if (A == Tp_BOOL && B == Tp_BOOL)
                         return Tp_BOOL;
					       else
                        yyerror("(sem) tipos incomp. para op lógica: "+ A.getTipoStr() + " || "+B.getTipoStr());
                 break;
            case EQUALS:
 	                if ((A == Tp_INT || A == Tp_FLOAT) && (B == Tp_INT || B == Tp_FLOAT))
                         return Tp_BOOL;
					       else
                        yyerror("(sem) tipos incomp. para op lógica: "+ A.getTipoStr() + " == "+B.getTipoStr());
                 break;
            case DIFF:
 	                if ((A == Tp_INT || A == Tp_FLOAT) && (B == Tp_INT || B == Tp_FLOAT))
                         return Tp_BOOL;
					       else
                        yyerror("(sem) tipos incomp. para op lógica: "+ A.getTipoStr() + " != "+B.getTipoStr());
                 break;
             case '[':
                  if (B != Tp_INT)
                    	yyerror("(sem) expressão indexadora deve ser inteira: " + B.getTipoStr());                
                  else if (A.getTipo() != Tp_ARRAY)
	                          yyerror("(sem) var <" + A.getTipoStr() + "> nao é do tipo array");                
								  else 
									   return A.getTipoBase();
                  break;
						}
            return Tp_ERRO;
			}



//#line 574 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
        //#### ERROR CHECK ####
        if (yychar < 0)    //it it didn't work/error
          {
          yychar = 0;      //change it to default string (no -1!)
          if (yydebug)
            yylexdebug(yystate,yychar);
          }
        }//yychar<0
      yyn = yysindex[yystate];  //get amount to shift by (shift index)
      if ((yyn != 0) && (yyn += yychar) >= 0 &&
          yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
        {
        if (yydebug)
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
        //#### NEXT STATE ####
        yystate = yytable[yyn];//we are in a new state
        state_push(yystate);   //save it
        val_push(yylval);      //push our lval as the input for next rule
        yychar = -1;           //since we have 'eaten' a token, say we need another
        if (yyerrflag > 0)     //have we recovered an error?
           --yyerrflag;        //give ourselves credit
        doaction=false;        //but don't process yet
        break;   //quit the yyn=0 loop
        }

    yyn = yyrindex[yystate];  //reduce
    if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
      {   //we reduced!
      if (yydebug) debug("reduce");
      yyn = yytable[yyn];
      doaction=true; //get ready to execute
      break;         //drop down to actions
      }
    else //ERROR RECOVERY
      {
      if (yyerrflag==0)
        {
        yyerror("syntax error");
        yynerrs++;
        }
      if (yyerrflag < 3) //low error count?
        {
        yyerrflag = 3;
        while (true)   //do until break
          {
          if (stateptr<0)   //check for under & overflow here
            {
            yyerror("stack underflow. aborting...");  //note lower case 's'
            return 1;
            }
          yyn = yysindex[state_peek(0)];
          if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
            if (yydebug)
              debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
            yystate = yytable[yyn];
            state_push(yystate);
            val_push(yylval);
            doaction=false;
            break;
            }
          else
            {
            if (yydebug)
              debug("error recovery discarding state "+state_peek(0)+" ");
            if (stateptr<0)   //check for under & overflow here
              {
              yyerror("Stack underflow. aborting...");  //capital 'S'
              return 1;
              }
            state_pop();
            val_pop();
            }
          }
        }
      else            //discard this token
        {
        if (yychar == 0)
          return 1; //yyabort
        if (yydebug)
          {
          yys = null;
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
          debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
          }
        yychar = -1;  //read another
        }
      }//end error recovery
    }//yyn=0 loop
    if (!doaction)   //any reason not to proceed?
      continue;      //skip action
    yym = yylen[yyn];          //get count of terminals on rhs
    if (yydebug)
      debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 28 "exemploSem.y"
{ currEscopo = null; currClass = ClasseID.VarGlobal; }
break;
case 6:
//#line 34 "exemploSem.y"
{  TS_entry nodo = ts.pesquisa(val_peek(1).sval);
    	                if (nodo != null) 
                            yyerror("(sem) variavel >" + val_peek(1).sval + "< jah declarada");
                        else ts.insert(new TS_entry(val_peek(1).sval, (TS_entry)val_peek(2).obj, currEscopo, currClass), currEscopo); 
                        }
break;
case 7:
//#line 40 "exemploSem.y"
{  TS_entry nodo = ts.pesquisa(val_peek(1).sval);
    	                  if (nodo != null) 
                            yyerror("(sem) variavel >" + val_peek(1).sval + "< jah declarada");
                    else ts.insert(new TS_entry(val_peek(1).sval, Tp_ARRAY, val_peek(3).ival, (TS_entry)val_peek(5).obj, currEscopo, currClass), currEscopo); 
                     }
break;
case 8:
//#line 47 "exemploSem.y"
{  TS_entry nodo = ts.pesquisa(val_peek(0).sval);
    	                    if (nodo != null) 
                                 yyerror("(sem) variavel >" + val_peek(0).sval + "< jah declarada");
                            else {
                                nodo = new TS_entry(val_peek(0).sval, Tp_STRUCT, currEscopo, ClasseID.NomeStruct);
                                ts.insert(nodo, currEscopo);
 								currEscopo = nodo; 
                                currClass = ClasseID.CampoStruct; 
                                }
                            }
break;
case 9:
//#line 59 "exemploSem.y"
{ currEscopo = null; currClass = ClasseID.VarGlobal; }
break;
case 11:
//#line 63 "exemploSem.y"
{ yyval.obj = Tp_INT; }
break;
case 12:
//#line 64 "exemploSem.y"
{ yyval.obj = Tp_FLOAT; }
break;
case 13:
//#line 65 "exemploSem.y"
{ yyval.obj = Tp_BOOL; }
break;
case 14:
//#line 66 "exemploSem.y"
{ yyval.obj = Tp_STRING; }
break;
case 15:
//#line 67 "exemploSem.y"
{ TS_entry nodo = ts.pesquisa(val_peek(0).sval);
    	                if (nodo == null ) 
                           yyerror("(sem) Nome de tipo <" + val_peek(0).sval + "> nao declarado ");
                        else 
                            yyval.obj = nodo;
                     }
break;
case 18:
//#line 77 "exemploSem.y"
{
          TS_entry existing = ts.pesquisa(val_peek(0).sval);
          if (existing != null) {
              yyerror("Função '" + val_peek(0).sval + "' já declarada");
          } else {
              existing = new TS_entry(val_peek(0).sval, (TS_entry)val_peek(1).obj, currEscopo, ClasseID.NomeFuncao);
              ts.insert(existing, currEscopo);
              currEscopo = existing;
              currClass = ClasseID.NomeParam;
          }
        }
break;
case 19:
//#line 88 "exemploSem.y"
{
            currEscopo = currEscopo;
            currClass = ClasseID.VarLocal;
        }
break;
case 20:
//#line 92 "exemploSem.y"
{
            currEscopo = null;
            currClass = ClasseID.VarGlobal;
        }
break;
case 22:
//#line 97 "exemploSem.y"
{ yyval.obj = Tp_VOID; }
break;
case 27:
//#line 103 "exemploSem.y"
{
        TS_entry existing = ts.pesquisa(val_peek(0).sval, currEscopo);
        if (existing != null) {
            yyerror("Parâmetro '" + val_peek(0).sval + "' já declarado");
        } else {
            ts.insert(new TS_entry(val_peek(0).sval, (TS_entry)val_peek(1).obj, currEscopo, currClass), currEscopo);
        }
      }
break;
case 32:
//#line 119 "exemploSem.y"
{
          if (((TS_entry)val_peek(1).obj).getTipo() != Tp_BOOL.getTipo()) {
              yyerror("Condição do IF deve ser booleana");
          }
      }
break;
case 34:
//#line 125 "exemploSem.y"
{
          TS_entry existing = ts.pesquisa(val_peek(1).sval);
          if (existing != null) {
              yyerror("Variável " + val_peek(1).sval + "' já declarada");
          } else {
            existing = ts.pesquisa(val_peek(1).sval);
            if(existing != null){
                yyerror("Variável local '" + val_peek(1).sval + "' já declarada");
            }else{
              ts.insert(new TS_entry(val_peek(1).sval, (TS_entry)val_peek(2).obj, currEscopo, currClass), currEscopo);
            }
          }
      }
break;
case 35:
//#line 138 "exemploSem.y"
{
          TS_entry existing = ts.pesquisa(val_peek(3).sval, currEscopo);
          if (existing != null) {
              yyerror("Variável local '" + val_peek(3).sval + "' já declarada");
          } else {
              TS_entry newVar = new TS_entry(val_peek(3).sval, (TS_entry)val_peek(4).obj, currEscopo, currClass);
              ts.insert(newVar, currEscopo);
              validaTipo(ATRIB, newVar.getTipo(), (TS_entry)val_peek(1).obj);
          }
      }
break;
case 36:
//#line 148 "exemploSem.y"
{
          if (((TS_entry)currEscopo).getTipo() == Tp_FLOAT) {
                        if (((TS_entry)val_peek(1).obj)!= Tp_INT && ((TS_entry)val_peek(1).obj) != Tp_FLOAT) {
                              yyerror("o retorno do tipo " + ((TS_entry)val_peek(1).obj).getTipoStr() + " é inválido para uma funcao " + ((TS_entry)currEscopo).getTipoString());
                        }
                        } else if (((TS_entry)currEscopo).getTipo() != ((TS_entry)val_peek(1).obj)) {
                              yyerror("o retorno do tipo " + ((TS_entry)val_peek(1).obj).getTipoStr() + " é inválido para uma funcao " + ((TS_entry)currEscopo).getTipoString());
                        }
      }
break;
case 37:
//#line 158 "exemploSem.y"
{
          TS_entry existing = ts.pesquisa(val_peek(0).sval);
          if (existing == null) {
              yyerror("Função '" + val_peek(0).sval + "' não declarada");
          } else {
            funcEntry = existing;
            expectedParams = funcEntry.getLocalTS().getLista().stream().filter(p -> p.Classe() == ClasseID.NomeParam).collect(Collectors.toList()).size();
            paramCounter = 0;
          }
      }
break;
case 38:
//#line 168 "exemploSem.y"
{
          if (paramCounter != expectedParams) {
              yyerror("Número de parâmetros incorreto. Esperado >" + expectedParams + "<, encontrado >" + paramCounter + "<.");
          } else {
              var expectedTypes = funcEntry.getLocalTS().getLista()
                  .stream().map(TS_entry::getTipo).collect(Collectors.toList());
                  
              for (int i = 0; i < paramCounter; i++) {
                  if (expectedTypes.get(i) != paramTypes.get(i)) {
                      yyerror("Tipo do parâmetro " + (i+1) + " incompatível");
                  }
              }
          }
          yyval.obj = funcEntry.getTipo();
      }
break;
case 41:
//#line 186 "exemploSem.y"
{
          paramTypes.add((TS_entry)val_peek(2).obj);
          paramCounter++;
      }
break;
case 42:
//#line 190 "exemploSem.y"
{
          paramTypes.add((TS_entry)val_peek(2).obj);
          paramCounter++;
      }
break;
case 43:
//#line 195 "exemploSem.y"
{ yyval.obj = validaTipo('+', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj); }
break;
case 44:
//#line 196 "exemploSem.y"
{ yyval.obj = validaTipo('-', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);}
break;
case 45:
//#line 197 "exemploSem.y"
{ yyval.obj = validaTipo('*', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);}
break;
case 46:
//#line 198 "exemploSem.y"
{ yyval.obj = validaTipo('/', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);}
break;
case 47:
//#line 199 "exemploSem.y"
{ yyval.obj = validaTipo('>', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);}
break;
case 48:
//#line 200 "exemploSem.y"
{ yyval.obj = validaTipo(AND, (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj); }
break;
case 49:
//#line 201 "exemploSem.y"
{ yyval.obj = validaTipo('<', (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);}
break;
case 50:
//#line 202 "exemploSem.y"
{ yyval.obj = validaTipo(OR, (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj); }
break;
case 51:
//#line 203 "exemploSem.y"
{ yyval.obj = validaTipo(EQUALS, (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj); }
break;
case 52:
//#line 204 "exemploSem.y"
{ yyval.obj = validaTipo(DIFF, (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj); }
break;
case 54:
//#line 206 "exemploSem.y"
{ yyval.obj = Tp_INT; }
break;
case 55:
//#line 207 "exemploSem.y"
{ yyval.obj = val_peek(1).obj; }
break;
case 56:
//#line 208 "exemploSem.y"
{ yyval.obj = Tp_STRING; }
break;
case 57:
//#line 209 "exemploSem.y"
{ TS_entry nodo = ts.pesquisa(val_peek(0).sval);
    	                 if (nodo == null) 
	                        yyerror("(sem) var <" + val_peek(0).sval + "> nao declarada");                
                      else
			                    yyval.obj = nodo.getTipo();
			            }
break;
case 58:
//#line 216 "exemploSem.y"
{ TS_entry nodo = ts.pesquisa(val_peek(3).sval);
    	             if (nodo == null) 
	                     yyerror("(sem) var <" + val_peek(3).sval + "> nao declarada");                
                   else
                       yyval.obj = validaTipo('[', nodo, (TS_entry)val_peek(1).obj);
						     }
break;
case 59:
//#line 223 "exemploSem.y"
{  yyval.obj = validaTipo(ATRIB, (TS_entry)val_peek(2).obj, (TS_entry)val_peek(0).obj);                      
                 }
break;
case 60:
//#line 226 "exemploSem.y"
{ if (((TS_entry)val_peek(2).obj).getTipo() != Tp_STRUCT) 
	                     yyerror("(sem) Esperado tipo STRUCT e recebido >" + ((TS_entry)val_peek(2).obj).getId() + ">" + val_peek(2).obj);                
                   else {
                         TS_entry nodo = ((TS_entry)val_peek(2).obj).getLocalTS().pesquisa(val_peek(0).sval);
                         if (nodo == null)
                            yyerror("(sem) <" +val_peek(0).sval+"> não é campo da STRUCT <"+ ((TS_entry)val_peek(2).obj).getId() + ">");                
                         else 
                             yyval.obj = nodo.getTipo();
                       }
						     }
break;
//#line 1010 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################
