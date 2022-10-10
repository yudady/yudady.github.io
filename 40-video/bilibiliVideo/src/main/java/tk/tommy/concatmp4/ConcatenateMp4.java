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

import tk.tommy.v3.FFmpegMediaUtil;

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
}
