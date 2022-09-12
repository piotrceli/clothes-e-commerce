package com.junior.company.ecommerce.service;

import com.junior.company.ecommerce.dto.CartResponse;
import com.junior.company.ecommerce.dto.OrderResponse;
import com.junior.company.ecommerce.exception.ResourceNotFoundException;
import com.junior.company.ecommerce.model.Address;
import com.junior.company.ecommerce.model.AppUser;
import com.junior.company.ecommerce.model.Cart;
import com.junior.company.ecommerce.model.CartItem;
import com.junior.company.ecommerce.model.Category;
import com.junior.company.ecommerce.model.Item;
import com.junior.company.ecommerce.model.Order;
import com.junior.company.ecommerce.model.OrderItem;
import com.junior.company.ecommerce.model.Product;
import com.junior.company.ecommerce.model.Role;
import com.junior.company.ecommerce.model.WeatherSeason;
import com.junior.company.ecommerce.repository.CartItemRepository;
import com.junior.company.ecommerce.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.junior.company.ecommerce.mapper.ShoppingMapper.mapCartToCartResponse;
import static com.junior.company.ecommerce.mapper.ShoppingMapper.mapOrdersToOrderResponses;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
class ShoppingServiceImplTest {

    @Mock
    private AppUserService appUserService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ShoppingServiceImpl shoppingService;

