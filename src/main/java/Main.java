import com.couchbase.client.java.*;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) {
        FileWriter myWriter = ReportFileUtils.getFileWriter();
        Collection collection = CouchbaseConnection.connect();

        //call to executeThreadTest with 5 options of thread pool size
        for (int i = 1; i < 6; i++) {
            executeThreadTest(i, collection, myWriter);
        }
        CouchbaseConnection.closeConnection();
        ReportFileUtils.closeWriter(myWriter);
    }

    /**
     * method execute 5 Threads in parallel based on numberOfThreads.
     * each thread run for 3 minutes: read and write to couchbase and save
     * the efficiency of DB access.
     * after all threads finished: write a benchmark Report.
     * @param numberOfThreads
     * @param collection
     * @param myWriter
     */
    private static void executeThreadTest(int numberOfThreads, Collection collection, FileWriter myWriter) {

        Map<Long, List<Integer>> runningTimeDataMap = new HashMap<>();
        // creates five tasks
        Runnable r1 = new CouchbaseReadAndWriteTestThread(collection, 1L, runningTimeDataMap);
        Runnable r2 = new CouchbaseReadAndWriteTestThread(collection, 2L, runningTimeDataMap);
        Runnable r3 = new CouchbaseReadAndWriteTestThread(collection, 3L, runningTimeDataMap);
        Runnable r4 = new CouchbaseReadAndWriteTestThread(collection, 4L, runningTimeDataMap);
        Runnable r5 = new CouchbaseReadAndWriteTestThread(collection, 5L, runningTimeDataMap);

        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        pool.execute(r1);
        pool.execute(r2);
        pool.execute(r3);
        pool.execute(r4);
        pool.execute(r5);

        // pool shutdown ( Step 4)
        pool.shutdown();

        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        writeBenchmarkReport(numberOfThreads, myWriter, runningTimeDataMap);

    }

    /**
     * method move over thread data and calculate
     * relevant metrics for this benchmark, and write it on report file.
     * @param numberOfThreads
     * @param myWriter
     * @param runningEfficiencyDataMap
     */
    public static void writeBenchmarkReport(int numberOfThreads, FileWriter myWriter, Map<Long, List<Integer>> runningEfficiencyDataMap) {
        ReportFileUtils.writeToFile("when " + numberOfThreads + " threads run in parallel:", myWriter);
        int sizeOfRuns = 0;
        for (Map.Entry<Long, List<Integer>> mapElement : runningEfficiencyDataMap.entrySet()) {
            Long task = mapElement.getKey();
            List<Integer> runningTimeDataList = mapElement.getValue();
            int time = 0;
            for (Integer runningTimeData : runningTimeDataList) {
                time += runningTimeData;
            }
            sizeOfRuns += runningTimeDataList.size();
            int average = time / runningTimeDataList.size();
            ReportFileUtils.writeToFile("task:" + task + " Average reading and writing time is " + average + " MilliSeconds. run: " + runningTimeDataList.size() + " times", myWriter);

        }
        ReportFileUtils.writeToFile("when " + numberOfThreads + " threads run in parallel:" + sizeOfRuns + " times read and write succeed.", myWriter);

    }


}


