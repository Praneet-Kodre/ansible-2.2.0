package com.pluribus.vcf.pagefactory;

import com.jcabi.log.Logger;
import com.pluribus.vcf.helper.PageInfra;

import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.pluribus.vcf.helper.PageInfra;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VCFManagerPage extends PageInfra {
	
	@FindBy(how = How.CSS, using = "button.btn.btn-info")
	WebElement addFabric;
	
	String nextButtonId = "button[type=submit]";
	String fileUploadId = "hostFile";
	String csvUploadId = "csvFile";
	String ztpMenuWindow = "div.form-horizontal.inner-dialog-container.ZTPDialog.ng-scope";
	String ztpIconId = "button.icon-ztp";
	String addSeedId = "button.icon-add";
	String backButton = "button.btn.btn-info";
	String seedWindowId = "div.form-horizontal.inner-dialog-container";
	String switchSelectId = "div.col-sm-8 select";
	String successButtonId = "button.btn.btn-success";
	String userIdField = "input[placeholder='User ID']";
	String pwdField = "input[type=Password]";
	String uNameField = "userName";
	String passField = "password";	
	String devDiscoveryFields = "div.ui-grid-cell-contents.ng-binding.ng-scope";
	String nextId = "button[class='btn btn-success'][type=submit]";
	String verifyNextId = "button[class='btn btn-success'][type=button]";
	String playbookLabel = "div.col-sm-4.ng-scope span";
	String playbookImg = "img[alt='";  
    String playbookLink = "div[ref='";
	String closeButton = "button.button";
	String fabricNodeImage = "image.fabricNode";
	String progressBar = "div.progress-bar.ng-isolate-scope.progress-bar-info span";
	String deleteIcon = "button.icon-delete.ng-scope";
	String confirmDelPopup = "div.inner-dialog-container";
	String vrrpNextButton = "button.btn.btn-success[type=submit][ng-show]";
	String fabricNameFieldSel = "input.form-control.ng-pristine.ng-untouched.ng-valid.ng-not-empty.ng-valid-required.ng-valid-pattern[value=gui-fabric]";
	String advancedButtonSel = "button.icon-add.ng-pristine.ng-untouched.ng-valid.ng-empty";
	String gatewayIpSel = "input.form-control.ng-pristine.ng-untouched.ng-valid.ng-not-empty.ng-valid-required.ng-valid-pattern[value='10.9.9.1']";
	String setupTabSel = "uib-tab-heading.ng-scope";
	String toggle40gSel = "select.form-control.ng-pristine.ng-untouched.ng-valid.ng-not-empty";
	String resetFabricCheckBox = "input[type='checkbox']";

	public VCFManagerPage(WebDriver driver) {
		super(driver);
	}
	
	public boolean terminateAndCleanZtp (String vcfIp) {
		boolean status = false;
		String cleanZtpUrl = "https://"+vcfIp+"/vcf-mgr/api/ansible/terminateAndCleanZtp";
		driver.navigate().to(cleanZtpUrl);
		try {
			Thread.sleep(60000);
		} catch (Exception e) {
			
		}
		WebElement message = driver.findElement(By.tagName("body"));
		if(message.getText().contains("Process Terminated Successfully"))  status = true;
		//driver.navigate().to("https://"+vcfIp+"/vcf-mgr/index.html#/fabrics/map");
		driver.navigate().back();
		try {
			Thread.sleep(10000);
		} catch (Exception e) {
			
		}
		return status;
	}
	
	public boolean addSeedSwitch(String switchName, String password) throws Exception {
		boolean status = false;
		waitForElementVisibility(addFabric,100);
		addFabric.click();
		waitForElementToClick(By.cssSelector(addSeedId),180);
		driver.findElement(By.cssSelector(addSeedId)).click();
		waitForElementToClick(By.cssSelector(seedWindowId),100);
		WebElement switchSelect = driver.findElement(By.cssSelector(switchSelectId));
		selectElement(switchSelect,switchName);
		setValue(driver.findElement(By.cssSelector(userIdField)),"network-admin");
		setValue(driver.findElement(By.cssSelector(pwdField)),password);
		WebElement successButton = driver.findElement(By.cssSelector(successButtonId));
		successButton.click();
		Thread.sleep(5000); //waiting for success message to go away
		waitForElementToClick(By.cssSelector("div.main-content-box"),100);
		status = getSeedSwitchDiscoveryStatus(switchName,10);
		return status;
	}
	
	//Verify seed switch successful add
	public boolean getSeedSwitchDiscoveryStatus (String switchName, int iter) throws Exception{
		boolean status = false;
		List<WebElement> rows = new ArrayList();
		int i = 0;
		boolean found = false;
		while(i < iter) {
			rows = driver.findElements(By.cssSelector(devDiscoveryFields));
			int idx = 0;
			for (WebElement row : rows) {
				if(row.getText().equals(switchName)) {
					if(rows.get(idx+6).getText().contains("completed")) {
						status = true;
						found = true;
						break;
					} else if (rows.get(idx+6).getText().contains("failed")) {
						status = false;
						found = true;
						break;
					}
				}
				idx += 1;
				Thread.sleep(3000);
			 }
			if(found == true) break;
			i++;
		}
		return status;
	}
	
	public void delAllSeedsVcfMgr() throws Exception{
		addFabric.click();
		Thread.sleep(10000);
		boolean status = false;
		if(isElementActive(deleteIcon)) {
			List <WebElement> delIconList = driver.findElements(By.cssSelector(deleteIcon)); 
			for (WebElement row : delIconList) {
				row.click();
				waitForElementToClick(By.cssSelector(confirmDelPopup),100);
				List <WebElement> buttons = driver.findElements(By.cssSelector(closeButton));
				for (WebElement button: buttons){
					if(button.getText().contains("Yes")) {
						button.click();
						break;
					}
				}
			Thread.sleep(2000); //waiting for success message to go away
			}
		}
		waitForElementToClick(By.cssSelector(backButton),100);
		addFabric.click();
		Thread.sleep(5000);
	}
	
	public boolean launchZTP(String hostFile, String csvFile, String password, int expNodeCount, String playbookName, String gatewayIp) throws Exception {
		boolean status = false;
		waitForElementVisibility(addFabric,100);
		waitForElementToClick(By.cssSelector(backButton),100);
		addFabric.click();
		//Thread.sleep(5000);	//waiting for click to go through. 
		while(!isElementActive(ztpIconId)) {
			Thread.sleep(5000);
		}
		waitForElementToClick(By.cssSelector(ztpIconId),100);
		driver.findElement(By.cssSelector(ztpIconId)).click();
		//Thread.sleep(5000); //waiting for click to go through
		while(!isElementActive(ztpMenuWindow)) {
			Thread.sleep(5000);
		}
		waitForElementToClick(By.cssSelector(ztpMenuWindow),100);
		WebElement fabricName = driver.findElement(By.cssSelector(fabricNameFieldSel));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddhh");
	    String dateAsString = simpleDateFormat.format(new Date());
	    setValue(fabricName, "gui-fabric-"+dateAsString);
		WebElement element = driver.findElement(By.id(fileUploadId));
		((RemoteWebElement) element).setFileDetector(new LocalFileDetector());
		element.sendKeys(hostFile);
		setValue(driver.findElement(By.name(uNameField)),"network-admin");
		setValue(driver.findElement(By.name(passField)),password);
		
		//Click on Advanced Settings
				List <WebElement> elements = driver.findElements(By.cssSelector(advancedButtonSel));
				elements.get(0).click();	
				
				Thread.sleep(2000);
				List <WebElement> dropdowns =driver.findElements(By.cssSelector(toggle40gSel));
				for (WebElement row: dropdowns) {
					if(row.getText().contains("True")) {
						Select dropdown = new Select(row);
						dropdown.selectByVisibleText("False");
						break;
					}
				}
				Thread.sleep(2000);
				setValue(driver.findElement(By.cssSelector(gatewayIpSel)),gatewayIp);
				
				//Run Fabric Reset playbook
				List <WebElement> rstFabric = driver.findElements(By.cssSelector(resetFabricCheckBox));
				for (WebElement row: rstFabric) {
							row.click();
				}
				
				waitForElementToClick(By.cssSelector(nextId),100);
				WebElement nextButton = driver.findElement(By.cssSelector(nextId));
				nextButton.click();
				Thread.sleep(2000); //Sleeping for the click to go through 
				
		/*
		if(isElementActive(closeButton)) {
			com.jcabi.log.Logger.info("playbookConfig","Error configuring playbook. Please try again..");
			return false;
		}
		*/
				
		int i = 0;
		//Sleep for fabric provisioning
		while(i < 100) {
				if((driver.findElement(By.cssSelector(progressBar)).getText().equalsIgnoreCase("Fabric Resetting"))||(driver.findElement(By.cssSelector(progressBar)).getText().equalsIgnoreCase("Provisioning Fabric"))) {
					com.jcabi.log.Logger.info("playbookConfig","Fabric provisioning being setup");
					Thread.sleep(120000);
					i++;
				} else break;
		}
		
		//Check status of fabric upload
		List <WebElement> statusTabs = driver.findElements(By.cssSelector(progressBar));
		status = false;
		if((statusTabs.get(0).getText().contains("Fabric configured"))&&(statusTabs.get(1).getText().contains("Verify Topology"))) {
			status = true;	
		}
		if(status == false) {
			com.jcabi.log.Logger.error("playbookConfig","Fabric configured message not found. Playbook configuration timed out");
			return false;
		}
		 //Click on Next
        i = 0;
        while ((i<5) && (!isElementActive(verifyNextId))) {
                Thread.sleep(100000);
                if(isElementActive(verifyNextId)) break;
                i++;
        }
        waitForElementToClick(By.cssSelector(verifyNextId),100);
        nextButton = driver.findElement(By.cssSelector(verifyNextId));
        nextButton.click();
        
        while(!isElementActive(playbookLabel)) {
        	Thread.sleep(2000);
        }
        
		//Select appropriate playbook
        Actions action = new Actions(driver);
        action.moveToElement(driver.findElement(By.cssSelector(playbookImg+playbookName+"']"))).perform();
        WebElement subElement = driver.findElement(By.cssSelector(playbookLink+playbookName+"']"));
        action.moveToElement(subElement);
        action.click();
        action.perform();
        
        Thread.sleep(5000);
		element = driver.findElement(By.id(csvUploadId));
		((RemoteWebElement) element).setFileDetector(new LocalFileDetector());
		element.sendKeys(csvFile);
		waitForElementToClick(By.cssSelector(vrrpNextButton),100);
		nextButton = driver.findElement(By.cssSelector(vrrpNextButton));
		retryingFindClick(nextButton);
		Thread.sleep(10000);
		
		i = 0;
		while((i<100) && (!isElementActive(closeButton))) {
			Thread.sleep(100000);
			if(isElementActive(closeButton)) {
				//System.out.println("successMsg:"+ driver.findElement(By.cssSelector(successMsgLbl)).getText());
				//driver.findElement(By.cssSelector(closeButton)).click();
				break;
			}
			i++;
		}
		waitForElementToClick(By.cssSelector(fabricNodeImage),100);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		int fabricNodeCount = driver.findElements(By.cssSelector(fabricNodeImage)).size();
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		com.jcabi.log.Logger.info("playbookConfig","fabric Node Count:"+fabricNodeCount);
		if(fabricNodeCount == expNodeCount) status = true;
		return status;
	}
	
	public boolean isElementActive(String el) {
		boolean status = false;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = (driver.findElements(By.cssSelector(el)).size() != 0);
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
		return exists;
	}
	
	public boolean checkConfigStatus() {
		boolean status = true;
		try {
			Thread.sleep(100000);
		} catch (Exception e) {
			com.jcabi.log.Logger.error("configurePlaybook",e.toString());
		}
		driver.getCurrentUrl();
		return status;
	}
}
