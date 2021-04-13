grammar VarLang;

import ArithLang; //Import all rules from Arithlang grammar.
 
 // New elements in the Grammar of this Programming Language
 //  - grammar rules start with lowercase

 exp returns [Exp ast]: 
		v=varexp { $ast = $v.ast; }
		| n=numexp { $ast = $n.ast; }
        | a=addexp { $ast = $a.ast; }
        | s=subexp { $ast = $s.ast; }
        | m=multexp { $ast = $m.ast; }
        | d=divexp { $ast = $d.ast; }
        | l=letexp { $ast = $l.ast; }
        | e=leteexp { $ast = $e.ast; }
        | dec=decexp { $ast = $dec.ast; }
        ;

 varexp returns [VarExp ast]: 
 		id=Identifier { $ast = new VarExp($id.text); }
 		;

 letexp  returns [LetExp ast] 
        locals [ArrayList<String> names, ArrayList<Exp> value_exps]
 		@init { $names = new ArrayList<String>(); $value_exps = new ArrayList<Exp>(); } :
 		'(' Let 
 			'(' ( '(' id=Identifier e=exp ')' { $names.add($id.text); $value_exps.add($e.ast); } )+  ')'
 			body=exp 
 			')' { $ast = new LetExp($names, $value_exps, $body.ast); }
 		;

 leteexp returns [LeteExp ast]
   		locals [ArrayList<String> names = new ArrayList<String>(), ArrayList<Exp> value_exps = new ArrayList<Exp>()]  :
   		'(' Lete
            key=exp
   	        '(' ( '(' id=Identifier e=exp ')' { $names.add($id.text); $value_exps.add($e.ast); } )+  ')'
            body=exp
   		    ')' { $ast = new LeteExp($names, $value_exps, $body.ast, $key.ast); }
   		;

 decexp returns [DecExp ast]
        locals [ArrayList<Exp> value_exps = new ArrayList<Exp>()]  :
    		'(' Dec
             key=exp
             body=exp
             ')' { $ast = new DecExp($body.ast, $key.ast); }
        ;

 // Lexical Specification of this Programming Language
 //  - lexical specification rules start with uppercase
 