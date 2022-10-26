public class ReportDataRow {
    String description;
    Long minimum = Long.MAX_VALUE;
    Long maximum = 0L;
    Long sum = 0L;

    public ReportDataRow(String desc){
        super();
        description = desc;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getMinimum() {
        return minimum;
    }

    public void setMinimum(Long minimum) {
        this.minimum = minimum;
    }

    public Long getMaximum() {
        return maximum;
    }

    public void setMaximum(Long maximum) {
        this.maximum = maximum;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    public String getMessage(){
        switch(description){
            case "write":
                return "writing";
            case "read1":
                return "reading 1st time";
            case "read2":
                return "reading 2nd time";
            case "read3":
                return "reading 3rd time";
        }
        return null;
    }
}
