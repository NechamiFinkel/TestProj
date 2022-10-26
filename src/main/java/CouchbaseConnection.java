import com.couchbase.client.core.env.IoConfig;
import com.couchbase.client.core.env.NetworkResolution;
import com.couchbase.client.java.*;
import com.couchbase.client.java.env.ClusterEnvironment;

import java.time.Duration;

public class CouchbaseConnection {
    static String connectionString = "localhost";
    static String username = "Administrator";
    static String password = "Nechami313";
    static String bucketName = "Test-Bucket";

    static Cluster cluster;

    /**
     * method connect to test couchbase
     * @return default collection
     */
    public static Collection connect(){
        ClusterEnvironment env = ClusterEnvironment.builder()
                .ioConfig(IoConfig
                        .networkResolution(NetworkResolution.AUTO)
                        .enableDnsSrv(false)
                )
                .build();
        cluster = Cluster.connect("couchbase://" + connectionString, ClusterOptions.clusterOptions(username, password).environment(env));

        // get a bucket reference
        Bucket bucket = cluster.bucket(bucketName);
        bucket.waitUntilReady(Duration.ofSeconds(10));
        Scope scope = bucket.scope("_default");
        Collection collection = scope.collection("_default");
        return collection;
    }

    public static void closeConnection() {
        cluster.disconnect();
    }
}
