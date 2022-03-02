import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class Tema2 {

    public static ArrayList<Map> m;
    public static int index;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        //retinem parametrii din linia de comanda
        int workers = Integer.parseInt(args[0]);
        String in_file = args[1];
        String out_file = args[2];

        //citim informatiile din fisierul primit ca parametru
        File file = new File(in_file);
        Scanner myReader = new Scanner(file);
        int chunk = Integer.parseInt(myReader.nextLine());
        int files = Integer.parseInt(myReader.nextLine());
        ArrayList<String> filesPath = new ArrayList<>();
        m = new ArrayList<>();

        for(int i = 0 ; i < files; i++) {
            filesPath.add(myReader.nextLine());
            //calculam numarul de caractere din fisier
            long offset = 0;
            //pornim ForkJoinPool cu numarul de workeri primit ca parametru
            m.add(i, new Map());
            index = i;
            ForkJoinPool fjp = new ForkJoinPool(workers);
            fjp.invoke(new Task(filesPath.get(i), chunk, offset));
            fjp.shutdown();
        }
        myReader.close();

        //pornim workerii pentru calcularea rangurilor fisierelor
        ForkJoinPool fjp = new ForkJoinPool(workers);
        fjp.invoke(new Reduce(files, 0));
        fjp.shutdown();

        //ordonam mapul dupa rank
        for(int i = 0 ; i < files - 1; i++) {
            for(int j = i + 1; j < files; j++) {
                if(Double.parseDouble(m.get(i).rang) < Double.parseDouble(m.get(j).rang)) {
                    Tema2.swap(m.get(i), m.get(j));
                    String temp = filesPath.get(i);
                    filesPath.set(i, filesPath.get(j));
                    filesPath.set(j, temp);
                }
            }
        }

        //scriem in fisierul de iesire
        FileOutputStream outputStream = new FileOutputStream(out_file);
        for(int i = 0; i < files; i++) {
            StringBuilder sb = new StringBuilder();
            String path = filesPath.get(i);
            String[] words = path.split("/");
            sb.append(words[words.length - 1]);
            sb.append(",");
            sb.append(m.get(i).rang);
            sb.append(",");
            sb.append(m.get(i).max);
            sb.append(",");
            sb.append(m.get(i).count);
            sb.append("\n");
            byte[] strToBytes = sb.toString().getBytes();
            outputStream.write(strToBytes);
        }
        outputStream.close();
    }

    //functie pt swap intre doua obiecte de tip map
    public static void swap(Map a, Map b) {
        String temp = a.rang;
        a.rang = b.rang;
        b.rang = temp;

        int tmp = a.max;
        a.max = b.max;
        b.max = tmp;

        tmp = a.count;
        a.count = b.count;
        b.count = tmp;
    }
}