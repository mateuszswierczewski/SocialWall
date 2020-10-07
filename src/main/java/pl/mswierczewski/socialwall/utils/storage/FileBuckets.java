package pl.mswierczewski.socialwall.utils.storage;

public enum FileBuckets {

    PROFILES_IMAGES("profiles-images"),
    POSTS_IMAGES("posts-images");

    private static final String APP_BUCKET_NAME = "socialwall-app";
    private final String bucketName;


    FileBuckets(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return String.format("%s/%s", APP_BUCKET_NAME, bucketName);
    }
}
