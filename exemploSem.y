%{
  import java.io.*;
  import java.util.*;
  import java.util.stream.*;
%}


%token IDENT, INT, FLOAT, BOOL, NUM, STRING
%token LITERAL, AND, VOID, MAIN, IF, RETURN
%token STRUCT, FUNC, OR, EQUALS, DIFF, ELSE

%right '='
%nonassoc '>' '<'
%left '+' '-'
%left '*' '/'
%left AND OR EQUALS DIFF
%left '[' '.' 

%type <sval> IDENT
%type <ival> NUM
%type <obj> type
%type <obj> exp
%type <obj> typeFunc
%type <obj> callFunc

%%

prog : { currEscopo = null; currClass = ClasseID.VarGlobal; } dList mList ;

dList : decl dList | ;

decl : declStruct 

      | type IDENT ';' {  TS_entry nodo = ts.pesquisa($2);
    	                if (nodo != null) 
                            yyerror("(sem) variavel >" + $2 + "< jah declarada");
                        else ts.insert(new TS_entry($2, (TS_entry)$1, currEscopo, currClass), currEscopo); 
                        }
      | type '[' NUM ']' IDENT  ';' 
                    {  TS_entry nodo = ts.pesquisa($5);
    	                  if (nodo != null) 
                            yyerror("(sem) variavel >" + $5 + "< jah declarada");
                    else ts.insert(new TS_entry($5, Tp_ARRAY, $3, (TS_entry)$1, currEscopo, currClass), currEscopo); 
                     }
      ;
              
declStruct : STRUCT IDENT  {  TS_entry nodo = ts.pesquisa($2);
    	                    if (nodo != null) 
                                 yyerror("(sem) variavel >" + $2 + "< jah declarada");
                            else {
                                nodo = new TS_entry($2, Tp_STRUCT, currEscopo, ClasseID.NomeStruct);
                                ts.insert(nodo, currEscopo);
 								currEscopo = nodo; 
                                currClass = ClasseID.CampoStruct; 
                                }
                            }
               
               '{' dList '}'
                { currEscopo = null; currClass = ClasseID.VarGlobal; }
                ';'
             ;

type : INT    { $$ = Tp_INT; }
     | FLOAT  { $$ = Tp_FLOAT; }
     | BOOL   { $$ = Tp_BOOL; }
	 | STRING { $$ = Tp_STRING; }
     | IDENT  { TS_entry nodo = ts.pesquisa($1);
    	                if (nodo == null ) 
                           yyerror("(sem) Nome de tipo <" + $1 + "> nao declarado ");
                        else 
                            $$ = nodo;
                     } 
     ;

mList : metodo mList | ;

metodo: FUNC typeFunc IDENT {
          TS_entry existing = ts.pesquisa($3);
          if (existing != null) {
              yyerror("Função '" + $3 + "' já declarada");
          } else {
              existing = new TS_entry($3, (TS_entry)$2, currEscopo, ClasseID.NomeFuncao);
              ts.insert(existing, currEscopo);
              currEscopo = existing;
              currClass = ClasseID.NomeParam;
          }
        }
        '(' parametros ')' {
            currEscopo = currEscopo;
            currClass = ClasseID.VarLocal;
        }
        bloco {
            currEscopo = null;
            currClass = ClasseID.VarGlobal;
        };

typeFunc: type | VOID { $$ = Tp_VOID; };

parametros: listaParam | ;

listaParam: param ',' listaParam | param;

param: type IDENT {
        TS_entry existing = ts.pesquisa($2, currEscopo);
        if (existing != null) {
            yyerror("Parâmetro '" + $2 + "' já declarado");
        } else {
            ts.insert(new TS_entry($2, (TS_entry)$1, currEscopo, currClass), currEscopo);
        }
      };

bloco : '{' listacmd '}';

listacmd : listacmd cmd
		|
 		;

