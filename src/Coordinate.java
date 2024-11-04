import java.util.List;
import java.lang.IndexOutOfBoundsException;
import java.util.Objects;

/**
 * Coordinate: class for expression location in a grid
*/
public class Coordinate {
	private int x;
	private int y;
	
/**
 *	Constructor
 *	@param row x coordinate 
 *	@param column y of coordinate
*/
	public Coordinate(int row, int col) {
		if(row < 0 || col < 0) {
			throw new IndexOutOfBoundsException();	
		}
		this.x = row;
		this.y = col;	
	}


/**
 *	Getter for row (x)
 *	@return row of this coordinate
*/
	public int row() {
		return x;
	}

/**
 *	Getter for column (y)
 *	@return column of this coordinate
*/
	public int col() {
		return y;
	}
	
	@Override
	public boolean equals(Object obj) {
		Coordinate objCoordinate = (Coordinate) obj;
		return this.row() == objCoordinate.row() && this.col() == objCoordinate.col();
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
