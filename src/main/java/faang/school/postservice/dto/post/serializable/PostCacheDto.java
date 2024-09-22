package faang.school.postservice.dto.post.serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import faang.school.postservice.model.ad.Ad;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.CLASS,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
public class PostCacheDto {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likesIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Ad ad;
    private List<Long> resourceIds;
    private boolean published;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime publishedAt;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime scheduledAt;

    private boolean deleted;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    private List<String> hashTags;
}
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.CLASS,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
//@JsonTypeInfo(
//        use = JsonTypeInfo.Id.CLASS,
//        include = JsonTypeInfo.As.PROPERTY,
//        property = "@class"
//)
//@Jacksonized
//@JsonIgnoreProperties(ignoreUnknown = true)
//public class PostCacheDto {
//    private long id;
//    private String content;
//    private Long authorId;
//    private Long projectId;
//
//    private List<Long> likesIds;
//    private List<Long> commentIds;
//    private List<Long> albumIds;
//    private Ad ad;
//    private List<Long> resourceIds;
//    private boolean published;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private LocalDateTime publishedAt;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private LocalDateTime scheduledAt;
//    private boolean deleted;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private LocalDateTime createdAt;
//    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    private LocalDateTime updatedAt;
//    private List<String> hashTags;
//}

//@Builder
//@Jacksonized
//@JsonIgnoreProperties(ignoreUnknown = true)
//public record PostDto(
//        Long id,
//
//        String content,
//        boolean published,
//
//        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        LocalDateTime publishedAt,
//        boolean deleted,
//
//        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        LocalDateTime createdAt,
//
//        @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
//        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//        @JsonSerialize(using = LocalDateTimeSerializer.class)
//        LocalDateTime updatedAt,
//
//        @NotBlank(message = "post author cannot be empty")
//        Long authorId,
//
//        @NotBlank(message = "project for the post cannot be empty")
//        Long projectId
//) {
//}
