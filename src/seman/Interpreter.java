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
		return begin(token, new HashMap<String, Integer>(), new HashMap<String, Integer>());
	}
	
	public Map<String, Integer> execute(Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception{
		Token token = tokens.get(index);
		return begin(token, mem, parentMem);
	}
	
	private Map<String, Integer> begin(Token token, Map<String, Integer> mem,
			Map<String, Integer> parentMem) throws Exception{
		
		
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
					
					begin(t, new HashMap<String, Integer>(), mem);
					
				}else {
					callFunc(t, mem, parentMem);
				}
			}			
		}
		
		return mem;
	}

	
	
	
	private void callFunc(Token t, Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception {

//		states.add(mem);
		
		TokenTag tag = t.tag;

		if(tag == TokenTag.VAR) {
			var(t, mem);
			
			states.add(overlay(mem, parentMem).toString());
			
		}else
			if(tag == TokenTag.IF) {
			conditional(t, mem, parentMem);
		}
//		else if(tag == TokenTag.BEGIN) {
//			
//			throw new Exception("not implemented");
//			begin(t);
//		
//		
//		}
//		
		else if(tag == TokenTag.WHILE) {
			loopWhile(t, mem, parentMem);
		}else if(tag == TokenTag.EXP) {
			exp(t, mem, parentMem);
			states.add(overlay(mem, parentMem).toString());
		}else {		
			throw new Exception("no func " +  t.tag);
		}

	}

//
//	private void begin(Token t) throws Exception {
//
//		
//		
//		while(true) {
//			index++;
//			t = tokens.get(index);
//
//			if(t.tag == TokenTag.END) break;
//			else {
//				callFunc(t);
//			}
//		}
//
//	}

	private void exp(Token var, Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception { //var = varOrValue0, var = varOrValue0 + varOrValue1 + varOrValue2... 
		index++;
		Token assign = tokens.get(index);
		
		if(assign.tag != TokenTag.ASSIGN) {
			throw new Exception("not assign");
		}
		
		index++;
		Token varOrValue0 = tokens.get(index);
		
		int valueAux = valueFromToken(varOrValue0, mem, parentMem);
		
		while(isOperator(tokens.get(index + 1).tag)) {
			
			index++;
			Token operator = tokens.get(index);
			
			index++;
			Token varOrValue1 = tokens.get(index);
			
			valueAux = arithmetic(valueAux, operator.tag, valueFromToken(varOrValue1, mem, parentMem));
		}
		
		if(mem.containsKey(var.name)) {
			mem.put(var.name, valueAux);
		}else if(parentMem.containsKey(var.name)) {
			parentMem.put(var.name, valueAux);
		}else {
			throw new Exception("No memory contains this var");
		}
		
		
	}
	
	private void var(Token t, Map<String, Integer> mem) throws Exception {

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

	private void loopWhile(Token t, Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception {

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
				
				if(t.tag == TokenTag.BEGIN || t.tag == TokenTag.IF || t.tag == TokenTag.WHILE) {
					aux++;
				}
			}
		}

		inner.add(0, new Token(TokenTag.BEGIN));
		inner.add(inner.size(), new Token(TokenTag.END));
		
		while(comp(valueFromToken(var1, mem, parentMem), operator.tag, valueFromToken(varOrValue, mem, parentMem))) {
			
			Interpreter inter = new Interpreter(inner, states);
			inter.execute(mem, parentMem);
		}
	
	}


	private void conditional(Token t, Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception { //if

		index++;		
		Token var1 = tokens.get(index); 

		index++;
		Token operator = tokens.get(index);

		index++; 
		Token varOrValue = tokens.get(index);

		boolean b = comp(valueFromToken(var1, mem, parentMem), operator.tag,
				valueFromToken(varOrValue, mem, parentMem));

		if(b) {
			while(true) {
				index++;
				t = tokens.get(index);

				if(t.tag == TokenTag.END) break;
				else {
					callFunc(t, mem, parentMem);
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
				}else if(t.tag == TokenTag.BEGIN || t.tag == TokenTag.IF || t.tag == TokenTag.WHILE) {
					aux++;
				}
			}

		}


	}

	private int valueFromToken(Token t, Map<String, Integer> mem, Map<String, Integer> parentMem) throws Exception {
		if(t.tag == TokenTag.VAR) {			
			
			if(mem.containsKey(t.name)) {
				return mem.get(t.name);
			}else if(parentMem.containsKey(t.name)) {
				return parentMem.get(t.name);
			}else {
				throw new Exception("no var with this name");
			}
			
		}else if(t.tag == TokenTag.INTEGER) {
			return t.value;
		}else {
			throw new Exception("not var and not integer");
		}

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

	private Map<String, Integer> overlay(Map<String, Integer> m1, Map<String, Integer> m2){
		Map<String, Integer> over = new HashMap<String, Integer>();
		
		for(String key : m1.keySet()) {
			over.put(key, m1.get(key));
		}
		
		for(String key : m2.keySet()) {
			if(!over.containsKey(key)) {
				over.put(key, m2.get(key));
			}
		}
		
		return over;
	}
	

}
