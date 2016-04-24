package server;

import java.util.LinkedHashMap;
import java.util.Map;

import constants.Skills;
import server.skills.*;

public class SkillProcessor {

	private SkillHandler[] handlers;
	
	private SkillProcessor() {
		int maxSkillId = 0;
		for (Skills skill : Skills.values()) {
			if(skill.getId() > maxSkillId) {
				maxSkillId = skill.getId();
			}
		}
		
		handlers = new SkillHandler[maxSkillId + 1];
	}
	
	public SkillHandler getHandler(int skillId) {
		/*
		if(skillId > handlers.length) {
			return null;
		}
		*/
		try {
			SkillHandler handler = handlers[skillId];
			if(handler != null) {
				return handler;
			}
		} catch (Exception ex) {
		}
		return new AbstractSkillHandler();
	}
	
	public void registerHandler(Skills skill, SkillHandler handler) {
		try {
			handlers[skill.getId()] = handler;
		} catch (ArrayIndexOutOfBoundsException ex) {
			System.out.println("Error registering skill handler " + skill.getId());
		}
	}
	
	public synchronized static SkillProcessor getProcessor() {
		SkillProcessor processor = new SkillProcessor();
		processor.reset();
		return processor;
	}
	
	public void reset() {
		handlers = new SkillHandler[handlers.length];
		
		registerHandler(Skills.HASTE, new HasteSkill());
	}
}
