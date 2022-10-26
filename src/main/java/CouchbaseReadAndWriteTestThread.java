

import com.couchbase.client.core.deps.com.fasterxml.jackson.databind.JsonNode;
import com.couchbase.client.java.*;
import com.couchbase.client.java.codec.RawJsonTranscoder;
import com.couchbase.client.java.kv.*;

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

    Long task;
    Map<Long,List<Integer>> runningTimeDataMap;

    public CouchbaseReadAndWriteTestThread(Collection collection, Long task, Map<Long,List<Integer>> runningTimeDataMap){
        super();
        this.collection = collection;
        this.runningTimeDataMap = runningTimeDataMap;
        this.task = task;
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
        long lastOperationTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < end) {
            String content = readJsonFile();
            collection.upsert("key", content,
                    UpsertOptions.upsertOptions().transcoder(RawJsonTranscoder.INSTANCE));
            for (int i = 0; i < 3; i++) {
                GetResult result = collection.get("key");
                JsonNode node = result.contentAs(JsonNode.class);
            }
            Integer runningTimeData = Math.toIntExact(System.currentTimeMillis() - lastOperationTime);
            lastOperationTime = System.currentTimeMillis();
            synchronized(runningTimeDataMap)
            {
                if (!runningTimeDataMap.containsKey(task)){
                    runningTimeDataMap.put(task,new ArrayList<Integer>());
                }
                runningTimeDataMap.get(task).add(runningTimeData);
            }
        }
    }

    private static String readJsonFile() {
        String content  = null;
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
