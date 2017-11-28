package seman;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	public String file;

	public static List<Token> tokenize(String code){
		List<Token> tokens = new ArrayList<Token>();

		String[] terms = code.split(" ");


		for(int x=0;x<terms.length;x++) {
			String term = terms[x];

			//			System.out.println("vinc " + term);

			switch (term) {
			case "VAR":

				term = terms[++x];
				tokens.add(new Token(TokenTag.VAR, term));

				term = terms[++x];
				if(isInteger(term)) { // it's a integer
					tokens.add(new Token(TokenTag.INTEGER, Integer.parseInt(term)));		
				}else { // it's another variable
					tokens.add(new Token(TokenTag.VAR, term));
				}

				break;
			case "EXP":

				break;	
			case "WHILE":
				tokens.add(new Token(TokenTag.WHILE));

				term = terms[++x];
				tokens.add(new Token(TokenTag.VAR, term)); //variable

				term = terms[++x];
				tokens.add(new Token(operator(term))); //operator

				term = terms[++x];
				if(isInteger(term)) { // it's a integer
					tokens.add(new Token(TokenTag.INTEGER, Integer.parseInt(term)));		
				}else { // it's another variable
					tokens.add(new Token(TokenTag.VAR, term));
				}

				break;
			case "PROC":
				term = terms[++x];
				tokens.add(new Token(TokenTag.PROC, term));
				
				break;
			case "CALL":
				term = terms[++x];
				tokens.add(new Token(TokenTag.CALL, term));
				
				break;
			case "IF":
				tokens.add(new Token(TokenTag.IF));

				term = terms[++x];
				tokens.add(new Token(TokenTag.VAR, term));

				term = terms[++x];
				tokens.add(new Token(operator(term)));

				term = terms[++x];
				if(isInteger(term)) { // it's a integer
					tokens.add(new Token(TokenTag.INTEGER, Integer.parseInt(term)));		
				}else { // it's another variable
					tokens.add(new Token(TokenTag.VAR, term));
				}

				break;
			case "BEGIN":
				tokens.add(new Token(TokenTag.BEGIN));

				break;
			case "END":
				tokens.add(new Token(TokenTag.END));
				break;

			default: // x = x + 1, x = 10
				
				tokens.add(new Token(TokenTag.EXP, term));

				term = terms[++x]; // = 
				tokens.add(new Token(TokenTag.ASSIGN));

				term = terms[++x]; // 								
				if(isInteger(term)) { // it's a integer
					tokens.add(new Token(TokenTag.INTEGER, Integer.parseInt(term)));		
				}else { // it's another variable
					tokens.add(new Token(TokenTag.VAR, term));
				}

				while(isOperator(terms[x + 1])) {
					term = terms[++x];
					tokens.add(new Token(operator(term)));
					
					term = terms[++x];
					if(isInteger(term)) { // it's a integer
						tokens.add(new Token(TokenTag.INTEGER, Integer.parseInt(term)));		
					}else { // it's another variable
						tokens.add(new Token(TokenTag.VAR, term));
					}
				}
				
				break;
			}

		}



		return tokens;
	}



	private static boolean isOperator(String term) {
		if(term.equals("+") || term.equals("-") || term.equals("*") || term.equals("/")) {
			return true;
		}
		return false;
	}



	private static TokenTag operator(String term) {

		if(term.equals("=")) {
			return TokenTag.EQUAL;
		}else if(term.equals("!=")) {
			return TokenTag.NOT_EQUAL;
		}else if(term.equals("+")) {
			return TokenTag.ADD;
		}else if(term.equals("-")) {
			return TokenTag.SUB;
		}else if(term.equals("*")) {
			return TokenTag.MUL;
		}else if(term.equals("/")) {
			return TokenTag.DIV;
		}else if(term.equals("<")) {
			return TokenTag.LESS;
		}else if(term.equals(">")) {
			return TokenTag.GREATER;
		}

		return null;
	}

	private static boolean  isInteger(String str) {
		try {
			Integer.parseInt(str);
		}catch(NumberFormatException e) {
			return false;
		}

		return true;
	}

}


















