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
import agi.qa.airdoctor.utils.SheetUtil;
import org.openqa.selenium.By;

import java.util.HashMap;

public class AirDoctorOrderFlowTestWithXpath extends BaseTest {

	Map<String, By> locatorMap = new HashMap<>();

	@BeforeClass()
	public void affilatePageSetup() throws InterruptedException {

	}

	@DataProvider
	public Object[][] getDataFromSheet() throws IOException, GeneralSecurityException {

		// List<List<Object>> values = SheetUtil.readSheet("xpaths");

		// for (List<Object> row : values) {
		// String key = row.get(0).toString();
		// String xpath = row.get(1).toString();

		// By locator = By.xpath(xpath);
		// locatorMap.put(key, locator);
		// }

		// System.out.println("Reading the sheet 1");

		// for (List<Object> row : values) {
		// System.out.println("Locator : " + row.get(0).toString() + " -> xpath : " +
		// row.get(1).toString());
		// }

		// // for (Object[] row : object) {
		// // for (Object data : row) {
		// // if (data != null) {
		// // String stringValue = data.toString();
		// // System.out.println(stringValue);
		// // }
		// // }
		// // }

		// System.out.println("Calling the click element method !");

		// clickElement("buyAD3500withAD5500increasequantitybtn");

		// // List<List<Object>> object = SheetUtil.readSheet("Testing");
		// List<List<Object>> object = SheetUtil.readSheet("xpaths");
		// System.out.println("Reading the sheet!");

		// int numRows = object.size();
		// int numCols = object.get(0).size(); // Assuming all rows have the same number
		// of columns

		// Object[][] objectArray = new Object[numRows][numCols];

		// for (int i = 0; i < numRows; i++) {
		// List<Object> row = object.get(i);
		// for (int j = 0; j < numCols; j++) {
		// objectArray[i][j] = row.get(j).toString();
		// }
		// }
		// return objectArray;

		// List<List<Object>> object = SheetUtil.readSheet("xpaths");
		// System.out.println("Reading the sheet!");

		// int numRows = object.size();
		// int numCols = object.get(0).size(); // Assuming all rows have the same number
		// of columns

		Object[][] objectArray = SheetUtil.readSheet("xpaths");

		for (Object[] row : objectArray) {
			for (Object element : row) {
				System.out.println(element);
			}
			System.out.println();
		}
		return objectArray;

	}

	// // Optionally, you can print or process the objectArray here

	// return objectArray;
	// }

	// ======>
	// llllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllllll

	//// =====> following function extract the data from google sheet api of
	//// checkout page

	// public Object[][] getDataFromSheet() throws IOException,
	// GeneralSecurityException {
	// List<List<Object>> values = SheetUtil.readSheet("Testing");
	// System.out.println("Reading the sheet!");

	// Object[][] data = new Object[values.size()][values.get(0).size()];
	// for (int i = 0; i < values.size(); i++) {
	// for (int j = 0; j < values.get(i).size(); j++) {
	// data[i][j] = values.get(i).get(j);
	// // System.out.println(data[i][j]);
	// }
	// }

	// return data;
	// }

	// =======> =========================> ================================>

	private By getLocator(String key) {
		return locatorMap.get(key);
	}

	public void clickElement(String key) {
		System.out.println(getLocator(key));
	}

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

			softAssert.assertAll();
		} finally {
			tearDown();
			setup();
		}
	}

}
