package seman;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Procedure {

	private List<Token> tokens;
	private List<Map> mems;
	private List<Map> lastProcs; // procedures defined before this Procedure plus this procedure
	private String name;
	
	public Procedure(String name, List<Token> tokens, List<Map> mems, List<Map> lastProcs) {
		this.tokens= tokens;
		this.mems = mems;
		this.lastProcs = lastProcs;
		
		if(lastProcs.size() > 0) {
			lastProcs.get(0).put(name, this);
		}else {
			lastProcs.add(new HashMap<String, Procedure>());
			lastProcs.get(0).put(name, this);
		}
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
