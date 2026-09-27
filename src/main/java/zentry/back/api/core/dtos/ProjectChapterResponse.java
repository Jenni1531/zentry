package zentry.back.api.core.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
public class ProjectChapterResponse {
    private Long id;
    private Long projectId;
    private String title;
    private String content;
    private Integer position;
    private Integer wordCount;
    private String authorUsername;
    private String lastEditedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
