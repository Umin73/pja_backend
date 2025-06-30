package com.project.PJA.sse.repository;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRepository {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long workspaceId, String uid, SseEmitter emitter) {
        String emitterId = getEmitterId(workspaceId, uid);
        emitters.put(emitterId, emitter);
        return emitter;
    }

    public Optional<SseEmitter> get(Long workspaceId, String uid) {
        String emitterId = getEmitterId(workspaceId, uid);
        return Optional.ofNullable(emitters.get(emitterId));
    }

    public void delete(Long workspaceId, String uid) {
        String emitterId = getEmitterId(workspaceId, uid);
        emitters.remove(emitterId);
    }

    private String getEmitterId(Long workspaceId, String uid) {
        return workspaceId + ":" + uid;
    }
}
