package com.valet.image_micro.controller;

import com.valet.image_micro.model.Titer;
import com.valet.image_micro.service.FontService;
import com.valet.image_micro.service.VideoService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ImageController {

    @Autowired VideoService videoService;
    @Autowired FontService saveFontService;

    @SneakyThrows
    @GetMapping(value = "/create",
                produces = "video/mp4")
    public @ResponseBody byte[] create(
            @RequestBody @DefaultValue({"{fontType:10}"}) Titer titer){
        System.out.println(titer);
        return videoService.create(titer);
    }

    @PostMapping("/uploadFonts")
    public String uploadFonts(@RequestPart MultipartFile font){
        return saveFontService.save(font);
    }

    @GetMapping("/getAllFonts")
    public List<String> getAllFonts(){
        return saveFontService.getAllFontName();
    }
}
