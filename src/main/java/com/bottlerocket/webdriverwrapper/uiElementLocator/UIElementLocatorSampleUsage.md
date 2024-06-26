

# How To Use UIElementLocator
- terminology:
    - **element**: an object in the user interface 
    - **locator**: a 'By' object that WebDriver uses to locate an element 
        - By.id, By.xpath, By.image, etc.
    - **locator strategy**: the technique used to locate an element
        - id, xpath, image, etc.
    - **selector**: a String or image used to identify an element 
        - 'buyButtonId', "//*[@content-desc='Buy button']", buyButtonImage.png

## How UIElementLocator works:
- a UIElementLocator object
    - contains one or more PlatformElementSelector objects
        - which contain one or more UIElementSelector objects
- For example:
    - a UIElementLocator for a "Buy" button allows a test to say "this is how you locate a 'Buy' button on each test platform (Android, iOS, ...)"
        - a PlatformElementSelector object says "for test platform 'Android', here is a list of UIElementSelectors"
            - a UIElementSelector says "for test platform 'Android' and locator strategy 'id' use selector 'buyButtonId' "
            - another UIElementSelector says "for test platform 'Android' and locator strategy 'xpath' use selector //*[@content-desc='Buy button'] "

## Creating an element locator
quickstart:
  1. create a UIElementLocator variable
      1. choose the constructor "UIElementLocator(TestPlatform testPlatform, LocatorStrategy locatorStrategy, String selector)"
      1. Congratulations! You have created a UIElementLocator that already contains a UIElementSelector
      1. **this is the fastest way to create an element locator** if you know it will only have one selector
          1. you can still add more element selectors later using the UIElementLocator.addElementSelector() methods

intermediate:
  1. create a list of UIElementSelector variables
  2. add the entire list to a UIElementLocator

advanced:
  1. create a UIElementLocator variable
  2. create one or more UIElementSelector variables
  3. (optional) assign tags to the UIElementSelector variables
  5. assign the UIElementSelector variables to a UIElementLocator

### Object Repository (ResourceLocator) style
element locators are created apart from page objects

```
public class ResourceLocator {

    ...

    // create element locator variables
    public static UIElementLocator RhHomeSignInButtonUnauth = new UIElementLocator();
    public static UIElementLocator rhMyAccountProfileMailingAddressLine1 = new UIElementLocator();

    // create element selector variables (ADVANCED)
        // NOTE: using the UIElementSelector(TestPlatform, LocatorStrategy, String selector, Integer rank) constructor 
    
    UIElementSelector rhMyAccountProfileMailingAddressLine1Editable = new UIElementSelector(
            TestPlatform.ANDROID, LocatorStrategy.XPATH,
            "//android.widget.EditText[descendant::android.view.View[@content-desc='MailingAddressViewAddress1TextView']]",
            1
    );
    
    UIElementSelector rhMyAccountProfileMailingAddressLine1Uneditable = new UIElementSelector(
            TestPlatform.ANDROID, LocatorStrategy.XPATH,
            "//android.view.View[@content-desc='MailingAddressViewAddress1TextView']",
            2
    );
    
    UIElementSelector rhMyAccountProfileMailingAddressLine1Text = new UIElementSelector(
            TestPlatform.ANDROID, LocatorStrategy.XPATH,
            "//android.view.View[@content-desc='MailingAddressViewAddress1TextView']/*[@content-desc='ContactInfoViewTextViewContent']",
            3
    );
    
    // add tags to element selectors (ADVANCED)
    
    public void assignTagsToSelectors() {
        this.rhMyAccountProfileMailingAddressLine1Editable.setTag("editable");
        this.rhMyAccountProfileMailingAddressLine1Uneditable.setTag("uneditable");
        this.rhMyAccountProfileMailingAddressLine1Text.setTag("text");
    }

    // assign element selectors to element locators
    
    public void setElementLocators() {
    
        /*
         * a single UIElementLocator variable can contain one or more element selectors 
         * for each platform and locator strategy 
         */
         
         // QUICKSTART: add element selectors directly into an element locator 
        
        this.RhHomeSignInButtonUnauth.addElementSelector(
            TestPlatform.ANDROID, LocatorStrategy.ID, "com.roberthalf.roberthalfdirect.debug:id/signInButton");
        
        this.RhHomeSignInButtonUnauth.addElementSelector(
            TestPlatform.IOS, LocatorStrategy.XPATH, "//XCUIElementTypeButton[@name='signInButton']");   
   
        // INTERMEDIATE: use a list to assign one or more element selectors to an element locator
        
        ArrayList<UIElementSelector> configSignInButtonSelectors = new ArrayList<>();
        configSignInButtonSelectors.add(new UIElementSelector(TestPlatform.IOS, LocatorStrategy.XPATH, "//XCUIElementTypeButton[@name='signInButton']"));
        configSignInButtonSelectors.add(new UIElementSelector(TestPlatform.IOS, LocatorStrategy.ID, "notAnActualId", 99));
        this.RhHomeConfigSignInButton = new UIElementLocator(configSignInButtonSelectors);
        
        // ADVANCED: add UIElementSelectors created earlier to an element locator
        
        this.rhMyAccountProfileMailingAddressLine1.addElementSelector(rhMyAccountProfileMailingAddressLine1Editable);
        this.rhMyAccountProfileMailingAddressLine1.addElementSelector(rhMyAccountProfileMailingAddressLine1Uneditable);
        this.rhMyAccountProfileMailingAddressLine1.addElementSelector(rhMyAccountProfileMailingAddressLine1Text);
        
    }

    // initialize element locator variables in the object repository constructor
    
    public ResourceLocator() {
        setTagsToSelectors();
        setElementLocators();
    }
    
    ...
}
```

