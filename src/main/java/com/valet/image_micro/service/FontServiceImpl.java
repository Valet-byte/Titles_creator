package com.valet.image_micro.service;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FontServiceImpl implements FontService {
    @Value("${font_directory}") private String fonts;

    @Override
    @SneakyThrows
    public Font getFont(float size, int type, String font) {
        File file = new File(fonts + font + ".ttf");
        System.out.println(fonts + font + ".ttf");
        return Font.createFont(type, file).deriveFont(size);
    }

    @Override
    public List<String> getAllFontName() {
        File file = new File(fonts);
        return Stream.of(Objects.requireNonNull(file.listFiles())).map(File::getName).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public String save(MultipartFile font) {
        File file = new File(fonts + font.getOriginalFilename());
        font.transferTo(file);
        return font.getOriginalFilename();
    }
}
