package pl.mswierczewski.socialwall.dtos.post;

import org.springframework.web.multipart.MultipartFile;
import pl.mswierczewski.socialwall.components.enums.PostType;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Optional;

public class PostRequest {
    
    // Optional field
    @Size(min = 1, max = 510, message = "Text cannot be empty or longer than {max} characters!")
    private String textContent;

    // Optional collection
    @Size(max = 10, message = "Max count of images is {max}!")
    private List<MultipartFile> images;

    public Optional<String> getTextContent() {
        return Optional.ofNullable(textContent);
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public Optional<List<MultipartFile>> getImages() {
        return Optional.ofNullable(images);
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }

    public PostType getPostType(){
        if (textContent != null && !textContent.isEmpty() && images != null && !images.isEmpty()){
            return PostType.TEXT_IMAGE;
        } else if (textContent == null || textContent.isEmpty()) {
            return PostType.IMAGE;
        } else {
            return PostType.TEXT;
        }
    }

    @Override
    public String toString() {
        return "PostRequest{" +
                "textContent='" + textContent + '\'' +
                ", images=" + images +
                '}';
    }
}
