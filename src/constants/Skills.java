package constants;

public enum Skills {
	
	MAGIC_GUARD(2001002),
	BLESS(2301004),
	ADVANCED_BLESS(2321005),
	HASTE(4001005),
	LEVELUP(80001770);
	
	private int skillid;
	
	private Skills(final int skillid) {
		this.skillid = skillid;
	}
	
	public int getId() {
		return skillid;
	}
}
