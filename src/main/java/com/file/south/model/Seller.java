package com.file.south.model;

import com.file.south.expection.FileException;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Seller extends File {

    private static final String SELLER_TYPE = "001";


    private String cpf;
    private String name;
    private BigDecimal salary;

    protected boolean validation(String[] args) {
        return args.length != 4 || !getType().equals(args[0]);
    }

    public Seller(String[] args) throws FileException {
        setType(SELLER_TYPE);
        if (validation(args)) {
            throw new FileException("Error for reading data.");
        }

        setCpf(args[1]);
        setName(args[2]);
        setSalary(new BigDecimal(args[3]));
    }
}
