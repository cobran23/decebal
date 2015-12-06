package decebal;

import java.util.ArrayList;
import java.util.Scanner;


public class Main {
	private static Board board;
	
	private static int DEVELOPMENT = 50;
	private static int CENTER = 50;
	private static int FORWARD = 50;
	private static int MATERIAL = 50;

	public static void main(String[] args) {
		board = new Board(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			String command = sc.nextLine();
			if (command.equals("exit")) {
				sc.close();
				System.exit(0);
			} else {
				processUCI(command);
			}
		}
		sc.close();
	}
	
	private static void processUCI(String inputLine) {
		if (inputLine.equals("uci")) {
			System.out.println("id name Decebal");
			System.out.println("id author Constantin Brincoveanu");
			System.out.println("option name Development type spin default 50 min 0 max 100");
			System.out.println("option name Center type spin default 50 min 0 max 100");
			System.out.println("option name Forward type spin default 50 min 0 max 100");
			System.out.println("option name Material type spin default 50 min 0 max 100");
			System.out.println("uciok");
		} else if (inputLine.equals("isready")) {
			System.out.println("readyok");
		} else if (inputLine.equals("ucinewgame")) {
			board = new Board(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
		} else if (inputLine.equals("position startpos")) {
			board = new Board(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
		} else if (inputLine.startsWith("position startpos moves")) {
			board = new Board(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
			String movesList = inputLine.substring(23);
			Scanner moveScanner = new Scanner(movesList);
			try {
				while (moveScanner.hasNext()) {
					String moveString = moveScanner.next();
					ArrayList<Move> moves = board.generateMoves();
					Move move = null;
					for (Move m : moves) {
						if (m.toString().equals(moveString)) {
							move = m;
						}
					}
					board.makemove(move);
				}
			} catch (Exception e) {
				System.err.println("The moves could not be parsed!");
			}
			moveScanner.close();
		} else if (inputLine.startsWith("setoption name")) {
			Scanner optionScanner = new Scanner(inputLine);
			while (optionScanner.hasNext()) {
				String s = optionScanner.next();
				if (s.equals("Development")) {
					if (optionScanner.next().equals("value")) {
						String v = optionScanner.next();
						DEVELOPMENT = new Integer(v);
					}
				}
				if (s.equals("Center")) {
					if (optionScanner.next().equals("value")) {
						String v = optionScanner.next();
						CENTER = new Integer(v);
					}
				}
				if (s.equals("Forward")) {
					if (optionScanner.next().equals("value")) {
						String v = optionScanner.next();
						FORWARD = new Integer(v);
					}
				}
				if (s.equals("Material")) {
					if (optionScanner.next().equals("value")) {
						String v = optionScanner.next();
						MATERIAL = new Integer(v);
					}
				}
			}
			optionScanner.close();
		} else if (inputLine.indexOf("go") == 0) {
			Search search = new Search(board);
			Scanner goScanner = new Scanner(inputLine);
			long wTime = 0, bTime = 0, wInc = 0, bInc = 0, moveTime = -1, movesToGo = 40;
			boolean infinite = false;
			while (goScanner.hasNext()) {
				String command = goScanner.next();
				if (command.equals("wtime")) {
					wTime = Long.parseLong(goScanner.next());
				} else if (command.equals("btime")) {
					bTime = Long.parseLong(goScanner.next());
				} else if (command.equals("winc")) {
					wInc = Long.parseLong(goScanner.next());
				} else if (command.equals("binc")) {
					bInc = Long.parseLong(goScanner.next());
				} else if (command.equals("movetime")) {
					moveTime = Long.parseLong(goScanner.next());
				} else if (command.equals("movestogo")) {
					movesToGo = Long.parseLong(goScanner.next());
				} else if (command.equals("infinite")) {
					infinite = true;
				}
			}
			goScanner.close();
			int maxThinkTime = 2000;
			if (infinite) {
				maxThinkTime = 30000; //Infinite not supported
			} else if (moveTime != -1) {
				maxThinkTime = (int)moveTime;
			} else {
				maxThinkTime = Math.min((int)(wTime/(movesToGo+5)+wInc),(int)(bTime/(movesToGo+5)+bInc));
			}
			maxThinkTime = maxThinkTime * 3; //
			System.out.println("bestmove "+ search.findMove(maxThinkTime));
		}
	}
}
