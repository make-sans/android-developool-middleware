package com.federlizer.servermiddleware;

import com.federlizer.servermiddleware.models.Profile;
import com.federlizer.servermiddleware.models.Project;
import com.federlizer.servermiddleware.models.ProjectFilter;

import java.util.function.Consumer;

public interface ServerMiddleware {
    /**
     * Sends an authentication request to the web server
     * @param email The email of the user
     * @param password The password of the user
     * @param callback The callback that's to be executed after the request is finished
     */
    void authenticate(final String email, final String password, Consumer<Result> callback);

    /**
     * Sends a request to register a new user
     * @param username The username
     * @param email The email
     * @param password The password
     * @param passwordConfirmation The password a second time, for confirmation by the server
     * @param callback The callback to be executed after the request is finished
     */
    void register(final String username, final String email, final String password, final String passwordConfirmation, Consumer<Result> callback);

    /**
     * Sends a request to fetch all projects owned by the authenticated user
     * @param token The JWT token of the authenticated user
     * @param callback The callback to be executed after the request is finished
     */
    void getOwnProjects(final String token, Consumer<Result> callback);

    /**
     * Creates a new project under the authenticated user's ownership
     * @param token The token of the authenticated user
     * @param newProject The data for the new project. Only the title field is required.
     * @param callback The callback to be executed after the request is finished
     */
    void createNewProject(final String token, final Project newProject, Consumer<Result> callback);

    /**
     * Sends a request to find a project
     * @param token The token of the authenticated user
     * @param projectID The ID of the project.
     * @param callback The callback to be executed after the request is finished
     */
    void getProjectById(final String token, final String projectID, Consumer<Result> callback);

    /**
     * Get's all projects saved on the server
     * @param filter A non-required filter to get only specific projects.
     * @param callback The callback to be executed after the request is finished
     */
    void getAllProjects(final ProjectFilter filter, Consumer<Result> callback);

    /**
     * Sends a request to update a project. Only the owner of a project can make such a request.
     * @param token The token of the authenticated user
     * @param projectID The project's ID
     * @param updatedProject The new data for the project
     * @param callback The callback to be executed after the request is finished
     */
    void updateProject(final String token, final String projectID, final Project updatedProject, Consumer<Result> callback);

    /**
     * Sends a request to delete a project. Only the owner of a project can make such a request.
     * @param token The token of the authenticated user
     * @param projectID The project's ID
     * @param callback The callback to be executed after the request is finished
     */
    void deleteProject(final String token, final String projectID, Consumer<Result> callback);

    /**
     * Sends a request to join a project
     * @param token The token of the authenticated user
     * @param projectID The project's ID
     * @param callback The callback to be executed after the request is finished
     */
    void joinProject(final String token, final String projectID, Consumer<Result> callback);

    /**
     * Sends a request to leave a project
     * @param token The token of the authenticated user
     * @param projectID The project's ID
     * @param callback The callback to be executed after the request is finished
     */
    void leaveProject(final String token, final String projectID, Consumer<Result> callback);

    /**
     * Get's a user's profile
     * @param token The token of the authenticated user
     * @param callback The callback to be executed after the request is finished
     */
    void getOwnProfile(final String token, Consumer<Result> callback);

    /**
     * Get's any account's profile
     * @param accountID The account ID of the profile to get
     * @param callback The callback to be executed after the request is finished
     */
    void getProfileByAccountID(final String accountID, Consumer<Result> callback);

    /**
     * Creates a new profile for the authenticated account
     * @param token the token of the authenticated user
     * @param profile The profile information to be saved
     * @param callback The callback to be executed after the request is finished
     */
    void createOwnProfile(final String token, final Profile profile, Consumer<Result> callback);

    /**
     * Updates an already existing profile for the authenticated account
     * @param token the token of the authenticated user
     * @param newProfile the new information for the profile
     * @param callback the callback to be executed after the request is finished
     */
    void updateOwnProfile(final String token, final Profile newProfile, Consumer<Result> callback);
}
