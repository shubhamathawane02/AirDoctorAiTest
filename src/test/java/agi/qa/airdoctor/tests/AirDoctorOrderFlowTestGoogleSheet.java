package agi.qa.airdoctor.tests;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import agi.qa.airdoctor.base.BaseTest;
import agi.qa.airdoctor.constants.AppConstants;
import agi.qa.airdoctor.pages.AirDoctorOrderFlowPage;
import agi.qa.airdoctor.pages.AirDoctorStagePage;
import agi.qa.airdoctor.utils.SheetUtil;
import agi.qa.airdoctor.utils.GeminiUtil;
import org.openqa.selenium.By;

import java.util.HashMap;

public class AirDoctorOrderFlowTestGoogleSheet extends BaseTest {

	Map<String, By> locatorMap = new HashMap<>();
	private AirDoctorOrderFlowPage airDoctorOrderFlowPage;

	@BeforeClass()
	public void affilatePageSetup() throws InterruptedException, IOException, GeneralSecurityException {
		System.out.println("Calling gemini api for generating the data");
		airDoctorOrderFlowPage = new AirDoctorOrderFlowPage(driver);
		String prompt = "give me 28 fictional addresses in which each fields  street1, street2,  city state and zip (total 5 fields) should be separated by comma  and it should contains following states : Alabama Alaska Arizona Arkansas California Colorado Connecticut Delaware District Columbia Florida Georgia Hawaii Idaho Illinois Indiana Iowa Kansas Kentucky Louisiana Maine Maryland Massachusetts Michigan Minnesota Mississippi Missouri Montana Nebraska Nevada New Hampshire New Jersey New Mexico New York North Carolina North Dakota Ohio Oklahoma Oregon Pennsylvania Rhode South Carolina South Dakota Tennessee Texas Utah Vermont Virginia Washington West Virginia Wisconsin Wyoming in json format";
		airDoctorOrderFlowPage.AddGeminiToGoogleSheet(prompt);
	}

	private By getLocator(String key) {
		return locatorMap.get(key);
	}

	public void clickElement(String key) {
		System.out.println(getLocator(key));
	}

	@DataProvider
	public Object[][] getDataFromSheet() throws IOException, GeneralSecurityException {
		System.out.println("Reading the sheet : " + AppConstants.GOOGLE_SHEET_NAME);
		return SheetUtil.readSheet(AppConstants.GOOGLE_SHEET_NAME);
	}

	int count = 0;

	@Test(dataProvider = "getDataFromSheet")
	public void placeOrder(ITestContext testContext, String modelName, String productQuantity, String secondModel,
			String secondModelQuantity, String email, String firstName, String lastName, String billingAddress1,
			String billingAddress2, String city, String state, String zipCode, String phone, String upsell1,
			String preUpsellSubtotalExp, String preUpsellShippingExp, String preUpsellTaxExp, String preUpsellTotalExp,
			String preUpsellSubtotalAct, String preUpsellShippingAct, String preUpsellTaxAct, String preUpsellTotalAct,
			String orderNumber) throws InterruptedException, Exception {

		try {
			airddoctorstg = loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
			airddoctorstg.clearCart();
			airddoctorstg = loginPage.clickShopNow();
			softAssert = new SoftAssert();
			int currentTest = airddoctorstg.testMe(testContext);

			Thread.sleep(3000);
			System.out.println(modelName);
			airddoctorstg.selectModel(modelName, productQuantity, secondModel, secondModelQuantity);

			Thread.sleep(5000);
			airddoctorstg.checkout(email, firstName, lastName, billingAddress1, billingAddress2, city, state, zipCode,
					phone);
			String cartText = cartPage.getCartText();
			System.out.println("Cart Text : " + cartText);

			Thread.sleep(15000);
			// airddoctorstg.selectUpsell(upsell1);

			Thread.sleep(5000);
			// airddoctorstg.getThankYouPageURL();

			Map<String, String> productActDetailsMap = airddoctorstg.getorderdetails();

			softAssert.assertEquals(productActDetailsMap.get("subtotal"), preUpsellSubtotalExp);
			System.out.println("=============================================================");
			System.out.println("Expected Subtotal: " + preUpsellSubtotalExp + " || Actual Subtotal: "
					+ productActDetailsMap.get("subtotal"));

			softAssert.assertEquals(productActDetailsMap.get("Shipping"), preUpsellShippingExp);
			System.out.println("Expected Shipping: " + preUpsellShippingExp + " || Actual Shipping: "
					+ productActDetailsMap.get("Shipping"));

			softAssert.assertEquals(productActDetailsMap.get("tax"), preUpsellTaxExp);
			System.out
					.println("Expected Tax: " + preUpsellTaxExp + " || Actual Tax: " + productActDetailsMap.get("tax"));

			softAssert.assertEquals(productActDetailsMap.get("total"), preUpsellTotalExp);
			System.out.println(
					"Expected Total: " + preUpsellTotalExp + " || Actual Total: " + productActDetailsMap.get("total"));
			System.out.println("=============================================================");

			// airddoctorstg.writeExcel(productActDetailsMap.get("subtotal"),
			// productActDetailsMap.get("Shipping"),
			// productActDetailsMap.get("tax"), productActDetailsMap.get("total"),
			// productActDetailsMap.get("OrderID"), currentTest);

			count += 1;
			try {
				System.out.println("Calling write sheet data : ");
				String sheetName = "Testing";
				String subtotal = productActDetailsMap.get("subtotal");
				String shipping = productActDetailsMap.get("Shipping");
				String tax = productActDetailsMap.get("tax");
				String total = productActDetailsMap.get("total");
				String orderId = productActDetailsMap.get("OrderID");
				SheetUtil.writeSheet(sheetName, subtotal, shipping, tax, total, orderId, currentTest);
				System.out.println("Done with writing sheet data : ");
			} catch (IOException | GeneralSecurityException e) {
				e.printStackTrace();
				System.out.println("Error here while writing the sheet!");
			}
			softAssert.assertAll();
		} finally {
			tearDown();
			setup();
		}
	}

}
