package com.project.PJA.api.service;

import com.project.PJA.api.dto.ApiRequest;
import com.project.PJA.api.dto.ApiResponse;
import com.project.PJA.api.entity.Api;
import com.project.PJA.api.repository.ApiRepository;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiService {
    private final WorkspaceRepository workspaceRepository;
    private final ApiRepository apiRepository;
    private final WorkspaceService workspaceService;

    // api 명세서 조회
    @Transactional(readOnly = true)
    public List<ApiResponse> getApi(Long userId, Long workspaceId) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.validateWorkspaceAccess(userId, foundWorkspace);

        List<Api> apis = apiRepository.findByWorkspace_WorkspaceId(workspaceId);

        return apis.stream()
                .map(api -> new ApiResponse(
                        api.getApiId(),
                        api.getTitle(),
                        api.getTag(),
                        api.getPath(),
                        api.getHttpMethod(),
                        api.getRequest(),
                        api.getResponse()
                ))
                .collect(Collectors.toList());
    }

    // api 명세서 ai 요청

    // api 명세서 저장
    @Transactional
    public List<ApiResponse> saveApi(Long userId, Long workspaceId, List<ApiRequest> apiRequests) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 오너확인? 멤버도 되나?

        List<Api> apiList = apiRequests.stream()
                .map(request -> Api.builder()
                        .workspace(foundWorkspace)
                        .title(request.getTitle())
                        .tag(request.getTag())
                        .path(request.getPath())
                        .httpMethod(request.getHttpMethod())
                        .request(request.getRequest())
                        .response(request.getResponse())
                        .build())
                .collect(Collectors.toList());

        List<Api> savedApis = apiRepository.saveAll(apiList);

        return savedApis.stream()
                .map(api -> new ApiResponse(
                        api.getApiId(),
                        api.getTitle(),
                        api.getTag(),
                        api.getPath(),
                        api.getHttpMethod(),
                        api.getRequest(),
                        api.getResponse()
                ))
                .collect(Collectors.toList());
    }

    // api 생성
    @Transactional
    public ApiResponse createApi(Long userId, Long workspaceId, ApiRequest apiRequest) {
        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        Api createdApi = apiRepository.save(
                Api.builder()
                        .workspace(foundWorkspace)
                        .title(apiRequest.getTitle())
                        .tag(apiRequest.getTag())
                        .path(apiRequest.getPath())
                        .httpMethod(apiRequest.getHttpMethod())
                        .request(apiRequest.getRequest())
                        .response(apiRequest.getResponse())
                        .build()
        );

        return new ApiResponse(
                createdApi.getApiId(),
                createdApi.getTitle(),
                createdApi.getTag(),
                createdApi.getPath(),
                createdApi.getHttpMethod(),
                createdApi.getRequest(),
                createdApi.getResponse()
        );
    }

    // api 수정
    @Transactional
    public ApiResponse updateApi(Long userId, Long workspaceId, Long apiId, ApiRequest apiRequest) {
        Api foundApi = apiRepository.findById(apiId)
                .orElseThrow(() -> new NotFoundException("요청하신 API를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 수정할 권한이 없습니다.");

        foundApi.update(
                apiRequest.getTitle(),
                apiRequest.getTag(),
                apiRequest.getPath(),
                apiRequest.getHttpMethod(),
                apiRequest.getRequest(),
                apiRequest.getResponse());

        return new ApiResponse(
                foundApi.getApiId(),
                apiRequest.getTitle(),
                apiRequest.getTag(),
                apiRequest.getPath(),
                apiRequest.getHttpMethod(),
                apiRequest.getRequest(),
                apiRequest.getResponse()
        );
    }

    // api 삭제
    @Transactional
    public ApiResponse deleteApi(Long userId, Long workspaceId, Long apiId) {
        Api foundApi = apiRepository.findById(apiId)
                .orElseThrow(() -> new NotFoundException("요청하신 API를 찾을 수 없습니다."));

        workspaceService.authorizeOwnerOrMemberOrThrow(userId, workspaceId, "이 워크스페이스에 삭제할 권한이 없습니다.");

        apiRepository.delete(foundApi);

        return new ApiResponse(
                foundApi.getApiId(),
                foundApi.getTitle(),
                foundApi.getTag(),
                foundApi.getPath(),
                foundApi.getHttpMethod(),
                foundApi.getRequest(),
                foundApi.getResponse()
        );
    }
}
