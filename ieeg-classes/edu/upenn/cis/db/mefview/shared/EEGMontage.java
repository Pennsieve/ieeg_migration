package edu.upenn.cis.db.mefview.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible(serializable = true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class EEGMontage implements Serializable, IHasName {

	private static final long serialVersionUID = 1L;
	private String name;
	private List<EEGMontagePair> pairs = new ArrayList<EEGMontagePair>();
	private Long serverId;
	private Boolean isPublic = false;
	private Boolean ownedByUser = false;

	public EEGMontage() {}

	public EEGMontage(String name) {
		this.name = name;
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElementWrapper(name = "montagePairs")
	@XmlElement(name = "montagePair")
	public List<EEGMontagePair> getPairs() {
		return pairs;
	}

	public String toString() {
		return "Montage ('" + name + "') with " + pairs.size()
				+ " montage pairs.";
	}

	public void setServerId(Long id) {
		this.serverId = id;

	}

	@XmlAttribute
	public Long getServerId() {
		return serverId;
	}

	@XmlAttribute(name = "public")
	public Boolean getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		if(isPublic != null)
			this.isPublic = isPublic;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pairs == null) ? 0 : pairs.hashCode());
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
		EEGMontage other = (EEGMontage) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pairs == null) {
			if (other.pairs != null)
				return false;
		} else if (!pairs.equals(other.pairs))
			return false;
		return true;
	}

	public Boolean getOwnedByUser() {
		return ownedByUser;
	}

	public void setOwnedByUser(Boolean ownedByUser) {
		this.ownedByUser = ownedByUser;
	}

}
