package school.hei.sary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.hei.sary.repository.model.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, String> {}
