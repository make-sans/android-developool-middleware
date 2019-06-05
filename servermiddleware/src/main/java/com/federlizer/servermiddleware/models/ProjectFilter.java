package com.federlizer.servermiddleware.models;

import java.util.List;

public class ProjectFilter {
    public String title;
    public Boolean isPublic;
    public Boolean isPrivate;
    public List<String> skills;
    public List<String> interests;

    public ProjectFilter(String title, Boolean isPublic, Boolean isPrivate, List<String> skills, List<String> interests) {
        this.title = title;
        this.isPublic = isPublic;
        this.isPrivate = isPrivate;
        this.skills = skills;
        this.interests = interests;
    }
}
