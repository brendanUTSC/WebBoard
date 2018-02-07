package acceptance;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.athaydes.automaton.FXer;

import client.scenes.AssignmentSelectionScene;
import database.DatabaseTest;
import javafx.scene.control.Label;

public class LoginAcceptanceTest extends DatabaseTest {

	private static final String USERNAME = "user";
	private static final String PASSWORD = "password";
	private FXer fxer;

	@Before
	public void setUp() {
		fxer = AcceptanceTestUtils.openLoginScreen();
	}

	@Test
	public void testLoginSucceeds() {
		AcceptanceTestUtils.login(fxer);
		Label headerLabel = (Label) fxer.getAt("#header-label");
		Assert.assertTrue(headerLabel.getText().equals(AssignmentSelectionScene.HEADER));
	}

	@Test
	public void testLoginWithIncorrectUsername() {
		fxer.clickOn("#username-box").type("not-a-valid-user").waitForFxEvents();
		fxer.clickOn("#password-box").type(PASSWORD).waitForFxEvents();
		fxer.clickOn("#login-button").waitForFxEvents();
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
		Label invalidLoginLabel = (Label) fxer.getAt("#invalid-login-label");
		Assert.assertEquals("Invalid username. Please sign up first!", invalidLoginLabel.getText());
	}

	@Test
	public void testLoginWithIncorrectPassword() {
		fxer.clickOn("#username-box").type(USERNAME).waitForFxEvents();
		fxer.clickOn("#password-box").type("not-a-valid-password").waitForFxEvents();
		fxer.clickOn("#login-button").waitForFxEvents();
		fxer.pause(AcceptanceTestUtils.LONG_DELAY);
		Label invalidLoginLabel = (Label) fxer.getAt("#invalid-login-label");
		Assert.assertEquals("Invalid password. Please try again!", invalidLoginLabel.getText());
	}
}
