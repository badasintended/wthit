package mcp.mobius.waila.api.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class TipList<E> extends ArrayList<E> {
	Map<E, Set<String>> tags = new HashMap();
	
	public Set<String> getTags(E e){
		Set<String> ret = tags.get(e);
		if (ret == null && this.contains(e)){
			tags.put(e, new HashSet<String>());
			ret = tags.get(e);
		}
		return ret;
	}
	
	public Set<String> getTags(int index){
		return this.getTags(this.get(index));
	}	
	
	public void addTag(E e, String tag){
		if (this.contains(e) && !tags.containsKey(e))
			tags.put(e, new HashSet<String>());
		
		tags.get(e).add(tag);
	}
	
	public void addTag(int index, String tag){
		this.addTag(this.get(index), tag);
	}
	
	public void removeTag(E e, String tag){
		if (this.contains(e) && !tags.containsKey(e))
			tags.put(e, new HashSet<String>());		

		tags.get(e).remove(tag);
	}
	
	public void removeTag(int index, String tag){
		this.removeTag(this.get(index), tag);
	}
	
	public Set<E> getEntries(String tag){
		Set<E> ret = new HashSet();
		for (Entry<E, Set<String>> s : tags.entrySet()){
			if (s.getValue().contains(tag))
				ret.add(s.getKey());
		}
		return ret;
	}	
	
	public String getTagsAsString(E e){
		String ret = "";
		for (String s : tags.get(e))
			ret += s + ",";
		
		if (ret.length() > 0)
			ret = ret.substring(0, ret.length()-1);
		
		return ret;
	}	
	
	public void clear(){
		tags.clear();
		super.clear();
	}	
	
	public E set(int index, E element){
		tags.remove(this.get(index));
		return super.set(index, element);
	}	
	
	public E remove(int index){
		tags.remove(this.get(index));
		return super.remove(index);
	}
	
	public boolean remove(Object o){
		tags.remove(o);
		return super.remove(o);
	}
	
	public boolean removeAll(Collection<?> c){
		for (Object o : c)
			tags.remove(o);
		
		return super.removeAll(c);
	}
	
	protected void removeRange(int fromIndex, int toIndex){
		for (int i = fromIndex; i < toIndex; i++)
			tags.remove(this.get(i));
		
		super.removeRange(fromIndex, toIndex);
	}
	
	public boolean retainAll(Collection<?> c){
		for (E e : tags.keySet())
			if (!c.contains(e))
				tags.remove(e);
		
		return super.retainAll(c);
	}	
	
	/*
	public boolean add(E e){
		this.setInitialTag(e);		
		return super.add(e);
	}
	
	public void add(int index, E element){
		this.setInitialTag(element);		
		super.add(index, element);
	}
	
	public boolean addAll(Collection<? extends E> c){
		if (c instanceof TipList){
			for (E e : c)
				tags.put(e, ((TipList)c).getTags(e));			
		} else {
			for (E e : c)
				this.setInitialTag(e);
		}			
	
		return super.addAll(c);
	}
	
	public boolean addAll(int index, Collection<? extends E> c){
		if (c instanceof TipList){
			for (E e : c)
				tags.put(e, ((TipList)c).getTags(e));			
		} else {
			for (E e : c)
				this.setInitialTag(e);
		}
		
		return super.addAll(index, c);
	}
	
	public void clear(){
		tags.clear();
		super.clear();
	}
	
	public Object clone(){
		return super.clone();
	}
	

	
	private void setInitialTag(E key){
		tags.put(key, new ArrayList());
	}
	
	private String getCallerClassName(int callStackDepth) {
		return sun.reflect.Reflection.getCallerClass(callStackDepth).getName();
	}
	*/
}
