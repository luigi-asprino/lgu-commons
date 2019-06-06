package it.cnr.istc.stlab.lgu.commons.sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SetRand<E> extends HashSet<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SetRand(Collection<E> coll) {
		super(coll);
	}

	public E getRandomObject() {
		int item = new Random().nextInt(this.size());
		int i = 0;
		for (E obj : this) {
			if (i == item)
				return obj;
			i++;
		}
		return null;
	}

	public List<E> getRandomizedList() {
		List<E> list = new ArrayList<E>(this);
		Collections.shuffle(list, new Random());
		return list;
	}

	public Set<E> getRandomItems(int n) {
		List<E> list = new ArrayList<E>(this);
		Set<E> result = new HashSet<>();
		Collections.shuffle(list, new Random());
		for (int i = 0; i < n && i<list.size(); i++) {
			result.add(list.get(i));
		}
		return result;
	}

}
