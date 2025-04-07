package chuchuchi.chuchuchi.global.file.service;

import chuchuchi.chuchuchi.global.file.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    String save(MultipartFile multipartFile) throws FileException;

    void delete(String filePath);
}
