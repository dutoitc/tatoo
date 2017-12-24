package ch.mno.tatoo.facade.tac.commands;

import org.json.simple.JSONObject;

/**
 * ----------------------------------------------------------
 Command: createTag
 ----------------------------------------------------------
 Description             : Create a tag into a project
 Requires authentication : true
 Since                   : 5.3
 Sample                  :
 {
 "actionName": "createTag",
 "authPass": "admin",
 "authUser": "admin@company.com",
 "projectName": "project1",
 "source": "trunk",
 "target": "tags/tag-1_0"
 }
 Specific error codes    :
 151: Database error while creating tag.
 */
public class CreateTagCommand extends AbstractCommand<Void> {

	private String projectName;
	private String tagname;

	public static CreateTagCommand build(String projectName, String tagname) {
		return new CreateTagCommand(projectName, tagname);
	}

	public CreateTagCommand(String projectName, String tagname) {
		super("createTag");
		this.projectName = projectName;
		this.tagname = tagname;
	}

	@Override
	public void completeObject(JSONObject obj) {
		super.completeObject(obj);
		obj.put("projectName", projectName);
		obj.put("source", "trunk");
		obj.put("target", "tags/"+tagname);
	}

	@Override
	public Void getData() {
		return null;
	}

	@Override
	public void keepResults(JSONObject result) {
		// TODO
	}

	@Override
	public String toString() {
		return "CreateTagCommand["+projectName+","+tagname+"]";
	}


}