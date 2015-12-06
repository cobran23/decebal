package test;

import java.util.ArrayList;
import java.util.Random;

import decebal.Engine;
import decebal.Game;
import decebal.Move;

public class Tuning {
	private static final int moveTime = 2500;
	private static final int gamesNum = 3;
	
	public static void main(String[] args) {
		doTuning();
	}
	
	private static void doTuning() {
		System.out.println("Expected cycle time: "+ moveTime*gamesNum/25 +"s");
		int d = 85;
		int c = 67;
		int f = 87;
		int m = 40;
		int cycleNum = 0;
		Random r = new Random();
		long start = System.currentTimeMillis();
		do {
			System.out.println("Cycle "+ (++cycleNum)+":");
			Engine e1 = new Engine(d, c, f, m);
			int d2 = (int)Math.round(d+r.nextGaussian()*5);
			int c2 = (int)Math.round(c+r.nextGaussian()*5);
			int f2 = (int)Math.round(f+r.nextGaussian()*5);
			int m2 = (int)Math.round(m+r.nextGaussian()*5);
			Engine e2 = new Engine(d2, c2, f2, m2);
			int result = playMatch(e1, e2);
			String currentTime = ((System.currentTimeMillis()-start)/1000)+"s: \t";
			if (result == -1) {
				d = (int)Math.round((d+d2)/2.0);
				c = (int)Math.round((c+c2)/2.0);
				f = (int)Math.round((f+f2)/2.0);
				m = (int)Math.round((m+m2)/2.0);
				System.out.println(currentTime + "Engine 2 won. New parameters: d="+d+", c="+c+", f="+f+", m="+m);
			}
			if (result == 1) {
				System.out.println(currentTime + "Engine 1 ("+d+","+c+","+f+","+m+") won against ("+d2+","+c2+","+f2+","+m2+").");
			}
			if (result == 0) {
				System.out.println(currentTime + "No winner.");
			}
		} while (true);
	}
	
	private static int playMatch(Engine e1, Engine e2) {
		e1.setEnableBook(false);
		e1.setOutput(false);
		e2.setEnableBook(false);
		e2.setOutput(false);
		int e1Result = 0;
		int e2Result = 0;
		int remis = 0;
		int gameResult;
		for (int g = 0; g < gamesNum; g++) {
			gameResult = playGame(e1,e2);
			if (gameResult == 0) {
				remis++;
			} else if (gameResult == 1) {
				e1Result++;
			} else {
				e2Result++;
			}
			gameResult = playGame(e2,e1);
			if (gameResult == 0) {
				remis++;
			} else if (gameResult == -1) {
				e1Result++;
			} else {
				e2Result++;
			}
		}
		System.out.println("Result: "+e1Result+" : "+e2Result+" ("+remis+" remis)");
		if (e1Result>e2Result) {
			return 1;
		}
		if (e1Result<e2Result) {
			return -1;
		}
		return 0;
	}
	
	private static int playGame(Engine e1, Engine e2) {
		e1.resetGame();
		e2.resetGame();
		boolean gameOver = false;
		Engine e = e1;
		int movesNum = 0;
		do {
			movesNum++;
			String m;
			try {
				m = e.findMove(moveTime);
			} catch (NullPointerException ex) {
				m = null;
			}
			if (movesNum > 150) {
				//System.out.println("\nEnding game (150+ moves)");
				gameOver = true;
			}
			if (m == null) {
				gameOver = true;
			} else {
				//System.out.print(m + " ");
				ArrayList<Move> moves = e.getGame().generateMoves();
				Move move = null;
				for (Move m1 : moves) {
					if (m1.toString().equals(m)) {
						move = m1;
					}
				}
				e1.getGame().makemove(move);
				e2.getGame().makemove(move);
				if (e == e1) {
					e = e2;
				} else {
					e = e1;
				}
			}
		} while (gameOver == false);
		
		ArrayList<Move> moves = e.getGame().generateMoves();
		boolean f = false;
		for (Move move : moves) {
			if (e.getGame().makemove(move)) {
				f = true;
				e.getGame().takeback();
			}
		}
		
		if (!f) {
			if (e.getGame().in_check(Game.WHITE)) {
				//System.out.println("\nGame over. Black won.");
				return -1;
			} else if (e.getGame().in_check(Game.BLACK)) {
				//System.out.println("\nGame over. White won.");
				return 1;
			} else {
				//System.out.println("\nGame over. Remis.");
				return 0;
			}
		}
		return 0;
	}
}
