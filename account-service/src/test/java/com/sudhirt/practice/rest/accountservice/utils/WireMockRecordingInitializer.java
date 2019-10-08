package com.sudhirt.practice.rest.accountservice.utils;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WireMockRecordingInitializer {

	private static final String TARGET_FOLDER = "target/generated-test-sources/wiremock";

	public static WireMockServer initialize(int port) throws IOException {
		createDirectories();
		WireMockServer wireMockServer = new WireMockServer(
				options().port(port + 1).usingFilesUnderDirectory(Paths.get(TARGET_FOLDER).toString()));
		wireMockServer.start();
		wireMockServer.startRecording("http://localhost:" + port);
		return wireMockServer;
	}

	private void createDirectories() {
		try {
			Files.createDirectories(Paths.get(TARGET_FOLDER, "mappings"));
			Files.createDirectories(Paths.get(TARGET_FOLDER, "__files"));
		}
		catch (IOException e) {
			throw new RuntimeException("Error occurred while creating wiremock directories ", e);
		}
	}

	public static void teardown(WireMockServer wireMockServer) {
		wireMockServer.stopRecording();
		wireMockServer.stop();
	}

}