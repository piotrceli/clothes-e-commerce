package com.junior.company.ecommerce.mapper;

import com.junior.company.ecommerce.dto.ItemRequest;
import com.junior.company.ecommerce.dto.ItemResponse;
import com.junior.company.ecommerce.model.Item;

import static com.junior.company.ecommerce.mapper.constant.SharedConstant.EMPTY_ID;

public class ItemMapper {

    public static Item mapItemRequestToItemCreate(ItemRequest itemRequest){
        return Item.builder()
                .id(EMPTY_ID)
                .size(itemRequest.getSize())
                .quantity(itemRequest.getQuantity())
                .build();
    }

    public static Item mapItemRequestToItemUpdate(ItemRequest itemRequest){
        return Item.builder()
                .id(itemRequest.getId())
                .size(itemRequest.getSize())
                .quantity(itemRequest.getQuantity())
                .build();
    }

    public static ItemResponse mapItemToItemResponse(Item item) {
        return ItemResponse.builder()
                .id(item.getId())
                .size(item.getSize())
                .quantity(item.getQuantity())
                .product(ProductMapper.mapProductToProductResponse(item.getProduct()))
                .build();
    }
}
