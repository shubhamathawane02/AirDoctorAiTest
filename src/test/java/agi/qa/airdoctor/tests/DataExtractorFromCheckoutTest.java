package agi.qa.airdoctor.tests;
// package agi.qa.airdoctor.tests;

import agi.qa.airdoctor.base.BaseTest;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class DataExtractorFromCheckoutTest extends BaseTest {

    @Test
    public void TestDataExtractionFromCart() throws GeneralSecurityException, IOException, InterruptedException {
        SoftAssert softassert = new SoftAssert();
        // driver.get("https://airdoctorazstg.wpengine.com/");
        homePage.NavigateToSite();

        Thread.sleep(3000);
        homePage.addItemToCart();
        cartPage.goToCart();

        String cartText = cartPage.getCartText();

        System.out.println("Cart Text : " + cartText);

        // using sort assertion /

        softassert.assertTrue(cartText.contains("the text"));

        softassert.assertAll();
    }
}
