package varlang;
import static varlang.AST.*;
import static varlang.Value.*;

import java.util.List;
import java.util.ArrayList;

import varlang.AST.AddExp;
import varlang.AST.NumExp;
import varlang.AST.DivExp;
import varlang.AST.MultExp;
import varlang.AST.Program;
import varlang.AST.SubExp;
import varlang.AST.VarExp;
import varlang.AST.Visitor;
import varlang.Env.EmptyEnv;
import varlang.Env.ExtendEnv;

public class Evaluator implements Visitor<Value> {
	
	Value valueOf(Program p) {
		Env env = new EmptyEnv();
		// Value of a program in this language is the value of the expression
		return (Value) p.accept(this, env);
	}
	
	@Override
	public Value visit(AddExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 0;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result += intermediate.v(); //Semantics of AddExp in terms of the target language.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(NumExp e, Env env) {
		return new NumVal(e.v());
	}

	@Override
	public Value visit(DivExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v(); 
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result / rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(MultExp e, Env env) {
		List<Exp> operands = e.all();
		double result = 1;
		for(Exp exp: operands) {
			NumVal intermediate = (NumVal) exp.accept(this, env); // Dynamic type-checking
			result *= intermediate.v(); //Semantics of MultExp.
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(Program p, Env env) {
		return (Value) p.e().accept(this, env);
	}

	@Override
	public Value visit(SubExp e, Env env) {
		List<Exp> operands = e.all();
		NumVal lVal = (NumVal) operands.get(0).accept(this, env);
		double result = lVal.v();
		for(int i=1; i<operands.size(); i++) {
			NumVal rVal = (NumVal) operands.get(i).accept(this, env);
			result = result - rVal.v();
		}
		return new NumVal(result);
	}

	@Override
	public Value visit(VarExp e, Env env) {
		// Previously, all variables had value 42. New semantics.
		return env.get(e.name());
	}

	@Override
	public Value visit(LetExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();
		List<Value> values = new ArrayList<>(value_exps.size());

		for(Exp exp : value_exps)
			values.add((Value)exp.accept(this, env));

		Env new_env = env;
		for (int i = 0; i < names.size(); i++)
			new_env = new ExtendEnv(new_env, names.get(i), values.get(i), false);

		return (Value) e.body().accept(this, new_env);		
	}

	@Override
	public Value visit(LeteExp e, Env env) { // New for varlang.
		List<String> names = e.names();
		List<Exp> value_exps = e.value_exps();

		Value key = (Value) e.key().accept(this, env);
		int keyInt = Integer.parseInt(key.toString());

		if(keyInt != 0){
			Env new_env = env;
			for (int i = 0; i < names.size(); i++) {
				Value value = (Value) value_exps.get(i).accept(this, new_env);
				int encVal = Integer.parseInt(value.toString());
				new_env = new ExtendEnv(new_env, names.get(i), new Value.NumVal(keyInt * encVal), true);
			}

			return (Value) e.body().accept(this, new_env);
		} else {
			System.out.println("Key CAN'T BE 0");
			return new Value.NumVal(-1);
		}
	}

	@Override
	public Value visit(DecExp e, Env env) { // New for varlang.
		if(env.getIsEnc()){
			Value key = (Value) e.key().accept(this, env);
			int keyInt = Integer.parseInt(key.toString());

			Value var = (Value) e.body().accept(this, env);
			int encVal = Integer.parseInt(var.toString());

			return new Value.NumVal(encVal / keyInt);
		} else {
			System.out.println("Variable not encoded");
			return new Value.NumVal(-1);
		}
	}
}
