package net.nature.mobile.rest;

import java.util.Date;

import net.nature.mobile.model.Account;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
public class AccountTest {
	private Account newAccount;

	@Before
	public void setUp(){
		ShadowLog.stream = System.out;
		newAccount = new Account();
		newAccount.setUsername("n" + (new Date()).getTime());
		newAccount.name = "first last";
		newAccount.setEmail("new@email.com");		
	}
	
	@Test
	public void load_from_remote(){		
		Account account = Account.loadFromRemote("tomyeh");
		assertThat(account, notNullValue());	
	}
	
	@Test
	public void load_from_remote_with_bad_username(){		
		Account account = Account.loadFromRemote("badusername");
		assertThat(account, nullValue());	
	}
	
	@Test
	public void find_by_username(){
		Account account = Account.findByUsername("tomyeh");
		// should auto load remotely
		assertThat(account, notNullValue());		
	}
	
	@Test
	public void find_by_username_locally_fail(){
		Account account = Account.findByUsernameLocally("tomyeh"); 
		assertThat(account, nullValue());		
	}
	
	@Test
	public void find_by_username_locally_succeed(){
		newAccount.save();
		Account account = Account.findByUsernameLocally(newAccount.getUsername()); 
		assertThat(account, notNullValue());		
	}		
	
	@Test
	public void new_account_does_not_exists_remotely(){
		assertThat(newAccount.existsRemotely(), equalTo(false));
	}
	
	@Test
	public void new_account_exists_remotely_after_save_to_remote(){
		newAccount.saveToRemote();
		assertThat(newAccount.existsRemotely(), equalTo(true));
	}
	
	@Test
	public void save_remotely_for_an_account_that_already_exists_remotely(){
		Account tomyeh = Account.loadFromRemote("tomyeh");		
		tomyeh.saveToRemote();
	}
	
}
