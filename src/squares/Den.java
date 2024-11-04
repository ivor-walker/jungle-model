package jungle.squares;
import jungle.Player;

public class Den extends Square {
	public Den(Player owner) {
		super(owner);
	}

	@Override
	public boolean isDen() {
		return true;
	}	
}
