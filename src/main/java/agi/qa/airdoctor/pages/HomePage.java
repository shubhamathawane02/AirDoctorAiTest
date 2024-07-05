package agi.qa.airdoctor.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {
    WebDriver driver;

    By addToCartButton = By.xpath("//*[@id='iot_productaddtocart_135514']");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void NavigateToSite() {
        driver.get("https://airdoctorazstg.wpengine.com/purifiers/");
    }

    public void addItemToCart() {
        WebElement addButton = driver.findElement(addToCartButton);
        addButton.click();
    }
}
