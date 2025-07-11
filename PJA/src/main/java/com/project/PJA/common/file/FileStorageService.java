package com.project.PJA.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${FILE_UPLOAD_DIR}")
    private String uploadDir;

    public String storeFile(MultipartFile file, String type, Long id) throws IOException {
        String fileName = type+id+"_"+ UUID.randomUUID()+"_"+file.getOriginalFilename();

        Path dirPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return "/images/profile/" + fileName; // DB에 저장할 URL
    }
    /*
    * 파일 저장 위치(서버 내부)
    *   : ${FILE_UPLOAD_DIR}/user1_abc1234_myprofile.png
    * DB에 저장되는 값
    *   : /images/profile/user1_abc1234_myprofile.png
    * 사용자는 다음 URL 경로로 접근
    *   : http://localhost:8080/images/profile/user1_abc1234_myprofile.png
    * */

    public void deleteFile(String filePath) throws IOException {
        if(filePath == null || filePath.isEmpty()) return;

        // /images/profile/filename.png → filename.png
        String fileName = Paths.get(filePath).getFileName().toString();

        // 실제 저장 경로로 변환
        Path fullPath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();

        // 파일 삭제
        Files.deleteIfExists(fullPath);
    }
}
