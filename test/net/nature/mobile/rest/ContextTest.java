package net.nature.mobile.rest;

import java.util.Date;

import net.nature.mobile.model.Account;
import net.nature.mobile.model.Context;
import net.nature.mobile.model.NNModel;
import net.nature.mobile.model.Note;
import net.nature.mobile.model.NNModel.STATE;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowLog;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(RobolectricTestRunner.class)
public class ContextTest {

	@Before
	public void setUp(){
		ShadowLog.stream = System.out;
	}
	
	@Test
	public void download_context(){
		Context context = NNModel.resolveByUID(Context.class, 1L);
		//System.out.println(context.getExtras().get("latitud"));
	}
		
}
