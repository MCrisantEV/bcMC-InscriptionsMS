package mc.bc.ms.inscriptions.app.models;

import lombok.Data;

@Data
public class Course {
	private String id;
	private String institute;
	private String name;
	private String teacher;
	private int min;
	private int max;
	private String state;

}
