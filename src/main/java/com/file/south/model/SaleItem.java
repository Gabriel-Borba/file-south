package com.file.south.model;

import com.file.south.expection.FileException;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SaleItem {

    private String itemId;
    private int quantity;
    private BigDecimal priceItem;
    private BigDecimal totalItem;

    protected boolean validation(String[] itemData) {
        return itemData.length == 3;
    }

    public SaleItem(String item) throws FileException {
        String[] itemData = decode(item);

        if (!validation(itemData)) {
            throw new FileException("Item not valid");
        }

        setItemId(itemData[0]);
        setQuantity(Integer.parseInt(itemData[1]));
        setPriceItem(new BigDecimal(itemData[2]));

        setTotalItem(getPriceItem().multiply(new BigDecimal(quantity)));
    }

    private String[] decode(String item) {
        return item.replaceAll("(\\[|\\])", "").split("-");
    }
}
