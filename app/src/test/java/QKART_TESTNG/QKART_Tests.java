package QKART_TESTNG;

import QKART_TESTNG.pages.Checkout;
import QKART_TESTNG.pages.Home;
import QKART_TESTNG.pages.Login;
import QKART_TESTNG.pages.Register;
import QKART_TESTNG.pages.SearchResult;

import static org.testng.Assert.*;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;
import org.testng.annotations.Test;

public class QKART_Tests {

    static ChromeDriver driver;
    public static String lastGeneratedUserName;

    @BeforeSuite(alwaysRun = true)
    public static void createDriver() throws MalformedURLException {
        // Launch Browser using Zalenium
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();

        driver.manage().window().maximize();
        System.out.println("createDriver()");
    }

    /*
     * Testcase01: Verify a new user can successfully register
     */
         @Test(description = "Verify registration happens correctly" , priority = 1,groups = {"Sanity"})
         @Parameters({"TC1_Username" , "TC1_Password"})
         public void TestCase01(@Optional("testUser") String TC1_Username, @Optional("abc@123") String TC1_Password) throws InterruptedException {
        Boolean status;
         logStatus("Start TestCase", "Test Case 1: Verify User Registration", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
         status = registration.registerUser(TC1_Username, TC1_Password, true);
        assertTrue(status, "Failed to register new user");

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the login page and login with the previuosly registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
         status = login.PerformLogin(lastGeneratedUserName, TC1_Password);
         logStatus("Test Step", "User Perform Login: ", status ? "PASS" : "FAIL");
        assertTrue(status, "Failed to login with registered user");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.PerformLogout();

        assertTrue(status,"Test Case 1: Verify user Registration: FAIL");

        logStatus("End TestCase", "Test Case 1: Verify user Registration : ", status ? "PASS" : "FAIL");
    }

    /*
     * Verify that an existing user is not allowed to re-register on QKart
     */
    @Test(description = "Verify re-registering an already registered user fails" , priority = 2,groups = {"Sanity"})    
    @Parameters({"TC2_Username" , "TC2_Password"})
    public void TestCase02(@Optional("testUser") String TC2_Username, @Optional("abc@123") String TC2_Password) throws InterruptedException {
        Boolean status;
        logStatus("Start Testcase", "Test Case 2: Verify User Registration with an existing username ", "DONE");

        // Visit the Registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(TC2_Username, TC2_Password, true);
        assertTrue(status,"Test Case 2: Verify user Registration : FAIL");        

        // Save the last generated username
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Visit the Registration page and try to register using the previously
        // registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUserName, TC2_Password, false);

        assertTrue(!status,"Test Case 2: Verify user Registration : FAIL");

        // If status is true, then registration succeeded, else registration has
        // failed. In this case registration failure means Success

        logStatus("End TestCase", "Test Case 2: Verify user Registration : ", status ? "FAIL" : "PASS");       
    }

    /*
     * Verify the functinality of the search text box
     */
    @Test(description = "Verify the functionality of search text box" , priority = 3,groups = {"Sanity"})
    @Parameters({"TC3_ProductNameToSearchFor"})
    public void TestCase03(String product) throws InterruptedException {
        logStatus("TestCase 3", "Start test case : Verify functionality of search box ", "DONE");
        boolean status;


        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct(product);
        assertTrue(status,"Test Case Failure. Unable to search for given product : FAIL");        

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        status = searchResults.size() == 0;
        assertTrue(!status,"Test Case Failure. There were no results for the given search string : FAIL");
        

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultelement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultelement.getTitleofResult();
            status = elementText.toUpperCase().contains("YONEX");
            assertTrue(status,"Test Case Failure. Test Results contains un-expected values: fail");            
        }

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        assertTrue(status,"Test Case Failure. Invalid keyword returned results : FAIL");        

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        status = searchResults.size() == 0;
        assertTrue(status,"Test Case Fail. Expected: no results , actual: Results were available : FAIL");
        
        status = homePage.isNoResultFound();
        assertTrue(status,"Successfully validated that no products found message is displayed : FAIL");
        
        assertTrue(status,"Test Case FAIL. Verified that no search results were found for the given text : FAIL");


        logStatus("TestCase 3", "Test Case PASS. Verified that no search results were found for the given text","PASS");
        
    }

