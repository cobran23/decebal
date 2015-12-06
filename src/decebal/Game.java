package decebal;

import java.util.ArrayList;

public class Game {
	public static final int WHITE = 0;
	public static final int BLACK = 1;
    private static final int PAWN = 0;
    private static final int KNIGHT = 1;
    private static final int BISHOP = 2;
    private static final int ROOK = 3;
    private static final int QUEEN = 4;
    private static final int KING = 5;
    private static final int EMPTY = 6;
    
    private static final int A1 = 56;
    private static final int B1 = 57;
    private static final int C1 = 58;
    private static final int D1 = 59;
    private static final int E1 = 60;
    private static final int F1 = 61;
    private static final int G1 = 62;
    private static final int H1 = 63;
    private static final int A8 = 0;
    private static final int B8 = 1;
    private static final int C8 = 2;
    private static final int D8 = 3;
    private static final int E8 = 4;
    private static final int F8 = 5;
    private static final int G8 = 6;
    private static final int H8 = 7;
    
    private static final int[] evalCenter = {
  		 0,  1,  2,  3,  3,  2,  1,  0,
  		 1,  6,  8, 10, 10,  8,  6,  1,
  		 2,  8, 20, 24, 24, 20,  8,  2,
  		 3, 10, 24, 40, 40, 24, 10,  3,
  		 3, 10, 24, 40, 40, 24, 10,  3,
  		 2,  8, 20, 24, 24, 20,  8,  2,
  		 1,  6,  8, 10, 10,  8,  6,  1,
  		 0,  1,  2,  3,  3,  2,  1,  0
  	};
    private static final double[] evalCenterWeight = {1, 1, 0.5, 0.1, 0.2, 0.2};
    private static final int[] evalForward = {
 		40, 40, 40, 40, 40, 40, 40, 40,
 		36, 36, 36, 36, 36, 36, 36, 36,
 		32, 32, 32, 32, 32, 32, 32, 32,
 		27, 27, 27, 27, 27, 27, 27, 27,
 		21, 21, 21, 21, 21, 21, 21, 21,
 		14, 14, 14, 14, 14, 14, 14, 14,
 		 7,  7,  7,  7,  7,  7,  7,  7,
 		 0,  0,  0,  0,  0,  0,  0,  0
 	};
    private static final double[] evalForwardWeight = {1, 0.8, 0.3, 0.1, 0.2, 0.1};

    public int side;
    public int ply;
    private int xside;
    private int castle;
    private int ep;
    private int fifty;
    private int eval;
    private int color[];
    private int piece[];
    private int mailbox[];
    private int mailbox64[];
    private int flip[];
    private boolean slide[];
    private int offsets[];
    private int offset[][];
    private int castle_mask[];
    private int piece_eval[];
    private int piece_sq_eval[][];
    private ArrayList<HistoryItem> history;

