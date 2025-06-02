%{
  import java.io.*;
  import java.util.*;
%}

%token IDENT, INT, FLOAT, BOOL, NUM, STRING
%token LITERAL, AND, VOID, MAIN, IF, RETURN
%token STRUCT, FUNC, OR, EQUALS, DIFF

%right '='
%nonassoc '>' '<'
%left '+' '-'
%left '*' '/'
%left AND OR EQUALS DIFF
%left '[' '.' 

%type <sval> IDENT
%type <ival> NUM
%type <obj> type
%type <obj> typeFunc
%type <obj> exp
%type <obj> callFunc

%%

prog : { 
         currEscopo = null; 
         currClass = ClasseID.VarGlobal; 
         functions = new HashMap<>();
       } 
       dList mList;

dList : decl dList | ;

decl : declStruct 
      | type IDENT ';' { 
          TS_entry existing = ts.pesquisa($2, currEscopo);
          if (existing != null) {
              yyerror("Variável '" + $2 + "' já declarada");
          } else {
              ts.insert(new TS_entry($2, (TS_entry)$1, currEscopo, currClass), currEscopo);
          }
        }
      | type '[' NUM ']' IDENT ';' {
          TS_entry existing = ts.pesquisa($5, currEscopo);
          if (existing != null) {
              yyerror("Array '" + $5 + "' já declarado");
          } else {
              ts.insert(new TS_entry($5, Tp_ARRAY, $3, (TS_entry)$1, currEscopo, currClass), currEscopo);
          }
        };

declStruct : STRUCT IDENT {
              TS_entry existing = ts.pesquisa($2);
              if (existing != null) {
                  yyerror("Struct '" + $2 + "' já declarada");
              } else {
                  TS_entry structEntry = new TS_entry($2, Tp_STRUCT, currEscopo, ClasseID.NomeStruct);
                  ts.insert(structEntry, currEscopo);
                  currEscopo = structEntry;
                  currClass = ClasseID.CampoStruct;
              }
            }
            '{' dList '}' ';' {
                currEscopo = currEscopo.getEscopo();
                currClass = ClasseID.VarGlobal;
            };

type : INT    { $$ = Tp_INT; }
     | FLOAT  { $$ = Tp_FLOAT; }
     | BOOL   { $$ = Tp_BOOL; }
     | STRING { $$ = Tp_STRING; }
     | IDENT  {
         TS_entry tipoEntry = ts.pesquisa($1);
         if (tipoEntry == null) {
             yyerror("Tipo '" + $1 + "' não declarado");
             $$ = Tp_ERRO;
         } else {
             $$ = tipoEntry;
         }
       };

mList : metodo mList | ;

