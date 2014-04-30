package net.nature.mobile.rest;

import net.nature.mobile.model.Note;

import com.google.common.base.Objects;

public class NoteJson {

	public String content;	
	public String kind;
	public Long id;
	public Account account;
	public Context context;
	
	static public class Account {
		public Long id;
		public String username;
	}
	
	static public class Context{
		public String kind;
		public Long id;
	}	
	
	public String toString(){
		return Objects.toStringHelper(this).
				add("kind", kind).
				add("content", content).				
				add("id", id).
				add("account", account.id).
				toString();
	}
	
//	public Note toNote(){
//		Note note = new Note();
//		note.content = json.content;
//		note.context_id = json.context.id;
//		note.user_id = json.account.id;
//		note.id = json.id;
//	}
//	public Activity create(){
//		
//	}
	
//	Activity createAc
//	Note create(){
//		
//	}
}
