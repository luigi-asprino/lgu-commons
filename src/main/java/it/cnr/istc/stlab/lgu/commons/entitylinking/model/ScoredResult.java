package it.cnr.istc.stlab.lgu.commons.entitylinking.model;

public class ScoredResult {

	private double score;
	private Object entity;
	
	public ScoredResult() {
		super();
	}

	public ScoredResult(double score, Object person) {
		super();
		this.score = score;
		this.entity = person;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}

	@Override
	public String toString() {
		return "ScoredResult [score=" + score + ", entity=" + entity + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		ScoredResult other = (ScoredResult) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}

}
