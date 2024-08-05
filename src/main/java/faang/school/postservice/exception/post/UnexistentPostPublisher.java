package faang.school.postservice.exception.post;

import faang.school.postservice.exception.exceptionmessages.PostServiceExceptionMessage;

public class UnexistentPostPublisher extends PostServiceException{
    public UnexistentPostPublisher(PostServiceExceptionMessage message, Object... args) {
        super(message, args);
    }
}
