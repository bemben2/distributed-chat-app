package client;

public class User {
	//private PrintWriter
	private String name;
	private String iconName;
	
	public User(String name, String iconName) {
		this.name = name;
		this.iconName = iconName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
