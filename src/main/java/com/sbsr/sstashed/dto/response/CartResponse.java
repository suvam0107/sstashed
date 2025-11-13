package com.sbsr.sstashed.dto.response;

import com.sbsr.sstashed.model.CartItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private List<CartItem> items;
    private BigDecimal total;
    private Integer itemCount;
}