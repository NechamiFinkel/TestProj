public class PerformanceResults {
    Long timeOfWrite;
    Long timeOfRead1;
    Long timeOfRead2;
    Long timeOfRead3;

    public Long getTimeOfWrite() {
        return timeOfWrite;
    }

    public void setTimeOfWrite(Long timeOfWrite) {
        this.timeOfWrite = timeOfWrite;
    }

    public Long getTimeOfRead1() {
        return timeOfRead1;
    }

    public void setTimeOfRead1(Long timeOfRead1) {
        this.timeOfRead1 = timeOfRead1;
    }

    public Long getTimeOfRead2() {
        return timeOfRead2;
    }

    public void setTimeOfRead2(Long timeOfRead2) {
        this.timeOfRead2 = timeOfRead2;
    }

    public Long getTimeOfRead3() {
        return timeOfRead3;
    }

    public void setTimeOfRead3(Long timeOfRead3) {
        this.timeOfRead3 = timeOfRead3;
    }

    public void setTimeOfRead(int i, Long timeOfRead) {
        switch(i){
            case 1:
                setTimeOfRead1(timeOfRead);
            case 2:
                setTimeOfRead2(timeOfRead);
            case 3:
                setTimeOfRead3(timeOfRead);
        }
    }

    public Long getTimeOf(String name) {
        switch(name){
            case "write":
               return getTimeOfWrite();
            case "read1":
                return getTimeOfRead1();
            case "read2":
                return getTimeOfRead2();
            case "read3":
                return getTimeOfRead3();
        }
        return null;
    }
}
