package br.com.frohlich.services;

import br.com.frohlich.config.FileStorageConfig;
import br.com.frohlich.exceptions.MyFileNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) throws IOException {
        try {
            this.fileStorageLocation = Path.of(fileStorageConfig.getUploadDir())
                    .toAbsolutePath().normalize();

            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            System.err.println("Could not create the directory where the uploaded files will be stored: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        String filename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        try {
            if (filename.contains("..")) {
                throw new RuntimeException("Sorry! Filename contains invalid path sequence " + filename);
            }

            if (filename.isEmpty()) {
                throw new RuntimeException("Sorry! Filename is empty " + filename);
            }
            Path targetLocation = this.fileStorageLocation.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Could not store file " + filename + ". Please try again!", e);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) return resource;
            else throw new MyFileNotFoundException("File not found " + filename);
        } catch (Exception e) {
            throw new MyFileNotFoundException("File not found " + filename, e);
        }
    }

}
