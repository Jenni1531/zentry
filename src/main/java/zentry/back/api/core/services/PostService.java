package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;
import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.Post;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.UserRepository;
import zentry.back.api.global.mappers;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

@Service
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public PostService(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    public Page<PostResponse> list(Pageable pageable) {
        return postRepo.findAll(pageable).map(post -> {
            User user = userRepo.findById(post.getUserId()).orElse(new User());
            return mapToResponse(user, post);
        });
    }
    
    public PostResponse getById(Integer id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
        User user = userRepo.findById(post.getUserId()).orElse(new User());
        
        return mapToResponse(user, post);
    }


   public PostResponse create(String email, PostRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Post post = Post.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .contenido(request.getContenido())
                .build();

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveImage(request.getImage(), user.getUsername(), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        return mapToResponse(user, postRepo.save(post));
    }

// MÉTODO PARA GUARDAR EN DISCO
    private String saveImage(MultipartFile file, String username, String type) {
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") 
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                : ".jpg";
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 5) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen de la obra", e);
        }
    }

    public PostResponse update(Integer id, String email, PostRequest request) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta obra");
        }

        post.setTitle(request.getTitle());
        post.setContenido(request.getContenido());

        return mapToResponse(user, postRepo.save(post));
    }

    public void delete(Integer id, String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta obra");
        }

        postRepo.delete(post);
    }

    private PostResponse mapToResponse(User user, Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .authorUsername(user.getUsername())
                .authorName(user.getUsername())
                .title(post.getTitle())
                .contenido(post.getContenido())
                .mediaUrls(new ArrayList<>())
                .tags(new ArrayList<>())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
