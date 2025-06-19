package com.project.PJA.user_act_log.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogSchedular {

    private final LogSenderService logSenderService;

    @Scheduled(fixedRate = 300_000) // 5분마다 전송하기
    public void sendLogs() {
        log.info("== User Action 로그 전송 스케줄 ==");
        logSenderService.sendLogsFromFile();
    }
}
