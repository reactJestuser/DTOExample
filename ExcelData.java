package ipd.administration.advancedSearch;

import java.util.List;

public class ExcelData {
    public final static String patent_no = "Patent Number";
    public final static String assignees = "Original Assignees";
    public final static String claims = "Claims";
    public final static String abstract_F = "Abstract";

    private String PatentNumber;
    private String Abstract;
    private String Claims;
    private List<String> taxonomy;

    public ExcelData(String PatentNumber, String Abstract, String Claims,
            List<String> taxonomy) {
        this.PatentNumber = PatentNumber;
        this.Abstract = Abstract;
        this.Claims = Claims;
        this.taxonomy = taxonomy;
    }

    public String getPatentNumber() {
        return PatentNumber;
    }

    public String getAbstract() {
        return Abstract;
    }

    public String getClaims() {
        return Claims;
    }

    public List<String> getTaxonomy() {
        return taxonomy;
    }

}
