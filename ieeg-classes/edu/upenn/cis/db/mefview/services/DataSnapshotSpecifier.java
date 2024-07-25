/*
 * Copyright 2012 Trustees of the University of Pennsylvania
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.upenn.cis.db.mefview.services;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * TODO: implement this class!
 * 
 * @author zives
 * 
 */

@XmlRootElement(name = "findSnapshot")
public class DataSnapshotSpecifier {
	public DataSnapshotSpecifier() {

	}

	public DataSnapshotSpecifier(String str) {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(DataSnapshotSpecifier.class);

			Unmarshaller unmarshaller = context.createUnmarshaller();

			StringReader sr = new StringReader(str);

			unmarshaller.unmarshal(sr);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(DataSnapshotSpecifier.class);
			Marshaller marshaller = context.createMarshaller();
			StringWriter sw = new StringWriter();

			marshaller.marshal(this, sw); // save to StringWriter, you can then
											// call sw.toString() to get
											// java.lang.String
			return sw.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
