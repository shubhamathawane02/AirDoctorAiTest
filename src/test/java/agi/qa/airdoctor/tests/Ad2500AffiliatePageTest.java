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
import org.testng.asserts.SoftAssert;

import agi.qa.airdoctor.base.BaseTest;
import agi.qa.airdoctor.constants.AppConstants;
import agi.qa.airdoctor.utils.ExcelUtil;

public class Ad2500AffiliatePageTest extends BaseTest {

	@BeforeClass()
	public void affilatePageSetup() throws InterruptedException {

		// affiliatePage = loginPage.clickShopNow();
		affiliatePage = loginPage.doLogin(prop.getProperty("username"), prop.getProperty("password"));
		// softAssert.assertEquals(loginPage.getLoginSuccessText(),AppConstants.LOGIN_SUCCESS_TEXT);
		// pdpPage= loginPage.clickBuyNow();

	}

	/*
	 * @DataProvider public Object[][] getDataFromExcel() { return
	 * ExcelUtil.getTestData(AppConstants.PRODUCT_SHEET_NAME); }
	 */

	@DataProvider
	public Object[][] getAffiliateLinkFromExcel() {
		return ExcelUtil.getTestData(AppConstants.AFFILIATE_LINK);
	}

	@Test
	public void LandingPageBannerTest() {
		String bannerText = affiliatePage.getBannerText();
		Assert.assertEquals(bannerText, AppConstants.AD_AFFILIATE_PAGE_BANNER_TEXT);
	}

	 @Test(dataProvider="getAffiliateLinkFromExcel") 
	  public void AffiliateLinkTest(String url) throws Exception { 
	  //setup(); 
	  affiliatePage = affiliatePage.getaffiliateURL(url); 
	  Thread.sleep(3000);
	  affiliatePage.clickShopNow(); 
	  Thread.sleep(3000); 
	  //String bannerText = affiliatePage.getBannerText(); 
	  //softAssert.assertEquals(bannerText,AppConstants.AD_AFFILIATE_PAGE_BANNER_TEXT); 
	  String purifierTitle = affiliatePage.getPurifierPageTitle(); 
	  softAssert.assertEquals(purifierTitle,AppConstants.AD_AFFILIATE_PURIFIER_PAGE_TITLE); 
	  Thread.sleep(10000);
	  //.selectModel("AirDoctor Wall-Mounted 2500","1"); 
	  String modelTitle = affiliatePage.getModelText(); 
	  Assert.assertEquals(modelTitle,AppConstants.AD_AFFILIATE_MODEL_TEXT); 
	  softAssert.assertAll(); 
	  //tearDown();
	  }
	
}


