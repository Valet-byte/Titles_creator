package com.valet.image_micro.service;

import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.util.List;

public interface FontService {
    Font getFont(float size, int type, String font);
    List<String> getAllFontName();
    public String save(MultipartFile font);
}
