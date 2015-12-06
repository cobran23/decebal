package decebal;

import java.util.ArrayList;

public class Search {
	private Game board;
	private Move bestMove;
	private boolean output;
	
	public Search(Game board, boolean output) {
		this.board = board;
		this.output = output;
	}
	
	public String findMove(int maxThinkTime) {
		bestMove = null;
		
		int depth = 1;
		long startTime = System.currentTimeMillis();
		long passedTime = 0;
		long newPassedTime = 0;
		double newBranchingFactor;
		double branchingFactor = 30;
		
		do {
			int score;
			if (board.side == Game.WHITE) {
				score = max(depth, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			} else {
				score = min(depth, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
			}
			if(output) {
				System.out.println("info score cp " + score + " depth " + depth
					+ " pv " + bestMove);
			}
			depth++;
			newPassedTime = System.currentTimeMillis()-startTime;
			if (passedTime != 0) {
				newBranchingFactor = (double)newPassedTime / (double)passedTime;
				branchingFactor = branchingFactor * 0.6 + newBranchingFactor * 0.4;
			}
			passedTime = newPassedTime;
		} while (passedTime*branchingFactor < maxThinkTime);
		
		return bestMove.toString();
	}
	
	private int max(int startdepth, int depth, int alpha, int beta) {

		// Return evaluated score if this is the end.
		if (depth == 0) {
			return board.eval();
		}
		ArrayList<Move> moves = board.generateMoves();

		boolean f = false;
		
		// Iterate through moves
		for (Move move : moves) {
			if (board.makemove(move)) {
				f = true;
				int x = min(startdepth, depth - 1, alpha, beta);
				board.takeback();
				if (x > alpha) {
					alpha = x;
					if (alpha >= beta) {
						break; // beta cut-off
					}
					if (depth == startdepth)
						bestMove = move;
				}
			}
		}
		
		if (!f) {
			if (board.in_check(Game.WHITE))
				return -100000 + startdepth - depth - 1;
			else
				return 0;
		}
		
		return alpha;
	}

	private int min(int startdepth, int depth, int alpha, int beta) {

		// Return evaluated score if this is the end.
		if (depth == 0) {
			return board.eval();
		}
		ArrayList<Move> moves = board.generateMoves();
		
		boolean f = false;

		// Iterate through moves
		for (Move move : moves) {
			if (board.makemove(move)) {
				f = true;
				int x = max(startdepth, depth - 1, alpha, beta);
				board.takeback();
				if (x < beta) {
					beta = x;
					if (beta <= alpha) {
						break; // alpha cut-off
					}
					if (depth == startdepth)
						bestMove = move;
				}
			}
		}
		
		if (!f) {
			if (board.in_check(Game.BLACK))
				return 100000 - startdepth + depth + 1;
			else
				return 0;
		}
		
		return beta;
	}
}
