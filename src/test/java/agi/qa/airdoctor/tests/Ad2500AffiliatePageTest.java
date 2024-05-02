package agi.qa.airdoctor.tests;

import java.io.IOException;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import agi.qa.airdoctor.base.BaseTest;
import agi.qa.airdoctor.constants.AppConstants;
import agi.qa.airdoctor.utils.ExcelUtil;

public class Ad2500AffiliatePageTest extends BaseTest {
	
	WebDriver driver;
	

	//@BeforeClass()
	public void affilatePageSetup() throws InterruptedException {
	
		
		//affiliatePage = loginPage.clickShopNow();
		//affiliatePage = loginPage.doLogin(prop.getProperty("username"),prop.getProperty("password"));
		//softAssert.assertEquals(loginPage.getLoginSuccessText(),AppConstants.LOGIN_SUCCESS_TEXT); 
		//pdpPage= loginPage.clickBuyNow();
		 
	}
	
	/*
	 * @DataProvider 
	 * public Object[][] getDataFromExcel() { 
	 * return ExcelUtil.getTestData(AppConstants.PRODUCT_SHEET_NAME); }
	 */
	
	@DataProvider
	public Object[][] getAffiliateLinkFromExcel() {
		return ExcelUtil.getTestData(AppConstants.AFFILIATE_LINK);
	}

	/*
	 * @Test public void ProductDispalyPageTitleTest() { String pdpTitle =
	 * pdpPage.getProductDisplayPageTitle(); Assert.assertEquals(pdpTitle,
	 * AppConstants.PRODUCT_PAGE_TITLE); }
	 */
	
	/*
	 * @Test public void PurifierPageTitleTest() { String purifierTitle =
	 * affiliatePage.getPurifierPageTitle(); Assert.assertEquals(purifierTitle,
	 * AppConstants.AD_AFFILIATE_PURIFIER_PAGE_TITLE); }
	 */
	
	/*
	 * @Test public void LandingPageBannerTest() { String bannerText =
	 * affiliatePage.getBannerText(); Assert.assertEquals(bannerText,
	 * AppConstants.AD_AFFILIATE_PAGE_BANNER_TEXT); }
	 */
	
	
	@Test(dataProvider="getAffiliateLinkFromExcel")
	public void AffiliateLinkTest(String url) throws Exception {
	//setup();
	affiliatePage = affiliatePage.getaffiliateURL(url);
	Thread.sleep(3000);
	affiliatePage.clickShopNow();
	Thread.sleep(3000);
	//String bannerText = affiliatePage.getBannerText();
	//softAssert.assertEquals(bannerText, AppConstants.AD_AFFILIATE_PAGE_BANNER_TEXT);
	String purifierTitle = affiliatePage.getPurifierPageTitle();
	softAssert.assertEquals(purifierTitle, AppConstants.AD_AFFILIATE_PURIFIER_PAGE_TITLE);
	Thread.sleep(10000);
	//.selectModel("AirDoctor Wall-Mounted 2500","1");
	String modelTitle = affiliatePage.getModelText();
	Assert.assertEquals(modelTitle, AppConstants.AD_AFFILIATE_MODEL_TEXT);
	softAssert.assertAll();
	//tearDown();
	}
	
	
	/*
	 * @Test(dataProvider = "getDataFromExcel") public void placeOrder(ITestContext
	 * testContext,String ModelName,String ProductQuantity, String email, String
	 * firstname, String lastname, String addone, String addtwo, String cty ,String
	 * state,String zipcode,String phonenumber, String upsellOne,String
	 * upsellQuantity, String subtotal, String flatrate, String tax, String
	 * finaltotal) throws InterruptedException, Exception {
	 * 
	 * int currenttest=affiliatePage.testMe(testContext);
	 * affiliatePage.clickShopNow(); Thread.sleep(15000); //PurifierPageTitleTest();
	 * affiliatePage.selectModel(ModelName, ProductQuantity); Thread.sleep(5000);
	 * affiliatePage.checkout(email,firstname, lastname, addone, addtwo, cty,
	 * state,zipcode,phonenumber); Thread.sleep(15000);
	 * //pdpPage.selectupsells(upsellOne, upsellQuantity); Map<String, String>
	 * productActDetailsMap = affiliatePage.getorderdetails();
	 * softAssert.assertEquals(productActDetailsMap.get("subtotal"), subtotal);
	 * softAssert.assertEquals(productActDetailsMap.get("Shipping"), flatrate);
	 * softAssert.assertEquals(productActDetailsMap.get("tax"), tax);
	 * softAssert.assertEquals(productActDetailsMap.get("total"), finaltotal);
	 * affiliatePage.writeexcel(productActDetailsMap.get("subtotal"),
	 * productActDetailsMap.get("Shipping"),productActDetailsMap.get("tax"),
	 * productActDetailsMap.get("total"),currenttest); //loginPage=
	 * pdpPage.logoutfromthankyoupage(); softAssert.assertAll();
	 * loginPage.doLogin(prop.getProperty("username"),
	 * prop.getProperty("password")); pdpSetup();
	 * 
	 * }
	 */
}

