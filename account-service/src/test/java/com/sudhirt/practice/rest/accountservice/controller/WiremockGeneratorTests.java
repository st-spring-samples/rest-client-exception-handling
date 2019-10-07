package com.sudhirt.practice.rest.accountservice.controller;

import static io.restassured.RestAssured.given;
import java.io.IOException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.sudhirt.practice.rest.accountservice.utils.WireMockRecordingInitializer;
import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, scripts = "classpath:sql/before.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, scripts = "classpath:sql/after.sql")
public class WiremockGeneratorTests {

	private static WireMockServer wireMockServer;

	@LocalServerPort
	private int port;

	@AfterClass
	public static void teardown() {
		WireMockRecordingInitializer.teardown(wireMockServer);
	}

	@Before
	public void initialize() throws IOException {
		if (wireMockServer == null) {
			wireMockServer = WireMockRecordingInitializer.initialize(port);
		}
	}

	@Test
	public void get_all_accounts_should_return_200_when_accounts_exist() throws Exception {
		given().port(port + 1).when().get("/accounts").then().statusCode(200);
	}

	@Test
	public void get_should_throw_404_when_account_number_does_not_exist() throws Exception {
		given().port(port + 1).when().get("/accounts/1234599").then().statusCode(404);
	}

	@Test
	public void get_transactions_should_throw_404_when_account_number_does_not_exist() throws Exception {
		given().port(port + 1).when().get("/accounts/1234599/transactions").then().statusCode(404);
	}

	@Test
	public void post_transaction_should_throw_404_when_account_number_does_not_exist() throws Exception {
		JSONObject jsonObject = new JSONObject().put("transactionDate", "2019-10-01").put("transactionAmount", "200")
				.put("transactionType", "CREDIT");
		given().port(port + 1).contentType("application/json").body(jsonObject.toString()).when()
				.post("/accounts/1234599/transactions").then().statusCode(404);
	}

	@Test
	public void post_transaction_should_return_201_when_account_number_does_not_exist() throws Exception {
		JSONObject jsonObject = new JSONObject().put("transactionDate", "2019-10-02").put("transactionAmount", "200")
				.put("transactionType", "CREDIT");
		given().port(port + 1).contentType("application/json").body(jsonObject.toString()).when()
				.post("/accounts/1234567/transactions").then().statusCode(201);
	}

	@Test
	public void get_account_should_return_an_existing_account() throws Exception {
		given().port(port + 1).when().get("/accounts/1234567").then().statusCode(200);
	}

	@Test
	public void get_transactions_should_return_empty_list_when_transactions_does_not_exist() throws Exception {
		given().port(port + 1).when().get("/accounts/1234568").then().statusCode(200);
	}

	@Test
	public void get_transactions_should_return_non_empty_list_when_transactions_exist() throws Exception {
		given().port(port + 1).when().get("/accounts/1234567").then().statusCode(200);
	}

}