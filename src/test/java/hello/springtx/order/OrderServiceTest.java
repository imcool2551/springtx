package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@SpringBootTest
class OrderServiceTest {

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    void order() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setUsername("정상");

        orderService.order(order);

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("완료");
    }

    @Test
    void runtimeException() throws NotEnoughMoneyException {
        Order order = new Order();
        order.setUsername("예외");

        assertThatThrownBy(() -> orderService.order(order))
                .isInstanceOf(RuntimeException.class);

        assertThat(orderRepository.findById(order.getId())).isEmpty();
    }

    @Test
    void businessException() {
        Order order = new Order();
        order.setUsername("잔고부족");

        try {
            orderService.order(order);
        } catch (NotEnoughMoneyException e) {
            log.info("고객에게 잔고 부족을 알리고 별도의 계좌로 입금하도록 안내");
        }

        Order findOrder = orderRepository.findById(order.getId()).get();
        assertThat(findOrder.getPayStatus()).isEqualTo("대기");
    }
}
