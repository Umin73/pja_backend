package com.project.PJA.project_progress.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@Getter
@ToString
public class UpdateActionPostDto {
    private String content;
//    private List<MultipartFile> fileList;
}
