package jungle.pieces;
import jungle.Player;
import jungle.squares.Square;

/**
 * Special piece lion
 * Has rank 7, can leap both directions
*/

public class Lion extends Piece {
	//Has rank 7
	private static int LION_RANK = 7;
	public Lion(Player owner, Square square) {
		super(owner, square, LION_RANK);
	}

	//Can leap horizontally and vertically
	@Override
	public boolean canLeapHorizontally() {
		return true;	
	}

	@Override
	public boolean canLeapVertically() {
		return true;	
	}
}
