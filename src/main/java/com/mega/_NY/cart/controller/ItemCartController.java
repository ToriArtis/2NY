package com.mega._NY.cart.controller;

import com.mega._NY.cart.dto.ItemCartDTO;
import com.mega._NY.cart.entity.Cart;
import com.mega._NY.cart.service.CartService;
import com.mega._NY.cart.service.ItemCartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@CrossOrigin
@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class ItemCartController {

    private final ItemCartService itemCartService;
    private final CartService cartService;

    // 장바구니에 상품 추가
    @PostMapping("/{userId}/{item-id}")
    public ResponseEntity<ItemCartDTO> postItemCart(@PathVariable("userId") Long userId, @Valid @RequestBody ItemCartDTO itemCartDTO,
                                                    @PathVariable("item-id") @Positive Long itemId) {
        Cart cart = cartService.findVerifiedCart(userId);   // 현재 사용자의 Cart를 가져옵니다.
        ItemCartDTO createdItemCart = itemCartService.addItemCart(itemCartDTO, itemId, cart);
        cartService.refreshCart(cart.getCartId());
        return new ResponseEntity<>(createdItemCart, HttpStatus.CREATED);
    }

    // 장바구니 상품 수량 변경
    @PatchMapping("/{userId}/itemcarts/{itemcart-id}")
    public ResponseEntity<ItemCartDTO> upDownItemCart(@PathVariable("userId") Long userId, @PathVariable("itemcart-id") @Positive long itemCartId,
                                                      @RequestParam(value="upDown") int upDown) {
        Cart cart = cartService.findVerifiedCart(userId);   // 현재 사용자의 Cart를 가져옵니다.
        ItemCartDTO updatedItemCart = itemCartService.updownItemCart(itemCartId, upDown);
        cartService.refreshCart(updatedItemCart.getCartId());
        return new ResponseEntity<>(updatedItemCart, HttpStatus.OK);
    }

    // 장바구니 상품 구매 여부 변경
    @PatchMapping("/{userId}/itemcarts/exclude/{itemcart-id}")
    public ResponseEntity<ItemCartDTO> excludeItemCart(@PathVariable("userId") Long userId, @PathVariable("itemcart-id") @Positive long itemCartId,
                                                       @RequestParam(value="buyNow", defaultValue = "false") boolean buyNow) {
        Cart cart = cartService.findVerifiedCart(userId);   // 현재 사용자의 Cart를 가져옵니다.
        ItemCartDTO itemCart = itemCartService.excludeItemCart(itemCartId, buyNow);
        cartService.refreshCart(itemCart.getCartId());
        return new ResponseEntity<>(itemCart, HttpStatus.OK);
    }

    // 장바구니에서 상품 삭제
    @DeleteMapping("/{userId}/itemcarts/{itemcart-id}")
    public ResponseEntity<Void> deleteItemCart(@PathVariable("userId") Long userId, @PathVariable("itemcart-id") @Positive long itemCartId) {
        Cart cart = cartService.findVerifiedCart(userId);   // 현재 사용자의 Cart를 가져옵니다.
        long cartId = itemCartService.deleteItemCart(itemCartId);
        cartService.refreshCart(cartId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
