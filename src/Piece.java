public class Piece {
	private Player owner;
	private Square square;
	private int rank;

	/*
 * 	Constructor
 * 	@param Owner of piece, square on which piece sits, rank of piece
 * 	*/
	public Piece(Player owner, Square square, int rank) {
		this.owner = owner;
		this.square = square;
		this.rank = rank;
	}

/*
 *      Checks if
 *      a) square is of type that can be owned (i.e not PlainSquare or WaterSquare)
 *      b) specified player owns the square
 *      @param specified player
 *      */
        public boolean isOwnedBy(Player player) {
                //Owner for unownable squares is null 
                return owner != null && owner.equals(player);
        }

}
