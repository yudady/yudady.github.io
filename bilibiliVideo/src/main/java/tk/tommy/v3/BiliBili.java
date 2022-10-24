package tk.tommy.v3;

import org.apache.commons.io.FileUtils;
import tk.tommy.v3.model.VideoItem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static tk.tommy.v3.OneVideo.createVideoInformation;

public class BiliBili {
    private static final String DEFAULT_OUT_FOLDER = "C:/Users/yu_da/Desktop/";
    public static final String VIDEO_FOLDER = "C:/Users/yu_da/Downloads/bilibiliViedo/";

    public static void main(String[] args) throws Exception {

        Files.list(Paths.get(VIDEO_FOLDER))
                .filter(path -> String.valueOf(path.getFileName()).matches("-?\\d+(\\.\\d+)?"))
                .map(path -> path.getFileName().toString())
                .sorted()
                .forEach(video -> {
                    System.out.println(video);
                    List<VideoItem> videoInformation = createVideoInformation(VIDEO_FOLDER, video);
                    videoInformation.forEach(BiliBili::createOneVideo);
                    deleteOneVideo(video);
                });
    }

    private static void deleteOneVideo(String video) {
        try {
            FileUtils.deleteDirectory(Paths.get(VIDEO_FOLDER, video).toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }


    private static void createOneVideo(VideoItem videoItem) {
        String tmpMp4 = "C:/Users/yu_da/Desktop/video.mp4";
        List<String> inputs = List.of(videoItem.video, videoItem.audio);
        FFmpegMediaUtil.videoConvert(inputs, tmpMp4);

        Path fileToMovePath = Paths.get(tmpMp4);
        String fileName = String.format("%s-%s.mp4", String.format("%03d", videoItem.index), videoItem.name);
        Path targetPath = Paths.get(DEFAULT_OUT_FOLDER, videoItem.title, fileName);
        targetPath.getParent().toFile().mkdirs();
        try {
            Files.move(fileToMovePath, targetPath);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