metodo: FUNC typeFunc IDENT {
          TS_entry existing = ts.pesquisa($3);
          if (existing != null) {
              yyerror("Função '" + $3 + "' já declarada");
          } else {
              TS_entry funcEntry = new TS_entry($3, (TS_entry)$2, currEscopo, ClasseID.NomeFuncao);
              ts.insert(funcEntry, currEscopo);
              functions.put($3, funcEntry);
              currEscopo = funcEntry;
              currClass = ClasseID.NomeParam;
          }
        }
        '(' parametros ')' {
            currClass = ClasseID.VarLocal;
        }
        bloco {
            currEscopo = currEscopo.getEscopo();
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

listacmd : listacmd cmd | ;

cmd : exp ';' 
    | IF '(' exp ')' {
          if (((TS_entry)$3).getTipo() != Tp_BOOL) {
              yyerror("Condição do IF deve ser booleana");
          }
      }
      cmd
    | type IDENT ';' {
          TS_entry existing = ts.pesquisa($2, currEscopo);
          if (existing != null) {
              yyerror("Variável local '" + $2 + "' já declarada");
          } else {
              ts.insert(new TS_entry($2, (TS_entry)$1, currEscopo, currClass), currEscopo);
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
    | callFunc ';'
    | RETURN exp ';' {
          TS_entry funcType = functions.get(currentFunction).getTipo();
          if (funcType != Tp_VOID && !compativel(funcType, (TS_entry)$2)) {
              yyerror("Tipo de retorno inválido para função " + currentFunction);
          }
      };

callFunc: IDENT {
          TS_entry funcEntry = functions.get($1);
          if (funcEntry == null) {
              yyerror("Função '" + $1 + "' não declarada");
          } else {
              currentFunction = $1;
              expectedParams = funcEntry.getLocalTS().getLista().size();
              paramCounter = 0;
              paramTypes = new ArrayList<>();
          }
      } 
      '(' args ')' {
          if (paramCounter != expectedParams) {
              yyerror("Número de parâmetros incorreto para " + currentFunction);
          } else {
              // Verificar tipos dos parâmetros
              List<TS_entry> expectedTypes = funcEntry.getLocalTS().getLista()
                  .stream().map(TS_entry::getTipo).toList();
                  
              for (int i = 0; i < paramCounter; i++) {
                  if (!compativel(expectedTypes.get(i), paramTypes.get(i))) {
                      yyerror("Tipo do parâmetro " + (i+1) + " incompatível");
                  }
              }
          }
          $$ = funcEntry.getTipo();
      };

args : exp {
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
    | exp '<' exp { $$ = validaTipo('<', (TS_entry)$1, (TS_entry)$3);}
    | exp AND exp { $$ = validaTipo(AND, (TS_entry)$1, (TS_entry)$3); }
    | exp OR exp  { $$ = validaTipo(OR, (TS_entry)$1, (TS_entry)$3); }
    | exp EQUALS exp { $$ = validaTipo(EQUALS, (TS_entry)$1, (TS_entry)$3); }
    | exp DIFF exp { $$ = validaTipo(DIFF, (TS_entry)$1, (TS_entry)$3); }
    | callFunc
    | NUM         { $$ = Tp_INT; }
    | '(' exp ')' { $$ = $2; }
    | LITERAL     { $$ = Tp_STRING; }
    | IDENT       { 
          TS_entry entry = ts.pesquisa($1, currEscopo);
          if (entry == null) {
              yyerror("Variável '" + $1 + "' não declarada");
              $$ = Tp_ERRO;
          } else {
              $$ = entry.getTipo();
          }
      }
    | IDENT '[' exp ']' {
          TS_entry array = ts.pesquisa($1, currEscopo);
          if (array == null) {
              yyerror("Array '" + $1 + "' não declarado");
              $$ = Tp_ERRO;
          } else if (array.getTipo() != Tp_ARRAY) {
              yyerror("'" + $1 + "' não é um array");
              $$ = Tp_ERRO;
          } else if (((TS_entry)$3).getTipo() != Tp_INT) {
              yyerror("Índice de array deve ser inteiro");
              $$ = Tp_ERRO;
          } else {
              $$ = array.getTipoBase();
          }
      }
    | exp '=' exp  { $$ = validaTipo(ATRIB, (TS_entry)$1, (TS_entry)$3); }
    | exp '.' IDENT {
          if (((TS_entry)$1).getTipo() != Tp_STRUCT) {
              yyerror("Operador '.' aplicado a não-struct");
              $$ = Tp_ERRO;
          } else {
              TS_entry struct = ((TS_entry)$1);
              TS_entry field = struct.getLocalTS().pesquisa($3);
              if (field == null) {
                  yyerror("Campo '" + $3 + "' não existe na struct");
                  $$ = Tp_ERRO;
              } else {
                  $$ = field.getTipo();
              }
          }
      };

%%

// Variáveis e funções auxiliares
private Map<String, TS_entry> functions;
private String currentFunction;
private int paramCounter;
private int expectedParams;
private List<TS_entry> paramTypes;

// ... (restante do código permanece similar ao do colega)

// Função para verificar compatibilidade de tipos
private boolean compativel(TS_entry tipoEsperado, TS_entry tipoRecebido) {
    if (tipoEsperado == tipoRecebido) return true;
    if (tipoEsperado == Tp_FLOAT && tipoRecebido == Tp_INT) return true;
    return false;
}

// Função validaTipo expandida
TS_entry validaTipo(int operador, TS_entry A, TS_entry B) {
    // ... (implementação similar à do colega, mas organizada)
}