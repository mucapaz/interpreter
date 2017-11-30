package seman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter {

	private int index = 0;
	//	Map<String, Integer> mem;
	List<Token> tokens;

	public List<String> states;

	public Interpreter(List<Token> tokens) {
		this.tokens = tokens;
		this.states = new ArrayList<String>();
		//		mem = new HashMap<String, Integer>();
	}

	public Interpreter(List<Token> tokens, List<String> states) {
		this.tokens = tokens;
		this.states = states;
	}

	//	private Interpreter(List<Token> tokens, Map<String, Integer> mem) { // same idea as in class
	//		this.tokens = tokens;
	//		this.mem = mem;
	//	}


	public List<Map> execute() throws Exception{
		Token token = tokens.get(index);

//		Map<String, Integer> mem = new HashMap<String, Integer>();
		//		Map<String, Integer> parentMem = new HashMap<String, Integer>();

		List<Map> mems = new ArrayList();
		mems.add(new HashMap<String, Integer>());

		List<Map> procs = new ArrayList();
		procs.add(new HashMap<String, Procedure>());
		
		return begin(token,mems, procs);
	}

	public List<Map> execute(List<Map> mems, List<Map> procs) throws Exception{


		Token token = tokens.get(index);
		return begin(token, mems, procs);
	}

	private List<Map> begin(Token token, List<Map> mems, List<Map> procs) throws Exception{


		if(token.tag != TokenTag.BEGIN) {
			throw new Exception("not begin");
		}else {

			//			states.add(mem.toString());

			int aux = 1;
			
			while(true) {
				index++;
				Token t = tokens.get(index);
				
				if(t.tag == TokenTag.END) {
					break;
				}else if(t.tag == TokenTag.BEGIN) {

					List memsAux = copyMems(mems);
					memsAux.add(0, new HashMap<String, Integer>());

					List procsAux = copyProcs(procs);
					procsAux.add(0, new HashMap<String, Integer>());
					
					begin(t, memsAux, procsAux);

				}else {
					
					
					callFunc(t, mems, procs);
				}
			}			
		}

		return mems;
	}




	private void callFunc(Token t, List<Map> mems, List<Map> procs) throws Exception {

		//		states.add(mem);

		TokenTag tag = t.tag;
		
		if(tag == TokenTag.VAR) {
			var(t, mems);

			states.add(overlay(mems).toString());

		}else if(tag == TokenTag.IF) {
			conditional(t, mems, procs);
		}else if(tag == TokenTag.PROC) {
			proc(t, mems, procs);
		}else if(tag == TokenTag.CALL) {
			call(t, procs);		
		}else if(tag == TokenTag.WHILE) {
			loopWhile(t, mems, procs);
		}else if(tag == TokenTag.EXP) {
			exp(t, mems);
			states.add(overlay(mems).toString());
		}else {		
			throw new Exception("no func " +  t.tag);
		}

	}

	private void exp(Token var, List<Map> mems) throws Exception { //var = varOrValue0, var = varOrValue0 + varOrValue1 + varOrValue2... 
		index++;
		Token assign = tokens.get(index);

		if(assign.tag != TokenTag.ASSIGN) {
			throw new Exception("not assign");
		}

		index++;
		Token varOrValue0 = tokens.get(index);

		int valueAux = valueFromToken(varOrValue0, mems);

		while(isOperator(tokens.get(index + 1).tag)) {

			index++;
			Token operator = tokens.get(index);

			index++;
			Token varOrValue1 = tokens.get(index);

			valueAux = arithmetic(valueAux, operator.tag, valueFromToken(varOrValue1, mems));
		}

		boolean found = false;
		
		for(Map m : mems) {
			
			if(m.containsKey(var.name)) {
				m.put(var.name, valueAux);
				found = true;
				break;
			}
		}
		
		if(!found) throw new Exception("No memory contains this var");
				
	}

	private void var(Token t, List<Map> mems) throws Exception {

		index++;
		Token next = tokens.get(index);

		if(next.tag == TokenTag.VAR) {
			
			int v = 0;
			boolean found = false;
			
			for(Map m : mems) {
				if(m.containsKey(next.name)) {
					found = true;
					v = (int) m.get(next.name);
					break;
				}
			}
			
			if(!found) {
				throw new Exception("no var " + next.name + " found");
			}
			
			mems.get(0).put(t.name, v);		
		}else if(next.tag == TokenTag.INTEGER){
			mems.get(0).put(t.name, next.value);
		}else {
			throw new Exception("busted");
		}

	}

	private void proc(Token t, List<Map> mems, List<Map> procs) throws Exception {
		
		List<Token> inner = new ArrayList<Token>();
		int aux = 1;

		while(true) {
			index++;
			Token nextToken = tokens.get(index);
			
			if(nextToken.tag == TokenTag.END) {
				aux--;
				if(aux == 0) {
					break;
				}else {
					inner.add(nextToken);
				}
			}else {
				inner.add(nextToken);

				if(commandWithEnd(nextToken.tag)) {
					aux++;
				}
			}
		}

		inner.add(0, new Token(TokenTag.BEGIN));
		inner.add(inner.size(), new Token(TokenTag.END));

		
		
		if(procs.get(0).containsKey(t.name)) {
			throw new Exception("procedure " + t.name + " already defined");
		}else {
			
			List procsAt = copyProcs(procs);
					
			Procedure p = new Procedure(t.name, inner, copyMems(mems), procsAt);
			
			procs.get(0).put(t.name, p);
		}
	}

	private void call(Token t, List<Map> procs) throws Exception {
		Procedure p = null;
		
		for(Map m : procs) {
			if(m.containsKey(t.name)) {
				p = (Procedure) m.get(t.name);
				break;
			}
		}
		
		
		if(p == null) {
			throw new Exception("No procedure " + t.name + " found");
		}else {
			
			Interpreter inter = new Interpreter(p.getTokens(), states);
			inter.execute(p.getMems(), p.getLastProcs());	
		}
	}

	private void loopWhile(Token t, List<Map> mems, List<Map> procs) throws Exception {

		index++;		
		Token var1 = tokens.get(index); 

		index++;
		Token operator = tokens.get(index);

		index++; 
		Token varOrValue = tokens.get(index);


		List<Token> inner = new ArrayList<Token>();
		int aux = 1;

		while(true) {
			index++;
			t = tokens.get(index);

			if(t.tag == TokenTag.END) {
				aux--;
				if(aux == 0) {
					break;
				}else {
					inner.add(t);
				}
			}else {
				inner.add(t);

				if(commandWithEnd(t.tag)) {
					aux++;
				}
			}
		}

		inner.add(0, new Token(TokenTag.BEGIN));
		inner.add(inner.size(), new Token(TokenTag.END));

		while(comp(valueFromToken(var1, mems), operator.tag, valueFromToken(varOrValue, mems))) {

			Interpreter inter = new Interpreter(inner, states);
			inter.execute(mems, procs);
		}

	}


	private void conditional(Token t, List<Map> mems, List<Map> procs) throws Exception { //if

		index++;		
		Token var1 = tokens.get(index); 

		index++;
		Token operator = tokens.get(index);

		index++; 
		Token varOrValue = tokens.get(index);

		boolean b = comp(valueFromToken(var1, mems), operator.tag,
				valueFromToken(varOrValue, mems));

		if(b) {
			while(true) {
				index++;
				t = tokens.get(index);

				if(t.tag == TokenTag.END) break;
				else {
					callFunc(t, mems, procs);
				}
			}
		}else {
			int aux = 1;
			while(true) {
				index++;
				t = tokens.get(index);

				if(t.tag == TokenTag.END) {
					aux--;
					if(aux == 0) {
						break;
					}
				}else if( commandWithEnd(t.tag)) {
					aux++;
				}
			}

		}


	}

//	private List<Token> procFromMaps(Token t, Map procs, Map parentProcs) throws Exception{
//		if(procs.containsKey(t.name)) {
//			return (List<Token>) procs.get(t.name);
//		}else if(parentProcs.containsKey(t.name)) {
//			return (List<Token>) parentProcs.get(t.name);
//		}else {
//			throw new Exception("procedure " + t.name + " not found");
//		}
//
//	}

	private int valueFromToken(Token t, List<Map> mems) throws Exception {
		if(t.tag == TokenTag.VAR) {			
			
			for(Map m : mems) {
				if(m.containsKey(t.name)) {
					return (int) m.get(t.name);
				}
			}
			
			throw new Exception("no var " + t.name + " found in mems");

		}else if(t.tag == TokenTag.INTEGER) {
			return t.value;
		}

		throw new Exception("no var with this and not integer: " + t.tag + " " + t.name);
	}

	private boolean isOperator(TokenTag tag) {
		return tag.equals(TokenTag.ADD) || tag.equals(TokenTag.SUB) ||
				tag.equals(TokenTag.DIV) || tag.equals(TokenTag.MUL);
	}

	private boolean comp(int int1, TokenTag operator, int int2) throws Exception {

		if(operator == TokenTag.EQUAL) {
			return int1 == int2;
		}else if(operator == TokenTag.LESS) {
			return int1 < int2;
		}else if(operator == TokenTag.GREATER) {
			return int1 > int2;
		}else if(operator == TokenTag.NOT_EQUAL) {
			return int1 != int2;
		}else {

			throw new Exception("not supported operator: " +  operator);
		}

	}

	private int arithmetic(int int1, TokenTag operator, int int2) throws Exception {

		if(operator == TokenTag.SUB) {
			return int1 - int2;
		}else if(operator == TokenTag.ADD) {
			return int1 + int2;
		}else if(operator == TokenTag.MUL) {
			return int1 * int2;
		}else if(operator == TokenTag.DIV) {
			return int1 / int2;
		}else {
			throw new Exception("no operator found");
		}


	}

	private Map overlay(List<Map> list){
		Map over = new HashMap();

		for(Map m2 : list) {
			for(Object key : m2.keySet()) {
				if(!over.containsKey(key)) {
					over.put(key, m2.get(key));
				}
			}
		}


		return over;
	}

	private Map overlayPROC(Map m1, Map m2) {
		return null;
	}
	
	private boolean commandWithEnd(TokenTag tag) {

		return tag == TokenTag.BEGIN || tag == TokenTag.IF || tag == TokenTag.WHILE || tag == TokenTag.PROC; 

	}

	public List<Map> copyMems(List<Map> list){
		List<Map> nlist = new ArrayList<>();

		for(Map m : list) {
			nlist.add(m);
		}

		return nlist;
	}
	
	public List<Map> copyProcs(List<Map> list){
		List<Map> nlist = new ArrayList<>();

		for(Map m : list) {
			nlist.add(clone(m));
		}

		return nlist;
	}
	
	public Map clone(Map m) {
		Map ret = new HashMap<>();
		
		for(Object o : m.keySet()) {
			ret.put(o, m.get(o));
		}
		
		return ret;
	}

}
