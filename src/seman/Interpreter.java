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


	public Map<String, Integer> execute() throws Exception{
		Token token = tokens.get(index);

		Map<String, Integer> mem = new HashMap<String, Integer>();
		//		Map<String, Integer> parentMem = new HashMap<String, Integer>();

		List<Map> parentsMem = new ArrayList();
		parentsMem.add(new HashMap<String, Integer>());

		return begin(token,mem, parentsMem);
	}

	public Map<String, Integer> execute(Map mem, List<Map> parentsMem) throws Exception{


		Token token = tokens.get(index);
		return begin(token, mem, parentsMem);
	}

	private Map<String, Integer> begin(Token token, Map mem,
			List<Map> parentsMem) throws Exception{


		if(token.tag != TokenTag.BEGIN) {
			throw new Exception("not begin");
		}else {

			//			states.add(mem.toString());

			while(true) {
				index++;
				Token t = tokens.get(index);

				if(t.tag == TokenTag.END) {
					break;
				}else if(t.tag == TokenTag.BEGIN) {

					List memsAux = clone(parentsMem);
					memsAux.add(0, mem);

					begin(t, new HashMap<String, Integer>(), memsAux);

				}else {
					callFunc(t, mem, parentsMem);
				}
			}			
		}

		return mem;
	}




	private void callFunc(Token t, Map mem, List<Map> parentsMem) throws Exception {

		//		states.add(mem);

		TokenTag tag = t.tag;

		if(tag == TokenTag.VAR) {
			var(t, mem);

			states.add(overlay(mem, parentsMem).toString());

		}else if(tag == TokenTag.IF) {
			conditional(t, mem, parentsMem);
		}
		
//		else if(tag == TokenTag.PROC) {
//			proc(t, mem, parentsMem);
//		}else if(tag == TokenTag.CALL) {
//			call(t, mem,parentsMem);		
//		}
//		
		else if(tag == TokenTag.WHILE) {
			loopWhile(t, mem, parentsMem);
		}else if(tag == TokenTag.EXP) {
			exp(t, mem, parentsMem);
			states.add(overlay(mem, parentsMem).toString());
		}else {		
			throw new Exception("no func " +  t.tag);
		}

	}

	private void exp(Token var, Map mem, List<Map> parentsMem) throws Exception { //var = varOrValue0, var = varOrValue0 + varOrValue1 + varOrValue2... 
		index++;
		Token assign = tokens.get(index);

		if(assign.tag != TokenTag.ASSIGN) {
			throw new Exception("not assign");
		}

		index++;
		Token varOrValue0 = tokens.get(index);

		int valueAux = valueFromToken(varOrValue0, mem, parentsMem);

		while(isOperator(tokens.get(index + 1).tag)) {

			index++;
			Token operator = tokens.get(index);

			index++;
			Token varOrValue1 = tokens.get(index);

			valueAux = arithmetic(valueAux, operator.tag, valueFromToken(varOrValue1, mem, parentsMem));
		}

		if(mem.containsKey(var.name)) {
			mem.put(var.name, valueAux);
		}else {
			boolean found = false;
			
			for(Map m : parentsMem) {
				if(m.containsKey(var.name)) {
					m.put(var.name, valueAux);
					found = true;
					break;
				}
			}
			
			if(!found) throw new Exception("No memory contains this var");
			
		}
		
	}

	private void var(Token t, Map mem) throws Exception {

		index++;
		Token next = tokens.get(index);

		if(next.tag == TokenTag.VAR) {
			mem.put(t.name, mem.get(next.name));
		}else if(next.tag == TokenTag.INTEGER){
			mem.put(t.name, next.value);
		}else {
			throw new Exception("busted");
		}

	}

//	private void proc(Token t, Map mem, List parentsMem) {
//		List<Token> inner = new ArrayList<Token>();
//		int aux = 1;
//
//		while(true) {
//			index++;
//			t = tokens.get(index);
//
//			if(t.tag == TokenTag.END) {
//				aux--;
//				if(aux == 0) {
//					break;
//				}else {
//					inner.add(t);
//				}
//			}else {
//				inner.add(t);
//
//				if(commandWithEnd(t.tag)) {
//					aux++;
//				}
//			}
//		}
//
//		inner.add(0, new Token(TokenTag.BEGIN));
//		inner.add(inner.size(), new Token(TokenTag.END));
//
//		procs.put(t.name, inner);
//	}

//	private void call(Token t, Map mem, Map parentMem, Map procs, Map parentProcs) throws Exception {
//		List<Token> procedure = procFromMaps(t, procs, parentProcs);
//
//		Interpreter inter = new Interpreter(procedure, states);
//		inter.execute(mem, parentMem, procs, parentProcs);
//	}

	private void loopWhile(Token t, Map mem, List<Map> parentsMem) throws Exception {

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

		while(comp(valueFromToken(var1, mem, parentsMem), operator.tag, valueFromToken(varOrValue, mem, parentsMem))) {

			Interpreter inter = new Interpreter(inner, states);
			inter.execute(mem, parentsMem);
		}

	}


	private void conditional(Token t, Map mem, List<Map> parentsMem) throws Exception { //if

		index++;		
		Token var1 = tokens.get(index); 

		index++;
		Token operator = tokens.get(index);

		index++; 
		Token varOrValue = tokens.get(index);

		boolean b = comp(valueFromToken(var1, mem, parentsMem), operator.tag,
				valueFromToken(varOrValue, mem, parentsMem));

		if(b) {
			while(true) {
				index++;
				t = tokens.get(index);

				if(t.tag == TokenTag.END) break;
				else {
					callFunc(t, mem, parentsMem);
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

	private int valueFromToken(Token t, Map<String, Integer> mem, List<Map> parentsMem) throws Exception {
		if(t.tag == TokenTag.VAR) {			

			if(mem.containsKey(t.name)) {
				return mem.get(t.name);
			}

			for(Map m : parentsMem) {
				if(m.containsKey(t.name)) {
					return (int) m.get(t.name);
				}
			}

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

	private Map overlay(Map m1, List<Map> list){
		Map over = new HashMap();

		for(Object key : m1.keySet()) {
			over.put(key, m1.get(key));
		}

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

	public List<Map> clone(List<Map> list){
		List<Map> nlist = new ArrayList<>();

		for(Map m : list) {
			nlist.add(m);
		}

		return nlist;
	}

}
