import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Task extends RecursiveTask<Void> {

    private final String path;
    private final long bytes;
    private final int chunk;
    private final long offset;

    public Task(String path, int chunk, long offset) throws IOException {
        this.path = path;
        this.bytes = Files.size(Path.of(path));
        this.chunk = chunk;
        this.offset = offset;
    }

    @Override
    protected Void compute() {
        if(offset < bytes) {
            //cream lista de taskuri
            List<Task> tasks = new ArrayList<>();
            String sb = "";
            //stringul de separatori
            String separators = ";:/?~\\.,><`[]{}()!@#$%^&-_+'=*\"| \t\r\n\0";
            int k = 0, c = 0;
            //deschidem fisieruk din care citim
            FileReader fr = null;
            try {
                fr = new FileReader(path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //trecem peste caracterele deja citite
            try {
                assert fr != null;
                fr.skip(offset);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //citim din fisier cat e nevoie pt crearea taskurilor
            if(bytes - offset > chunk) {
                try {
                    for(int i = 0; i < chunk; i++) {
                        c = fr.read();
                        sb += (char)c;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    for(int i = 0; i < bytes - offset; i++) {
                        c = fr.read();
                        sb += (char)c;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //in cazul cuvintelor rupte de unim inainte de pornirea workerilor
            if(separators.indexOf( (char)c ) == -1) {
                try {
                    c = fr.read();
                    while(separators.indexOf( (char)c ) == -1 && offset + chunk + k < bytes) {
                        sb += (char)c;
                        k++;
                        c = fr.read();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //cream taskurile si le adaugam in gramada de unde workerii iau taskuri
            Task task = null;
            try {
                task = new Task(path, chunk, offset + chunk + k);
            } catch (IOException e) {
                e.printStackTrace();
            }
            tasks.add(task);
            assert task != null;
            task.fork();

            //cautam si retinem toate cuvintele intr-un map si un array
            String word = "";
            for(char ch: sb.toCharArray()) {
                if(separators.indexOf( ch ) == -1) {
                    word += ch;
                } else {
                    if(!word.equals("")) {
                        Tema2.m.get(Tema2.index).words.add(word);
                        Tema2.m.get(Tema2.index).map.computeIfPresent(word.length(), (key, value) -> value + 1);
                        Tema2.m.get(Tema2.index).map.computeIfAbsent(word.length(),  value -> value = 1);
                        word = "";
                    }
                }
            }
            if(!word.equals("")) {
                Tema2.m.get(Tema2.index).words.add(word);
                Tema2.m.get(Tema2.index).map.computeIfPresent(word.length(), (key, value) -> value + 1);
                Tema2.m.get(Tema2.index).map.computeIfAbsent(word.length(),  value -> value = 1);
            }

            //dam join la taskuri
            for(Task t : tasks) {
                t.join();
            }
        }

        return null;
    }
}
