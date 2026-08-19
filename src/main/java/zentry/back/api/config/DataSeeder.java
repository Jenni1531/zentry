package zentry.back.api.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zentry.back.api.core.models.Community;
import zentry.back.api.core.repositories.CommunityRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(CommunityRepository communityRepo) {
        return args -> {
            if (communityRepo.count() == 0) {
                
                communityRepo.save(Community.builder()
                        .nombre("Zentry Network")
                        .descripcion("El espacio oficial para discutir sobre la plataforma, compartir ideas para el sistema de Learning Analytics y conectar con los fundadores.")
                        .categoria("Oficial")
                        .creatorId(1) // El ID de tu usuario admin
                        .build());

                communityRepo.save(Community.builder()
                        .nombre("Zentry Creators")
                        .descripcion("Comunidad exclusiva para artistas digitales y desarrolladores. Comparte tu portafolio, recibe feedback y gana recompensas (ZC).")
                        .categoria("Arte y Desarrollo")
                        .creatorId(1)
                        .build());
                
                System.out.println("🌱 Comunidades oficiales de Zentry creadas exitosamente.");
            }
        };
    }
}