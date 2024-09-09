package com.dnd.runus.domain.oauth;

/**
 * 회원 탈퇴(hard delete)를 위한 임시 구현
 * FIXME soft delete 완료 후 삭제
 */
public interface OauthRepository {
    void deleteAllDataAboutMember(long memberId);
}
