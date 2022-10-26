

import com.couchbase.client.java.*;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CouchbaseReadAndWriteTestThread implements Runnable {
    // Update these variables to point to your Couchbase Server instance and credentials.

    Collection collection;
    List<PerformanceResults> performanceResultsList;

    public CouchbaseReadAndWriteTestThread(Collection collection, List<PerformanceResults> performanceResultsList) {
        super();
        this.collection = collection;
        this.performanceResultsList = performanceResultsList;
    }


    /**
     * method is working for 3 minutes:
     * method call couchbase:
     * 1. read from json file
     * 2. write it to couchbase
     * 3. read this key from couchbase 3 times.
     * 4. Writes the duration of the run time on map.
     */
    public void run() {
        long start = System.currentTimeMillis();
        long end = start + 1000;

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

    private void calcPerformanceOfWrite(PerformanceResults performanceResults) {
        StopWatch stopwatch = StopWatch.createStarted();
        String content = readJsonFile();
        collection.upsert("key", content,
                UpsertOptions.upsertOptions().transcoder(RawJsonTranscoder.INSTANCE));
        stopwatch.stop();
        performanceResults.setTimeOfWrite(stopwatch.getTime());
    }

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
