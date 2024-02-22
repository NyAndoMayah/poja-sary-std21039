package school.hei.sary.endpoint.rest.controller;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
public class ImageController {
  @PostMapping("/convertToBlackAndWhite")
  public ResponseEntity<byte[]> convertToBlackAndWhite(@RequestPart("file") MultipartFile file) {
    try {
      // Récupération du fichier en entrée
      InputStream inputStream = file.getInputStream();
      BufferedImage image = ImageIO.read(inputStream);

      // Transformation en noir et blanc
      BufferedImage blackAndWhiteImage =
          new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
      Graphics2D g = blackAndWhiteImage.createGraphics();
      g.drawImage(image, 0, 0, null);
      g.dispose();

      // Conversion de l'image en tableau de bytes
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(blackAndWhiteImage, "jpg", baos);
      byte[] blackAndWhiteBytes = baos.toByteArray();

      // Fermeture des flux
      inputStream.close();
      baos.close();

      // Retourne la réponse avec le fichier transformé
      return ResponseEntity.ok()
          .contentLength(blackAndWhiteBytes.length)
          .contentType(org.springframework.http.MediaType.IMAGE_JPEG)
          .body(blackAndWhiteBytes);

    } catch (IOException e) {
      e.printStackTrace();
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }
}
