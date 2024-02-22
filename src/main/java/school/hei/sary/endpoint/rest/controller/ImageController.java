package school.hei.sary.endpoint.rest.controller;

import java.io.IOException;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.endpoint.rest.model.RestImage;
import school.hei.sary.service.ImageService;

@RestController
@AllArgsConstructor
public class ImageController {
  private ImageService imageService;

  @PutMapping(value = "/blacks/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
  public byte[] convertToBlackAndWhite(
      @PathVariable String id, @RequestParam(name = "image") MultipartFile coloredImage)
      throws IOException {
    return imageService.processImage(id, coloredImage);
  }

  @GetMapping(value = "/blacks/{id}")
  public RestImage convertToBlackAndWhite(@PathVariable String id) {
    return imageService.getImageById(id);
  }
}
