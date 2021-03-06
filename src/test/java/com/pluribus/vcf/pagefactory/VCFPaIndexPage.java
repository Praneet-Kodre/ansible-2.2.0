package com.pluribus.vcf.pagefactory;

import com.jcabi.ssh.SSHByPassword;
import com.jcabi.ssh.Shell;
import com.pluribus.vcf.helper.PageInfra;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class VCFPaIndexPage extends PageInfra {
	@FindBy(how = How.CSS, using = "a.list-group-item.category.pa-dashboard-menu")
	WebElement dashboardIcon;
	
	@FindBy(how = How.CSS, using = "a.list-group-item.category.pcap-engine-menu")
	WebElement configIcon;
	
	@FindBy(how = How.CSS, using = "a.list-group-item.category.vflow-menu")
	WebElement vFlowConfig;
	
	@FindBy(how = How.CSS, using = "div.modal-body")
	WebElement pcapAddMenu;
	
	@FindBy(how= How.CSS, using = "button.btn.btn-sm.btn-primary")
	WebElement addButton;
	
	@FindBy(how= How.CSS, using = "button.btn.btn-primary.btn-sm")
	WebElement fetchButton;
	
	@FindBy(how= How.CSS, using = "button.btn.btn-primary")
	WebElement confirmOkButton;
	
	@FindBy(how = How.NAME, using = "name")
	WebElement name;
		
	@FindBy(how = How.NAME, using = "ip")
	WebElement ip;
	
	@FindBy(how = How.NAME, using = "port")
	WebElement port;

	@FindBy(how = How.NAME, using = "ok")
	WebElement okButton;
	
	@FindBy(how = How.ID, using = "selectedInport")
	WebElement inPortText;
	
	@FindBy(how = How.ID, using = "selectedOutport")
	WebElement outPortText;
	
	@FindBy(how = How.CSS, using = "div.col-sm-9")
	WebElement interfaceList;
	
	@FindBy(how = How.CSS, using = "ng-transclude")
	WebElement pcapList;
	
	@FindBy(how = How.CSS, using = "div.panel.panel-default")
	WebElement configFlowList;

	/* Names for findElement(s) methods */
	String switchListName = "ul.dropdown-menu";
	String dropdownName = "button.btn.btn-default.btn-sm";
	String lblCheckBox = "label.checkbox";
	String checkBox = "input.ng-pristine.ng-untouched.ng-valid.ng-empty";
	String pcapNameId = "div.td span";
	String pcapListId = "ng-form";
	String flowHeaderId = "div.panel-heading.mirror-head";
	String flowNameId = "span.name-ellipsis";
	String toggleSwitch = "span.switch";
	String switchOnState = "span.toggle-bg.on";
	String switchOffState = "span.toggle-bg.off";
	String addButtonId = "button.btn.btn-sm.btn-primary";
	
	public VCFPaIndexPage(WebDriver driver) {
		super(driver);
	}
	
	public List getSwitchList() {
		List<WebElement> rows = new ArrayList();
		rows = driver.findElements(By.cssSelector(switchListName));
		return rows;
	}
	
	public List getDropDownButtons() {
		List<WebElement> rows = new ArrayList();
		rows = driver.findElements(By.cssSelector(dropdownName));
		return rows;
	}
	
	public String getEth1Ip(String hostIp) {
		String eth1Ip = null;
		try {
			Shell sh1 = new Shell.Verbose(
					new SSHByPassword(
							hostIp,
							22,
							"vcf",
							"changeme"
					)
	        );
			String out1 = new Shell.Plain(sh1).exec("ifconfig eth1 | grep 'inet addr:' | cut -d: -f2 | awk '{ print $1}'");
			eth1Ip = out1.trim();
		}
		catch(Exception e) {
		}
		return eth1Ip;
	}
	public void addLocalPcap(String pcapName, String hostIp) {
		String eth0Ip = hostIp;
		String eth1Ip = getEth1Ip(hostIp); 
		configIcon.click();
		
		//Check if pcap by that name already exists. Then skip adding it. 
		if(!verifyPcap(pcapName)) {
			waitForElementVisibility(addButton,1000);
			addButton.click();
			waitForElementVisibility(pcapAddMenu,100);
			setValue(name,pcapName);
			setValue(ip,eth1Ip);
			setValue(port,"8080");
			fetchButton.click();
			waitForElementVisibility(interfaceList,100);
			List <WebElement> ifNames = driver.findElements(By.cssSelector(lblCheckBox));
			int index = 0;
			int hitIdx = 0;
			for (WebElement row: ifNames) {
				if(row.getText().contains("eth1")) {
					row.findElement(By.cssSelector(checkBox)).click();
					break;
				}
				index++;
			}
			okButton.click();
			waitForElementVisibility(pcapList,100);
		}
	}
	
	public boolean verifyPcap(String pcapName) {
		boolean status = false; 
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = (driver.findElements(By.cssSelector(pcapListId)).size() != 0);
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		List <WebElement> configuredPcaps = null;
		if(exists) {
			configuredPcaps = driver.findElements(By.cssSelector(pcapNameId));
			for (WebElement row:configuredPcaps) {
				if(row.getText().equalsIgnoreCase(pcapName)) {
					status = true;
					break;
				}
			}
 		}
		return status;
	}
	
	public void addVFlow(String flowName,String switchName, String inPort, String outPort, String duration, String pcapName) {
		vFlowConfig.click();
		if(!isFlowConfigured(flowName)) {
			waitForElementVisibility(addButton,1000);
			addButton.click();
			setValue(name,flowName);
			List <WebElement> dds = getDropDownButtons();
			dds.get(0).click();
			List <WebElement> rows = getSwitchList();
			for (WebElement row : rows) {
				if(row.getText().contains(switchName)) {
					row.click();
					break;
				}
			}
			setValue(inPortText,inPort);
			setValue(outPortText,outPort);
			dds.get(1).click();
			rows = getSwitchList();
			for (WebElement row : rows) {
				if(row.getText().contains(duration)) {
					row.click();
					break;
				}
			}
			dds.get(2).click();
			rows = getSwitchList();
			for (WebElement row : rows) {
				if(row.getText().contains(pcapName)) {
					row.click();
					break;
				}
			}
			okButton.click();
		}
	}
	
	public boolean chkCurrentFlowState (WebElement flow) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean existsOn = false;
		existsOn = (flow.findElements(By.cssSelector(switchOnState)).size() != 0);
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		return existsOn;	
	}
	
	public boolean chkCurrentFlowState (String flowName,boolean expState) {
		boolean status = false;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean existsOn = false;
		List <WebElement> flowList = driver.findElements(By.cssSelector(flowHeaderId));
		for (WebElement flow: flowList) {
			if(flow.findElement(By.cssSelector(flowNameId)).getText().contains(flowName)) {
				existsOn = (flow.findElements(By.cssSelector(switchOnState)).size() != 0);
				break;
			}
		}
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		if(existsOn == expState) {
			status = true;
		}
		return status;
	}
	
	public boolean isFlowConfigured(String flowName) {
		boolean status = false;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = (driver.findElements(By.cssSelector(flowHeaderId)).size() != 0);
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		if(exists) {
			List <WebElement> flowList = driver.findElements(By.cssSelector(flowHeaderId));
			for (WebElement flow: flowList) {
				if(flow.findElement(By.cssSelector(flowNameId)).getText().contains(flowName)) {
					status = true;
					break;
				}
			}
		}
		return status;
	}
	
	public boolean togglevFlowState(String flowName) {
		boolean status = false;
		boolean currentState = false;
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.MILLISECONDS);
		boolean exists = (driver.findElements(By.cssSelector(flowHeaderId)).size() != 0);
		driver.manage().timeouts().implicitlyWait(100, TimeUnit.SECONDS);
		if(exists) {
			List <WebElement> flowList = driver.findElements(By.cssSelector(flowHeaderId));
			for (WebElement flow: flowList) {
				if(flow.findElement(By.cssSelector(flowNameId)).getText().contains(flowName)) {
					currentState = chkCurrentFlowState(flow); //findCurrentState of the switch
					flow.findElement(By.cssSelector(toggleSwitch)).click();
					waitForElementVisibility(confirmOkButton,100);
					confirmOkButton.click();
					waitForElementVisibility(addButton,100);
					waitForElementToClick(By.cssSelector(addButtonId),100);
					if(chkCurrentFlowState(flowName,!currentState)) {
						status = true;
					}
					break;
				}			
			}
		} else {
			com.jcabi.log.Logger.error("togglevFlowState","No vflows configured!");
		}
		return status;
	}
	
	public void gotoPADashboard() {
		dashboardIcon.click();
	}
 
	public String getUrl() {
		return driver.getCurrentUrl();
	}
}
