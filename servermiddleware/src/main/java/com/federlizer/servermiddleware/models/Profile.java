package com.federlizer.servermiddleware.models;

import java.util.List;

public class Profile {
    public String firstName;
    public String lastName;
    public List<String> interests;
    public List<String> skills;
    public List<Education> educations;
    public List<PastExperience> pastExperiences;
    public String github;
    public String facebook;
    public String linkedIn;
    public String twitter;
    public String instagram;
    public String accountID;

    public Profile(String firstName, String lastName, List<String> interests, List<String> skills, List<Education> educations, List<PastExperience> pastExperiences, String github, String facebook, String linkedIn, String twitter, String instagram) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.interests = interests;
        this.skills = skills;
        this.educations = educations;
        this.pastExperiences = pastExperiences;
        this.github = github;
        this.facebook = facebook;
        this.linkedIn = linkedIn;
        this.twitter = twitter;
        this.instagram = instagram;
    }

    public Profile(String firstName, String lastName, List<String> interests, List<String> skills, List<Education> educations, List<PastExperience> pastExperiences, String github, String facebook, String linkedIn, String twitter, String instagram, String accountID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.interests = interests;
        this.skills = skills;
        this.educations = educations;
        this.pastExperiences = pastExperiences;
        this.github = github;
        this.facebook = facebook;
        this.linkedIn = linkedIn;
        this.twitter = twitter;
        this.instagram = instagram;
        this.accountID = accountID;
    }
}
