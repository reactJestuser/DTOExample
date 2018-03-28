package ipd.administration.advancedSearch;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import ipd.administration.advancedSearch.ExcelData;
import ipd.administration.projects.CreateProject;
import ipd.Base;

public class Search extends Base {
    SoftAssert softassert = new SoftAssert();
    Select sel1;
    String path = "/src/test/java/ipd/administration/advancedSearch/search.properties";
    String excelpath = Paths
            .get(System.getProperty("user.dir")
                    + "/../../apps/evsd_documents/tests/Glucose_sensors_importable_test10.xlsx")
            .toRealPath().toString();
    List<String> tagList;
    String projectcreated, abstractText, abstractExcelText, claimsText,
            claimsExcelText;

    public Search() throws IllegalArgumentException, IOException {
        super();
    }

    /* load property file for create project package */
    @BeforeClass
    public void load() throws IOException {
        prop = new Properties();
        loadPropertyFile(path);
    }

    @Test(groups = { "RunThird" }, enabled = true)
    public void submitQuery(ITestContext itc) throws Exception {
        Boolean val1, val2;
        WebElement myElement;
        int tagCount = 0;
        projectcreated = (String) itc.getAttribute(CreateProject.projectname);
        click("nav_dropdown");
        navigateTo("nav_list", "Administration");
        click("homeButton");
        navigateTo("projectlist2", projectcreated);
        click("search_tab");

        // check if patent families checkbox and article checkboxis checked
        val1 = driver.findElement(
                By.cssSelector(prop.getProperty("patentfamilies_checkbox")))
                .isSelected();
        if (!val1 == true) {
            click("patentfamilies_checkbox");
        }

        val2 = driver.findElement(
                By.cssSelector(prop.getProperty("articles_checkbox")))
                .isSelected();
        if (val2 == true) {
            click("articles_checkbox");
        }
        myElement = (new WebDriverWait(driver, 60)).until(ExpectedConditions
                .visibilityOfElementLocated(By.id("biblio-options1")));
        sel1 = new Select(myElement);
        sel1.selectByValue("assignees");
        Thread.sleep(2000);
        // select value from Assignee dropdown
        click("assignee_dropdown");
        input2("inputAssigneename", "ABBOTT");
        Thread.sleep(3000);
        List<WebElement> weblist = driver.findElements(By.cssSelector(prop
                .getProperty("namelist")));
        int count = weblist.size();
        for (WebElement namelist : weblist) {
            if (namelist.getText().equals("ABBOTT")) {
                namelist.click();
                break;
            }
        }
        click("search_button");
        Thread.sleep(2000);
        SaveQuery();

        // save query on patent family.*/

        // pull data from excel to list
        XLSRead xls = null;
        try {
            xls = new XLSRead();
            xls.setExcelFile(excelpath, "Patent Results");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // pull patent number from search page prop = new Properties();
        loadPropertyFile(path);
        click("Patentlink");

        // add explicit wait to avoid stale element reference exception
        WebDriverWait wait = new WebDriverWait(driver, 20);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By
                .id("doc-list")));

        // verify count of patents and patent number matches with actual
        Set<String> pathPatents = new HashSet<String>(), xlsPatents = new HashSet<String>();
        List<WebElement> patentlist = driver.findElements(By.cssSelector(prop
                .getProperty("PatentNo")));
        for (WebElement patent : patentlist) {
            pathPatents.add(patent.getText());
        }
        for (ExcelData dd : xls.getData()) {
            xlsPatents.add(dd.getPatentNumber());
        }

        if (patentlist.size() == xls.getData().size()) {
            xlsPatents.removeAll(pathPatents);
            if (xlsPatents.isEmpty())
                ;
            Reporter.log("Pass- Patents count is correct", true);
        }

