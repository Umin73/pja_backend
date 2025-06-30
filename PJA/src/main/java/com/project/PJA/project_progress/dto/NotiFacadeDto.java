package com.project.PJA.project_progress.dto;

import com.project.PJA.project_progress.entity.ActionPost;
import com.project.PJA.user.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotiFacadeDto {
    Map<String, Object> result;
    List<Users> receivers;
    String notiMessage;
    ActionPost actionPost;
}
