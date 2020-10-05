package com.file.south.service;

import com.file.south.expection.FileException;
import com.file.south.model.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Log4j2
public class ProcessFileService {
    @Value("${sourceFolder}")
    private String sourceFolder;
    @Value("${replacedFilename}")
    private String replacedFilename;

    @Value("${regexFileName}")
    private String regexFileName;

    @Value("${destinationFolder}")
    private String destinationFolder;

    public void processFile(String fileName) {
        if (fileName != null && fileName.endsWith(".dat")) {
            List<File> fileList = loadFile(fileName);
            generateReport(fileName, getNumberOfClients(fileList), getNumberOfSellers(fileList), getMostExpensiveSale(fileList), getWorstSeller(fileList));
        }
    }

    public long getNumberOfClients(List<File> fileList) {
        return fileList.stream().filter(item -> item instanceof Client).count();
    }

    public long getNumberOfSellers(List<File> fileList) {
        return fileList.stream().filter(item -> item instanceof Seller).count();
    }

    public String getMostExpensiveSale(List<File> fileList) {
        Comparator<Sale> comparator = Comparator.comparing(Sale::getTotalSale);
        Sale sale = fileList.stream().filter(item -> item instanceof Sale).map(item -> (Sale) item).max(comparator)
                .orElse(null);
        return sale != null ? sale.getSaleId() : "No sell for file list";
    }

    public String getWorstSeller(List<File> dataList) {
        Map<String, BigDecimal> saleBySeller = dataList.stream().filter(item -> item instanceof Sale)
                .map(item -> (Sale) item)
                .collect(Collectors.groupingBy(Sale::getSalesman, Collectors.mapping(Sale::getTotalSale, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        File listOfSellers = dataList.stream()
                .filter(item -> item instanceof Seller && !saleBySeller.containsKey(((Seller) item).getName()))
                .findFirst().orElse(null);
        if (listOfSellers != null) {
            Seller sellerWithoutSale = (Seller) listOfSellers;
            return sellerWithoutSale.getName();
        }

        return saleBySeller.entrySet().stream().min(Map.Entry.comparingByValue()).get().getKey();
    }

    public List<File> loadFile(String fileName) {
        List<File> list = new ArrayList<>();
        Path filePath = resolvePathToFile(fileName);
        try (Stream<String> stream = fileReader(filePath)) {
            list = stream
                    .filter(line -> (line.startsWith("001") || line.startsWith("002") || line.startsWith("003")))
                    .map(this::decode).collect(Collectors.toList());

        } catch (IOException e) {
        }

        return list;
    }

    protected void generateReport(String fileName, long countClients, long countSellers, String expensiveSale, String wrostSeller) {
        Path destinationPath = resolveDestinationReport(fileName);
        try (BufferedWriter writer = fileWrite(destinationPath)) {
            writer.write("Quantidade de clientes no arquivo de entrada: ".concat(Long.toString(countClients)));
            writer.newLine();
            writer.write("Quantidade de vendedor no arquivo de entrada: ".concat(Long.toString(countSellers)));
            writer.newLine();
            writer.write("ID da venda mais cara: ".concat(expensiveSale));
            writer.newLine();
            writer.write("O pior vendedor: ".concat(wrostSeller));
        } catch (IOException e) {
            log.warn("error generating report");
        }
    }

    protected Path resolveDestinationReport(String fileName) {
        String fileDestinationName = fileName.replaceAll(regexFileName, replacedFilename);
        return Paths.get(destinationFolder).resolve(fileDestinationName);
    }

    protected Path resolvePathToFile(String fileName) {
        return Paths.get(sourceFolder).resolve(fileName);
    }

    protected Stream<String> fileReader(Path filePath) throws IOException {
        return Files.lines(filePath);
    }

    protected File decode(String line) {
        try {
            String regex = "(?=ç[0-9A-Z\\[])ç";
            String[] split = line.split(regex);
            return FileFactory.decodeFile(split);
        } catch (FileException e) {
            log.warn("error decoding file");

        }
        return null;
    }

    protected BufferedWriter fileWrite(Path destinationPath) throws IOException {
        return Files.newBufferedWriter(destinationPath);
    }
}
