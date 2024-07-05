package agi.qa.airdoctor.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CartPage {
    WebDriver driver;

    By cartButton = By.xpath("//*[contains(concat(' ', normalize-space(@class), ' '), ' btn btn-checkout ')]\r\n" + //
            "");
    By cartItemText = By.xpath(
            "//*[contains(concat(' ', normalize-space(@class), ' '), ' checkout-sidebar ') and contains(concat(' ', normalize-space(@class), ' '), ' sm-touch-scroll ')]\n"
                    + //
                    "");

    public CartPage(WebDriver driver) {
        this.driver = driver;
    }

    public void goToCart() {
        WebElement cartBtn = driver.findElement(cartButton);
        cartBtn.click();
    }

    public String getCartText() {
        WebElement cartTextElement = driver.findElement(cartItemText);
        return cartTextElement.getText();
    }
}
