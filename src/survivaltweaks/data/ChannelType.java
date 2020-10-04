package survivaltweaks.data;

import java.util.concurrent.atomic.AtomicInteger;

public class ChannelType {
	public static AtomicInteger Generator = new AtomicInteger(2);
	
	public static final ChannelType GLOBAL = new ChannelType(0, 'G', "global", true);
	public static final ChannelType LOCAL = new ChannelType(1, 'L', "local", true);
	
	private int id;
	private char prefix;
	private String name;
	private boolean persistent;
	
	ChannelType(int id, char prefix, String name, boolean persistent){
		this.id = id;
		this.prefix = prefix;
		this.name = name;
		this.persistent = persistent;
	}
	
	public static ChannelType createNew(char prefix, String name, boolean persistent) {
		return new ChannelType(Generator.getAndIncrement(), prefix, name, persistent);
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPrefix() {
		return "§b[§e" + prefix + "§b]";
	}
	
	public boolean isPersistent() {
		return persistent;
	}
	
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		ChannelType that = (ChannelType) o;
		
		if (id != that.id) return false;
		if (prefix != that.prefix) return false;
		if (persistent != that.persistent) return false;
		if (!name.equals(that.name)) return false;
		
		return true;
	}
	
	public int hashCode() {
		int result = id;
		result = 31 * result + (int) prefix;
		result = 31 * result + name.hashCode();
		result = 31 * result + (persistent ? 1 : 0);
		return result;
	}
	
	public String toString() {
		return "ChannelType{" +
				"id=" + id +
				", prefix=" + prefix +
				", name='" + name + '\'' +
				", persistent=" + persistent +
				'}';
	}
}