cmd : exp ';' 
    | IF '(' exp ')' {
          if (((TS_entry)$3).getTipo() != Tp_BOOL.getTipo()) {
              yyerror("Condição do IF deve ser booleana");
          }
      }
      cmd
    | type IDENT ';' {
          TS_entry existing = ts.pesquisa($2);
          if (existing != null) {
              yyerror("Variável " + $2 + "' já declarada");
          } else {
            existing = ts.pesquisa($2);
            if(existing != null){
                yyerror("Variável local '" + $2 + "' já declarada");
            }else{
              ts.insert(new TS_entry($2, (TS_entry)$1, currEscopo, currClass), currEscopo);
            }
          }
      }
    | type IDENT '=' exp ';' {
          TS_entry existing = ts.pesquisa($2, currEscopo);
          if (existing != null) {
              yyerror("Variável local '" + $2 + "' já declarada");
          } else {
              TS_entry newVar = new TS_entry($2, (TS_entry)$1, currEscopo, currClass);
              ts.insert(newVar, currEscopo);
              validaTipo(ATRIB, newVar.getTipo(), (TS_entry)$4);
          }
      }
    | RETURN exp ';' {
          if (((TS_entry)currEscopo).getTipo() == Tp_FLOAT) {
                        if (((TS_entry)$2)!= Tp_INT && ((TS_entry)$2) != Tp_FLOAT) {
                              yyerror("o retorno do tipo " + ((TS_entry)$2).getTipoStr() + " é inválido para uma funcao " + ((TS_entry)currEscopo).getTipoString());
                        }
                        } else if (((TS_entry)currEscopo).getTipo() != ((TS_entry)$2)) {
                              yyerror("o retorno do tipo " + ((TS_entry)$2).getTipoStr() + " é inválido para uma funcao " + ((TS_entry)currEscopo).getTipoString());
                        }
      };

callFunc: IDENT {
          TS_entry existing = ts.pesquisa($1);
          if (existing == null) {
              yyerror("Função '" + $1 + "' não declarada");
          } else {
            funcEntry = existing;
            expectedParams = funcEntry.getLocalTS().getLista().stream().filter(p -> p.Classe() == ClasseID.NomeParam).collect(Collectors.toList()).size();
            paramCounter = 0;
          }
      } 
      '(' argsOuSemArgs ')' {
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
          $$ = funcEntry.getTipo();
      };

argsOuSemArgs: args | ;

args : exp ',' args{
          paramTypes.add((TS_entry)$1);
          paramCounter++;
      }
    | exp ',' args {
          paramTypes.add((TS_entry)$1);
          paramCounter++;
      };

exp : exp '+' exp { $$ = validaTipo('+', (TS_entry)$1, (TS_entry)$3); }
    | exp '-' exp { $$ = validaTipo('-', (TS_entry)$1, (TS_entry)$3);}
    | exp '*' exp { $$ = validaTipo('*', (TS_entry)$1, (TS_entry)$3);}
    | exp '/' exp { $$ = validaTipo('/', (TS_entry)$1, (TS_entry)$3);}
   	| exp '>' exp { $$ = validaTipo('>', (TS_entry)$1, (TS_entry)$3);}
 	| exp AND exp { $$ = validaTipo(AND, (TS_entry)$1, (TS_entry)$3); } 
    | exp '<' exp { $$ = validaTipo('<', (TS_entry)$1, (TS_entry)$3);}
    | exp OR exp  { $$ = validaTipo(OR, (TS_entry)$1, (TS_entry)$3); }
    | exp EQUALS exp { $$ = validaTipo(EQUALS, (TS_entry)$1, (TS_entry)$3); }
    | exp DIFF exp { $$ = validaTipo(DIFF, (TS_entry)$1, (TS_entry)$3); }
    | callFunc
    | NUM         { $$ = Tp_INT; }      
    | '(' exp ')' { $$ = $2; }
    | LITERAL     { $$ = Tp_STRING; }      
    | IDENT       { TS_entry nodo = ts.pesquisa($1);
    	                 if (nodo == null) 
	                        yyerror("(sem) var <" + $1 + "> nao declarada");                
                      else
			                    $$ = nodo.getTipo();
			            }   
    | IDENT '[' exp ']' 
                 { TS_entry nodo = ts.pesquisa($1);
    	             if (nodo == null) 
	                     yyerror("(sem) var <" + $1 + "> nao declarada");                
                   else
                       $$ = validaTipo('[', nodo, (TS_entry)$3);
						     }
     | exp '=' exp  
                 {  $$ = validaTipo(ATRIB, (TS_entry)$1, (TS_entry)$3);                      
                 } 
     | exp '.' IDENT 
                 { if (((TS_entry)$1).getTipo() != Tp_STRUCT) 
	                     yyerror("(sem) Esperado tipo STRUCT e recebido >" + ((TS_entry)$1).getId() + ">" + $1);                
                   else {
                         TS_entry nodo = ((TS_entry)$1).getLocalTS().pesquisa($3);
                         if (nodo == null)
                            yyerror("(sem) <" +$3+"> não é campo da STRUCT <"+ ((TS_entry)$1).getId() + ">");                
                         else 
                             $$ = nodo.getTipo();
                       }
						     } 
    ;

%%

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



