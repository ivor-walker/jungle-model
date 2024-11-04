public class Trap extends Square {
        public Trap(Player owner) {
                super(owner);
        }

        @Override
        public boolean isTrap() {
		return true;
	}
}
