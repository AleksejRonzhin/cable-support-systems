package ru.rsreu.cable.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class FileUtils {
    public static List<String> getLinesFromFile(MultipartFile file) {
        List<String> args = new ArrayList<>();
        try (Scanner scanner = new Scanner(file.getInputStream())) {
            while (scanner.hasNext()) {
                args.add(scanner.nextLine());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return args;
    }

    public static void saveResultToFile(String result, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String addSuffixToFilename(String sourceFilename, String suffix, String extension) {
        return Objects.requireNonNull(sourceFilename)
                .replace(String.format(".%s", extension), String.format("%s.%s", suffix, extension));
    }
}