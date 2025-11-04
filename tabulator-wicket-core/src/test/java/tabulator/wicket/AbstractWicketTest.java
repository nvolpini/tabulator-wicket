package tabulator.wicket;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.jupiter.api.BeforeEach;

public class AbstractWicketTest {

    protected WicketTester tester;

    @BeforeEach
    public void setup() {
        tester = new WicketTester(new WebApplication() {
            @Override
            public Class<? extends Page> getHomePage() {
                return DummyPage.class;
            }

            @Override
            public void init() {
                ITabulatorSettings settings = new TabulatorSettings();
                settings.setUseCdn(true);
                TabulatorWicketPlugin.install(this, settings);
            }
        });
    }
}