### Page Object style
element locators and element behaviors appear together within the page object
```
public class NavOpsHomeAndroid extends NavOpsHome {

    // follow the same process as in the ResourceLocator example:
        // create UIElementLocator variables
          // create selector variables 
          // assign tags to selectors
        // assign element selectors to UIElementLocators

    // initialize element locator variables in the page object constructor
    public NavOpsHomeAndroid() {
        setTagsToElementSelectors();
        setElementLocators();
    }
    
    ...
}
```

## Locating an element
```
public class NavOpsHomeAndroid extends NavOpsHome {

    ...
    
    /*
     * Element locators return a By object when you call UIElementLocator.getLocator().
     * Used together with getElementBy(), your code will no longer break 
     * when you change the locator strategy (such as from XPath to ID)
     * as previously occurred when using getElementById(), getElementByXpath(), etc.
     */ 
     
    public void clickSignInText() {
        if(isUnAuthHomeScreen()) {
            am.driverWrapper.getElementBy(            
                    RhHomeSignInButtonUnauth.getLocator(),
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                    15
            ).click();

        } else if(isConfigHomeScreen()) {
            am.driverWrapper.getElementBy(
                    configSignInButton.getLocator(),
                    ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                    15
            ).click();

        } else {
            throw new NotFoundException("Unable to find 'sign in' text on Home screen.");
        }
    }
    
    // you can get a list of locators
    // iterate through the list to locate an element using any available selector
    
    private WebElement getPersonalInfoMailingAddressLine1Element(int waitTimeInSeconds) throws NotFoundException {
    WebElement webElement = null;

        if (!rhMyAccountProfileMailingAddressLine1.getAllLocators().isEmpty()) {
            for(By locator : rhMyAccountProfileMailingAddressLine1.getAllLocators()) {
                try {
                    return am.driverWrapper.getElementBy(
                            locator,
                            ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
                            waitTimeInSeconds
                    );
                } catch (TimeoutException e) {
                    webElement = null;
                }
            }
        }

        throw new NotFoundException("Unable to locate element for Mailing Address line 1.");
    }

    // you can get locators by tag 
    // if a selector has that tag assigned to it
    
    public String getPersonalInfoMailingAddressLine1() {
        return am.driverWrapper.getElementBy(
           rhMyAccountProfileMailingAddressLine1.getLocator("text"),
           ExpectedConditionsWrapper.EXPECTED_CONDITION.PRESENT,
           15
        ).getText();
    }

    ...
}

```
