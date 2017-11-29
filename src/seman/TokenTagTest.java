package seman;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TokenTagTest {

	@Test
	public void testIf() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"IF X = 10\n" +
					"X = 20\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		assertEquals((int)mem.get("X"), 20);
		System.out.println(inter.states);
		assertEquals("[{X=10}, {X=20}]", inter.states.toString());
	}
	
	@Test
	public void testProc() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"PROC proca\n" + 
					"X = 20\n" +
					"END\n"+
					"CALL proca\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 20);

		assertEquals("[{X=10}, {X=20}]", inter.states.toString());
	}
	
	@Test
	public void testProc2() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					
					"PROC proc1\n"+
					"X = 20\n"+
					"END\n"+
					
					"PROC proc2\n" + 
					"X = 30\n" +
					"END\n"+
					
					"CALL proc2\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		System.out.println(mem);
		System.out.println(inter.states);
		
		assertEquals(30, (int)mem.get("X"));

		assertEquals("[{X=10}, {X=30}]", inter.states.toString());
	}
	
	@Test
	public void testProc3() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"VAR Y 100\n"+
					
					"PROC proc1\n"+
					"X = 20\n"+
					"END\n"+
					
					"PROC proc2\n" + 
					"Y = 200\n" +
					"CALL proc1\n"+
					"END\n"+
					
					"CALL proc2\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		System.out.println(mem);
		System.out.println(inter.states);
		
		assertEquals(20, (int)mem.get("X"));
		assertEquals(200, (int)mem.get("Y"));

		assertEquals("[{X=10}, {X=10, Y=100}, {X=10, Y=200}, {X=20, Y=200}]", inter.states.toString());
	}
	
	@Test
	public void testIfSum() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"IF X = 10\n" +
					"X = X + 100 + X + 200\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

//		for(Token t : list) {
//			System.out.println(t.tag);
//		}
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		assertEquals("[{X=10}, {X=320}]", inter.states.toString());
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 320);
	}
	
	@Test
	public void testIfSub() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"IF X = 10\n" +
					"X = 10 - 5\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		assertEquals(inter.states.toString(), "[{X=10}, {X=5}]");
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 5);
	}
	
	@Test
	public void testIfProd() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"IF X = 10\n" +
					"X = 10 * 5\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
//		assertEquals(inter.states, "[{}, {X=10}, {X=50}]");
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 50);
	}
	
	@Test
	public void testIfDiv() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 100\n" +
					"IF X = 100\n" +
					"X = X / 5\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 20);
	}
	
	@Test
	public void testNotIf() throws Exception {
		String code = "BEGIN\n" +
					"VAR X 0\n" +
					"IF X != 10\n" +
					"X = 20\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

//		for(Token t : list) {
//			System.out.println(t.tag);
//			System.out.println(" ?????");
//		}
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
//		
//		System.out.println(mem);
//		
		System.out.println(inter.states);
		assertEquals((int) mem.get("X"), 20);
	}
	
	@Test
	public void testNegIf() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"IF X = 20\n" +
					"X = 20\n" + 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		
		if (mem.get("X") != 10) {
			fail("Expected X = 10");
		}
		
		System.out.println(inter.states);
	}
	
	@Test
	public void testNestedIf() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"VAR Y 30\n" + 
					"IF X = 10\n" +
					"IF Y = 30\n" +
					"X = 20\n" + 
					"END\n"+
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);		
		assertEquals((int) mem.get("X"), 20);
		assertEquals((int) mem.get("Y"), 30);
	}
	
	@Test
	public void testWhile() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"WHILE X < 20\n" + 
					"X = X + 1\n" +
					"END\n" +
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int) mem.get("X"), 20);
	}
	
	@Test
	public void testNestedWhileOnce() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"WHILE X = 10\n" +
					"WHILE X < 20\n" + 
					"X = X + 1\n" +
					"END\n" +
					"END\n" +
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int) mem.get("X"), 20);
	}
	
	@Test
	public void testNestedWhileNever() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"WHILE X = 0\n" +
					"WHILE X < 20\n" + 
					"X = X + 1\n" +
					"END\n" +
					"END\n" +
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int) mem.get("X"), 10);
	}
	
	@Test
	public void testNestedWhile() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"VAR Y 100\n" +
					"VAR Z 400\n" +
					"VAR AUX 0\n" + 
					"WHILE AUX < Z\n" +
					"WHILE X < Y\n" + 
					"X = X + 10\n" +
					"END\n" +
					"AUX = AUX + X\n" +
					"X = 10\n" +
					"END\n" +
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);
		
		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println("WHILE:");
		System.out.println(inter.states);
		assertEquals(400, (int) mem.get("AUX"));
	}
	
	
	@Test
	public void testBeginEnd1() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"BEGIN\n" +
					"VAR Y 200\n"+
					"X = X + Y\n"+ 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		
		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 210);
	}
	
	@Test
	public void testBeginEnd2() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"BEGIN\n" +
					"VAR Y 100\n"+
					"VAR X 200\n"+
					"X = X + Y\n"+ 
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 10);
	}

	@Test
	public void testBeginEnd3() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"BEGIN\n" +
					"BEGIN\n" +
					"VAR Y 100\n"+
					"VAR X 200\n"+
					"X = X + Y\n"+
					"END\n"+
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
//		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 10);
	}
	
	@Test
	public void testBeginEnd4() throws Exception {
		
		String code = "BEGIN\n" +
					"VAR X 10\n" +
					"BEGIN\n" +
					"BEGIN\n" +
					"VAR Y 200\n"+
					"X = X + Y\n"+ 
					"END\n"+
					"END\n"+
					"END\n";
		
		code = code.replaceAll("\n", " ");
		
		List<Token> list = Parser.tokenize(code);

		Interpreter inter = new Interpreter(list);
		
		Map<String, Integer> mem = inter.execute().get(0);
		
		
		System.out.println(mem);
		System.out.println(inter.states);
		assertEquals((int)mem.get("X"), 210);
	}
	
}
