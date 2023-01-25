package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void testOrder() throws Exception {
        //given
        Member member = createMember();

        Book book = createBook();

        //when
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);
        assertEquals("상품상태는 ORDER", OrderStatus.ORDER,getOrder.getStatus());
        assertEquals("ㅜ문한 상품 종류가 정확해야 한다.", 1,getOrder.getOrderItems().size());
        assertEquals("주문 가격은 가격 * 수량이다", 10000 * orderCount, getOrder.getTotalPrice());
        assertEquals("주문한 만큼 수량이 줄어야 한다. ", 8, book.getStockQuantity());

    }

    private Book createBook() {
        Book book = new Book();
        book.setName("haha");
        book.setPrice(10000);
        book.setStockQuantity(10);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(member);
        return member;
    }

    @Test
    public void testCancel() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("주문 취소시 상태는 CANCLE",OrderStatus.CANCEL, getOrder.getStatus());
        assertEquals("주문이 취소 복구",10,book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void testOverQuantity() throws Exception {
        //given
        Member member = createMember();
        Book book = createBook();
        int orderCount = 11;


        //when

        orderService.order(member.getId(), book.getId(), orderCount);
        //then
        fail("제고 수량 부족예외가 발생 해야 합니다 ");
    }
}