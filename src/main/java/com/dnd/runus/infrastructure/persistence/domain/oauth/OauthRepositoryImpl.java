package com.dnd.runus.infrastructure.persistence.domain.oauth;

import com.dnd.runus.domain.oauth.OauthRepository;
import com.dnd.runus.infrastructure.persistence.jooq.oauth.JooqOauthRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * 회원 탈퇴(hard delete)를 위한 임시 구현
 * FIXME soft delete 완료 후 삭제
 */
@Repository
@RequiredArgsConstructor
public class OauthRepositoryImpl implements OauthRepository {

    private final JooqOauthRepository jooqOauthRepository;

    private final EntityManager em;

    @Override
    public void deleteAllDataAboutMember(long memberId) {
        em.flush();
        em.clear();
        jooqOauthRepository.deleteAllDataAboutMember(memberId);
    }
}
