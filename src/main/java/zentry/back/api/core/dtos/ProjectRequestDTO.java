package zentry.back.api.core.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ProjectRequestDTO {
    private String title;
    private String description;
    private String category;
    private String priority;
<<<<<<< HEAD
    private String status; // "active", "completed", "paused"
=======
>>>>>>> 7a1026500d2fa605f096db22f93fbab6417dc45f
    private String deadline;
    private List<String> tags;
}
