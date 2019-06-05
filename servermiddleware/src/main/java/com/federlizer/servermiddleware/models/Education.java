package com.federlizer.servermiddleware.models;

import java.util.Date;

public class Education {
    public String instituteName;
    public String degree;
    public String fieldOfStudy;
    public Date fromDate;
    public Date endDate;
    public String description;

    public Education(String instituteName, String degree, String fieldOfStudy, Date fromDate, Date endDate, String description) {
        this.instituteName = instituteName;
        this.degree = degree;
        this.fieldOfStudy = fieldOfStudy;
        this.fromDate = fromDate;
        this.endDate = endDate;
        this.description = description;
    }
}
