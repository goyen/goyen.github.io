package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;


    @Test
    public void testJoin() throws Exception {
        //given
        Member member = new Member();
        member.setName("ok!!");
        //when
        Long joinId = memberService.join(member);


        //then
        Assert.assertEquals(member,memberRepository.findOne(joinId));
    }

    @Test(expected = IllegalStateException.class)
    public void testDuplicate() throws Exception {
        //given

        Member member1 = new Member();
        Member member2 = new Member();

        member1.setName("Oh");
        member2.setName("Oh");
        //when

        memberService.join(member1);
        memberService.join(member2);


        //then
        Assert.fail("예외 발생해야함");
    }
}