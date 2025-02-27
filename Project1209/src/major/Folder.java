package major;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Folder {
    private String name;
    private String authorName;
    private int rating;
    private Date modifiedDate;
    private String reviewContent;
    private ArrayList<String> tags;
    private String imagePath; // 이미지 경로 추가

    public Folder(String name, String authorName, int rating, Date modifiedDate, String reviewContent, String tagInput, String imagePath) {
        this.name = name;
        this.authorName = authorName;
        this.rating = rating;
        this.modifiedDate = modifiedDate;
        this.reviewContent = reviewContent;
        this.tags = parseTags(tagInput);
        this.imagePath = imagePath;
    }

    private ArrayList<String> parseTags(String tagInput) {
        ArrayList<String> tagList = new ArrayList<>();
        if (tagInput != null && !tagInput.isEmpty()) {
            String[] tagArray = tagInput.split("#");
            for (String tag : tagArray) {
                if (!tag.trim().isEmpty()) {
                    tagList.add(tag.trim());
                }
            }
        }
        return tagList;
    }

    public String getName() {
        return name;
    }

    public String getAuthorName() {
        return authorName;
    }

    public int getRating() {
        return rating;
    }

    public Date getModifiedDateAsDate() {
        return modifiedDate;
    }

    public String getModifiedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(modifiedDate);
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public String getTagsAsString() {
        return String.join(", ", tags);
    }
    
    public String getImagePath() {
        return imagePath; // 이미지 경로 반환
    }
    
    // Setter 메서드들 추가
    public void setName(String name) {
        this.name = name;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    // 태그를 직접 문자열로 받는 Setter 추가
    public void setTags(String tagInput) {
        this.tags = parseTags(tagInput);
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
