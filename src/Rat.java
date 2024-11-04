/**
 * Special pieces: Rat
 * Behaviours: rank 1, can swim, can defeat elephants
*/
public class Rat extends Piece {
        //Has rank 1
        private static int RAT_RANK = 1;
        public Rat(Player owner, Square square){
                super(owner, square, RAT_RANK);
        }

        //Can swim      
        @Override
        public boolean canSwim() {
                return true;
        }

        //Can defeat elephants  
        @Override
        public boolean canDefeat(Piece target) {
                return super.canDefeat(target) || target.getRank()==8;
        }
}

