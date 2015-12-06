package decebal;

public class Engine {
	private boolean enableBook;
	private boolean output;
	private int DEVELOPMENT;
	private int CENTER;
	private int FORWARD;
	private int MATERIAL;
	private Game game;
	
	public Engine(int DEVELOPMENT, int CENTER, int FORWARD, int MATERIAL) {
		this.DEVELOPMENT = DEVELOPMENT;
		this.CENTER = CENTER;
		this.FORWARD = FORWARD;
		this.MATERIAL = MATERIAL;
		enableBook = true;
		output = true;
		game = new Game(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
	}

	public void setEnableBook(boolean enableBook) {
		this.enableBook = enableBook;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	public void resetGame() {
		game = new Game(DEVELOPMENT, CENTER, FORWARD, MATERIAL);
	}

	public Game getGame() {
		return game;
	}

	public String findMove(int thinkTime) {
		if (enableBook) {
			String bookMove = Book.getBestMove(game.getShortFen());
			if (bookMove != null) {
				return bookMove;
			}
		}
		
		return new Search(game, output).findMove(thinkTime);
	}
	
}
