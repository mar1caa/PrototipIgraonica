/*
 * ================================================================
 * FAJL: GalleryImageRepository.java
 * SVRHA: Upiti nad slikama galerije.
 * GDE MENJATI: Redosled slika se određuje preko sortOrder.
 * ================================================================
 */
package rs.blabla.igraonica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.blabla.igraonica.model.GalleryImage;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {
    // Najmanji sortOrder ide prvi; prva slika se koristi kao naslovna.
    List<GalleryImage> findAllByOrderBySortOrderAsc();
}
