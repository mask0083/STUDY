package org.codelab.batch.dto;

import lombok.Data;

@Data
public class Person {
	private int personId;
	private String firstName;
	private String lastName;
	
	public Person() {}

	public Person(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}
}


