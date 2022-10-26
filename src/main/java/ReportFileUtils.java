import java.io.FileWriter;
import java.io.IOException;

public class ReportFileUtils {

    public static FileWriter getFileWriter(){
        FileWriter myWriter = null;
        try {
            myWriter = new FileWriter("report.txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return myWriter;
    }

    public static void closeWriter(FileWriter myWriter) {
        try {
            myWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeToFile(String msg, FileWriter myWriter) {
        try {
            myWriter.write(msg + "\n ");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
