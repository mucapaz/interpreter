package seman;

public class Token {

	public int value;
	public String name;
	public TokenTag tag;
	
	public Token(TokenTag tag) {
		this.tag = tag;
	}
	
	public Token(TokenTag tag, String name) {
		this.tag = tag;
		this.name = name;
	}
	
	public Token(TokenTag tag, int value) {
		this.tag = tag;
		this.value = value;
	}

}
