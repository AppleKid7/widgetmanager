/**
  * Created by imoreno on 8/13/17.
  */
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.test.WithBrowser

@RunWith(classOf[JUnitRunner])
class IntegrationSpec extends Specification {
  "Application" should {

    "work from within a browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port)

      browser.pageSource must contain("Your database is ready.")
    }

    "remove data through the browser" in new WithBrowser {

      browser.goTo("http://localhost:" + port + "/cleanup")

      browser.pageSource must contain("Your database is clean.")
    }
  }
}
