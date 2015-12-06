package test;

import java.util.ArrayList;
import java.util.Random;

import decebal.Engine;
import decebal.Game;
import decebal.Move;

public class Tuning {
	private static final int moveTime = 1000;
	private static final int gamesNum = 1;
	private static final double avgDelta = 5;
	private static final double applyFactor = 0.1;
	
	public static void main(String[] args) {
		//doTuning();
		doRoundRobin();
		//(59,79,68,50)
	}
	
	private static void doRoundRobin() {
		int engineNum = 10;
		Engine[] engine = new Engine[engineNum];
		int[] score = new int[engineNum];
		Random r = new Random();
		for (int i = 0; i < engineNum; i++) {
			engine[i] = new Engine((int)Math.round(59+avgDelta*r.nextGaussian()),(int)Math.round(79+avgDelta*r.nextGaussian()),(int)Math.round(68+avgDelta*r.nextGaussian()),50);
			score[i] = 0;
		}
		for (int i = 0; i < engineNum; i++) {
			for (int j = i+1; j < engineNum; j++) {
				int result = playMatch(engine[i], engine[j]);
				if (result == 1) {
					score[i]+=2;
				}
				if (result == -1) {
					score[j]+=2;
				}
				if (result == 0) {
					score[i]++;
					score[j]++;
				}
			}
		}
		for (int i = 0; i < engineNum; i++) {
			System.out.print(score[i]/2.0 +"/"+(engineNum-1)+": ");
			engine[i].printParameters();
		}
	}
	
	private static void doTuning() {
		System.out.println("Expected cycle time: "+ moveTime*gamesNum/25 +"s");
		double d = 59;
		double c = 79;
		double f = 68;
		double m = 50;
		int cycleNum = 0;
		Random r = new Random();
		long start = System.currentTimeMillis();
		do {
			System.out.println("Cycle "+ (++cycleNum)+":");
			double deltaD = r.nextGaussian()*avgDelta;
			double deltaC = r.nextGaussian()*avgDelta;
			double deltaF = r.nextGaussian()*avgDelta;
			double deltaM = r.nextGaussian()*avgDelta;
			Engine e1 = new Engine((int)Math.round(d+deltaD), (int)Math.round(c+deltaC), (int)Math.round(f+deltaF), (int)Math.round(m+deltaM));
			Engine e2 = new Engine((int)Math.round(d-deltaD), (int)Math.round(c-deltaC), (int)Math.round(f-deltaF), (int)Math.round(m-deltaM));
			int result = playMatch(e1, e2);
			String currentTime = ((System.currentTimeMillis()-start)/1000)+"s: \t";
			if (result == -1) {
				d -= deltaD*applyFactor;
				c -= deltaC*applyFactor;
				f -= deltaF*applyFactor;
				m -= deltaM*applyFactor;
				System.out.println(currentTime + "New parameters: d="+d+", c="+c+", f="+f+", m="+m);
			}
			if (result == 1) {
				d += deltaD*applyFactor;
				c += deltaC*applyFactor;
				f += deltaF*applyFactor;
				m += deltaM*applyFactor;
				System.out.println(currentTime + "New parameters: d="+d+", c="+c+", f="+f+", m="+m);
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
			if (movesNum > 100) {
				//System.out.println("\nEnding game (100+ moves)");
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
