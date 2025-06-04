package com.project.PJA.idea.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DevelopmentTimeline {
    @JsonProperty("estimated_duration")
    private String estimatedDuration;

    @JsonProperty("key_milestones")
    private List<KeyMilestone> keyMilestones;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KeyMilestone {
        private String phase;
        private String duration;
    }
}
