package decebal;

public class Move {
	public int from, to, promote, bits;
	
	public Move(int from, int to, int promote, int bits) {
		this.from = from;
		this.to = to;
		this.promote = promote;
		this.bits = bits;
	}
	
	public String toString() {
		return getLetter(from & 7) + (8-(from >> 3)) + getLetter(to & 7) + (8-(to >> 3)) + getPromoteLetter(promote);
	}
	
	private String getLetter(int col) {
		switch (col) {
		case 0:
			return "a";
		case 1:
			return "b";
		case 2:
			return "c";
		case 3:
			return "d";
		case 4:
			return "e";
		case 5:
			return "f";
		case 6:
			return "g";
		case 7:
			return "h";
		default:
			return "?";
		}
	}
	
	private String getPromoteLetter(int promote) {
		switch (promote) {
		case 1:
			return "n";
		case 2:
			return "b";
		case 3:
			return "r";
		case 4:
			return "q";
		default:
			return "";
		}
	}
}
