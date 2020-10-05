package com.file.south.model;

import com.file.south.expection.FileException;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Client extends File {
    private static final String CLIENT_TYPE = "002";

    private String cnpj;
    private String name;
    private String businessArea;

    protected boolean validation(String[] args) {
        return args.length != 4 || !getType().equals(args[0]);
    }

    public Client(String[] args) throws FileException {
        setType(CLIENT_TYPE);
        if(validation(args)) {
            throw new FileException("Error reading client file.");
        }
        setCnpj(args[1]);
        setName(args[2]);
        setBusinessArea(args[3]);
    }
}
