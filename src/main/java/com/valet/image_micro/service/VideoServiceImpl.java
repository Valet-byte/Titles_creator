package com.valet.image_micro.service;

import com.valet.image_micro.model.FrameMaker;
import com.valet.image_micro.model.Titer;
import lombok.SneakyThrows;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import org.apache.commons.io.FileUtils;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {
    @Autowired private FontService fontService;
    @Value("${img_directory}") private String imgDirectory;
    @Value("${video_directory}") private String videoDirectory;
    @Value("${ffmpeg}") private String pathToffmpeg;


    @Override
    @SneakyThrows
    public byte[] create(Titer titer) {
        List<FrameMaker> makers = new ArrayList<>(Runtime.getRuntime().availableProcessors());
        double y = 1080;
        File imges = new File(imgDirectory);
        FileUtils.cleanDirectory(imges);

        titer.setText(titer.getText().stream().map(String::trim).collect(Collectors.toList()));
        Font font = fontService.getFont(titer.getTextSize(), titer.getFontType(), titer.getFont());

        List<List<Pair<Double, String>>> text = new ArrayList<>(titer.getText().size());

        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = image.createGraphics();
        if (titer.isCentring()){
            for (String s : titer.getText()) {
                List<Pair<Double, String>> data = new ArrayList<>(s.split("\n").length);
                for (String s1 : s.split("\n")) {
                    TextLayout tl = new TextLayout(s1, font, g.getFontRenderContext());
                    data.add(Pair.of(tl.getBounds().getWidth() / 2 + titer.getOffset(), s1));
                }
                text.add(data);
            }
        } else {
            for (String s : titer.getText()) {
                List<Pair<Double, String>> data = new ArrayList<>(s.split("\n").length);
                for (String s1 : s.split("\n")) {
                    data.add(Pair.of(titer.getOffset(), s1));
                }
                text.add(data);
            }
        }

        TextLayout tl = new TextLayout("1", font, g.getFontRenderContext());

        double interval = (((tl.getBounds().getHeight() + (titer.getDistance() - tl.getBounds().getHeight())) * text.size()) + y + tl.getBounds().getHeight() + tl.getBounds().getHeight()) / Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            FrameMaker maker = new FrameMaker();
            maker.setText(new ArrayList<>(text));
            maker.setImgDir(imgDirectory);
            maker.setFont(fontService.getFont(titer.getTextSize(), titer.getFontType(), titer.getFont()));
            maker.setSpeed(titer.getSpeed());
            maker.setColor(Color.decode(titer.getColor()));
            maker.setBufferedImage(new BufferedImage(1920, 1080, BufferedImage.TYPE_3BYTE_BGR));
            maker.setDistance(titer.getDistance());
            maker.setPosStart((1080 - (interval * i) - interval) + tl.getBounds().getHeight());
            maker.setPosEnd((1080 - (interval * i)) + tl.getBounds().getHeight());
            maker.setBackgroundColor(Color.decode(titer.getBackgroundColor()));

            makers.add(maker);
        }

        List<Thread> threads = new ArrayList<>(Runtime.getRuntime().availableProcessors());
        for (FrameMaker maker : makers) {
            threads.add(new Thread(maker));
        }

        for (Thread thread : threads) {
            thread.start();
        }

        boolean isAllThreadStop = false;

        while (!isAllThreadStop){

            boolean tmp = true;

            for (FrameMaker maker : makers) {
                if (tmp){
                    tmp = maker.isEnd();
                }
            }

            isAllThreadStop = tmp;

        }

        List<String> files = new LinkedList<>();
        Map<Integer, File> map = Arrays.stream(Objects.requireNonNull(imges.listFiles()))
                .collect(Collectors.toMap(file -> Integer.parseInt(file.getName().split(".jpg")[0]), file -> file));
        SortedSet<Integer> keys = new TreeSet<>(map.keySet());
        for (Integer key : keys) {
            File value = map.get(key);
            files.add(value.getAbsolutePath());
        }
        Collections.reverse(files);

        JpegImagesToMovie.main(files, "file:" + videoDirectory + "test.mov");

        FFmpeg ffmpeg = new FFmpeg(pathToffmpeg);
        FFprobe ffprobe = new FFprobe(pathToffmpeg);
        FFmpegBuilder builder = new FFmpegBuilder();
        builder.setInput( videoDirectory + "test.mov")
                .addOutput(videoDirectory + "1.mp4")
                .setVideoCodec("libx264")
                .setVideoBitRate(200_000)
                .setVideoFrameRate(30, 1)
                .done();

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
        executor.createJob(builder).run();

        File file = new File(videoDirectory + "1.mp4");

        InputStream stream = Files.newInputStream(file.toPath());
        return stream.readAllBytes();
    }
}
