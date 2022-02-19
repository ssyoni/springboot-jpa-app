package jpabook.jpashop.domain;

import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.FetchType.*;
/*
* 주문된 상품을 관리하는 엔티티
* 상품(Item), 주문(Order) 와 참조관계를 갖는다.
* 주문이 들어와서 OrderItem이 생성될 때, 해당 주문 상품의 재고 수량을 주문 수량만큼 깎아줘야한다.
* 비즈니스 로직 -> 주문 취소시 주문 수량만큼 재고 늘려줌 품
* 조회 로직 -> 주문상품 전체 가격 조회 orderPrice * count
* */
@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice; //주문가격
    private int count; //주문수량

    /*
    protected OrderItem(){
        //생성하는 방식을 누구는 createOrderItem메소드를 사용하고 누구는 new 로 생성할 수 있음.
        // 이렇게 되면 유지보수성이 안좋아짐. 중구난방 코드가 된다.
        // => protected로 기본 생성자 만들어 놓으면 외부에서 new ~~방식으로 생성자 호출할 수 없게 됨.
    }
    ==> @NoArgsConstructor(access = AccessLevel.PROTECTED)
    ==> 코드를 제약하는 방식으로 짜는게 좋은 설계가 될 수 있다.
    * */

    //==생성 메서드==//
    public static OrderItem createOrderItem(Item item, int orderPrice, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 주문이 들어온 만큼 재고를 까줘야함
        item.removeStock(count);
        return orderItem;
    }

    //== 비즈니스 로직 ==//
    /**
     * 주문 취소
     * */
    public void cancel() {
        getItem().addStock(count); // 주문 취소 시 주문 수량만큼 재고를 늘려준다.
    }

    //==조회로직==//
    /**
     * 주문상품 전체 가격 조회
     * */
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
