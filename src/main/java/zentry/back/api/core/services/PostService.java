package zentry.back.api.core.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import zentry.back.api.core.dtos.PostRequest;
import zentry.back.api.core.dtos.PostResponse;
import zentry.back.api.core.models.User;
import zentry.back.api.core.models.Post;
import zentry.back.api.core.repositories.PostRepository;
import zentry.back.api.core.repositories.UserRepository;

@Service
@SuppressWarnings("null")
public class PostService {

    private final PostRepository postRepo;
    private final UserRepository userRepo;

    public PostService(PostRepository postRepo, UserRepository userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    private User findUserByIdentifier(String identifier) {
        if (identifier == null || identifier.isBlank() || "anonimo".equalsIgnoreCase(identifier)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        var byEmail = userRepo.findByEmail(identifier);
        if (byEmail.isPresent()) return byEmail.get();

        var byUsername = userRepo.findByUsername(identifier);
        if (byUsername.isPresent()) return byUsername.get();

        var byBoth = userRepo.findByUsernameOrEmail(identifier, identifier);
        if (byBoth.isPresent()) return byBoth.get();

        var byEmailPrefix = userRepo.findByEmailStartingWith(identifier + "@");
        if (byEmailPrefix.isPresent()) return byEmailPrefix.get();

        var byEmailPrefixRaw = userRepo.findByEmailStartingWith(identifier);
        if (byEmailPrefixRaw.isPresent()) return byEmailPrefixRaw.get();

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
    }

    public List<PostResponse> getMyPosts(String identifier) {
        User user = findUserByIdentifier(identifier);
        List<Post> posts = postRepo.findByUserIdOrderByCreatedAtDesc(user.getId());
        return posts.stream().map(p -> mapToResponse(user, p)).collect(Collectors.toList());
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

    public Page<PostResponse> getAllPosts(Pageable pageable) {
        return list(pageable);
    }

    public PostResponse create(String identifier, PostRequest request) {
        User user = findUserByIdentifier(identifier);

        Post post = Post.builder()
                .userId(user.getId())
                .title(request.getTitle())
                .contenido(request.getContenido())
                .contentType(request.getContentType() != null ? request.getContentType() : "canvas")
                .visibility(request.getVisibility() != null ? request.getVisibility() : "public")
                .build();

        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().isBlank()) {
            if (request.getThumbnailUrl().startsWith("data:image/")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), user.getUsername(), "thumb");
                post.setThumbnailUrl(savedPath);
                post.setImageUrl(savedPath);
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
                if (post.getImageUrl() == null) {
                    post.setImageUrl(request.getThumbnailUrl());
                }
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            post.setImageUrl(request.getImageUrl());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveImage(request.getImage(), user.getUsername(), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
            if (post.getThumbnailUrl() == null) {
                post.setThumbnailUrl("/uploads/posts/" + imageName);
            }
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        return mapToResponse(user, postRepo.save(post));
    }

    public PostResponse update(Integer id, String identifier, PostRequest request) {
        User user = findUserByIdentifier(identifier);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para editar esta obra");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            post.setTitle(request.getTitle());
        }
        if (request.getContenido() != null) {
            post.setContenido(request.getContenido());
        }
        if (request.getContentType() != null) {
            post.setContentType(request.getContentType());
        }
        if (request.getVisibility() != null) {
            post.setVisibility(request.getVisibility());
        }

        if (request.getThumbnailUrl() != null && !request.getThumbnailUrl().isBlank()) {
            if (request.getThumbnailUrl().startsWith("data:image/")) {
                String savedPath = saveBase64Image(request.getThumbnailUrl(), user.getUsername(), "thumb");
                post.setThumbnailUrl(savedPath);
                post.setImageUrl(savedPath);
            } else {
                post.setThumbnailUrl(request.getThumbnailUrl());
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isBlank()) {
            post.setImageUrl(request.getImageUrl());
        }

        if (request.getImage() != null && !request.getImage().isEmpty()) {
            String imageName = saveImage(request.getImage(), user.getUsername(), "post");
            post.setImageUrl("/uploads/posts/" + imageName);
        }

        if (request.getTools() != null && !request.getTools().isEmpty()) {
            post.setTools(java.util.Arrays.asList(request.getTools().split(",")));
        }

        post.setUpdatedAt(java.time.LocalDateTime.now());
        return mapToResponse(user, postRepo.save(post));
    }

    public void delete(Integer id, String identifier) {
        User user = findUserByIdentifier(identifier);

        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));

        if (!post.getUserId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para eliminar esta obra");
        }

        postRepo.delete(post);
    }

    public PostResponse likePost(Integer id) {
        Post post = postRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Obra no encontrada"));
        User user = userRepo.findById(post.getUserId()).orElse(new User());
        return mapToResponse(user, post);
    }

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
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return newFilename;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al guardar la imagen de la obra", e);
        }
    }

    private String saveBase64Image(String base64Data, String username, String type) {
        try {
            Path uploadPath = Paths.get("uploads/posts");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String base64Image = base64Data;
            String extension = ".png";
            if (base64Data.contains(",")) {
                String[] parts = base64Data.split(",");
                if (parts[0].contains("image/jpeg") || parts[0].contains("image/jpg")) {
                    extension = ".jpg";
                } else if (parts[0].contains("image/webp")) {
                    extension = ".webp";
                }
                base64Image = parts[1];
            }

            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.trim());
            String newFilename = username + "_" + type + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            Path filePath = uploadPath.resolve(newFilename);
            Files.write(filePath, decodedBytes);

            return "/uploads/posts/" + newFilename;
        } catch (Exception e) {
            return base64Data;
        }
    }

    private PostResponse mapToResponse(User user, Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .authorUsername(user.getUsername() != null ? user.getUsername() : "usuario")
                .authorName(user.getUsername() != null ? user.getUsername() : "usuario")
                .title(post.getTitle())
                .contenido(post.getContenido())
                .contentType(post.getContentType() != null ? post.getContentType() : "canvas")
                .thumbnailUrl(post.getThumbnailUrl())
                .imageUrl(post.getImageUrl())
                .visibility(post.getVisibility())
                .tools(post.getTools() != null ? post.getTools() : new ArrayList<>())
                .mediaUrls(new ArrayList<>())
                .tags(new ArrayList<>())
                .likesCount(0)
                .commentsCount(0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
