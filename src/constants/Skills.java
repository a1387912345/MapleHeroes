package constants;

public enum Skills {
	
	MAGIC_GUARD(2001002),
	BLESS(2301004),
	HASTE(4001005);
	
	private int skillid;
	
	private Skills(final int skillid) {
		this.skillid = skillid;
	}
	
	public int getId() {
		return skillid;
	}
}
