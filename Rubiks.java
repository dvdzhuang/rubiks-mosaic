public class Rubiks {

  public static void main(String[] args) {
    RubiksImage ri = new RubiksImage(args[0], Integer.parseInt(args[1]));
    RubiksAverage ra = new RubiksAverage(args[0], Integer.parseInt(args[1]));
  }
}
