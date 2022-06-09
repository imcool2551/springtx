package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    LogRepository logRepository;

    /**
     * memberService      @Transactional: OFF
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON
     */
    @Test
    void outerTxOff_success() {
        String username = "outerTxOff_success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService      @Transactional: OFF
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON Exception
     */
    @Test
    void outerTxOff_fail() {
        String username = "outerTxOff_fail_로그예외";

        assertThatThrownBy((() -> memberService.joinV1(username)))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * memberService      @Transactional: ON
     * memberRepository   @Transactional: OFF
     * logRepository      @Transactional: OFF
     */
    @Test
    void singleTransaction() {
        String username = "singleTransaction";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService      @Transactional: ON
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON
     */
    @Test
    void outerTxOn_Success() {
        String username = "outerTxOn_Success";

        memberService.joinV1(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isPresent();
    }

    /**
     * memberService      @Transactional: ON
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON Exception
     */
    @Test
    void outerTxOn_Fail() {
        String username = "outerTxOn_Fail_로그예외";

        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }

    /**
     * memberService      @Transactional: ON
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON Exception
     */
    @Test
    void recoverException_Fail() {
        String username = "recoverException_Fail_로그예외";

        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        assertThat(memberRepository.find(username)).isEmpty();
        assertThat(logRepository.find(username)).isEmpty();
    }


    /**
     * memberService      @Transactional: ON
     * memberRepository   @Transactional: ON
     * logRepository      @Transactional: ON(REQUIRES_NEW) Exception
     */
    @Test
    void recoverException_Success() {
        String username = "recoverException_Success_로그예외";

        memberService.joinV2(username);

        assertThat(memberRepository.find(username)).isPresent();
        assertThat(logRepository.find(username)).isEmpty();
    }
}
