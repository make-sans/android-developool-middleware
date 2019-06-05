package com.federlizer.servermiddleware.models;

import java.util.List;

public class Project {
    public String id;
    public String title;
    public String publicDescription;
    public String privateDescription;
    public List<String> interests;
    public List<String> skills;
    public Boolean isPrivate;

    public Project(String title, String publicDescription, String privateDescription, List<String> interests, List<String> skills, Boolean isPrivate) {
        this.title = title;
        this.publicDescription = publicDescription;
        this.privateDescription = privateDescription;
        this.interests = interests;
        this.skills = skills;
        this.isPrivate = isPrivate;
    }

    public Project(String id, String title, String publicDescription, String privateDescription, List<String> interests, List<String> skills, Boolean isPrivate) {
        this.id = id;
        this.title = title;
        this.publicDescription = publicDescription;
        this.privateDescription = privateDescription;
        this.interests = interests;
        this.skills = skills;
        this.isPrivate = isPrivate;
    }
}
