package tk.tommy.v3;

import org.json.JSONObject;
import tk.tommy.v3.model.VideoItem;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Charsets.UTF_8;

public class OneVideo {


    public static void main(String[] args) {
        String videoFolder = "C:/Users/yu_da/Downloads/bilibiliViedo/";
        String child = "289840142";
        createVideoInformation(videoFolder, child).forEach(System.out::println);

    }

    public static List<VideoItem> createVideoInformation(String videoFolder, String child) {
        Path path = Paths.get(videoFolder, child);
        return Arrays.stream(path.toFile().list())
                .map(folderName -> toVideoItem(path.toString(), folderName))
                .sorted(Comparator.comparingInt(it -> it.index))
                .collect(Collectors.toList());
    }

    private static VideoItem toVideoItem(String basePath, String folderName) {
        try {
            Path path = Paths.get(basePath, folderName);
            String source = Files.readString(Paths.get(path.toString(), "entry.json"), UTF_8);
            System.out.println(source);
            JSONObject jsonObject = new JSONObject(source);
            String typeTag = jsonObject.getString("type_tag");

            String title = jsonObject.getString("title").replaceAll("\\*", "")
                    .replaceAll("\\|", "")
                    .replaceAll("\\.", "");
            JSONObject pageData = jsonObject.getJSONObject("page_data");
            String part = pageData.getString("part");
            int page = pageData.getInt("page");

            VideoItem to = new VideoItem();
            to.path = path.toString();
            to.title = title;
            to.name = part;
            to.index = page;
            to.video = Paths.get(path.toString(), typeTag, "video.m4s").toString();
            to.audio = Paths.get(path.toString(), typeTag, "audio.m4s").toString();
            return to;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }
}
