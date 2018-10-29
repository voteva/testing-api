package com.voteva.gateway.service.impl;

import com.voteva.gateway.converter.CommonConverter;
import com.voteva.gateway.converter.UsersInfoConverter;
import com.voteva.gateway.grpc.client.GRpcQuizServiceClient;
import com.voteva.gateway.grpc.client.GRpcUsersServiceClient;
import com.voteva.gateway.service.UsersService;
import com.voteva.gateway.util.GRpcExceptionUtils;
import com.voteva.gateway.web.to.common.PagedResult;
import com.voteva.gateway.web.to.common.UserInfo;
import com.voteva.gateway.web.to.in.AddUserRequest;
import com.voteva.quiz.grpc.model.v1.GBlockUserRequest;
import com.voteva.quiz.grpc.model.v1.GGetAllUsersRequest;
import com.voteva.quiz.grpc.model.v1.GGetAllUsersResponse;
import com.voteva.quiz.grpc.model.v1.GGetUserRequest;
import com.voteva.quiz.grpc.model.v1.GRemoveAdminGrantsRequest;
import com.voteva.quiz.grpc.model.v1.GSetAdminGrantsRequest;
import com.voteva.quiz.grpc.model.v1.GUnblockUserRequest;
import io.grpc.StatusRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsersServiceImpl implements UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    private final GRpcUsersServiceClient grpcUsersServiceClient;
    private final GRpcQuizServiceClient grpcQuizServiceClient;

    @Autowired
    public UsersServiceImpl(GRpcUsersServiceClient grpcUsersServiceClient,
                            GRpcQuizServiceClient grpcQuizServiceClient) {
        this.grpcUsersServiceClient = grpcUsersServiceClient;
        this.grpcQuizServiceClient = grpcQuizServiceClient;
    }

    @Override
    public PagedResult<UserInfo> getUsers(int page, int size) {
        try {
            GGetAllUsersResponse users = grpcQuizServiceClient.getAllUsers(
                    GGetAllUsersRequest.newBuilder()
                            .setPageable(CommonConverter.convert(page, size))
                            .build());

            return UsersInfoConverter.convert(users.getUsersList(), users.getPage());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to get users for page={} and page size={}", page, size);

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public UserInfo getUserByUid(UUID userUid) {
        try {
            return UsersInfoConverter.convert(
                    grpcQuizServiceClient.getUser(
                            GGetUserRequest.newBuilder()
                                    .setUserUid(CommonConverter.convert(userUid))
                                    .build())
                            .getUserInfo());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to get user info by uid={}", userUid);

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public UserInfo addUser(AddUserRequest addUserRequest) {
        try {
            /*return UsersInfoConverter.convert(
                    grpcQuizServiceClient.addUser(
                            GGetUserRequest.newBuilder()
                                    .setUserUid(CommonConverter.convert(userUid))
                                    .build())
                            .getUserInfo());*/
            return null; // TODO

        } catch (StatusRuntimeException e) {
            logger.error("Failed to add user with email={}", addUserRequest.getEmail());

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public void setAdminGrants(UUID userUid) {
        try {
            grpcQuizServiceClient.setAdminGrants(
                    GSetAdminGrantsRequest.newBuilder()
                            .setUserUid(CommonConverter.convert(userUid))
                            .build());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to set admin grants for user with uid={}", userUid);

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public void removeAdminGrants(UUID userUid) {
        try {
            grpcQuizServiceClient.removeAdminGrants(
                    GRemoveAdminGrantsRequest.newBuilder()
                            .setUserUid(CommonConverter.convert(userUid))
                            .build());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to remove admin grants for user with uid={}", userUid);

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public void blockUser(UUID userUid) {
        try {
            grpcQuizServiceClient.blockUser(
                    GBlockUserRequest.newBuilder()
                            .setUserUid(CommonConverter.convert(userUid))
                            .build());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to block user with uid={}", userUid);

            throw GRpcExceptionUtils.convert(e);
        }
    }

    @Override
    public void unblockUser(UUID userUid) {
        try {
            grpcQuizServiceClient.unblockUser(
                    GUnblockUserRequest.newBuilder()
                            .setUserUid(CommonConverter.convert(userUid))
                            .build());

        } catch (StatusRuntimeException e) {
            logger.error("Failed to unblock user with uid={}", userUid);

            throw GRpcExceptionUtils.convert(e);
        }
    }
}
