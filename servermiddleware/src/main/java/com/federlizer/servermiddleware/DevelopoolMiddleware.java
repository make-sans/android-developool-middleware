package com.federlizer.servermiddleware;

import android.security.keystore.UserNotAuthenticatedException;
import android.util.SparseArray;

import com.federlizer.servermiddleware.exceptions.EmailNotVerifiedException;
import com.federlizer.servermiddleware.exceptions.InvalidCredentialsException;
import com.federlizer.servermiddleware.exceptions.NotFoundException;
import com.federlizer.servermiddleware.exceptions.ProjectAlreadyExistsException;
import com.federlizer.servermiddleware.exceptions.UserAlreadyExistsException;
import com.federlizer.servermiddleware.models.Profile;
import com.federlizer.servermiddleware.models.Project;
import com.federlizer.servermiddleware.models.ProjectFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class DevelopoolMiddleware implements ServerMiddleware {
    private final String ILLEGAL_ARGUMENTS = "Please provide valid arguments";

    private final String AUTHORIZATION_HEADER = "Authorization";
    private final String CONTENT_TYPE_HEADER = "Content-type";

    private final String JSON_HEADER_VALUE = "application/json";

    private final String API_ROUTE = "http://developool.com/api/";

    private final Request AUTHENTICATE = new Request("POST", API_ROUTE + "auth/");
    private final Request REGISTER = new Request("POST", API_ROUTE + "register/");
    private final Request CREATE_NEW_PROJECT = new Request("POST", API_ROUTE + "project/");
    private final Request GET_OWN_PROJECTS = new Request("GET", API_ROUTE + "accounts/projects/");
    private final Request GET_PROJECT_BY_ID = new Request("GET", API_ROUTE + "project/");
    private final Request GET_ALL_PROJECTS = new Request("GET", API_ROUTE + "project/");
    private final Request UPDATE_PROJECT = new Request("PUT", API_ROUTE + "project/");
    private final Request DELETE_PROJECT = new Request("DELETE", API_ROUTE + "project/");
    private final Request JOIN_PROJECT = new Request("POST", API_ROUTE + "project/join/");
    private final Request LEAVE_PROJECT = new Request("POST", API_ROUTE + "project/leave/");
    private final Request GET_OWN_PROFILE = new Request("GET", API_ROUTE + "profile/");
    private final Request GET_PROFILE_BY_ACCOUNT_ID = new Request("GET", API_ROUTE + "profile/");
    private final Request CREATE_OWN_PROFILE = new Request("POST", API_ROUTE + "profile/");
    private final Request UPDATE_OWN_PROFILE = new Request("PUT", API_ROUTE + "profile/");


    /**
     * Sends an authentication request to the web server
     *
     * @param email    The email of the user
     * @param password The password of the user
     * @param callback The callback that's to be executed after the request is finished
     */
    @Override
    public void authenticate(final String email, final String password, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = AUTHENTICATE.Route();

                // build request body
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("email", email);
                    reqBody.put("password", password);
                } catch (JSONException e) {
                    return new Result(e);
                }
                byte[] output = reqBody.toString().getBytes();

                // setup request headers (properties)
                Map<String, String> requestProps = new HashMap<>();
                requestProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new InvalidParameterException("Please provide a valid email"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new InvalidCredentialsException("Please provide valid credentials"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_FORBIDDEN, new EmailNotVerifiedException("Email hasn't been verified"));

                return makeRequest(url, true, true, output, requestProps, 0, AUTHENTICATE.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to register a new user
     *
     * @param username             The username
     * @param email                The email
     * @param password             The password
     * @param passwordConfirmation The password a second time, for confirmation by the server
     * @param callback             The callback to be executed after the request is finished
     */
    @Override
    public void register(final String username, final String email, final String password, final String passwordConfirmation, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {

                // validate
                if (username == null ||
                        email == null ||
                        password == null ||
                        passwordConfirmation == null ||
                        username.isEmpty() ||
                        email.isEmpty() ||
                        password.isEmpty() ||
                        passwordConfirmation.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = REGISTER.Route();

                // build request body if any
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("username", username);
                    reqBody.put("email", email);
                    reqBody.put("password", password);
                    reqBody.put("password2", passwordConfirmation);
                } catch (JSONException e) {
                    return new Result(e);
                }

                byte[] output = reqBody.toString().getBytes();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new InvalidParameterException("The email provided isn't valid or the passwords don't match"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_CONFLICT, new UserAlreadyExistsException("An account with the same email already exists"));

                return makeRequest(url, true, true, output, reqProps, 0, REGISTER.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to fetch all projects owned by the authenticated user
     *
     * @param token    The JWT token of the authenticated user
     * @param callback The callback to be executed after the request is finished
     */
    @Override
    public void getOwnProjects(final String token, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || token.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = GET_OWN_PROJECTS.Route();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User not authenticated"));

                return makeRequest(url, true, false, null, reqProps, 0, GET_OWN_PROJECTS.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Creates a new project under the authenticated user's ownership
     *
     * @param token      The token of the authenticated user
     * @param newProject The data for the new project. Only the title field is required.
     * @param callback   The callback to be executed after the request is finished
     */
    @Override
    public void createNewProject(final String token, final Project newProject, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || newProject == null || token.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }
                if (newProject.title == null || newProject.isPrivate == null) {
                    return new Result(new IllegalArgumentException("Please provide valid title and privacy fields"));
                }

                // build url
                String url = CREATE_NEW_PROJECT.Route();

                // build request body if any
                JSONObject reqBody = new JSONObject();
                try {
                    reqBody.put("title", newProject.title);
                    reqBody.put("private", newProject.isPrivate);
                    if (newProject.publicDescription != null && !newProject.publicDescription.isEmpty()) {
                        reqBody.put("publicDescription", newProject.publicDescription);
                    }
                    if (newProject.privateDescription != null && !newProject.privateDescription.isEmpty()) {
                        reqBody.put("privateDescription", newProject.privateDescription);
                    }
                    if (newProject.interests != null && !newProject.interests.isEmpty()) {
                        reqBody.put("interests", new JSONArray(newProject.interests));
                    }
                    if (newProject.skills != null && !newProject.skills.isEmpty()) {
                        reqBody.put("skills", new JSONArray(newProject.interests));
                    }
                } catch (JSONException e) {
                    return new Result(e);
                }
                byte[] output = reqBody.toString().getBytes();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new InvalidParameterException("One or more of the fields are incorrectly formatted"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_CONFLICT, new ProjectAlreadyExistsException("Another project with the same title already exists"));

                return makeRequest(url, true, true, output, reqProps, 0, CREATE_NEW_PROJECT.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to find a project
     *
     * @param token     The token of the authenticated user
     * @param projectID The ID of the project.
     * @param callback  The callback to be executed after the request is finished
     */
    @Override
    public void getProjectById(final String token, final String projectID, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || projectID == null || token.isEmpty() || projectID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = GET_PROJECT_BY_ID.Route() + projectID;

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Project with that ID wasn't found"));

                return makeRequest(url, true, false, null, reqProps, 0, GET_PROJECT_BY_ID.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Get's all projects saved on the server
     *
     * @param filter   A non-required filter to get only specific projects.
     * @param callback The callback to be executed after the request is finished
     */
    @Override
    public void getAllProjects(final ProjectFilter filter, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // build url
                StringBuilder url = new StringBuilder();

                url.append(GET_ALL_PROJECTS.Route());
                url.append('?');

                if (filter.title != null && !filter.title.isEmpty()) {
                    url.append("title=");
                    url.append(filter.title);
                    url.append('&');
                }
                if (filter.isPublic != null) {
                    url.append("public=");
                    url.append(filter.isPublic);
                    url.append('&');
                }
                if (filter.isPrivate != null) {
                    url.append("private=");
                    url.append(filter.isPrivate);
                    url.append('&');
                }
                if (filter.interests != null && !filter.interests.isEmpty()) {
                    for (String interest : filter.interests) {
                        url.append("interests[]=");
                        url.append(interest);
                        url.append('&');
                    }
                }
                if (filter.skills != null && !filter.skills.isEmpty()) {
                    for (String skill : filter.skills) {
                        url.append("skills[]=");
                        url.append(skill);
                        url.append('&');
                    }
                }

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_SERVER_ERROR, new IOException("Server error"));

                return makeRequest(url.toString(), true, false, null, null, 0, GET_ALL_PROJECTS.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to update a project. Only the owner of a project can make such a request.
     *
     * @param token          The token of the authenticated user
     * @param projectID      The project's ID
     * @param updatedProject The new data for the project
     * @param callback       The callback to be executed after the request is finished
     */
    @Override
    public void updateProject(final String token, final String projectID, final Project updatedProject, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || projectID == null || updatedProject == null ||
                        token.isEmpty() || projectID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = UPDATE_PROJECT.Route() + projectID;

                // build request body if any
                JSONObject reqBody = new JSONObject();
                try {
                    if (updatedProject.title != null && !updatedProject.title.isEmpty()) {
                        reqBody.put("title", updatedProject.title);
                    }
                    if (updatedProject.publicDescription != null && !updatedProject.publicDescription.isEmpty()) {
                        reqBody.put("publicDescription", updatedProject.publicDescription);
                    }
                    if (updatedProject.privateDescription != null && !updatedProject.privateDescription.isEmpty()) {
                        reqBody.put("privateDescription", updatedProject.privateDescription);
                    }
                    if (updatedProject.isPrivate != null) {
                        reqBody.put("private", updatedProject.isPrivate);
                    }
                    if (updatedProject.interests != null && !updatedProject.interests.isEmpty()) {
                        reqBody.put("interests", new JSONArray(updatedProject.interests));
                    }
                    if (updatedProject.skills != null && !updatedProject.skills.isEmpty()) {
                        reqBody.put("skills", new JSONArray(updatedProject.skills));
                    }
                } catch (JSONException e) {
                    return new Result(e);
                }

                byte[] output = reqBody.toString().getBytes();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);
                reqProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new InvalidParameterException("One or more of the fields in the updated project is invalid"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User didn't provide a token"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Project with such ID doesn't exist"));

                return makeRequest(url, true, true, output, reqProps, 0, UPDATE_PROJECT.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to delete a project. Only the owner of a project can make such a request.
     *
     * @param token     The token of the authenticated user
     * @param projectID The project's ID
     * @param callback  The callback to be executed after the request is finished
     */
    @Override
    public void deleteProject(final String token, final String projectID, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || projectID == null || token.isEmpty() || projectID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = DELETE_PROJECT.Route() + projectID;

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User hasn't been authenticated"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Project with that ID wasn't found"));

                return makeRequest(url, true, false, null, reqProps, 0, DELETE_PROJECT.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to join a project
     *
     * @param token     The token of the authenticated user
     * @param projectID The project's ID
     * @param callback  The callback to be executed after the request is finished
     */
    @Override
    public void joinProject(final String token, final String projectID, Consumer<Result> callback) {
        final RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || projectID == null || token.isEmpty() || projectID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = JOIN_PROJECT.Route() + projectID;

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.put(HttpURLConnection.HTTP_BAD_REQUEST, new IllegalAccessException("You can't join the project as the owner of the project"));
                responseCodeExceptions.put(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User hasn't been authenticated"));
                responseCodeExceptions.put(HttpURLConnection.HTTP_FORBIDDEN, new IllegalAccessException("You can't join this project"));
                responseCodeExceptions.put(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Project with that ID wasn't found"));
                responseCodeExceptions.put(HttpURLConnection.HTTP_CONFLICT, new IllegalAccessException("You're already a member of this project"));

                return makeRequest(url, true, false, null, reqProps, 0, JOIN_PROJECT.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Sends a request to leave a project
     *
     * @param token     The token of the authenticated user
     * @param projectID The project's ID
     * @param callback  The callback to be executed after the request is finished
     */
    @Override
    public void leaveProject(final String token, final String projectID, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || projectID == null || token.isEmpty() || projectID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = LEAVE_PROJECT + projectID;

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new IllegalAccessException("You can't leave the project as the owner"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User not authenticated"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_FORBIDDEN, new IllegalAccessException("User not a member of the project"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Project with that ID wasn't found"));

                return makeRequest(url, true, false, null, reqProps, 0, LEAVE_PROJECT.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Get's a user's profile
     *
     * @param token    The token of the authenticated user
     * @param callback The callback to be executed after the request is finished
     */
    @Override
    public void getOwnProfile(final String token, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || token.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = GET_OWN_PROFILE.Route();


                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_UNAUTHORIZED, new UserNotAuthenticatedException("User not authenticated"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Profile hasn't been created yet"));

                return makeRequest(url, true, false, null, reqProps, 0, GET_OWN_PROFILE.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Get's any account's profile
     *
     * @param accountID The account ID of the profile to get
     * @param callback  The callback to be executed after the request is finished
     */
    @Override
    public void getProfileByAccountID(final String accountID, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (accountID == null || accountID.isEmpty()) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = GET_PROFILE_BY_ACCOUNT_ID.Route() + accountID;

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_BAD_REQUEST, new IllegalAccessException("Profile of that account hasn't been created yet"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Account with such ID wasn't found"));

                return makeRequest(url, true, false, null, null, 0, GET_PROFILE_BY_ACCOUNT_ID.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Creates a new profile for the authenticated account
     *
     * @param token    the token of the authenticated user
     * @param profile  The profile information to be saved
     * @param callback The callback to be executed after the request is finished
     */
    @Override
    public void createOwnProfile(final String token, final Profile profile, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || token.isEmpty() || profile == null) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = CREATE_OWN_PROFILE.Route();

                // build request body if any
                JSONObject reqBody = new JSONObject();
                try {
                    if (profile.firstName != null && !profile.firstName.isEmpty()) {
                        reqBody.put("firstName", profile.firstName);
                    }
                    if (profile.lastName != null && !profile.lastName.isEmpty()) {
                        reqBody.put("lastName", profile.lastName);
                    }
                    if (profile.interests != null && !profile.interests.isEmpty()) {
                        reqBody.put("interests", new JSONArray(profile.interests));
                    }
                    if (profile.skills != null && !profile.skills.isEmpty()) {
                        reqBody.put("skills", new JSONArray(profile.skills));
                    }
                    if (profile.educations != null && !profile.educations.isEmpty()) {
                        reqBody.put("education", new JSONArray(profile.educations));
                    }
                    if (profile.pastExperiences != null && !profile.pastExperiences.isEmpty()) {
                        reqBody.put("pastExperience", new JSONArray(profile.pastExperiences));
                    }
                    if (profile.github != null && !profile.github.isEmpty()) {
                        reqBody.put("github", profile.github);
                    }
                    if (profile.facebook != null && !profile.facebook.isEmpty()) {
                        reqBody.put("facebook", profile.facebook);
                    }
                    if (profile.instagram != null && !profile.instagram.isEmpty()) {
                        reqBody.put("instagram", profile.instagram);
                    }
                    if (profile.linkedIn != null && !profile.linkedIn.isEmpty()) {
                        reqBody.put("linkedin", profile.linkedIn);
                    }
                    if (profile.twitter != null && !profile.twitter.isEmpty()) {
                        reqBody.put("twitter", profile.twitter);
                    }
                } catch (JSONException e) {
                    return new Result(e);
                }

                byte[] output = reqBody.toString().getBytes();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                // This shouldn't really happen, given that we're sending a token with the request, but it's part of the API documentation..
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Account wasn't found"));
                responseCodeExceptions.append(HttpURLConnection.HTTP_CONFLICT, new IllegalAccessException("Profile already exists, try updating instead"));

                return makeRequest(url, true, true, output, reqProps, 0, CREATE_OWN_PROFILE.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    /**
     * Updates an already existing profile for the authenticated account
     *
     * @param token      the token of the authenticated user
     * @param newProfile the new information for the profile
     * @param callback   the callback to be executed after the request is finished
     */
    @Override
    public void updateOwnProfile(final String token, final Profile newProfile, Consumer<Result> callback) {
        RequestTask request = new RequestTask(new Callable<Result>() {
            @Override
            public Result call() {
                // validate
                if (token == null || token.isEmpty() || newProfile == null) {
                    return new Result(new IllegalArgumentException(ILLEGAL_ARGUMENTS));
                }

                // build url
                String url = UPDATE_OWN_PROFILE.Route();

                // build request body if any
                JSONObject reqBody = new JSONObject();
                try {
                    if (newProfile.firstName != null && !newProfile.firstName.isEmpty()) {
                        reqBody.put("firstName", newProfile.firstName);
                    }
                    if (newProfile.lastName != null && !newProfile.lastName.isEmpty()) {
                        reqBody.put("lastName", newProfile.lastName);
                    }
                    if (newProfile.interests != null && !newProfile.interests.isEmpty()) {
                        reqBody.put("interests", new JSONArray(newProfile.interests));
                    }
                    if (newProfile.skills != null && !newProfile.skills.isEmpty()) {
                        reqBody.put("skills", new JSONArray(newProfile.skills));
                    }
                    if (newProfile.educations != null && !newProfile.educations.isEmpty()) {
                        reqBody.put("education", new JSONArray(newProfile.educations));
                    }
                    if (newProfile.pastExperiences != null && !newProfile.pastExperiences.isEmpty()) {
                        reqBody.put("pastExperience", new JSONArray(newProfile.pastExperiences));
                    }
                    if (newProfile.github != null && !newProfile.github.isEmpty()) {
                        reqBody.put("github", newProfile.github);
                    }
                    if (newProfile.facebook != null && !newProfile.facebook.isEmpty()) {
                        reqBody.put("facebook", newProfile.facebook);
                    }
                    if (newProfile.instagram != null && !newProfile.instagram.isEmpty()) {
                        reqBody.put("instagram", newProfile.instagram);
                    }
                    if (newProfile.linkedIn != null && !newProfile.linkedIn.isEmpty()) {
                        reqBody.put("linkedin", newProfile.linkedIn);
                    }
                    if (newProfile.twitter != null && !newProfile.twitter.isEmpty()) {
                        reqBody.put("twitter", newProfile.twitter);
                    }
                } catch (JSONException e) {
                    return new Result(e);
                }

                byte[] output = reqBody.toString().getBytes();

                // setup request headers (props)
                Map<String, String> reqProps = new HashMap<>();
                reqProps.put(CONTENT_TYPE_HEADER, JSON_HEADER_VALUE);
                reqProps.put(AUTHORIZATION_HEADER, token);

                // setup response code exceptions
                SparseArray<Exception> responseCodeExceptions = new SparseArray<>();
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("A profile doesn't exist on this account, try creating one instead"));
                // This shouldn't really happen, given that we're sending a token with the request, but it's part of the API documentation..
                responseCodeExceptions.append(HttpURLConnection.HTTP_NOT_FOUND, new NotFoundException("Account wasn't found"));

                return makeRequest(url, true, true, output, reqProps, 0, UPDATE_OWN_PROFILE.RequestMethod(), responseCodeExceptions);
            }
        }, callback);
        request.execute();
    }

    private Result makeRequest(
            String urlString,
            Boolean input,
            Boolean output,
            byte[] outputValue,
            Map<String, String> requestProps,
            Integer chunkLen,
            String requestMethod,
            SparseArray<Exception> responseCodeExceptions
    ) {
        Result result;

        try {
            URL url = new URL(urlString);

            BufferedReader in = null;
            OutputStream out = null;
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();

            try {
                connection = (HttpURLConnection) url.openConnection();

                // connection settings
                connection.setDoOutput(output);
                connection.setDoInput(input);
                connection.setChunkedStreamingMode(chunkLen);
                connection.setRequestMethod(requestMethod);

                // set request properties (headers) if there are any
                if (requestProps != null && !requestProps.isEmpty()) {
                    for (Map.Entry<String, String> prop : requestProps.entrySet()) {
                        connection.setRequestProperty(prop.getKey(), prop.getValue());
                    }
                }

                // handle output
                if (output) {
                    out = new BufferedOutputStream(connection.getOutputStream());
                    out.write(outputValue);
                    out.flush();
                }

                // make the request itself
                connection.connect();

                // check response code
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    // Map through the response code exceptions map and return the appropriate exception

                    int size = responseCodeExceptions.size();

                    for (int i = 0; i < size - 1; i++) {
                        if (responseCodeExceptions.keyAt(i) == responseCode) {
                            return new Result(responseCodeExceptions.valueAt(i));
                        }
                    }
                }

                // handle input from server
                if (input) {
                    String temp;
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    while ((temp = in.readLine()) != null) {
                        response.append(temp);
                        response.append(System.lineSeparator());
                    }
                } else {
                    response.append("200");
                }
            } finally {
                // make sure to close everything as appropriate
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }

            // check if there was any response from the server
            if (!response.toString().isEmpty()) {
                result = new Result(response.toString());
            } else {
                result = new Result(new IOException("No response from server"));
            }
        } catch (Exception e) {
            result = new Result(e);
        }
        return result;
    }
}
