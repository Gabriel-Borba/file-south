package com.file.south.model;

import com.file.south.expection.FileException;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class Sale extends File {
    private static final String SALE_TYPE = "003";

    private String saleId;
    private String salesman;
    private List<SaleItem> items;
    private BigDecimal totalSale = new BigDecimal(0);

    protected boolean validation(String[] args) {
        return args.length != 4 || !getType().equals(args[0]);
    }

    public Sale(String[] args) throws FileException {
        setType(SALE_TYPE);
        if (validation(args)) {
            throw new FileException("Error for reading data ");
        }
        setSaleId(args[1]);
        setItems(decodeSaleList(args[2]));
        setSalesman(args[3]);
    }

    private List<SaleItem> decodeSaleList(String itemsList) {
        return Arrays.stream(itemsList.split(",")).map(this::buildItemValue).collect(Collectors.toList());
    }

    private SaleItem buildItemValue(String itemData) {
        try {
            SaleItem saleItem = new SaleItem(itemData);
            setTotalSale(getTotalSale().add(saleItem.getTotalItem()));
            return saleItem;
        } catch (FileException e) {
        }
        return null;
    }
}
