import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveTask;

public class Reduce extends RecursiveTask<Void> {

    int files;
    int index;

    public Reduce(int files, int index) {
        this.files = files;
        this.index = index;
    }

    //calculam al k-lea termen fibonacci
    public int sumfib(int k) {
        int[] fibo =new int[k+1];
        fibo[0] = 0; fibo[1] = 1;
        int sum = fibo[1];
        for (int i=2; i<=k; i++)
        {
            fibo[i] = fibo[i-1]+fibo[i-2];
            sum = fibo[i];
        }
        return sum;
    }

    @Override
    protected Void compute() {
        if(index < files) {
            List<Reduce> tasks = new ArrayList<>();

            //cream taskurile
            Reduce t = new Reduce(files, index + 1);
            tasks.add(t);
            t.fork();

            //calculam cel mai lung cuvant, numarul de astfel de cuvinte si rangul
            int sum = 0, count = 0;
            for (Integer key : Tema2.m.get(index).map.keySet()) {
                Integer value = Tema2.m.get(index).map.get(key);
                if(key > Tema2.m.get(index).max) {
                    Tema2.m.get(index).max = key;
                    Tema2.m.get(index).count = value;
                }

                int sumf = sumfib(key + 1);
                sum += sumf * value;
                count += value;
            }
            Tema2.m.get(index).rang = String.format("%.2f", (double)sum/count);

            for (Reduce task : tasks) {
                task.join();
            }
        }
        return null;
    }
}
