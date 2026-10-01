/*
 * ================================================================
 * FAJL: WebConfig.java
 * SVRHA: Omogućava da slike iz uploads foldera budu dostupne preko /uploads/... URL-a.
 * GDE MENJATI: Menjaj samo ako menjaš lokaciju ili način čuvanja uploadovanih slika.
 * ================================================================
 */
package rs.blabla.igraonica.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Folder se podešava preko app.upload-dir u application.properties.
    @Value("${app.upload-dir}")
    private String uploadDir;

    /**
     * Primer: fizički fajl uploads/slika.jpg postaje dostupan browseru kao /uploads/slika.jpg.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(path);
    }
}
