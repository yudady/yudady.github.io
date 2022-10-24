package tk.tommy.v3;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

/**
 * https://www.gyan.dev/ffmpeg/builds/
 * download ffmpeg.exe
 */
public class FFmpegMediaUtil {

    private static final String FFMPEG_PATH = FFmpegMediaUtil.class.getResource("/ffmpeg.exe").getFile();

    /**
     * "ffmpeg -i video.m4s -i audio.m4s -c:v copy -strict experimental 输出名.mp4"
     * 简易视频处理 -- （cmd(windows): ffmpeg.exe -i test1.mp4 newVideo.avi）
     *
     * @param videoInputPaths 视频文件路径（输入）
     * @param videoOutputPath 转换完成的文件路径（输出）
     */
    public static void videoConvert(List<String> videoInputPaths, String videoOutputPath) {
        // 构建命令
        List<String> command = new ArrayList<>();
        command.add(FFMPEG_PATH);
        videoInputPaths.forEach(input -> {
            command.add("-i");
            command.add(input);
        });
        command.add("-c:v");
        command.add("copy");
        command.add("-strict");
        command.add("experimental");
        command.add(videoOutputPath);
        // 执行操作
        ProcessBuilder builder = new ProcessBuilder(command);
        try {
            ioProcess(builder.start());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void ioProcess(final Process process) {
        try (InputStream errorStream = process.getErrorStream(); BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("[LOG] line = " + line);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}