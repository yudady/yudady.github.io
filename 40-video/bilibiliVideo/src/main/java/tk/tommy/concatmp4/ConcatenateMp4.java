package tk.tommy.concatmp4;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.ProcessFunction;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import tk.tommy.v3.FFmpegMediaUtil;


/**
 * $ for f in *.mp4; do echo "file '$f'" >> videos.txt; done
 *
 * $ ffmpeg -f concat -i videos.txt -c copy output9.mp4
 */
public class ConcatenateMp4 {
	static Path path = Paths.get("D:\\Downloads\\1\\1\\");

	private static final String FFMPEG_PATH = FFmpegMediaUtil.class.getResource("/ffmpeg.exe").getFile();

	public static void main(String[] args) throws IOException {

		File[] files = path.toFile().listFiles();
		String targetFileName = "merge-" + files[0].getName();
		String names = Arrays.stream(files)
				.filter(f -> f.getName().contains(".mp4"))
				.map(File::getAbsolutePath)
				.sorted()
				.collect(Collectors.joining("|"));


		// 构建命令
		List<String> command = new ArrayList<>();
		command.add(FFMPEG_PATH);
		command.add("-i");
		command.add("concat:" + names);
		command.add("-c");
		command.add("copy");
		command.add(Path.of(path.toString(), targetFileName).toString());
		// 执行操作
		ProcessBuilder builder = new ProcessBuilder(command);
		try {
			ioProcess(builder.start());
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	// ./ffmpeg -f concat -safe 0 -i fileList.txt -c copy mergedVideo.mp4

	private static void ioProcess(final Process process) {
		try (InputStream errorStream = process.getErrorStream(); BufferedReader br = new BufferedReader(new InputStreamReader(errorStream));) {
			String line;
			while ((line = br.readLine()) != null) {
				System.out.println("[LOG] line = " + line);
			}
		}
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}


	public void testExample6() throws IOException {
		FFmpeg ffmpeg = new FFmpeg(this.getClass().getClassLoader().getResource("/ffmpeg.exe").toString());
		FFprobe ffprobe = new FFprobe("/usr/local/Cellar/ffmpeg/4.1/bin/ffprobe");

		FFmpegBuilder builder =
				new FFmpegBuilder()
						.addInput("image%03d.png")
						.addOutput("output.mp4")
						.setVideoFrameRate(FFmpeg.FPS_24)
						.done();

		String expected = "ffmpeg -y -v error -i image%03d.png -r 24/1 output.mp4";
		String actual = Joiner.on(" ").join(ffmpeg.path(builder.build()));

		System.out.println(expected.equals(actual));

		FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
		executor.createJob(builder).run();

	}
}
