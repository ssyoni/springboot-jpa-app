package jpabook.jpashop;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Test
    @Transactional  // 테스트 케이스에 Transactional 어노테이션이 있으면, 테스트가 끝난 다음 바로 롤백 해버림 그래서 디비에서 데이터 조회 안됨 -> 데이터가 들어가있으면 반복 테스트를 못하기 때문에 ..
    @Rollback(false) // <= 추가해주면 롤백 안됨
    public void testMember() throws Exception {
        //given
        Member member = new Member();
        member.setUsername("memberA");

        //when
        Long saveId = memberRepository.save(member);
        Member findMember = memberRepository.find(saveId);

        //then
        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); //true -> 같은 트랜잭션 안에서 저장하고 조회하면, 즉 같은 영속성 컨텍스트 안에서 아이디값이 같을 경우 동일 엔티티로 식별한다.

    }

}