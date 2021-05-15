package jp.co.newrelikk.labs;

public class Authentication {
	
	private String username;
	private String password;
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isConfigured() {
		return username != null && !username.isEmpty() && password != null && !password.isEmpty();
	}

}
