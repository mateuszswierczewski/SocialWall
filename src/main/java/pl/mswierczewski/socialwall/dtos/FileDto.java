package pl.mswierczewski.socialwall.dtos;

import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

public class FileDto {

    private MediaType contentType;
    private byte[] file;

    public FileDto(byte[] file, String contentType) {
        this.file = file;
        this.contentType = MediaType.asMediaType(MimeType.valueOf(contentType));
    }

    public MediaType getContentType() {
        return contentType;
    }

    public void setContentType(MediaType contentType) {
        this.contentType = contentType;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