    // /*
    //  * Verify the presence of size chart and check if the size chart content is as
    //  * expected
    //  */
    @Test(description = "Verify the existence of size chart for certain items and validate contents of size chart" , priority = 4,groups = {"Regression"})
    @Parameters({"TC4_ProductNameToSearchFor"})
    public void TestCase04(@Optional("Roadster") String TC4_ProductNameToSearchFor) throws InterruptedException {
        logStatus("TestCase 4", "Start test case : Verify the presence of size Chart", "DONE");
        boolean status = false;


        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct(TC4_ProductNameToSearchFor);
        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(Arrays.asList("6", "6", "40", "9.8"),
                Arrays.asList("7", "7", "41", "10.2"), Arrays.asList("8", "8", "42", "10.6"),
                Arrays.asList("9", "9", "43", "11"), Arrays.asList("10", "10", "44", "11.5"),
                Arrays.asList("11", "11", "45", "12.2"), Arrays.asList("12", "12", "46", "12.6"));

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);

            // Verify if the size chart exists for the search result
            status = result.verifySizeChartExists();
            assertTrue(status,"Test Case Fail. Size Chart Link does not exist : FAIL");           

            // Verify if size dropdown exists
            status = result.verifyExistenceofSizeDropdown(driver);
            logStatus("Step Success", "Validated presence of drop down", status ? "PASS" : "FAIL");

            // Open the size chart
            status = result.openSizechart();
            assertTrue(status,"Test Case Fail. Failure to open Size Chart : FAIL");
            
