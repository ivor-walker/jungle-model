package jungle.squares;public class PlainSquare extends Square {
	public PlainSquare() {
		//Passing null as PlainSquare cannot be owned by a Player
		super(null);
	}
}
