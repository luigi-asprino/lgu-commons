package it.cnr.istc.stlab.lgu.commons.entitylinking.model;

import java.util.ArrayList;
import java.util.List;

public class Mention {

	private int begin, end;
	private String mention;
	private List<Object> mentionedEntities = new ArrayList<>();

	public Mention(int begin, int end, String mention) {
		super();
		this.begin = begin;
		this.end = end;
		this.mention = mention;
	}
	
	public Mention() {
		
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public String getMention() {
		return mention;
	}

	public void setMention(String mention) {
		this.mention = mention;
	}

	public void addMentionedEntities(Object mentionedEntity) {
		this.mentionedEntities.add(mentionedEntity);
	}

	public List<Object> getMentionedEntities() {
		return this.mentionedEntities;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + ((mention == null) ? 0 : mention.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mention other = (Mention) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (mention == null) {
			if (other.mention != null)
				return false;
		} else if (!mention.equals(other.mention))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mention [begin=" + begin + ", end=" + end + ", mention=" + mention + ", mentionedEntities=" + mentionedEntities + "]";
	}

}
