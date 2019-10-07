package com.sudhirt.practice.rest.accountservice.utils;

import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.experimental.UtilityClass;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

@UtilityClass
public class WireMockRecordingInitializer {

	public static WireMockServer initialize(int port) throws IOException {
		String tmpFolder = System.getProperty("java.io.tmpdir");
		if (!cleanupIfExists(Paths.get(tmpFolder + "/mappings"))) {
			File mappingsDirectory = new File(tmpFolder, "mappings");
			mappingsDirectory.mkdir();
		}
		WireMockServer wireMockServer = new WireMockServer(
				options().port(port + 1).usingFilesUnderDirectory(tmpFolder));
		wireMockServer.start();
		wireMockServer.startRecording("http://localhost:" + port);
		return wireMockServer;
	}

	public static void teardown(WireMockServer wireMockServer) {
		wireMockServer.stopRecording();
		wireMockServer.stop();
		try {
			// Pause a moment to make sure wiremock server is stopped
			Thread.sleep(1000L);
		}
		catch (InterruptedException var2) {
			throw new RuntimeException(var2);
		}
	}

	private static boolean cleanupIfExists(Path path) throws IOException {
		if (Files.exists(path)) {
			Files.list(path).forEach(p -> {
				try {
					Files.deleteIfExists(p);
				}
				catch (IOException e) {
					throw new RuntimeException("Unable to cleanup mappings directory");
				}
			});
			return true;
		}
		return false;
	}

}