package com.file.south.model;

import com.file.south.expection.FileException;

public class FileFactory {


    private FileFactory() {

    }

    public static File decodeFile(String[] data) throws FileException {
        switch (data[0]) {
            case "001":
                return new Seller(data);
            case "002":
                return new Client(data);
            case "003":
                return new Sale(data);
            default:
                throw new FileException("Error reading data.");
        }
    }
}
