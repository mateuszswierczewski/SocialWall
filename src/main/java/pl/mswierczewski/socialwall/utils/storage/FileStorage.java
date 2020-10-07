package pl.mswierczewski.socialwall.utils.storage;


import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.exceptions.FileDownloadException;
import pl.mswierczewski.socialwall.exceptions.FileUploadException;

public interface FileStorage {

    void save(String path, String fileName, MultipartFile file) throws FileUploadException;

    FileDto download(String path, String fileName) throws FileDownloadException;
}
