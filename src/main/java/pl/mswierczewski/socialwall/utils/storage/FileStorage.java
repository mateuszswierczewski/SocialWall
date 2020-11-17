package pl.mswierczewski.socialwall.utils.storage;


import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.exceptions.FileDownloadException;
import pl.mswierczewski.socialwall.exceptions.FileUploadException;

import java.util.List;

public interface FileStorage {

    void save(String path, String fileName, MultipartFile file) throws FileUploadException;

    FileDto download(String path, String fileName) throws FileDownloadException;

    void deleteFile(String path, String fileName);

    void deleteFiles(String path, List<String> fileNames);

    void deleteBucket(String path);
}
