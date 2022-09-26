package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.UserDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;

@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/user",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final UserDataFacade userDataFacade;

    public UserController(UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @PostMapping()
    @Operation(summary = "Create user with books.",
            responses = {
                    @ApiResponse(description = "User created.", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserBookRequest.class))),
            parameters = {
                    @Parameter(name = "rqid", description = "Request id.")})
    public UserBookResponse createUserWithBooks(@RequestBody UserBookRequest request,
                                                @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId) {
        UserBookResponse response = userDataFacade.createUserWithBooks(request);
        log.info("Response with created user and his books: {}", response);
        return response;
    }

    @PutMapping("{userId}")
    @Operation(summary = "Update user by ID.",
            responses = {
                    @ApiResponse(description = "User updated.", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserBookRequest.class))),
            parameters = {
                    @Parameter(name = "userId", description = "User ID to update.",
                            content = @Content(schema = @Schema(implementation = Long.class)))})
    public UserBookResponse updateUserWithBooks(@PathVariable Long userId,
                                                @RequestBody UserBookRequest request) {
        UserBookResponse response = userDataFacade.updateUser(request, userId);
        log.info("Response with updated user and his books: {}", response);
        return response;
    }

    @GetMapping("{userId}")
    @Operation(summary = "Get user with books by ID.",
            responses = {
                    @ApiResponse(description = "User received", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))},
            parameters = {
                    @Parameter(name = "userId", description = "User ID to get.",
                            content = @Content(schema = @Schema(implementation = Long.class)))})
    public UserBookResponse getUserWithBooks(@PathVariable Long userId) {
        UserBookResponse response = userDataFacade.getUserWithBooks(userId);
        log.info("Response with user and his books: {}", response);
        return response;
    }

    @GetMapping()
    @Operation(summary = "Get all users with books.",
            responses = {
                    @ApiResponse(description = "All users are received.", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = UserBookResponse.class)))})
    public List<UserBookResponse> getAllUsersWithBooks() {
        List<UserBookResponse> response = userDataFacade.getAll();
        log.info("Response with all users and books: {}", response);
        return response;
    }

    @DeleteMapping("{userId}")
    @Operation(summary = "Delete user with books by user ID.",
            responses = {
                    @ApiResponse(description = "User deleted", responseCode = "200")},
            parameters = {
                    @Parameter(name = "userId", description = "User ID to delete",
                            content = @Content(schema = @Schema(implementation = Long.class)))})
    public void deleteUserWithBooks(@PathVariable Long userId) {
        log.info("Delete user and his books: userId {}", userId);
        userDataFacade.deleteUserWithBooks(userId);
    }

}
