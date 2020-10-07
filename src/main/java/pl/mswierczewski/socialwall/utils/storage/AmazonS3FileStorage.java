package pl.mswierczewski.socialwall.utils.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.dtos.FileDto;
import pl.mswierczewski.socialwall.exceptions.FileDownloadException;
import pl.mswierczewski.socialwall.exceptions.FileUploadException;

import java.io.IOException;


@Service
public class AmazonS3FileStorage implements FileStorage {

    private final AmazonS3 amazonS3;

    @Autowired
    public AmazonS3FileStorage(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public void save(String path, String fileName, MultipartFile file) throws FileUploadException{
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        try {
            amazonS3.putObject(path, fileName, file.getInputStream(), metadata);
        } catch (AmazonServiceException | IOException e){
            String msg = String.format("Failed to save file (%s) to s3 storage!", fileName);
            throw FileUploadException.saveError(msg, e);
        }
    }

    @Override
    public FileDto download(String path, String fileName) throws FileDownloadException{
        try {
            S3Object object = amazonS3.getObject(path, fileName);
            String contentType = object.getObjectMetadata().getContentType();

            return new FileDto(IOUtils.toByteArray(object.getObjectContent()), contentType);
        } catch (AmazonServiceException | IOException e) {
            String msg = String.format("Failed to download file (%s) from s3 storage!", fileName);
            throw FileDownloadException.downloadError(msg, e);
        }
    }
}