    private Role role;
    private Address address;
    private AppUser appUser;
    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(1L)
                .totalValue(0.0)
                .cartItems(new ArrayList<>())
                .build();
        role = Role.builder()
                .id(1L)
                .name("USER")
                .build();
        address = Address.builder()
                .apartmentNumber(101)
                .street("street_name_1")
                .city("city_name_1")
                .country("country_name_1")
                .build();
        appUser = AppUser.builder()
                .id(1L)
                .email("user@email.com")
                .password("password")
                .roles(List.of(role))
                .enabled(true)
                .firstName("user")
                .lastName("user")
                .phoneNumber("100100100")
                .dob(LocalDate.of(1980, 1, 1))
                .address(address)
                .cart(cart)
                .orders(new ArrayList<>())
                .build();
    }

    @Test
    void shouldRetrieveCartView() {

        // given
        given(appUserService.getCurrentUser()).willReturn(appUser);
        CartResponse cartResponse = mapCartToCartResponse(cart);

        // when
        CartResponse result = shoppingService.viewCart();

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(cartResponse);
    }

    @Test
    void shouldAddItemToCart_whenItemNotFoundInCart_givenValidItemIdAndAmount() {

        // given
        Long itemId = 1L;
        Integer amount = 1;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .item(item)
                .amount(amount)
                .build();

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when
        boolean result = shoppingService.addCartItem(itemId, amount);

        // then
        ArgumentCaptor<CartItem> categoryArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        verify(cartItemRepository).save(categoryArgumentCaptor.capture());
        CartItem capturedCartItem = categoryArgumentCaptor.getValue();

        assertThat(capturedCartItem).usingRecursiveComparison().isEqualTo(cartItem);
        assertThat(result).isTrue();
    }

    @Test
    void shouldAddItemToCart_whenItemAlreadyExistInCart_givenValidItemIdAndAmount() {

        // given
        Long itemId = 1L;
        Integer amount = 1;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(amount)
                .build();
        cart.getCartItems().add(cartItem);

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when
        boolean result = shoppingService.addCartItem(itemId, amount);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotAddItemToCart_whenItemAlreadyExistInCart_givenInvalidOrderedAmount() {

        // given
        Long itemId = 1L;
        Integer amount = 10;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(3)
                .build();
        cart.getCartItems().add(cartItem);

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when then
        assertThatThrownBy(() -> shoppingService.addCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ordered amount must not exceed stock quantity of item");
    }

    @Test
    void shouldNotAddItemToCart_givenNegativeOrderedAmount() {

        // given
        Long itemId = 1L;
        Integer amount = -99;
        given(appUserService.getCurrentUser()).willReturn(appUser);
        // when then
        assertThatThrownBy(() -> shoppingService.addCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ordered amount must be minimum 1");
    }

    @Test
    void shouldNotAddItemToCart_givenInvalidItemId() {

        // given
        Long itemId = 0L;
        Integer amount = 1;
        given(appUserService.getCurrentUser()).willReturn(appUser);
        given(itemRepository.findById(anyLong())).willReturn(Optional.empty());

        // when then
        assertThatThrownBy(() -> shoppingService.addCartItem(itemId, amount))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(String.format("Item with id: %s not found", itemId));
    }

    @Test
    void shouldNotAddItemToCart_givenAmountExceedQuantityInStock() {

        // given
        Long itemId = 1L;
        Integer amount = 10;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(5)
                .product(product)
                .build();
        product.setItems(List.of(item));

        given(appUserService.getCurrentUser()).willReturn(appUser);
        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));

        // when then
        assertThatThrownBy(() -> shoppingService.addCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ordered amount must not exceed quantity of item");
    }

    @Test
    void shouldRemoveCartItemFromCart_givenValidItemIdAndAmount() {

        // given
        Long itemId = 1L;
        Integer amount = 1;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        cart.getCartItems().add(cartItem);

        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when
        boolean result = shoppingService.removeCartItem(itemId, amount);

        // then
        assertThat(result).isTrue();
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void shouldNotRemoveCartItemFromCart_givenAmountToDeleteIsHigherThanOrderedAmount() {

        // given
        Long itemId = 1L;
        Integer amount = 10;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(itemId)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        cart.getCartItems().add(cartItem);

        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when then
        assertThatThrownBy(() -> shoppingService.removeCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Amount to delete is higher than actual ordered amount in cart");
    }

    @Test
    void shouldNotRemoveCartItemFromCart_givenInvalidItemId() {

        // given
        Long itemId = 99L;
        Integer amount = 1;
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        cart.getCartItems().add(cartItem);

        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when then
        assertThatThrownBy(() -> shoppingService.removeCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("Item with id: %s not found in cart", itemId));
    }

    @Test
    void shouldNotRemoveCartItemFromCart_givenNegativeAmountToDelete() {

        // given
        Long itemId = 1L;
        Integer amount = -99;
        given(appUserService.getCurrentUser()).willReturn(appUser);
        // when then
        assertThatThrownBy(() -> shoppingService.removeCartItem(itemId, amount))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Amount to delete must be minimum 1");
    }

    @Test
    void shouldPerformCheckout() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        cart.getCartItems().add(cartItem);
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .item(item)
                .amount(cartItem.getAmount())
                .build();
        Order order = Order.builder()
                .id(1L)
                .orderItems(List.of(orderItem))
                .dateOfOrder(LocalDateTime.now())
                .build();
        appUser.getOrders().add(order);

        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when
        boolean result = shoppingService.checkout();

        // then
        assertThat(result).isTrue();
    }

    @Test
    void shouldNotPerformCheckout_whenOrderedAmountExceedQuantityInStock() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Item item = Item.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(99)
                .build();
        cart.getCartItems().add(cartItem);
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .item(item)
                .amount(cartItem.getAmount())
                .build();
        Order order = Order.builder()
                .id(1L)
                .orderItems(List.of(orderItem))
                .dateOfOrder(LocalDateTime.now())
                .build();
        appUser.getOrders().add(order);

        given(appUserService.getCurrentUser()).willReturn(appUser);

        // when then
        assertThatThrownBy(() -> shoppingService.checkout())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Ordered amount must not exceed quantity of item");
    }

    @Test
    void shouldRetrieveViewOfOrders() {

        // given
        Product product = Product.builder()
                .id(1L)
                .name("name")
                .price(10.0)
                .imageUrl("url")
                .description("description")
                .items(new ArrayList<>())
                .build();
        Category category = Category.builder()
                .id(1L)
                .name("coat")
                .weatherSeason(WeatherSeason.SUMMER)
                .products(List.of(product))
                .build();
        product.setCategories(Set.of(category));
        Item item = Item.builder()
                .id(1L)
                .size("L")
                .quantity(10)
                .product(product)
                .build();
        product.setItems(List.of(item));
        CartItem cartItem = CartItem.builder()
                .id(1L)
                .item(item)
                .amount(1)
                .build();
        cart.getCartItems().add(cartItem);
        OrderItem orderItem = OrderItem.builder()
                .id(1L)
                .item(item)
                .amount(cartItem.getAmount())
                .build();
        Order order = Order.builder()
                .id(1L)
                .orderItems(List.of(orderItem))
                .totalValue(cart.getTotalValue())
                .dateOfOrder(LocalDateTime.now())
                .build();
        appUser.getOrders().add(order);

        given(appUserService.getCurrentUser()).willReturn(appUser);
        List<OrderResponse> orderResponses = mapOrdersToOrderResponses(appUser.getOrders());

        // when
        List<OrderResponse> result = shoppingService.viewOrders();

        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(orderResponses);
    }
}