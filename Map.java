import java.util.ArrayList;
import java.util.HashMap;

public class Map {

    ArrayList<String> words;
    HashMap<Integer, Integer> map;
    String rang;
    Integer max, count;

    public Map() {
        this.words = new ArrayList<>();
        this.map = new HashMap<>();
        this.rang = "";
        this.count = 0;
        this.max = 0;
    }
}
