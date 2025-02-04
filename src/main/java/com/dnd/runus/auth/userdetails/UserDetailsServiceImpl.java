package com.dnd.runus.auth.userdetails;

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
    //    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String identity) throws UsernameNotFoundException {
        return null;
        //        try {
        //            long memberId = Long.parseLong(identity);
        //            Member member = memberRepository
        //                    .findById(memberId)
        //                    .orElseThrow(
        //                            () -> new AuthException(ErrorType.FAILED_AUTHENTICATION, "Member not found: " +
        // memberId));
        //            return AuthUserDetails.of(memberId, member.role());
        //        } catch (NumberFormatException exception) {
        //            throw new UsernameNotFoundException(identity);
        //        }
    }
}
