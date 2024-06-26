package com.mega._NY.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mega._NY.item.dto.ItemDTO;
import com.mega._NY.item.entity.Item;
import com.mega._NY.item.entity.ItemCategory;
import com.mega._NY.item.entity.ItemColor;
import com.mega._NY.item.entity.ItemSize;
import com.mega._NY.item.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDTO itemDTO;

    @BeforeEach
    void setUp() {
        itemDTO = new ItemDTO();
        itemDTO.setTitle("Test Item");                       // 상품 제목
        itemDTO.setContent("Test Content");                  // 상품 본문
        itemDTO.setThumbnail(Arrays.asList("test_thumbnail.jpg"));          // 썸네일 이미지 경로
        itemDTO.setDescriptionImage(Arrays.asList("test_description.jpg")); // 제품 상세이미지 경로
        itemDTO.setPrice(1000);                              // 가격
        itemDTO.setDiscountPrice(900);                       // 할인 된 가격
        itemDTO.setDiscountRate(10);                         // 할인율
        itemDTO.setSales(0);                                 // 판매량
        itemDTO.setColor(ItemColor.BLACK);                   // 옷 색상
        itemDTO.setSize(ItemSize.L);                         // 옷 사이즈 (int가 아닌 string) // ex) S , M , L, XL
        itemDTO.setCategory(ItemCategory.TOP);               // 제품 카테고리
    }

    @Test
    void createItem() throws Exception {
        String content = objectMapper.writeValueAsString(itemDTO);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(itemDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(itemDTO.getContent()))
                .andExpect(jsonPath("$.price").value(itemDTO.getPrice()))
                .andExpect(jsonPath("$.discountPrice").value(itemDTO.getDiscountPrice()))
                .andExpect(jsonPath("$.discountRate").value(itemDTO.getDiscountRate()))
                .andExpect(jsonPath("$.color").value(itemDTO.getColor().name()))
                .andExpect(jsonPath("$.size").value(itemDTO.getSize().name()))
                .andExpect(jsonPath("$.category").value(itemDTO.getCategory().name()));

    }

    @Test
    void getItem() throws Exception {
        Item savedItem = itemRepository.save(Item.builder()
                .title(itemDTO.getTitle())
                .content(itemDTO.getContent())
                .thumbnail(itemDTO.getThumbnail())
                .descriptionImage(itemDTO.getDescriptionImage())
                .price(itemDTO.getPrice())
                .discountPrice(itemDTO.getDiscountPrice())
                .discountRate(itemDTO.getDiscountRate())
                .sales(itemDTO.getSales())
                .color(itemDTO.getColor())
                .size(itemDTO.getSize())
                .category(itemDTO.getCategory())
                .build());

        mockMvc.perform(get("/items/{itemId}", savedItem.getItemId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(itemDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(itemDTO.getContent()))
                .andExpect(jsonPath("$.price").value(itemDTO.getPrice()))
                .andExpect(jsonPath("$.discountPrice").value(itemDTO.getDiscountPrice()))
                .andExpect(jsonPath("$.color").value(itemDTO.getColor().name()))
                .andExpect(jsonPath("$.size").value(itemDTO.getSize().name()))
                .andExpect(jsonPath("$.category").value(itemDTO.getCategory().name()));

    }

    @Test
    void updateItem() throws Exception {
        Item savedItem = itemRepository.save(Item.builder()
                .title(itemDTO.getTitle())
                .content(itemDTO.getContent())
                .thumbnail(itemDTO.getThumbnail())
                .descriptionImage(itemDTO.getDescriptionImage())
                .price(itemDTO.getPrice())
                .discountPrice(itemDTO.getDiscountPrice())
                .discountRate(itemDTO.getDiscountRate())
                .sales(itemDTO.getSales())
                .color(itemDTO.getColor())
                .size(itemDTO.getSize())
                .category(itemDTO.getCategory())
                .build());

        ItemDTO updatedItemDTO = new ItemDTO();
        updatedItemDTO.setTitle("Updated Title");
        updatedItemDTO.setContent("Updated Content");
        updatedItemDTO.setPrice(2000);
        updatedItemDTO.setDiscountPrice(1800);
        updatedItemDTO.setDiscountRate(20);
        updatedItemDTO.setColor(ItemColor.WHITE);
        updatedItemDTO.setSize(ItemSize.M);
        updatedItemDTO.setCategory(ItemCategory.DRESS);


        String content = objectMapper.writeValueAsString(updatedItemDTO);

        mockMvc.perform(put("/items/{itemId}", savedItem.getItemId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(updatedItemDTO.getTitle()))
                .andExpect(jsonPath("$.content").value(updatedItemDTO.getContent()))
                .andExpect(jsonPath("$.price").value(updatedItemDTO.getPrice()))
                .andExpect(jsonPath("$.discountPrice").value(updatedItemDTO.getDiscountPrice()))
                .andExpect(jsonPath("$.discountRate").value(updatedItemDTO.getDiscountRate()))
                .andExpect(jsonPath("$.color").value(itemDTO.getColor().name()))
                .andExpect(jsonPath("$.size").value(itemDTO.getSize().name()))
                .andExpect(jsonPath("$.category").value(itemDTO.getCategory().name()));
    }

    @Test
    void deleteItem() throws Exception {
        Item savedItem = itemRepository.save(Item.builder()
                .title(itemDTO.getTitle())
                .content(itemDTO.getContent())
                .thumbnail(itemDTO.getThumbnail())
                .descriptionImage(itemDTO.getDescriptionImage())
                .price(itemDTO.getPrice())
                .discountPrice(itemDTO.getDiscountPrice())
                .discountRate(itemDTO.getDiscountRate())
                .sales(itemDTO.getSales())
                .category((itemDTO.getCategory()))
                .build());

        mockMvc.perform(delete("/items/{itemId}", savedItem.getItemId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/items/{itemId}", savedItem.getItemId()))
                .andExpect(status().isNotFound());
    }
}