package ru.bsuedu.cad.lab.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import ru.bsuedu.cad.lab.TestJpaConfig;
import ru.bsuedu.cad.lab.entity.Category;
import ru.bsuedu.cad.lab.entity.Customer;
import ru.bsuedu.cad.lab.entity.CustomerOrder;
import ru.bsuedu.cad.lab.entity.Product;
import ru.bsuedu.cad.lab.repository.CategoryRepository;
import ru.bsuedu.cad.lab.repository.CustomerOrderRepository;
import ru.bsuedu.cad.lab.repository.CustomerRepository;
import ru.bsuedu.cad.lab.repository.ProductRepository;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestJpaConfig.class)
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CustomerOrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        categoryRepository.deleteAll();

        Category category = new Category();
        category.setCategoryId(1);
        category.setName("Корма");
        category.setDescription("test");
        categoryRepository.save(category);

        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setName("Тест Клиент");
        customer.setEmail("test@example.com");
        customer.setPhone("+7000");
        customer.setAddress("Адрес");
        customerRepository.save(customer);

        Product product = new Product();
        product.setProductId(1);
        product.setName("Корм тест");
        product.setDescription("desc");
        product.setCategory(category);
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(5);
        productRepository.save(product);
    }

    @Test
    void createOrder_savesToDatabase() {
        CustomerOrder order = orderService.createOrder(1, 1, 2, "NEW", "Адрес 1");

        assertThat(order.getOrderId()).isNotNull();
        assertThat(orderRepository.findById(order.getOrderId())).isPresent();
        assertThat(productRepository.findById(1).orElseThrow().getStockQuantity()).isEqualTo(3);
        assertThat(order.getTotalPrice()).isEqualByComparingTo("200.00");
    }

    @Test
    void createOrder_whenStockTooSmall_shouldFail() {
        assertThatThrownBy(() -> orderService.createOrder(1, 1, 50, "NEW", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Недостаточно товара");

        assertThat(orderRepository.findAll()).isEmpty();
        assertThat(productRepository.findById(1).orElseThrow().getStockQuantity()).isEqualTo(5);
    }
}
