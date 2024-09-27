package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.model.cache.UserForCache;
import faang.school.postservice.repository.redis.UserCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private final UserCacheRepository userCacheRepository;
    private final UserServiceClient userServiceClient;
    private final UserMapper userMapper;

    @Async
    public void save(Long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        UserForCache userForSaveToCache = userMapper.toUserForCache(userDto);
        userCacheRepository.save(userForSaveToCache);
    }

    public Optional<UserForCache> getUserFromCache(Long userId) {
        return userCacheRepository.findById(userId);
    }
}
