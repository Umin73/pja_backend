package com.project.PJA.api.entity;

import com.project.PJA.api.dto.Data;
import com.project.PJA.api.dto.Response;
import com.project.PJA.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@Table(name = "api")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Api {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_id")
    private Long apiId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false, foreignKey = @ForeignKey(name = "FK_API_WORKSPACE"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Column(name = "title")
    private String title;

    @Column(name = "tag")
    private String tag;

    @Column(name = "path")
    private String path;

    @Column(name = "http_method")
    private String httpMethod;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request", columnDefinition = "jsonb")
    private List<Data> request;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response", columnDefinition = "jsonb")
    private List<Response> response;

    @Builder
    public Api(Workspace workspace, String title, String tag, String path, String httpMethod, List<Data> request, List<Response> response) {
        this.workspace = workspace;
        this.title = title;
        this.tag = tag;
        this.path = path;
        this.httpMethod = httpMethod;
        this.request = request;
        this.response = response;
    }

    public void update(String title, String tag, String path, String httpMethod, List<Data> request, List<Response> response) {
        this.title = title;
        this.tag = tag;
        this.path = path;
        this.httpMethod = httpMethod;
        this.request = request;
        this.response = response;
    }
}
