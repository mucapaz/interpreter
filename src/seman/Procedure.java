package seman;

import java.util.List;
import java.util.Map;

public class Procedure {

	private List<Token> tokens;
	private List<Map> mems;
	private List<Map> lastProcs; // procedures defined before this Procedure
	
	
	public Procedure(List<Token> tokens, List<Map> mems, List<Map> lastProcs) {
		this.tokens= tokens;
		this.mems = mems;
		this.lastProcs = lastProcs;
	}

	public List<Token> getTokens() {
		return tokens;
	}

	public List<Map> getMems() {
		return mems;
	}

	public List<Map> getLastProcs() {
		return lastProcs;
	}
	
	
	
}
