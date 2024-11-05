package jungle;
import java.util.List;
import java.util.Objects;

/**
 * Coordinate: class for expression location in a grid.
*/
public class Coordinate {
	private int x;
	private int y;
	private int heightLimit;
	private int widthLimit;
/**
 *	Constructor.
 *	@param row x coordinate 
 *	@param column y of coordinate
*/
	public Coordinate(int row, int col) {
		this.x = row;
		this.y = col;	
	}

/**
 *	Getter for row (x).
 *	@return row of this coordinate
*/
	public int row() {
		return x;
	}

/**
 *	Getter for column (y).
 *	@return column of this coordinate
*/
	public int col() {
		return y;
	}

	//Overriden equals to enable searching lists for a given coordinate	
	@Override
	public boolean equals(Object obj) {
		Coordinate objCoordinate = (Coordinate) obj;
		return this.row() == objCoordinate.row() && this.col() == objCoordinate.col();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

/**
 * 	Sets limits for coordinates
*/
}
