package com.dnd.runus.auth.userdetails;

import com.dnd.runus.auth.exception.AuthException;
import com.dnd.runus.domain.member.Member;
import com.dnd.runus.domain.member.MemberRepository;
import com.dnd.runus.global.exception.type.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String identity) throws UsernameNotFoundException {
        try {
            long memberId = Long.parseLong(identity);
            Member member = memberRepository
                    .findById(memberId)
                    .orElseThrow(
                            () -> new AuthException(ErrorType.FAILED_AUTHENTICATION, "Member not found: " + memberId));
            return AuthUserDetails.of(memberId, member.role());
        } catch (NumberFormatException exception) {
            throw new UsernameNotFoundException(identity);
        }
    }
}