        // click on 2nd element on patent list and test detail pane
        try {
            if (patentlist.get(1).isDisplayed()) {
                String webPatentNo = patentlist.get(1).getText();
                WebElement secondElement = (new WebDriverWait(driver, 50))
                        .until(ExpectedConditions.visibilityOfElementLocated(By
                                .cssSelector("ul#doc-list li:nth-child(2)>div span")));
                secondElement.click();
                Thread.sleep(2000);

                for (ExcelData dd : xls.getData()) {
                    if (dd.getPatentNumber().equals(webPatentNo)) {
                        tagCount = dd.getTaxonomy().size();
                    }

                }

                // test details of abstract tab
                driver.findElement(By.linkText("Abstract")).click();
                WebElement ab = (new WebDriverWait(driver, 60))
                        .until(ExpectedConditions.visibilityOfElementLocated(By
                                .cssSelector(prop.getProperty("abstractInfo"))));

                abstractText = driver.findElement(
                        By.cssSelector(prop.getProperty("abstractInfo")))
                        .getText();
                for (ExcelData dd : xls.getData()) {
                    if (dd.getPatentNumber().equals(webPatentNo)) {
                        abstractExcelText = dd.getAbstract();
                        break;
                    }
                }

                // test details of Claims tab
                driver.findElement(By.linkText("Claims")).click();

                String text = driver.findElement(
                        By.cssSelector(prop.getProperty("collapsedlink")))
                        .getAttribute("aria-expanded");
                if (text.equals("false")) {
                    click("collapsedlink");
                }
                Thread.sleep(3000);
                WebElement claims = (new WebDriverWait(driver, 60))
                        .until(ExpectedConditions.visibilityOfElementLocated(By
                                .cssSelector(prop.getProperty("claimsInfo"))));

                claimsText = claims.getText();

                for (ExcelData dd : xls.getData()) {
                    if (dd.getPatentNumber().equals(webPatentNo)) {
                        claimsExcelText = dd.getClaims();
                        break;
                    }
                }

                // test taxonomy pane(count is correct)
                List<WebElement> list1 = driver.findElements(By
                        .cssSelector(prop.getProperty("count")));
                int tagCountWeb = list1.size();
                /*
                 * if (tagCount == tagCountWeb) {
                 * Reporter.log("Taxonomy tag count is correct.", true); } else
                 * { Reporter.log("Taxonomy tag count is not correct.", true); }
                 */
                softassert.assertEquals(tagCountWeb, tagCount);
                compareText(abstractExcelText, abstractText);
                compareText(claimsExcelText.replaceAll("\\s+", ""),
                        claimsText.replaceAll("\\s+", ""));
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();

        }

        // verify query is saved, click on visualize result and make bubble
        // chart

        click("nav_dropdown");
        navigateTo("nav_list", "Administration");
        click("homeButton");
        navigateTo("projectlist2", projectcreated);
        click("search_tab");
        try {
            WebElement saved_queries = (new WebDriverWait(driver, 50))
                    .until(ExpectedConditions.visibilityOfElementLocated(By
                            .cssSelector("a.btn-item")));
            if (saved_queries.isDisplayed()) {
                saved_queries.click();
            }

            List<WebElement> tableValue = driver.findElements(By
                    .cssSelector(prop.getProperty("historyTable_queryName")));
            for (WebElement we : tableValue) {
                if (we.getText().equals("MyQuery")) {
                    String Resultcount = driver.findElement(
                            By.cssSelector(prop
                                    .getProperty("historyTable_resultCount")))
                            .getText();
                    Reporter.log("Query is saved and result count is: "
                            + Resultcount, true);
                    click("visualizeResult");
                    WebElement analytics = (new WebDriverWait(driver, 60))
                            .until(ExpectedConditions.visibilityOfElementLocated(By
                                    .cssSelector(prop
                                            .getProperty("analyticsTab"))));
                    select_value("chartTypeDropdown", "bubble_Custom");
                    select_value("parameter1", "priority_year");
                    select_value("parameter2", "assignees");
                    click("createButton");
                    AssertChart();
                    break;
                }

            }

        } catch (Exception e) {
            Reporter.log("There is no saved search in this project", true);
        }

    }

    public void SaveQuery() throws InterruptedException {
        click("queryActionsDropdown");
        navigateTo("queryList", "Save Search");
        input2("queryName", "MyQuery");
        click("saveButton");
        Thread.sleep(3000);
    }

    public void AssertChart() {
        WebElement chart = (new WebDriverWait(driver, 60))
                .until(ExpectedConditions.visibilityOfElementLocated(By
                        .cssSelector(prop.getProperty("chart"))));

        // click("chart");
        WebElement yaxisLabel = driver.findElement(By.cssSelector(prop
                .getProperty("y-axisText")));

        WebElement xaxisLabel = driver.findElement(By.cssSelector(prop
                .getProperty("x-axisText")));

        Reporter.log("X-axis label is: " + xaxisLabel.getText()
                + " Y axis label is: " + yaxisLabel.getText(), true);

        List<WebElement> bubbles = driver.findElements(By.cssSelector(prop
                .getProperty("bubble")));
        int i = 1;
        for (WebElement we : bubbles) {

            Actions act = new Actions(driver);
            act.moveToElement(we).build().perform();
            WebElement tooltip = (new WebDriverWait(driver, 60))
                    .until(ExpectedConditions.visibilityOfElementLocated(By
                            .cssSelector(prop.getProperty("tooltip"))));
            String tooltiptext = tooltip.getText();
            Reporter.log("Tooltip text for bubble " + i + " is " + tooltiptext,
                    true);
            i++;
        }
        softassert.assertAll();
    }
}
