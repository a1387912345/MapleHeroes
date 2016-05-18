package constants;

public enum Skills {
	
	MAGIC_GUARD(2001002),
	BLESS(2301004),
	HEAL(2301002),
	ADVANCED_BLESS(2321005),
	HASTE(4001005),
	SHADOW_WEB(4111003),
	LEVELUP(80001770);
	
	private int skillid;
	
	private Skills(final int skillid) {
		this.skillid = skillid;
	}
	
	public int getId() {
		return skillid;
	}
	
	public boolean equals(final int skillid) { // Debating if this method should be named is() or equals(). is() is shorter to type, but equals() is conventional.
		return this.skillid == skillid;
	}
}
