package zentry.back.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import zentry.back.api.core.models.Community;
import zentry.back.api.core.models.User;
import zentry.back.api.core.repositories.CommunityRepository;
import zentry.back.api.core.repositories.UserRepository;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CommunityRepository communityRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {
        return args -> {
            User admin = userRepo.findByEmail("admin@zentry.com").orElse(null);
            if (admin == null) {
                admin = User.builder()
                        .username("admin")
                        .email("admin@zentry.com")
                        .password(passwordEncoder.encode("admin123"))
                        .verified(true)
                        .build();
                admin = userRepo.save(admin);
                System.out.println("👤 Usuario Administrador de prueba creado: admin@zentry.com");
            }

            Integer adminId = admin.getId();
            String adminUsername = admin.getHandle();

            if (!communityRepo.existsBySlug("eterna") && communityRepo.findBySlugOrNombre("eterna").isEmpty()) {
                communityRepo.save(Community.builder()
                        .slug("eterna")
                        .nombre("Eterna")
                        .descripcion("Comunidad oficial dedicada al arte digital, diseño conceptual y experiencias creativas Eterna.")
                        .categoria("Arte Digital")
                        .avatarUrl("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500")
                        .bannerUrl("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=1200")
                        .imageUrl("https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?w=500")
                        .creatorId(adminId)
                        .ownerUsername(adminUsername)
                        .rules(List.of(
                                "Respeto mutuo y trato profesional entre creadores",
                                "Publicar únicamente contenido y arte original o con licencia adecuada",
                                "No realizar spam ni contenido no relacionado con la comunidad"
                        ))
                        .build());
            }

            if (!communityRepo.existsBySlug("ui-ux-designers") && communityRepo.findBySlugOrNombre("ui-ux-designers").isEmpty()) {
                communityRepo.save(Community.builder()
                        .slug("ui-ux-designers")
                        .nombre("UI/UX Designers")
                        .descripcion("Comunidad para diseñadores de interfaz y experiencia de usuario. Comparte wireframes, design systems y prototipos.")
                        .categoria("Diseño UI/UX")
                        .avatarUrl("https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=500")
                        .bannerUrl("https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=1200")
                        .imageUrl("https://images.unsplash.com/photo-1581291518633-83b4ebd1d83e?w=500")
                        .creatorId(adminId)
                        .ownerUsername(adminUsername)
                        .rules(List.of(
                                "Dar feedback constructivo sobre diseños",
                                "Respetar derechos de autor en recursos compartidos"
                        ))
                        .build());
            }

            if (!communityRepo.existsBySlug("digital-art") && communityRepo.findBySlugOrNombre("digital-art").isEmpty()) {
                communityRepo.save(Community.builder()
                        .slug("digital-art")
                        .nombre("Digital Art")
                        .descripcion("Espacio para artistas digitales, ilustradores y diseñadores 3D. Comparte tu flujo de trabajo y recibe feedback.")
                        .categoria("Arte y Diseño")
                        .avatarUrl("https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=500")
                        .bannerUrl("https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=1200")
                        .imageUrl("https://images.unsplash.com/photo-1579783902614-a3fb3927b675?w=500")
                        .creatorId(adminId)
                        .ownerUsername(adminUsername)
                        .rules(List.of(
                                "Muestra siempre el proceso de creación si es solicitado",
                                "Cita tus herramientas y recursos utilizados"
                        ))
                        .build());
            }

            if (!communityRepo.existsBySlug("zentry-network") && communityRepo.findBySlugOrNombre("zentry-network").isEmpty()) {
                communityRepo.save(Community.builder()
                        .slug("zentry-network")
                        .nombre("Zentry Network")
                        .descripcion("El espacio oficial para discutir sobre la plataforma, compartir ideas para el sistema de Learning Analytics y conectar con los fundadores.")
                        .categoria("Oficial")
                        .avatarUrl("https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=500")
                        .bannerUrl("https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=1200")
                        .imageUrl("https://images.unsplash.com/photo-1522071820081-009f0129c71c?w=500")
                        .creatorId(adminId)
                        .ownerUsername(adminUsername)
                        .rules(List.of(
                                "Comentarios y retroalimentación respetuosa sobre la plataforma"
                        ))
                        .build());
            }

            if (!communityRepo.existsBySlug("zentry-creators") && communityRepo.findBySlugOrNombre("zentry-creators").isEmpty()) {
                communityRepo.save(Community.builder()
                        .slug("zentry-creators")
                        .nombre("Zentry Creators")
                        .descripcion("Comunidad exclusiva para artistas digitales y desarrolladores. Comparte tu portafolio, recibe feedback y gana recompensas (ZC).")
                        .categoria("Arte y Desarrollo")
                        .avatarUrl("https://images.unsplash.com/photo-1552664730-d307ca884978?w=500")
                        .bannerUrl("https://images.unsplash.com/photo-1552664730-d307ca884978?w=1200")
                        .imageUrl("https://images.unsplash.com/photo-1552664730-d307ca884978?w=500")
                        .creatorId(adminId)
                        .ownerUsername(adminUsername)
                        .rules(List.of(
                                "Colaboración libre entre miembros de la red Zentry"
                        ))
                        .build());
            }

            System.out.println("🌱 Comunidades oficiales de Zentry (incluyendo 'UI/UX Designers', 'Eterna' y 'Digital Art') verificadas y sincronizadas.");
        };
    }
}