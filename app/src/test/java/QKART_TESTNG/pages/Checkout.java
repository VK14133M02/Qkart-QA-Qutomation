package QKART_TESTNG.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Checkout {
    RemoteWebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(RemoteWebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /*
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addresString) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Click on the "Add new address" button, enter the addressString in the address
             * text box and click on the "ADD" button to save the address
             */
            driver.findElement(By.className("css-rfvjbl")).click();
            Thread.sleep(2000);

            WebElement addressBox = driver.findElement(By.className("css-u36398"));
            if(addressBox.isDisplayed()){
                addressBox.sendKeys(addresString);
                driver.findElement(By.className("css-177pwqq")).click();

                // Thread.sleep(2000);
                WebDriverWait wait = new WebDriverWait(driver,30);
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@class='address-item not-selected MuiBox-root css-0']")));
                return true;
            }

            return false;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
            return false;

        }
    }

    /*
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            /*
             * Iterate through all the address boxes to find the address box with matching
             * text, addressToSelect and click on it
             */
            List<WebElement> addresses = driver.findElements(By.xpath("//*[@id='root']/div/div[2]/div[1]/div/div[1]/div/div[1]/p"));
            for(WebElement address : addresses){
                if(address.getText().equalsIgnoreCase(addressToSelect)){
                    address.click();
                    Thread.sleep(2000);
                    System.out.println("Address found Successfully");
                    return true;
                }
            }

            System.out.println("Unable to find the given address");
            return false;
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
            return false;
        }

    }

    /*
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 05: MILESTONE 4
            // Find the "PLACE ORDER" button and click on it
            WebElement placeButton = driver.findElement(By.className("css-177pwqq"));
            if(placeButton.isEnabled()){
                placeButton.click();
                // Thread.sleep(2000);
                System.out.println("Order Placed Successful");
                return true;
            }
            return false;

        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
            return false;
        }
    }

    /*
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        try {
            // TODO: CRIO_TASK_MODULE_TEST_AUTOMATION - TEST CASE 08: MILESTONE 7

            WebElement alertMessage = driver.findElement(By.id("notistack-snackbar"));
            if (alertMessage.isDisplayed()) {
                if (alertMessage.getText().equals("You do not have enough balance in your wallet for this purchase")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
            return false;
        }
    }
}


// import java.util.List;

// import org.openqa.selenium.By;
// import org.openqa.selenium.WebElement;
// import org.openqa.selenium.remote.RemoteWebDriver;
// import org.openqa.selenium.support.ui.ExpectedConditions;
// import org.openqa.selenium.support.ui.WebDriverWait;

// public class Checkout {
//     RemoteWebDriver driver;
//     String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

//     public Checkout(RemoteWebDriver driver) {
//         this.driver = driver;
//     }

//     public void navigateToCheckout() {
//         if (!this.driver.getCurrentUrl().equals(this.url)) {
//             this.driver.get(this.url);
//         }
//     }

//     /*
//      * Return Boolean denoting the status of adding a new address
//      */
//     public Boolean addNewAddress(String addresString) {
//         try {
//             /*
//              * Click on the "Add new address" button, enter the addressString in the address
//              * text box and click on the "ADD" button to save the address
//              */
//             WebElement addNewAddressButton = driver.findElement(By.id("add-new-btn"));
//             addNewAddressButton.click();

//             WebElement AddressBox = driver.findElement(By.className("MuiOutlinedInput-input"));
//             AddressBox.clear();
//             AddressBox.sendKeys(addresString);

//             List<WebElement> buttons = driver.findElements(By.className("css-177pwqq"));
//             for (WebElement button : buttons) {
//                 if (button.getText().equals("ADD")) {
//                     button.click();
//                     WebDriverWait wait = new WebDriverWait(driver, 30);
//                     wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(String.format(
//                             "//*[@class='MuiTypography-root MuiTypography-body1 css-yg30e6' and text()='%s']",
//                             addresString))));
//                     Thread.sleep(2000);
//                     return true;
//                 }
//             }
//             return false;
//         } catch (Exception e) {
//             System.out.println("Exception occurred while entering address: " + e.getMessage());
//             return false;

//         }
//     }

//     /*
//      * Return Boolean denoting the status of selecting an available address
//      */
//     public Boolean selectAddress(String addressToSelect) {
//         try {
//             /*
//              * Iterate through all the address boxes to find the address box with matching
//              * text, addressToSelect and click on it
//              */
//             WebElement address = driver.findElementByXPath(
//                     String.format("//p[@class = 'MuiTypography-root MuiTypography-body1 css-yg30e6' and text()= '%s']",
//                             addressToSelect));
//             address.click();

//             return false;
//         } catch (Exception e) {
//             System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
//             return false;
//         }

//     }

//     /*
//      * Return Boolean denoting the status of place order action
//      */
//     public Boolean placeOrder() {
//         try {
//             WebElement placeOrder = driver.findElementByXPath("//button[text()='PLACE ORDER']");
//             placeOrder.click();
//             return true;

//         } catch (Exception e) {
//             System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
//             return false;
//         }
//     }

//     /*
//      * Return Boolean denoting if the insufficient balance message is displayed
//      */
//     public Boolean verifyInsufficientBalanceMessage() {
//         try {
//             WebElement alertMessage = driver.findElement(By.id("notistack-snackbar"));
//             if (alertMessage.isDisplayed()) {
//                 if (alertMessage.getText().equals("You do not have enough balance in your wallet for this purchase")) {
//                     return true;
//                 }
//             }
//             return false;
//         } catch (Exception e) {
//             System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
//             return false;
//         }
//     }
// }