    public Game(int DEVELOPMENT, int CENTER, int FORWARD, int MATERIAL) {
        initBoard(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
    }

    private void initBoard(int DEVELOPMENT, int CENTER, int FORWARD, int MATERIAL) {
        side = WHITE;
        ply = 0;
        xside = BLACK;
        castle = 15;
        ep = -1;
    	fifty = 0;
    	eval = 0;
        color = new int[] {
        		BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,
        		BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,BLACK,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		WHITE,WHITE,WHITE,WHITE,WHITE,WHITE,WHITE,WHITE,
        		WHITE,WHITE,WHITE,WHITE,WHITE,WHITE,WHITE,WHITE
        };
        piece = new int[] {
        		ROOK,KNIGHT,BISHOP,QUEEN,KING,BISHOP,KNIGHT,ROOK,
        		PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,EMPTY,
        		PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,PAWN,
        		ROOK,KNIGHT,BISHOP,QUEEN,KING,BISHOP,KNIGHT,ROOK
        };
        mailbox = new int[] {
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1,  0,  1,  2,  3,  4,  5,  6,  7, -1,
                -1,  8,  9, 10, 11, 12, 13, 14, 15, -1,
                -1, 16, 17, 18, 19, 20, 21, 22, 23, -1,
                -1, 24, 25, 26, 27, 28, 29, 30, 31, -1,
                -1, 32, 33, 34, 35, 36, 37, 38, 39, -1,
                -1, 40, 41, 42, 43, 44, 45, 46, 47, -1,
                -1, 48, 49, 50, 51, 52, 53, 54, 55, -1,
                -1, 56, 57, 58, 59, 60, 61, 62, 63, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
                -1, -1, -1, -1, -1, -1, -1, -1, -1, -1
        };
        mailbox64 = new int[] {
                21, 22, 23, 24, 25, 26, 27, 28,
                31, 32, 33, 34, 35, 36, 37, 38,
                41, 42, 43, 44, 45, 46, 47, 48,
                51, 52, 53, 54, 55, 56, 57, 58,
                61, 62, 63, 64, 65, 66, 67, 68,
                71, 72, 73, 74, 75, 76, 77, 78,
                81, 82, 83, 84, 85, 86, 87, 88,
                91, 92, 93, 94, 95, 96, 97, 98
        };
        flip = new int[] {
        		 56,  57,  58,  59,  60,  61,  62,  63,
        		 48,  49,  50,  51,  52,  53,  54,  55,
        		 40,  41,  42,  43,  44,  45,  46,  47,
        		 32,  33,  34,  35,  36,  37,  38,  39,
        		 24,  25,  26,  27,  28,  29,  30,  31,
        		 16,  17,  18,  19,  20,  21,  22,  23,
        		  8,   9,  10,  11,  12,  13,  14,  15,
        		  0,   1,   2,   3,   4,   5,   6,   7
        };
        slide = new boolean[]{false, false, true, true, true, false};
        offsets = new int[] {0, 8, 4, 4, 8, 8};
        offset = new int[][]{
            {   0,   0,  0,  0, 0,  0,  0,  0 },
            { -21, -19,-12, -8, 8, 12, 19, 21 },
            { -11,  -9,  9, 11, 0,  0,  0,  0 },
            { -10,  -1,  1, 10, 0,  0,  0,  0 },
            { -11, -10, -9, -1, 1,  9, 10, 11 },
            { -11, -10, -9, -1, 1,  9, 10, 11 }
        };
        castle_mask = new int[] {
       		 7, 15, 15, 15,  3, 15, 15, 11,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		15, 15, 15, 15, 15, 15, 15, 15,
       		13, 15, 15, 15, 12, 15, 15, 14
        };
        piece_eval = new int[] {100,320,330,500,900,0};
        piece_sq_eval = new int[][] {
        		{ //pawn
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0
           		},
        		{ //knight
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,-40,  0,  0,  0,  0,-40,  0
           		},
        		{ //bishop
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,-40,  0,  0,-40,  0,  0
           		},
        		{ //rook
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0
           		},
        		{ //queen
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0
           		},
        		{ //king
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0, 40,  0,  0,  0, 40,  0
           		},
        		{
          			 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0,
          	   		 0,  0,  0,  0,  0,  0,  0,  0
           		}
        };
        
        for (int p = PAWN; p <= KING; p++) {
        	for (int i = 0; i < 64; i++) {
        		piece_sq_eval[p][i] *= (DEVELOPMENT/50.0);
        		piece_sq_eval[p][i] += (MATERIAL/50.0)*(double)piece_eval[p];
        		piece_sq_eval[p][i] += (CENTER/50.0)*(double)evalCenter[i]*evalCenterWeight[p];
        		piece_sq_eval[p][i] += (FORWARD/50.0)*(double)evalForward[i]*evalForwardWeight[p];
        	}
        }
        history = new ArrayList<>();
    }

    public ArrayList<Move> generateMoves() {
    	ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 64; ++i) {
            if (color[i] == side) {
                int p = piece[i];
                if (p == PAWN) {
                	//pawn moves
                	if (side == WHITE) {
    					if ((i & 7) != 0 && color[i - 9] == BLACK) {
    						if (i-9 <= H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 9, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i - 9, 0, 17));
    						}
    					}
    					if ((i & 7) != 7 && color[i - 7] == BLACK) {
    						if (i-7 <= H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 7, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i - 7, 0, 17));
    						}
    					}
    					if (color[i - 8] == EMPTY) {
    						if (i-8 <= H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 8, v, 48));
    							}
    						} else {
    							moves.add(new Move(i, i - 8, 0, 16));
    						}
    						if (i >= 48 && color[i - 16] == EMPTY) {
    							moves.add(new Move(i, i - 16, 0, 24));
    						}
    					}
    				}
    				else {
    					if ((i & 7) != 0 && color[i + 7] == WHITE) {
    						if (i+7 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 7, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i + 7, 0, 17));
    						}
    					}
    					if ((i & 7) != 7 && color[i + 9] == WHITE) {
    						if (i+9 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 9, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i + 9, 0, 17));
    						}
    					}
    					if (color[i + 8] == EMPTY) {
    						if (i+8 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 8, v, 48));
    							}
    						} else {
    							moves.add(new Move(i, i + 8, 0, 16));
    						}
    						if (i <= 15 && color[i + 16] == EMPTY) {
    							moves.add(new Move(i, i + 16, 0, 24));
    						}
    					}
    				}
                } else {
                	//other piece moves
                    for (int j = 0; j < offsets[p]; ++j) {
                        for (int n = i;;) {
                            n = mailbox[mailbox64[n] + offset[p][j]];
                            if (n == -1){
                            	break; //outside board
                            }
                            if (color[n] != EMPTY) {
                                if (color[n] == xside) {
                                	moves.add(new Move(i, n, 0, 1)); //capture from i to n
                                }
                                break;
                            }
                            moves.add(new Move(i, n, 0, 0)); //quiet move from i to n
                            if (!slide[p]){
                            	break; //next direction
                            }
                        }
                    }
                }
            }
        }
        
    	/* generate castle moves */
    	if (side == WHITE) {
    		if ((castle & 1) != 0)
    			moves.add(new Move(E1, G1, 0, 2));
    		if ((castle & 2) != 0)
    			moves.add(new Move(E1, C1, 0, 2));
    	}
    	else {
    		if ((castle & 4) != 0)
    			moves.add(new Move(E8, G8, 0, 2));
    		if ((castle & 8) != 0)
    			moves.add(new Move(E8, C8, 0, 2));
    	}
    	
    	/* generate en passant moves */
    	if (ep != -1) {
    		if (side == WHITE) {
    			if ((ep & 7) != 0 && color[ep + 7] == WHITE && piece[ep + 7] == PAWN)
    				moves.add(new Move(ep+7, ep, 0, 21));
    			if ((ep & 7) != 7 && color[ep + 9] == WHITE && piece[ep + 9] == PAWN)
    				moves.add(new Move(ep+9, ep, 0, 21));
    		}
    		else {
    			if ((ep & 7) != 0 && color[ep - 9] == BLACK && piece[ep - 9] == PAWN)
    				moves.add(new Move(ep-9, ep, 0, 21));
    			if ((ep & 7) != 7 && color[ep - 7] == BLACK && piece[ep - 7] == PAWN)
    				moves.add(new Move(ep-7, ep, 0, 21));
    		}
    	}
        
        return moves;
    }
    
    public ArrayList<Move> generateCapMoves() {
    	ArrayList<Move> moves = new ArrayList<>();
        for (int i = 0; i < 64; ++i) {
            if (color[i] == side) {
                int p = piece[i];
                if (p == PAWN) {
                	//pawn moves
                	if (side == WHITE) {
    					if ((i & 7) != 0 && color[i - 9] == BLACK) {
    						if (i-9 < H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 9, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i - 9, 0, 17));
    						}
    					}
    					if ((i & 7) != 7 && color[i - 7] == BLACK) {
    						if (i-7 < H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 7, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i - 7, 0, 17));
    						}
    					}
    					if (color[i - 8] == EMPTY) {
    						if (i-8 < H8) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i - 8, v, 48));
    							}
    						}
    					}
    				}
    				else {
    					if ((i & 7) != 0 && color[i + 7] == WHITE) {
    						if (i+7 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 7, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i + 7, 0, 17));
    						}
    					}
    					if ((i & 7) != 7 && color[i + 9] == WHITE) {
    						if (i+9 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 9, v, 49));
    							}
    						} else {
    							moves.add(new Move(i, i + 9, 0, 17));
    						}
    					}
    					if (color[i + 8] == EMPTY) {
    						if (i+8 >= A1) {
    							for (int v = KNIGHT; v <= QUEEN; v++) {
    								moves.add(new Move(i, i + 8, v, 48));
    							}
    						}
    					}
    				}
                } else {
                	//other piece moves
                    for (int j = 0; j < offsets[p]; ++j) {
                        for (int n = i;;) {
                            n = mailbox[mailbox64[n] + offset[p][j]];
                            if (n == -1){
                            	break; //outside board
                            }
                            if (color[n] != EMPTY) {
                                if (color[n] == xside) {
                                	moves.add(new Move(i, n, 0, 1)); //capture from i to n
                                }
                                break;
                            }
                            //moves.add(new Move(i, n, 0, 0)); //quiet move from i to n
                            if (!slide[p]){
                            	break; //next direction
                            }
                        }
                    }
                }
            }
        }
    	
    	/* generate en passant moves */
    	if (ep != -1) {
    		if (side == WHITE) {
    			if ((ep & 7) != 0 && color[ep + 7] == WHITE && piece[ep + 7] == PAWN)
    				moves.add(new Move(ep+7, ep, 0, 21));
    			if ((ep & 7) != 7 && color[ep + 9] == WHITE && piece[ep + 9] == PAWN)
    				moves.add(new Move(ep+9, ep, 0, 21));
    		}
    		else {
    			if ((ep & 7) != 0 && color[ep - 9] == BLACK && piece[ep - 9] == PAWN)
    				moves.add(new Move(ep-9, ep, 0, 21));
    			if ((ep & 7) != 7 && color[ep - 7] == BLACK && piece[ep - 7] == PAWN)
    				moves.add(new Move(ep-7, ep, 0, 21));
    		}
    	}
        
        return moves;
    }
    
    public boolean in_check(int s) {
    	for (int i = 0; i < 64; ++i)
    		if (piece[i] == KING && color[i] == s)
    			return attack(i, s ^ 1);
    	return true;  //shouldn't get here
    }
    
    private boolean attack(int sq, int s) {
    	for (int i = 0; i < 64; ++i)
    		if (color[i] == s) {
    			if (piece[i] == PAWN) {
    				if (s == WHITE) {
    					if ((i & 7) != 0 && i - 9 == sq)
    						return true;
    					if ((i & 7) != 7 && i - 7 == sq)
    						return true;
    				}
    				else {
    					if ((i & 7) != 0 && i + 7 == sq)
    						return true;
    					if ((i & 7) != 7 && i + 9 == sq)
    						return true;
    				}
    			}
    			else
    				for (int j = 0; j < offsets[piece[i]]; ++j)
    					for (int n = i;;) {
    						n = mailbox[mailbox64[n] + offset[piece[i]][j]];
    						if (n == -1)
    							break;
    						if (n == sq)
    							return true;
    						if (color[n] != EMPTY)
    							break;
    						if (!slide[piece[i]])
    							break;
    					}
    		}
    	return false;
    }
    
    public boolean makemove(Move m)
    {
    	int currentEval = eval;
    	
    	/* test to see if a castle move is legal and move the rook
    	   (the king is moved with the usual move code later) */
    	if ((m.bits & 2) != 0) {
    		int from, to;

    		if (in_check(side))
    			return false;
    		switch (m.to) {
    			case 62:
    				if (color[F1] != EMPTY || color[G1] != EMPTY ||
    						attack(F1, xside) || attack(G1, xside))
    					return false;
    				from = H1;
    				to = F1;
    				break;
    			case 58:
    				if (color[B1] != EMPTY || color[C1] != EMPTY || color[D1] != EMPTY ||
    						attack(C1, xside) || attack(D1, xside))
    					return false;
    				from = A1;
    				to = D1;
    				break;
    			case 6:
    				if (color[F8] != EMPTY || color[G8] != EMPTY ||
    						attack(F8, xside) || attack(G8, xside))
    					return false;
    				from = H8;
    				to = F8;
    				break;
    			case 2:
    				if (color[B8] != EMPTY || color[C8] != EMPTY || color[D8] != EMPTY ||
    						attack(C8, xside) || attack(D8, xside))
    					return false;
    				from = A8;
    				to = D8;
    				break;
    			default:  // shouldn't get here
    				from = -1;
    				to = -1;
    				break;
    		}
    		color[to] = color[from];
    		piece[to] = piece[from];
    		color[from] = EMPTY;
    		piece[from] = EMPTY;
    		if (color[to] == WHITE) {
    			eval -= piece_sq_eval[piece[to]][from];
    			eval += piece_sq_eval[piece[to]][to];
    		} else {
    			eval += piece_sq_eval[piece[to]][flip[from]];
    			eval -= piece_sq_eval[piece[to]][flip[to]];
    		}
    	}

    	/* back up information so we can take the move back later. */
    	HistoryItem h = new HistoryItem();
    	h.m = m;
    	h.capture = piece[(int)m.to];
    	h.castle = castle;
    	h.ep = ep;
    	h.fifty = fifty;
    	h.eval = currentEval;
    	history.add(h);

    	/* update the castle, en passant, and
    	   fifty-move-draw variables */
    	castle &= castle_mask[(int)m.from] & castle_mask[(int)m.to];
    	if ((m.bits & 8) != 0) {
    		if (side == WHITE)
    			ep = m.to + 8;
    		else
    			ep = m.to - 8;
    	}
    	else
    		ep = -1;
    	if ((m.bits & 17) != 0)
    		fifty = 0;
    	else
    		++fifty;

    	/* move the piece */
    	if (color[m.to] == WHITE) {
    		eval -= piece_sq_eval[piece[m.to]][m.to];
    	} else if (color[m.to] == BLACK) {
    		eval += piece_sq_eval[piece[m.to]][flip[m.to]];
    	}
    	color[m.to] = side;
    	if ((m.bits & 32) != 0) {
    		piece[m.to] = m.promote;
    	} else {
    		piece[m.to] = piece[m.from];
    	}
    	if (side == WHITE) {
    		eval -= piece_sq_eval[piece[m.from]][m.from];
    		eval += piece_sq_eval[piece[m.to]][m.to];
    	} else {
    		eval += piece_sq_eval[piece[m.from]][flip[m.from]];
    		eval -= piece_sq_eval[piece[m.to]][flip[m.to]];
    	}
    	color[m.from] = EMPTY;
    	piece[m.from] = EMPTY;

    	/* erase the pawn if this is an en passant move */
    	if ((m.bits & 4) != 0) {
    		if (side == WHITE) {
    			eval += piece_sq_eval[piece[m.to + 8]][flip[m.to + 8]];
    			color[m.to + 8] = EMPTY;
    			piece[m.to + 8] = EMPTY;
    		}
    		else {
    			eval -= piece_sq_eval[piece[m.to + 8]][m.to + 8];
    			color[m.to - 8] = EMPTY;
    			piece[m.to - 8] = EMPTY;
    		}
    	}

    	/* switch sides and test for legality (if we can capture
    	   the other guy's king, it's an illegal position and
    	   we need to take the move back) */
    	ply++;
    	side ^= 1;
    	xside ^= 1;
    	if (in_check(xside)) {
    		takeback();
    		return false;
    	}
    	return true;
    }
    
    public void takeback()
    {
    	ply--;
    	HistoryItem historyItem = history.remove(history.size()-1);
    	Move m = historyItem.m;
    	side ^= 1;
    	xside ^= 1;
    	castle = historyItem.castle;
    	ep = historyItem.ep;
    	fifty = historyItem.fifty;
    	eval = historyItem.eval;
    	color[(int)m.from] = side;
    	if ((m.bits & 32)!=0)
    		piece[(int)m.from] = PAWN;
    	else
    		piece[(int)m.from] = piece[(int)m.to];
    	if (historyItem.capture == EMPTY) {
    		color[(int)m.to] = EMPTY;
    		piece[(int)m.to] = EMPTY;
    	}
    	else {
    		color[(int)m.to] = xside;
    		piece[(int)m.to] = historyItem.capture;
    	}
    	if ((m.bits & 2) != 0) {
    		int from, to;

    		switch(m.to) {
    			case 62:
    				from = F1;
    				to = H1;
    				break;
    			case 58:
    				from = D1;
    				to = A1;
    				break;
    			case 6:
    				from = F8;
    				to = H8;
    				break;
    			case 2:
    				from = D8;
    				to = A8;
    				break;
    			default:  // shouldn't get here
    				from = -1;
    				to = -1;
    				break;
    		}
    		color[to] = side;
    		piece[to] = ROOK;
    		color[from] = EMPTY;
    		piece[from] = EMPTY;
    	}
    	if ((m.bits & 4) != 0) {
    		if (side == WHITE) {
    			color[m.to + 8] = xside;
    			piece[m.to + 8] = PAWN;
    		}
    		else {
    			color[m.to - 8] = xside;
    			piece[m.to - 8] = PAWN;
    		}
    	}
    }
    
    public void printBoard() {
    	System.out.println();
    	for (int i = 0; i < 64; i++) {
    		if (color[i] == WHITE) {
    			System.out.print(" "+pieceChar(piece[i]).toUpperCase());
    		} else {
    			System.out.print(" "+pieceChar(piece[i]).toLowerCase());
    		}
    		
    		if (i % 8 == 7) {
    			System.out.println();
    		}
    	}
    	System.out.println("Eval: "+ eval);
    	System.out.println();
    }
    
    private String pieceChar(int p) {
    	switch (p) {
    	case PAWN:
			return "P";
    	case KNIGHT:
			return "N";
    	case BISHOP:
			return "B";
    	case ROOK:
			return "R";
    	case QUEEN:
			return "Q";
    	case KING:
			return "K";
    	case EMPTY:
			return ".";
		default:
			return "?";
		}
    }
    
    public int eval() {
    	return eval;
    }
    
    public String getShortFen() {
    	//without en passant and move number
    	String fen = "";
    	int emptyCount = 0;
    	for (int i = 0; i < 64; i++) {
    		if (color[i] == EMPTY) {
    			emptyCount++;
    		} else {
    			if (emptyCount > 0) {
    				fen += ""+emptyCount;
    				emptyCount = 0;
    			}
    			if (color[i] == WHITE) {
	    			fen += pieceChar(piece[i]).toUpperCase();
	    		} else {
	    			fen += pieceChar(piece[i]).toLowerCase();
	    		}
    		}
    		
    		if (i % 8 == 7) {
    			if (emptyCount > 0) {
    				fen += ""+emptyCount;
    				emptyCount = 0;
    			}
    			if (i < 63) {
    				fen +="/";
    			}
    		}
    	}
    	fen += " ";
    	if (side == WHITE) {
    		fen += "w";
    	} else {
    		fen += "b";
    	}
    	fen += " ";
		if (castle == 0) {
			fen += "-";
		} else {
			if ((castle & 1) != 0)
				fen += "K";
			if ((castle & 2) != 0)
				fen += "Q";
			if ((castle & 4) != 0)
				fen += "k";
			if ((castle & 8) != 0)
				fen += "q";
		}
    	return fen;
    }
}
