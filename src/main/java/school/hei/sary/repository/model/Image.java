package school.hei.sary.repository.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
@ToString
public class Image {
  @Id private String id;
  private String filename;
}
