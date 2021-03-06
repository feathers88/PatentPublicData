package gov.uspto.patent;

import java.io.IOException;
import java.io.Reader;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import gov.uspto.patent.doc.greenbook.Greenbook;
import gov.uspto.patent.doc.pap.PatentAppPubParser;
import gov.uspto.patent.doc.sgml.Sgml;
import gov.uspto.patent.doc.xml.ApplicationParser;
import gov.uspto.patent.doc.xml.GrantParser;
import gov.uspto.patent.model.Patent;

/**
 * Detect Patent Document Type and/or Parse Document into Patent Object
 * 
 * @author Brian G. Feldman (brian.feldman@uspto.gov)
 *
 */
public class PatentReader implements PatentDocReader<Patent> {

	private PatentDocFormat patentDocFormat;

	/**
	 * Load Reader
	 *  
	 * @param reader
	 * @param PatentType
	 */
	public PatentReader(final PatentDocFormat patentDocFormat) {
		Preconditions.checkNotNull(patentDocFormat, "patentType can not be Null");
		this.patentDocFormat = patentDocFormat;
	}

	/**
	 * Parse Dom4j Document
	 * 
	 * @param document
	 */
	/*
	public PatentReader(Document document) {
		this.document = document;
		this.reader = new StringReader(document.asXML());
	}
	*/

	/**
	 * Parse Document and Return Patent Object.
	 * 
	 * @param reader
	 * @return
	 * @throws PatentReaderException
	 * @throws IOException 
	 */
	@Override
    public Patent read(Reader reader) throws PatentReaderException, IOException {
        Preconditions.checkNotNull(reader, "reader can not be Null");

		switch (patentDocFormat) {
		case Greenbook:
			return new Greenbook().parse(reader);
		case RedbookApplication:
			return new ApplicationParser().parse(getJDOM(reader));
		case RedbookGrant:
			return new GrantParser().parse(getJDOM(reader));
		case Sgml:
			return new Sgml().parse(getJDOM(reader));
		case Pap:
			return new PatentAppPubParser().parse(getJDOM(reader));
		default:
			throw new PatentReaderException("Invalid or Unknown Document Type");
		}
	}

	/**
	 * Load XML Document
	 * 
	 * @param reader
	 * @return
	 * @throws PatentReaderException
	 */
	public static Document getJDOM(Reader reader) throws PatentReaderException {
		try {
			SAXReader sax = new SAXReader(false);
			sax.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			return sax.read(reader);
		} catch (DocumentException | SAXException e) {
			throw new PatentReaderException("Failed to load XML", e);
		}
	}
	
}
