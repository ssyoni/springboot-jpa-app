package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class) // Junit 실행할 때 스프링이랑 엮어서 실행하겠다. 스부 서버 띄움
@SpringBootTest // 스프링부트를 띄운 상태에서 테스트하려면 있어야함 없으면 Autowired 실패함
@Transactional // 기본적으로 롧백 하게 함
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("Park");

        //when
        Long id = memberService.join(member);

        //then
        assertEquals(member, memberRepository.findOne(id));
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("Kim");

        Member member2 = new Member();
        member2.setName("Kim");

        //when
        memberService.join(member1);
        memberService.join(member2);
        /*
        try{
            memberService.join(member2); // 예외 발생해야함 --> 같은 이름...
        }catch (IllegalStateException e){
            return;
        }
        */


        //then
        fail("예외가 발생해야함...");
    }
}