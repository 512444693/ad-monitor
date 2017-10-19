package com.baofeng.ad.mobiletest.appium;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.remote.DesiredCapabilities;
import io.appium.java_client.android.AndroidDriver;

import static com.zm.frame.log.Log.log;

public class StorminAppium {
    private String deviceName;
    private String platformVersion;
    private String appPackage;
    private String appActivity;
    private String url;
    private AndroidDriver driver;

    public StorminAppium(String deviceName, String platformVersion, String appPackage, String appActivity, String url) {
        this.deviceName = deviceName;
        this.platformVersion = platformVersion;
        this.appPackage = appPackage;
        this.appActivity = appActivity;
        this.url = url;
    }
    public AndroidDriver Devicefun() throws IOException,MalformedURLException{

        DesiredCapabilities cap=new DesiredCapabilities();
        cap.setCapability("automationName", "Appium");
        cap.setCapability("deviceName", deviceName);                           // 设备名称
        cap.setCapability("platformName", "Android");                  // 安卓自动化还是IOS自动化
        cap.setCapability("platformVersion", platformVersion);
        cap.setCapability("appPackage",appPackage);                            // 被测app的包名
        cap.setCapability("appActivity",appActivity);
        cap.setCapability("unicodeKeyboard", "True");
        cap.setCapability("resetKeyboard", "True");

        driver = new AndroidDriver(new URL(url),cap);
        //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        log.info("Devicefun() called");
        return driver;
    }

    public Boolean SwipeToUp(int during,int num) throws Exception{
        log.info("<wu:> swip start!");
        if (driver.findElementByName("推荐").isDisplayed()){
            int width  = driver.manage().window().getSize().width;                  // 获取当前屏幕的宽度
            int height = driver.manage().window().getSize().height;                 // 获取当前屏幕的高度
            for(int i=0;i<num;i++){
                driver.swipe(width/2, height*3/4, width/2, height/4, during);
                //System.out.println("swipe"+i );
            }
            log.info("<wu:> swip finish!");
            return Boolean.TRUE;
        }
        else{
            throw new Exception("error in swip page!");
        }
    }
    public void closedriver(){
        if ( driver != null ) {
            driver.quit();
            log.info("closedriver() called");
        }
    }
    public Boolean StartStorm() throws MalformedURLException{
        int i=0;
        Boolean result=false;
        while ((i++)<10) {
            log.info("<wu:> startstorm "+i+" time");
            try {
                Devicefun();
                Thread.sleep(10000);
                SwipeToUp(4000, 45);
                closedriver();
                result = true;
                break;
            } catch (MalformedURLException e) {
                log.error("<wu:> new AndroidDriver error!");
                log.error(e);
                closedriver();
                throw new MalformedURLException("start APP failed in new AndroidDriver!");
            } catch (Exception e) {
                log.error("<wu:> run storm failed!");
                log.error(e);
                closedriver();
                result = false;
            }

        }
        return result;
    }
    public static void main(String[] args){
        StorminAppium appi = new StorminAppium("DU2SSE149P051713", "4.4.2",
                "com.storm.smart", "com.storm.smart.activity.MainActivity",
                "http://127.0.0.1:4723/wd/hub");
        try{
            appi.StartStorm();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
