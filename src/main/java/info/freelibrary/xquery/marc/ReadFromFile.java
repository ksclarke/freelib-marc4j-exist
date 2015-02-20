
package info.freelibrary.xquery.marc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.sax.SAXResult;

import org.apache.log4j.Logger;
import org.exist.EXistException;
import org.exist.collections.Collection;
import org.exist.dom.QName;
import org.exist.memtree.DocumentBuilderReceiver;
import org.exist.memtree.DocumentImpl;
import org.exist.memtree.MemTreeBuilder;
import org.exist.security.PermissionDeniedException;
import org.exist.storage.DBBroker;
import org.exist.storage.txn.TransactionManager;
import org.exist.storage.txn.Txn;
import org.exist.util.LockException;
import org.exist.xmldb.XmldbURI;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.BooleanValue;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;
import org.marc4j.MarcException;
import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlWriter;
import org.marc4j.marc.Record;
import org.xml.sax.SAXException;

import info.freelibrary.marc4j.converter.impl.AnselToUnicode;
import info.freelibrary.util.IOUtils;

/**
 * Reads MARC data from the file system into a MARCXML record.
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 */
public class ReadFromFile extends BasicFunction {

    private final static Logger LOGGER = Logger.getLogger(ReadFromFile.class);

    private final static String UTF8 = "UTF-8";

    private final static String FN_READ_LN = "read";

    private final static String FN_STORE_LN = "store";

    public final static FunctionSignature[] SIGNATURES = new FunctionSignature[] {
        new FunctionSignature(new QName("read", MARCModule.NAMESPACE_URI, MARCModule.PREFIX),
                "Reads the content of a MARC file. This method is only available to the DBA role.",
                new SequenceType[] { new FunctionParameterSequenceType("path", Type.STRING, Cardinality.EXACTLY_ONE,
                        "The URI or file system location of a MARC file to read") }, new FunctionReturnSequenceType(
                        Type.DOCUMENT, Cardinality.EXACTLY_ONE, "An in-memory MARCXML document")),
        new FunctionSignature(new QName("store", MARCModule.NAMESPACE_URI, MARCModule.PREFIX),
                "Stores contents of a MARC file into a collection. This method is only available to the DBA role.",
                new SequenceType[] {
                    new FunctionParameterSequenceType("path", Type.STRING, Cardinality.EXACTLY_ONE,
                            "The URI or file system location of a MARC file to read"),
                    new FunctionParameterSequenceType("collection", Type.STRING, Cardinality.EXACTLY_ONE,
                            "The path of the collection into which the records should be stored") },
                new FunctionReturnSequenceType(Type.BOOLEAN, Cardinality.EXACTLY_ONE,
                        "A boolean indicating whether all documents were stored as expected")) };

    /**
     * @param aContext An XQuery context
     * @param aSignature A function signature
     */
    public ReadFromFile(final XQueryContext aContext, final FunctionSignature aSignature) {
        super(aContext, aSignature);
    }

    /*
     * (non-Javadoc)
     * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)
     */
    @Override
    public Sequence eval(final Sequence[] aSequenceArray, final Sequence aContextSequence) throws XPathException {

        if (!context.getSubject().hasDbaRole()) {
            throw new XPathException(this, ErrorCodes.MARC0001, ErrorCodes.MARC0001.getDescription(),
                    new StringValue(context.getSubject().getName()));
        }

        if (!isCalledAs(FN_READ_LN) && !isCalledAs(FN_STORE_LN)) {
            throw new XPathException(this, "Undefined function");
        }

        final String filePath = aSequenceArray[0].getStringValue();
        final File marcFile = MARCModuleHelper.getFile(filePath);

        InputStream inputStream = null;
        MarcReader reader;

        try {
            inputStream = new FileInputStream(marcFile);
            reader = new MarcStreamReader(inputStream);

            if (isCalledAs(FN_READ_LN)) {
                return readMARCRecords(reader);
            } else {
                return storeMARCRecords(reader, aSequenceArray[1].getStringValue());
            }
        } catch (final MarcException details) {
            throw new XPathException(ErrorCodes.MARC0007, ErrorCodes.MARC0007.getDescription(), new StringValue(
                    details.getMessage()), details);
        } catch (final IOException details) {
            throw new XPathException(ErrorCodes.MARC0012, ErrorCodes.MARC0012.getDescription(), new StringValue(
                    details.getMessage()), details);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private Sequence readMARCRecords(final MarcReader aReader) throws XPathException {
        final MemTreeBuilder builder = context.getDocumentBuilder();
        final DocumentBuilderReceiver receiver = new DocumentBuilderReceiver(builder);
        final SAXResult saxResult = new SAXResult(receiver);
        final MarcWriter writer = new MarcXmlWriter(saxResult);

        writer.setConverter(new AnselToUnicode());

        while (aReader.hasNext()) {
            final Record record = aReader.next();
            final String recordId = record.getControlNumber();

            if (LOGGER.isDebugEnabled() && recordId != null) {
                LOGGER.debug("Reading MARC record: " + recordId);
            }

            writer.write(record);
        }

        writer.close();

        return (DocumentImpl) receiver.getDocument();
    }

    private Sequence storeMARCRecords(final MarcReader aReader, final String aCollectionPath) throws XPathException {
        final XmldbURI collectionPath = XmldbURI.create(getCollectionPath(aCollectionPath));
        final DBBroker broker = context.getBroker();
        final TransactionManager txnManager = broker.getBrokerPool().getTransactionManager();

        final Txn txn = txnManager.beginTransaction();

        try {
            final Collection coll = broker.getOrCreateCollection(txn, collectionPath);

            broker.saveCollection(txn, coll);
            broker.flush();

            while (aReader.hasNext()) {
                final ByteArrayOutputStream xmlStream = new ByteArrayOutputStream();
                final Record record = aReader.next();
                final String recordId = record.getControlNumber();
                final XmldbURI name = XmldbURI.create(recordId + ".xml");
                final String xml;

                if (LOGGER.isDebugEnabled() && recordId != null) {
                    LOGGER.debug("Reading MARC record: " + recordId);
                }

                MarcXmlWriter.writeSingleRecord(record, xmlStream, false);
                xml = new String(xmlStream.toByteArray());

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Writing MARCXML record: " + recordId);
                    LOGGER.debug(xml);
                }

                coll.store(txn, broker, coll.validateXMLResource(txn, broker, name, xml), xml, true);
            }

            txnManager.commit(txn);
        } catch (final SAXException details) {
            txnManager.abort(txn);
            throw new XPathException(ErrorCodes.MARC0009, ErrorCodes.MARC0009.getDescription(), details);
        } catch (final LockException | IOException | EXistException details) {
            txnManager.abort(txn);
            throw new XPathException(this, details.getMessage(), details);
        } catch (final PermissionDeniedException details) {
            txnManager.abort(txn);
            throw new XPathException(this, "Permission denied exception", details);
        } finally {
            txnManager.close(txn);
        }

        return BooleanValue.TRUE;
    }

    private String getCollectionPath(final String aCollectionPath) {
        final StringBuilder builder = new StringBuilder();

        if (!aCollectionPath.startsWith(XmldbURI.ROOT_COLLECTION)) {
            builder.append(XmldbURI.ROOT_COLLECTION);

            if (!aCollectionPath.startsWith("/")) {
                builder.append('/');
            }
        }

        return builder.append(aCollectionPath).toString();
    }
}
