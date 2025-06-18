package com.project.PJA.erd.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.erd.dto.aiGenerateDto.*;
import com.project.PJA.erd.entity.*;
import com.project.PJA.erd.repository.ErdColumnRepository;
import com.project.PJA.erd.repository.ErdRelationshipsRepository;
import com.project.PJA.erd.repository.ErdRepository;
import com.project.PJA.erd.repository.ErdTableRepository;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.ConflictException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.ideainput.dto.IdeaInputData;
import com.project.PJA.ideainput.entity.IdeaInput;
import com.project.PJA.ideainput.repository.IdeaInputRepository;
import com.project.PJA.projectinfo.dto.ProjectData;
import com.project.PJA.projectinfo.entity.ProjectInfo;
import com.project.PJA.projectinfo.repository.ProjectInfoRepository;
import com.project.PJA.requirement.dto.RequirementData;
import com.project.PJA.requirement.entity.Requirement;
import com.project.PJA.requirement.repository.RequirementRepository;
import com.project.PJA.user.entity.Users;
import com.project.PJA.workspace.entity.Workspace;
import com.project.PJA.workspace.repository.WorkspaceRepository;
import com.project.PJA.workspace.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ErdService {

    private final ErdRepository erdRepository;
    private final WorkspaceService workspaceService;
    private final IdeaInputRepository ideaInputRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RequirementRepository requirementRepository;
    private final ProjectInfoRepository projectInfoRepository;
    private final WorkspaceRepository workspaceRepository;
    private final RestTemplate restTemplate;
    private final ErdTableRepository erdTableRepository;
    private final ErdColumnRepository erdColumnRepository;
    private final ErdRelationshipsRepository erdRelationshipsRepository;

    // workspaceID로 erdId 찾기
    public Long findErdId(Users user, Long workspaceId) {

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                        .orElseThrow(() -> new NotFoundException("해당 워크스페이스 아이디로 발견된 워크스페이스가 없습니다."));

        workspaceService.validateWorkspaceAccess(user.getUserId(), foundWorkspace);

        Optional<Erd> erdOptional = erdRepository.findByWorkspaceId(workspaceId);

        if(erdOptional.isEmpty()) {
            throw new NotFoundException("erd가 존재하지 않습니다.");
        }
        return erdOptional.get().getErdId();
    }

    // 사용자가 ERD 생성
    public Erd createErd(Users user, Long workspaceId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId,"게스트는 ERD를 생성할 권한이 없습니다.");

        if(erdRepository.existsByWorkspaceId(workspaceId)) {
            throw new
                    ConflictException("해당 워크스페이스에는 이미 ERD가 존재합니다.");
        }
        Erd erd = new Erd();
        erd.setWorkspaceId(workspaceId);
        erd.setCreatedAt(LocalDateTime.now());
        erd.setTables(new ArrayList<>());

        return erdRepository.save(erd);
    }

    // ERD AI 생성 요청
    @Transactional
    public List<ErdAiCreateResponse> recommendErd(Users user, Long workspaceId) {
        workspaceService.authorizeOwnerOrMemberOrThrow(user.getUserId(), workspaceId, "이 워크스페이스에 생성할 권한이 없습니다.");

        // erd 존재 시 예외처리
        if(erdRepository.existsByWorkspaceId(workspaceId)) {
            throw new BadRequestException("이미 해당 워크스페이스에 ERD가 존재합니다.");
        }

        Workspace foundWorkspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 워크스페이스를 찾을 수 없습니다."));

        // 아이디어 입력 조회 및 데이터 생성
        IdeaInput foundIdeaInput = ideaInputRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(() -> new NotFoundException("요청하신 아이디어 입력을 찾을 수 없습니다."));
        IdeaInputData ideaInputData = new IdeaInputData(
                foundIdeaInput.getIdeaInputId(),
                foundIdeaInput.getProjectName(),
                foundIdeaInput.getProjectTarget(),
                foundIdeaInput.getProjectDescription()
        );

        // 요구사항 명세서 조회 및 데이터 생성
        List<Requirement> foundRequirementList = requirementRepository.findByWorkspace_WorkspaceId(workspaceId);
        List<RequirementData> requirementDataList = foundRequirementList.stream()
                .map(requirement -> new RequirementData(
                        requirement.getRequirementId(),
                        requirement.getRequirementType().toString(),
                        requirement.getContent()
                ))
                .toList();

        // 프로젝트 요약 정보 조회 및 데이터 생성
        ProjectInfo foundProjectInfo  = projectInfoRepository.findByWorkspace_WorkspaceId(workspaceId)
                .orElseThrow(()-> new NotFoundException("요청하신 아이디어 요약 정보를 찾을 수 없습니다."));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = foundProjectInfo.getCreatedAt().format(formatter);
        String updatedAtStr = foundProjectInfo.getUpdatedAt().format(formatter);

        ProjectData projectInfoData = new ProjectData(
                foundProjectInfo.getProjectInfoId(),
                foundProjectInfo.getTitle(),
                foundProjectInfo.getCategory(),
                foundProjectInfo.getTargetUsers(),
                foundProjectInfo.getCoreFeatures(),
                foundProjectInfo.getTechnologyStack(),
                foundProjectInfo.getProblemSolving(),
                createdAtStr,
                updatedAtStr
        );

        // 직렬화용 객체로 변환
        String projectOverviewJson;
        String requirementJson;
        String projectSummaryJson;

        try {
            projectOverviewJson = objectMapper.writeValueAsString(ideaInputData);
            requirementJson = objectMapper.writeValueAsString(requirementDataList);
            projectSummaryJson = objectMapper.writeValueAsString(projectInfoData);
        } catch (Exception e) {
            throw new RuntimeException("JSON 직렬화 실패: " + e.getMessage(), e);
        }

        ErdAiRequestDto erdCreateRequest = ErdAiRequestDto.builder()
                .project_overview(projectOverviewJson)
                .requirements(requirementJson)
                .project_summury(projectSummaryJson)
                .max_tokens(8000L)
                .temperature(0.3)
                .model("ft:gpt-4o-mini-2024-07-18:test::BebIPMSD")
                .build();

        String mlopsUrl = "http://3.34.185.3:8000/api/PJA/json_ERD/generate";

        try {
            ResponseEntity<ErdAiCreateResponse> response = restTemplate.postForEntity(
                    mlopsUrl,
                    erdCreateRequest,
                    ErdAiCreateResponse.class
            );

            ErdAiCreateResponse body = response.getBody();

            // DB 저장
            Erd savedErd = erdRepository.save(
                    Erd.builder()
                        .workspaceId(workspaceId)
                        .createdAt(LocalDateTime.now())
                        .tables(new ArrayList<>()) // 이후에 추가
                        .build());

            Map<String, ErdTable> tableMap = new HashMap<>();
            List<ErdTable> savedTables = new ArrayList<>();
            List<ErdColumn> savedColumns = new ArrayList<>();

            for(AiErdTable aiErdTable : body.getJson().getErdTables()) {
                ErdTable erdTable = ErdTable.builder()
                        .erd(savedErd)
                        .name(aiErdTable.getName())
                        .columns(new ArrayList<>())
                        .build();

                List<ErdColumn> columns = aiErdTable.getErdColumns().stream()
                        .map(col -> ErdColumn.builder()
                                .erdTable(erdTable)
                                .name(col.getName())
                                .dataType(col.getDataType())
                                .isPrimaryKey(col.isPrimaryKey())
                                .isForeignKey(col.isForeignKey())
                                .isNullable(col.isNullable())
                                .build())
                        .toList();

                erdTable.setColumns(columns);
                savedTables.add(erdTable);
                savedColumns.addAll(columns);
                tableMap.put(aiErdTable.getName(), erdTable);
            }

            erdTableRepository.saveAll(savedTables);
            erdColumnRepository.saveAll(savedColumns);

//            List<ErdRelationships> savedRelations = new ArrayList<>();
//            for (AiErdRelationships rel : body.getJson().getErdRelationships()) {
//                ErdTable fromTable = savedTables.stream()
//                        .filter(t -> t.getName().equals(rel.getFromTable()))
//                        .findFirst()
//                        .orElseThrow(() -> new NotFoundException("fromTable not found"));
//
//                ErdTable toTable = savedTables.stream()
//                        .filter(t -> t.getName().equals(rel.getToTable()))
//                        .findFirst()
//                        .orElseThrow(() -> new NotFoundException("toTable not found"));
//
//                Optional<ErdColumn> optionalForeignColumn = erdColumnRepository.findByErdTableAndName(toTable, rel.getForeignKey());
//                ErdColumn foreignErdColumn = null;
//
//                if (optionalForeignColumn.isPresent()) {
//                    foreignErdColumn = optionalForeignColumn.get();
//                }
//
//                ErdRelationships relation = ErdRelationships.builder()
//                        .fromTable(fromTable)
//                        .toTable(toTable)
//                        .foreignColumn(foreignErdColumn)
//                        .constraintName(rel.getConstraintName())
//                        .build();
//
//                savedRelations.add(relation);
//            }
//            erdRelationshipsRepository.saveAll(savedRelations);
            return body != null ? List.of(body) : new ArrayList<>();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("MLOps API 호출 실패: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }
    }

    public Erd findByIdOrThrow(Long id) {
        return erdRepository.findById(id).orElseThrow(
                () -> new NotFoundException("ERD를 찾을 수 없습니다.")
        );
    }
}
