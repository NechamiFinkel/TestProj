

import com.couchbase.client.java.*;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;



public class CouchbaseReadAndWriteTestThread implements Runnable {
    Collection collection;
    List<PerformanceResults> performanceResultsList;

    public CouchbaseReadAndWriteTestThread(Collection collection, List<PerformanceResults> performanceResultsList) {
        super();
        this.collection = collection;
        this.performanceResultsList = performanceResultsList;
    }


    /**
     * method is working for 3 minutes:
     * 1. read from json file and write it to couchbase - save the time duration
     * 3. read this key from couchbase 3 times -save the time duration.
     * 4. Writes the results in performanceResultsList.
     */
    public void run() {
        long start = System.currentTimeMillis();
        long end = start + 180000;

        while (System.currentTimeMillis() < end) {
            PerformanceResults performanceResults = new PerformanceResults();
            calcPerformanceOfWrite(performanceResults);
            for (int i = 1; i <= 3; i++) {
                calcPerformanceOfRead(performanceResults, i);
            }

            synchronized (performanceResultsList) {
                performanceResultsList.add(performanceResults);
            }
        }
    }

    /**
     * method read json file,
     * write it to couchbase and save the performance results.
     * @param performanceResults
     */
    private void calcPerformanceOfWrite(PerformanceResults performanceResults) {
        StopWatch stopwatch = StopWatch.createStarted();
        String content = readJsonFile();
        collection.upsert("key", content,
                UpsertOptions.upsertOptions().transcoder(RawJsonTranscoder.INSTANCE));
        stopwatch.stop();
        performanceResults.setTimeOfWrite(stopwatch.getTime());
    }

    /**
     * method read from couchbase by key and save the performance results.
     * @param performanceResults
     * @param i
     */
    private void calcPerformanceOfRead(PerformanceResults performanceResults, int i) {
        StopWatch stopwatch = StopWatch.createStarted();
        GetResult result = collection.get("key");
        stopwatch.stop();
        performanceResults.setTimeOfRead(i, stopwatch.getTime());
    }

    private static String readJsonFile() {
        String content = null;
        try {
            String fileName = "jsonFile.json";
            content = new String(
                    Files.readAllBytes(
                            Paths.get(fileName)),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

}
