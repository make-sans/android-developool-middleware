package com.federlizer.servermiddleware.models;

import java.util.Date;

public class PastExperience {
    public String company;
    public String jobTitle;
    public String location;
    public Date fromDate;
    public Date endDate;
    public String description;

    public PastExperience(String company, String jobTitle, String location, Date fromDate, Date endDate, String description) {
        this.company = company;
        this.jobTitle = jobTitle;
        this.location = location;
        this.fromDate = fromDate;
        this.endDate = endDate;
        this.description = description;
    }
}
