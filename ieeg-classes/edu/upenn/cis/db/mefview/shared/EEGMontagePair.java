package edu.upenn.cis.db.mefview.shared;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.annotations.GwtCompatible;

/**
 * EEGMontage pair describes pair of electrode in montage el2 can be null which
 * means that El1 is plotted as recorded.
 * 
 * @author joost
 *
 */
@GwtCompatible(serializable = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class EEGMontagePair implements Serializable {

	private static final long serialVersionUID = 1L;
	private String el1;
	private String el2;

	@SuppressWarnings("unused")
	private EEGMontagePair() {}

	public EEGMontagePair(String el1, @Nullable String el2) {
		this.el1 = checkNotNull(el1);
		this.el2 = el2;
	}
	
	@XmlAttribute(name = "channel")
	public String getEl1() {
		return el1;
	}

	@XmlAttribute(name = "refChannel")
	public String getEl2() {
		return el2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((el1 == null) ? 0 : el1.hashCode());
		result = prime * result + ((el2 == null) ? 0 : el2.hashCode());
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
		EEGMontagePair other = (EEGMontagePair) obj;
		if (el1 == null) {
			if (other.el1 != null)
				return false;
		} else if (!el1.equals(other.el1))
			return false;
		if (el2 == null) {
			if (other.el2 != null)
				return false;
		} else if (!el2.equals(other.el2))
			return false;
		return true;
	}

	public void setEl1(String el1) {
		this.el1 = checkNotNull(el1);
	}

	public void setEl2(@Nullable String el2) {
		this.el2 = el2;
	}
	
	public String toString(){
		if (this.el2==null){
			return this.el1;
		}else {
			return this.el1 + " vs " + this.el2;
		}
	}
	

}
