package ru.bsuedu.cad.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.CustomerOrder;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CustomerOrderRepository;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceUnitTest {

    @Mock
    private CustomerOrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Customer customer;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Алексей Иванов");
        customer.setAddress("Москва");

        product = new Product();
        product.setProductId(1);
        product.setName("Корм");
        product.setPrice(new BigDecimal("1500"));
        product.setStockQuantity(10);
    }

    @Test
    void createOrder_success() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(CustomerOrder.class))).thenAnswer(inv -> {
            CustomerOrder order = inv.getArgument(0);
            order.setOrderId(100);
            return order;
        });

        CustomerOrder created = orderService.createOrder(1, 1, 2, "NEW", null);

        assertThat(created.getOrderId()).isEqualTo(100);
        assertThat(created.getTotalPrice()).isEqualByComparingTo("3000");
        assertThat(product.getStockQuantity()).isEqualTo(8);
        verify(orderRepository).save(any(CustomerOrder.class));
        verify(productRepository).save(product);
    }

    @Test
    void createOrder_whenCustomerMissing_shouldFail() {
        when(customerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(99, 1, 1, "NEW", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Клиент не найден");
    }

    @Test
    void createOrder_whenNotEnoughStock_shouldFail() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(1, 1, 100, "NEW", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Недостаточно товара");
    }

    @Test
    void createOrder_whenQuantityInvalid_shouldFail() {
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.createOrder(1, 1, 0, "NEW", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Количество");
    }
}
