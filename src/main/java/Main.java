import com.couchbase.client.java.*;
import org.apache.commons.lang3.time.StopWatch;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) {
        try {
            //open file writer
            FileWriter myWriter = ReportFileUtils.getFileWriter();
            //open connection to couchbase
            Collection collection = CouchbaseConnection.connect();

            //call to executeThreadTest with 5 options of thread pool size
            for (int i = 1; i < 6; i++) {
                executeThreadTest(i, collection, myWriter);
            }
            CouchbaseConnection.closeConnection();
            ReportFileUtils.closeWriter(myWriter);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * method execute 5 Threads in parallel based on numberOfThreads.
     * each thread run for 3 minutes: read and write to couchbase
     * and save the performance results.
     * after all threads are finished: write a benchmark Report.
     * @param numberOfThreads
     * @param collection
     * @param myWriter
     */
    private static void executeThreadTest(int numberOfThreads, Collection collection, FileWriter myWriter) {
        StopWatch stopwatch = StopWatch.createStarted();
        List<PerformanceResults> performanceResultsList = new ArrayList<>();
        ExecutorService pool = Executors.newFixedThreadPool(numberOfThreads);
        for (int i = 0; i < 5; i++) {
            Runnable r = new CouchbaseReadAndWriteTestThread(collection, performanceResultsList);
            pool.execute(r);
        }
        pool.shutdown();

        //wait until all threads will finish
        try {
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        stopwatch.stop();
        writeBenchmarkReport(numberOfThreads, myWriter, performanceResultsList, stopwatch.getTime());
    }

    /**
     * method move over data inserted from threads and calculate
     * relevant metrics for this benchmark, write it on report file.
     * @param numberOfThreads
     * @param myWriter
     * @param totalRunningTime - how long take to run all threads
     */
    public static void writeBenchmarkReport(int numberOfThreads, FileWriter myWriter, List<PerformanceResults> performanceResultsList, Long totalRunningTime) {
        ReportFileUtils.writeToFile("when " + numberOfThreads + " threads run in parallel:", myWriter);
        ReportFileUtils.writeToFile("   Total running time is " + totalRunningTime +" milliseconds", myWriter);
        ReportFileUtils.writeToFile("   Total number of executions in all threads: "+performanceResultsList.size(), myWriter);

        List<ReportDataRow> reportDataRowList = new ArrayList<>();
        reportDataRowList.add(new ReportDataRow("write"));
        reportDataRowList.add(new ReportDataRow("read1"));
        reportDataRowList.add(new ReportDataRow("read2"));
        reportDataRowList.add(new ReportDataRow("read3"));

        for (PerformanceResults performanceResults : performanceResultsList) {
            for (ReportDataRow reportDataRow : reportDataRowList) {
                fillDataInReportRow(reportDataRow, performanceResults);
            }
        }
        for (ReportDataRow reportDataRow : reportDataRowList) {
            ReportFileUtils.writeToFile("   Average duration of " +reportDataRow.getMessage()+ " is " + (double)reportDataRow.getSum()/performanceResultsList.size() +" milliseconds." , myWriter);
            ReportFileUtils.writeToFile("   Minimum duration of " +reportDataRow.getMessage()+ " is " + reportDataRow.getMinimum() +" milliseconds." , myWriter);
            ReportFileUtils.writeToFile("   Maximum duration of " +reportDataRow.getMessage()+ " is " + reportDataRow.getMaximum() +" milliseconds." , myWriter);
        }

    }

    private static void fillDataInReportRow(ReportDataRow reportDataRow, PerformanceResults performanceResults) {
        Long timeOfRow = performanceResults.getTimeOf(reportDataRow.getDescription());
        if (timeOfRow < reportDataRow.getMinimum())
            reportDataRow.setMinimum(timeOfRow);
        if (timeOfRow > reportDataRow.getMaximum())
            reportDataRow.setMaximum(timeOfRow);

        reportDataRow.setSum(reportDataRow.getSum()+ timeOfRow);
    }


}


