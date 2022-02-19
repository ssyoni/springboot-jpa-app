package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
/*
* 사용자가 물건을 주문할 때 생성되는 엔티티
* 사용자(member), 주문 상품(orderItem), 주문(delivery) 와 참조관계를 갖는다.
* 비즈니스 로직 : 주문취소기능 -> orderStatus : CANCEL , 주문 상품의 재고수량 정정(OrderItem.cancel -> Item.removeStock)
* 조회로직 : 전체 주문가격 조회 -> OrderItem 에서 총 금액을 가져와서 totalPrice 에 더해줌
* */
@Entity
@Table(name = "orders")
@Getter @Setter
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member; //주문회원

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    // java8 에서는 hibernate 가 알아서 매핑 해줌
    private LocalDateTime orderDate; //주문시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status; //주문상태 [ORDER, CANCEL]

    //==연관관계 메서드==//
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this); // 양방향에 세팅
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //==생성 메서드==//
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for (OrderItem orderItem : orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER); // 처음엔 무조건 주문상태로 고정
        order.setOrderDate(LocalDateTime.now()); // 현재시간으로 고정
        return order;
    }

    //==비즈니스 로직==//
    /**
     * 주문 취소
     * */
    public void cancel(){
        if (delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송완료된 상품은 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCEL); // 취소상태로 변경
        for (OrderItem orderItem : orderItems){ // 주문상품의 재고 수정해주기
            orderItem.cancel();
        }
    }

    //==조회 로직==//
    /** 전체 주문 가격 조회 */
    public int getTotalPrice(){
        int totalPrice = orderItems.stream().mapToInt(OrderItem::getTotalPrice).sum();
        /*for (OrderItem orderItem:orderItems){
            totalPrice += orderItem.getTotalPrice(); // 상품 가격 * 주문수량

        }*/
        // 상품 가격 * 주문수량
        return totalPrice;
    }
}
