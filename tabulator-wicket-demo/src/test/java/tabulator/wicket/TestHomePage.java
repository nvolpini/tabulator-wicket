package tabulator.wicket;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tabulator.wicket.demo.DemoApplication;
import tabulator.wicket.demo.DemoHomePage;

/**
 * Simple test using the WicketTester
 */
public class TestHomePage
{
	private WicketTester tester;

	@BeforeEach
	public void setUp()
	{
		tester = new WicketTester(new DemoApplication());
	}

	@Test
	public void homepageRendersSuccessfully()
	{
		//start and render the test page
		tester.startPage(DemoHomePage.class);

		//assert rendered page class
		tester.assertRenderedPage(DemoHomePage.class);
	}
}