            // Verify if the size chart contents matches the expected values
            status = result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver);
            assertTrue(status,"Failure while validating contents of Size Chart Link : FAIL");
            
            // Close the size chart modal
            status = result.closeSizeChart(driver);                           
        }

        assertTrue(status,"End Test Case: Validated Size Chart Details : FAIL");


        logStatus("TestCase 4", "End Test Case: Validated Size Chart Details", status ? "PASS" : "FAIL");
        
    }

    // /*
    //  * Verify the complete flow of checking out and placing order for products is
    //  * working correctly
    //  */
    @Test(description = "Verify that a new user can add multiple products in to the cart and Checkout" , priority = 5,groups = {"Sanity"})
    @Parameters({"TC5_ProductNameToSearchFor","TC5_ProductNameToSearchFor2","TC5_AddressDetails"})
    public void TestCase05(String product1,String product2,String address) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 5: Verify Happy Flow of buying products", "DONE");

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"Test Case Failure. Happy Flow Test Failed in Registration : FAIL");        

        // Save the username of the newly registered user
        lastGeneratedUserName = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");

        assertTrue(status,"User Perform Login Failed : FAIL");
        

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(product1);
        assertTrue(status,"Not able to findout the given product : YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        status = homePage.searchForProduct(product2);
        assertTrue(status,"Not able to findout the given product : Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));

        // Check if placing order redirected to the Thansk page
        status = driver.getCurrentUrl().endsWith("/thanks");
        assertTrue(status,"The url is not found for thanks page");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.PerformLogout();


        logStatus("End TestCase", "Test Case 5: Happy Flow Test Completed : ", status ? "PASS" : "FAIL");        
    }

    // /*
    //  * Verify the quantity of items in cart can be updated
    //  */
    @Test(description = "Verify that the contents of the cart can be edited" , priority = 6,groups = {"Regression"})
    @Parameters({"TC6_ProductNameToSearch1" , "TC6_ProductNameToSearch2"})
    public void TestCase06(String product1 , String product2) throws InterruptedException {
        Boolean status;
        logStatus("Start TestCase", "Test Case 6: Verify that cart can be edited", "DONE");

        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Register Failed : FAIL");
       
        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed : FAIL");       

        homePage.navigateToHome();
        status = homePage.searchForProduct(product1);
        assertTrue(status,"Not able to findout the given product : Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        status = homePage.searchForProduct(product2);
        assertTrue(status,"Not able to findout the given product : Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");

        // update watch quantity to 2
        homePage.changeProductQuantityinCart(product1, 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityinCart(product1, 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityinCart(product1, 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.urlToBe("https://crio-qkart-frontend-qa.vercel.app/thanks"));
        } catch (TimeoutException e) {
            System.out.println("Error while placing order in: " + e.getMessage());            
        }

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();
        homePage.PerformLogout();

        logStatus("End TestCase", "Test Case 6: Verify that cart can be edited: ", status ? "PASS" : "FAIL");       
    }

    // /*
    //  * Verify that the cart contents are persisted after logout
    //  */
    @Test(description = "Verify that the contents made to the cart are saved against the user's login details" , priority = 7,groups = {"Regression"})
    @Parameters({"TC7_ListOfProductsToAddToCart"})
    public void TestCase07(String products) throws InterruptedException {
        Boolean status = false;

        List<String> expectedResult = Arrays.asList(products.split(";"));

        logStatus("Start TestCase", "Test Case 7: Verify that cart contents are persisted after logout", "DONE");

        Register registration = new Register(driver);
        Login login = new Login(driver);
        Home homePage = new Home(driver);

        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Registration Failed : Fail");     

        lastGeneratedUserName = registration.lastGeneratedUsername;

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");        
        assertTrue(status,"User Perform Login Failed : FAIL");        

        homePage.navigateToHome();
        status = homePage.searchForProduct(expectedResult.get(0));
        assertTrue(status,"Not able to findout the given product : Stylecon");
        homePage.addProductToCart(expectedResult.get(0));

        status = homePage.searchForProduct(expectedResult.get(1));
        assertTrue(status,"Not able to findout the given product : Xtend");
        homePage.addProductToCart(expectedResult.get(1));

        homePage.PerformLogout();

        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed After Logout: FAIL");        

        status = homePage.verifyCartContents(expectedResult);
        assertTrue(status,"Verify that cart contents are persisted after logout : FAIL");


        logStatus("End TestCase", "Test Case 7: Verify that cart contents are persisted after logout: ",status ? "PASS" : "FAIL");

        homePage.PerformLogout();       
    }

    @Test(description = "Verify that insufficient balance error is thrown when the wallet balance is not enough" , priority = 8,groups = {"Sanity"})
    @Parameters({"TC8_ProductName" , "TC8_Qty"})
    public void TestCase08(String product , int quantity) throws InterruptedException {
        Boolean status;

        logStatus("Start TestCase","Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is not enough","DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Registration Failed : Fail");   
        
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed : FAIL");       
        
        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        assertTrue(status,"Not able to findout the given product : Stylecon");
        homePage.addProductToCart(product);

        homePage.changeProductQuantityinCart(product, quantity);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();
        assertTrue(status,"Verify that insufficient balance error is thrown when the wallet balance is not enough : FAIL");


        logStatus("End TestCase","Test Case 8: Verify that insufficient balance error is thrown when the wallet balance is not enough: ",status ? "PASS" : "FAIL");
        
    }

    @Test(dependsOnMethods = {"TestCase10"} , description = "Verify that a product added to a cart is available when a new tab is added", groups = {"Regression"})
    public void TestCase09() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase","Test Case 9: Verify that product added to cart is available when a new tab is opened","DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Registration Failed : FAIL");
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed : FAIL");        

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct("YONEX");
        assertTrue(status,"Not able to search the given prduct : YONEX");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        String currentURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);

        driver.get(currentURL);
        Thread.sleep(2000);

        List<String> expectedResult = Arrays.asList("YONEX Smash Badminton Racquet");
        status = homePage.verifyCartContents(expectedResult);
        assertTrue(status, "Test Case 9: Verify that product added to cart is available when a new tab is opened : FAIL");

        driver.close();

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase","Test Case 9: Verify that product added to cart is available when a new tab is opened",status ? "PASS" : "FAIL");       
    }

    @Test(description = "Verify that privacy policy and about us links are working fine" ,groups = {"Regression"})
    public void TestCase10() throws InterruptedException {
        Boolean status = false;

        logStatus("Start TestCase","Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ","DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Registration Failed : FAIL");       
        
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed : FAIL");               

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        String basePageURL = driver.getCurrentUrl();

        driver.findElement(By.linkText("Privacy policy")).click();
        status = driver.getCurrentUrl().equals(basePageURL);
        assertTrue(status,"Verifying parent page url didn't change on privacy policy link click failed : FAIL");        

        Set<String> handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]);
        WebElement PrivacyPolicyHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = PrivacyPolicyHeading.getText().equals("Privacy Policy");
        assertTrue(status,"Verifying new tab opened has Privacy Policy page heading failed : FAIL");
        

        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);
        driver.findElement(By.linkText("Terms of Service")).click();

        handles = driver.getWindowHandles();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[2]);
        WebElement TOSHeading = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/h2"));
        status = TOSHeading.getText().equals("Terms of Service");
        assertTrue(status,"Verifying new tab opened has Terms Of Service page heading failed : FAIL");        

        driver.close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[1]).close();
        driver.switchTo().window(handles.toArray(new String[handles.size()])[0]);

        logStatus("End TestCase","Test Case 10: Verify that the Privacy Policy, About Us are displayed correctly ","PASS");
    }

    @Test(description = "Verify that the contact us dialog works fine" , priority = 11,groups = {"Regression"})
    @Parameters({"TC11_ContactusUserName","TC11_ContactUsEmail","TC11_QueryContent"})
    public void TestCase11(String contectName,String contectEmail, String content) throws InterruptedException {
        logStatus("Start TestCase","Test Case 11: Verify that contact us option is working correctly ","DONE");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        driver.findElement(By.xpath("//*[text()='Contact us']")).click();

        WebElement name = driver.findElement(By.xpath("//input[@placeholder='Name']"));
        name.sendKeys(contectName);
        WebElement email = driver.findElement(By.xpath("//input[@placeholder='Email']"));
        email.sendKeys(contectEmail);
        WebElement message = driver.findElement(By.xpath("//input[@placeholder='Message']"));
        message.sendKeys(content);

        WebElement contactUs = driver.findElement(
                By.xpath("/html/body/div[2]/div[3]/div/section/div/div/div/form/div/div/div[4]/div/button"));

        contactUs.click();

        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOf(contactUs));
        logStatus("End TestCase","Test Case 11: Verify that contact us option is working correctly ","PASS");

    }

    @Test(description = "Ensure that the Advertisement Links on the QKART page are clickable" , priority = 12,groups = {"Sanity"})
    @Parameters({"TC12_ProductNameToSearch","TC12_AddresstoAdd"})
    public void TestCase12(String product, String address) throws InterruptedException {
        Boolean status = false;
        logStatus("Start TestCase","Test Case 12: Ensure that the links on the QKART advertisement are clickable","DONE");

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        assertTrue(status,"User Perform Registration Failed : FAIL");
        
        lastGeneratedUserName = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.PerformLogin(lastGeneratedUserName, "abc@123");
        assertTrue(status,"User Perform Login Failed : FAIL");      

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(product);
        homePage.addProductToCart(product);
        homePage.changeProductQuantityinCart(product, 1);
        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress(address);
        checkoutPage.selectAddress(address);
        checkoutPage.placeOrder();
        Thread.sleep(3000);

        String currentURL = driver.getCurrentUrl();

        List<WebElement> Advertisements = driver.findElements(By.xpath("//iframe"));

        status = Advertisements.size() == 3;
        assertTrue(status,"Verify that 3 Advertisements are available : FAIL");
        // logStatus("Step ", "Verify that 3 Advertisements are available", status ? "PASS" : "FAIL");

        WebElement Advertisement1 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[1]"));
        driver.switchTo().frame(Advertisement1);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        assertTrue(status,"Verify that Advertisement 1 is clickable : FAIL");
        // logStatus("Step ", "Verify that Advertisement 1 is clickable ", status ? "PASS" : "FAIL");

        driver.get(currentURL);
        Thread.sleep(3000);

        WebElement Advertisement2 = driver.findElement(By.xpath("//*[@id=\"root\"]/div/div[2]/div/iframe[2]"));
        driver.switchTo().frame(Advertisement2);
        driver.findElement(By.xpath("//button[text()='Buy Now']")).click();
        driver.switchTo().parentFrame();

        status = !driver.getCurrentUrl().equals(currentURL);
        assertTrue(status,"Verify that Advertisement 2 is clickable : FAIL");
        // logStatus("Step ", "Verify that Advertisement 2 is clickable ", status ? "PASS" : "FAIL");
        logStatus("End TestCase","Test Case 12:  Ensure that the links on the QKART advertisement are clickable",status ? "PASS" : "FAIL");
    }


    @AfterSuite
    public static void quitDriver() {
        System.out.println("quit()");
        driver.quit();
    }

    public static void logStatus(String type, String message, String status) {

        System.out.println(String.format("%s |  %s  |  %s | %s", String.valueOf(java.time.LocalDateTime.now()), type,
                message, status));
    }
    
}

