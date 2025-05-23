package app.docuport.utilities;


//import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.concurrent.TimeUnit;

public class Driver {

    /*
     Creating the private constructor so this class' object
     is not reachable from outside
      */
    private Driver() {
    }

    /*
    Making our 'driver' instance private so that it is not reachable from outside of the class.
    We make it static, because we want it to run before everything else, and also we will use it in a static method
     */
    //private static ThreadLocal<WebDriver> driverPool = new ThreadLocal<>();
    private static InheritableThreadLocal <WebDriver> dr = new InheritableThreadLocal<>();
    /*
    Creating re-usable utility method that will return same 'driver' instance everytime we call it.
     */
    public static WebDriver getDriver() {

        if (dr.get() == null) {

            synchronized (Driver.class) {
            /*
            We read our browser type from configuration.properties file using
            .getProperty method we creating in ConfigurationReader class.
             */
                String browserType = ConfigurationReader.getProperty("browser");

            /*
            Depending on the browser type our switch statement will determine
            to open specific type of browser/driver
             */
                switch (browserType) {
                    case "chrome":

                        //WebDriverManager.chromedriver().setup();
                        dr.set(new ChromeDriver());
                        dr.get().manage().window().maximize();
                        dr.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                        break;
                    case "firefox":
                        //WebDriverManager.firefoxdriver().setup();
                        dr.set(new FirefoxDriver());
                        dr.get().manage().window().maximize();
                        dr.get().manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
                        break;
                }
            }
        }

        /*
        Same driver instance will be returned every time we call Driver.getDriver(); method
         */
        return dr.get();


    }

    /*
    This method makes sure we have some form of driver sesion or driver id has.
    Either null or not null it must exist.
     */
    public static void closeDriver() {
        if (dr.get() != null) {
            dr.get().quit();
            dr.remove();
        }
    }

}