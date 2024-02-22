package school.hei.sary.service;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.InternalServerErrorException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import javax.imageio.ImageIO;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.endpoint.rest.model.RestImage;
import school.hei.sary.file.BucketComponent;
import school.hei.sary.repository.ImageRepository;
import school.hei.sary.repository.model.Image;

@Service
@NoArgsConstructor
public class ImageService {
  private BucketComponent bucketComponent;
  private ImageRepository imageRepository;
  private final Duration DURATION = Duration.ofMinutes(30);

  protected File convertToBlackAndWhite(File inputImage) {
    try {
      BufferedImage bufferedImage = ImageIO.read(inputImage);
      if (bufferedImage == null) {
        throw new BadRequestException("It seems like the image is not valid");
      } else {
        BufferedImage BWBufferedImage =
            new BufferedImage(
                bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        BWBufferedImage.getGraphics().drawImage(bufferedImage, 0, 0, null);

        ByteArrayOutputStream byteOSImage = new ByteArrayOutputStream();
        ImageIO.write(BWBufferedImage, "jpeg", byteOSImage);
        byte[] bytes = byteOSImage.toByteArray();
        return Files.write(Path.of("/tmp/" + inputImage.getName()), bytes).toFile();
      }
    } catch (IOException e) {
      throw new InternalServerErrorException("Image cannot be processed");
    }
  }

  @Transactional
  public byte[] processImage(String id, MultipartFile multipartFile) throws IOException {
    File originalImage = multipartFile.getResource().getFile();
    File modifiedImage = convertToBlackAndWhite(originalImage);

    Image imageToSave =
        Image.builder().id(id).filename(multipartFile.getOriginalFilename()).build();

    Image savedImage = imageRepository.save(imageToSave);

    String originalBucketKey = "original-" + savedImage.getFilename();
    String modifiedBucketKey = "modified-" + savedImage.getFilename();

    bucketComponent.upload(originalImage, originalBucketKey);
    bucketComponent.upload(modifiedImage, modifiedBucketKey);

    return Files.readAllBytes(Path.of(modifiedImage.getPath()));
  }

  public RestImage getImageById(String id) {
    Image image = imageRepository.findById(id).orElseThrow();

    String originalBucketKey = "original-" + image.getFilename();
    String modifiedBucketKey = "modified-" + image.getFilename();

    RestImage restImage =
        RestImage.builder()
            .modifiedUrl(bucketComponent.presign(modifiedBucketKey, DURATION).toString())
            .originalUrl(bucketComponent.presign(originalBucketKey, DURATION).toString())
            .build();
    return restImage;
  }
}
