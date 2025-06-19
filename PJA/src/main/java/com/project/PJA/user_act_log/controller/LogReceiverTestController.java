package com.project.PJA.user_act_log.controller;

import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user_act_log.dto.UserActionLog;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class LogReceiverTestController {

    @PostMapping("/user-act-logs")
    public ResponseEntity<String> receiveLogs(@RequestBody List<UserActionLog> logs) {
        System.out.println("== ML팀 서버로부터 받은 로그 수: " + logs.size());
        return ResponseEntity.ok("received");
    }
}
