
package info.freelibrary.xquery.marc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.exist.dom.QName;
import org.exist.storage.serializers.Serializer;
import org.exist.util.serializer.Receiver;
import org.exist.util.serializer.ReceiverToSAX;
import org.exist.xquery.BasicFunction;
import org.exist.xquery.Cardinality;
import org.exist.xquery.FunctionSignature;
import org.exist.xquery.XPathException;
import org.exist.xquery.XQueryContext;
import org.exist.xquery.value.BooleanValue;
import org.exist.xquery.value.FunctionParameterSequenceType;
import org.exist.xquery.value.FunctionReturnSequenceType;
import org.exist.xquery.value.NodeValue;
import org.exist.xquery.value.Sequence;
import org.exist.xquery.value.SequenceIterator;
import org.exist.xquery.value.SequenceType;
import org.exist.xquery.value.StringValue;
import org.exist.xquery.value.Type;
import org.marc4j.MarcException;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlHandler;
import org.marc4j.RecordStack;
import org.marc4j.marc.Record;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import info.freelibrary.marc4j.converter.impl.UnicodeToAnsel;

public class WriteToFile extends BasicFunction {

    private final static Logger LOGGER = Logger.getLogger(WriteToFile.class);

    private final static String FN_WRITE_LN = "write";

    /**
     * Defined functions for the <code>WriteToFile</code> class.
     */
    public final static FunctionSignature[] SIGNATURES = new FunctionSignature[] { new FunctionSignature(new QName(
            FN_WRITE_LN, MARCModule.NAMESPACE_URI, MARCModule.PREFIX),
            "Writes a MARCXML node-set into a MARC file on the file system. "
                    + "This method is only available to the DBA role.", new SequenceType[] {
                new FunctionParameterSequenceType("node-set", Type.NODE, Cardinality.ZERO_OR_MORE,
                        "A MARCXML node-set to write to the file system as a MARC record."),
                new FunctionParameterSequenceType("path", Type.ITEM, Cardinality.EXACTLY_ONE,
                        "The full file system path or URI to the file to be created.") },
            new FunctionReturnSequenceType(Type.BOOLEAN, Cardinality.ONE,
                    "True on success or false if the specified file can not be created or is not writable. "
                            + "An empty sequence is returned if the argument sequence is empty.")) };

    /**
     * Constructor for the <code>WriteToFile</code> class.
     *
     * @param aContext A XQuery context
     * @param aSignature A function signature
     */
    public WriteToFile(final XQueryContext aContext, final FunctionSignature aSignature) {
        super(aContext, aSignature);
    }

    /*
     * (non-Javadoc)
     * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)
     */
    @Override
    public Sequence eval(final Sequence[] aSequenceArray, final Sequence aContextSequence) throws XPathException {

        if (aSequenceArray[0].isEmpty()) {
            if (LOGGER.isEnabledFor(Level.WARN)) {
                LOGGER.debug("An empty sequence was passed into the MARC WriteToFile function");
            }

            return BooleanValue.FALSE;
        }

        // Confirm our user has DBA privileges; we don't want just anyone writing to the file system
        if (!context.getSubject().hasDbaRole()) {
            throw new XPathException(this, ErrorCodes.MARC0001, ErrorCodes.MARC0001.getDescription(),
                    new StringValue(context.getSubject().getName()));
        }

        final File file = MARCModuleHelper.getFile(aSequenceArray[1].getStringValue());

        if (file.isDirectory()) {
            throw new XPathException(this, ErrorCodes.MARC0002, ErrorCodes.MARC0002.getDescription(),
                    new StringValue(file.getAbsolutePath()));
        }

        if (file.exists()) {
            throw new XPathException(this, ErrorCodes.MARC0003, ErrorCodes.MARC0003.getDescription(),
                    new StringValue(file.getAbsolutePath()));
        }

        try {
            if (!file.createNewFile()) {
                throw new XPathException(this, ErrorCodes.MARC0004, ErrorCodes.MARC0004.getDescription(),
                        new StringValue(file.getAbsolutePath()));
            }
        } catch (final IOException details) {
            throw new XPathException(this, ErrorCodes.MARC0005, ErrorCodes.MARC0005.getDescription(),
                    new StringValue(file.getAbsolutePath()), details);
        }

        if (isCalledAs(FN_WRITE_LN)) {
            writeMARCRecords(aSequenceArray[0].iterate(), file);
        } else {
            throw new XPathException(this, "Unknown function name");
        }

        return BooleanValue.TRUE;
    }

    private void writeMARCRecords(final SequenceIterator aNodeIterator, final File aFile) throws XPathException {
        MarcWriter writer = null;

        try {
            final RecordStack recordStack = new RecordStack();
            final MarcXmlHandler marcHandler = new MarcXmlHandler(recordStack);
            final Receiver marcReceiver = new ReceiverToSAX(marcHandler);
            final Serializer serializer = context.getBroker().getSerializer();

            serializer.reset();
            serializer.setReceiver(marcReceiver);

            writer = new MarcStreamWriter(new FileOutputStream(aFile));
            writer.setConverter(new UnicodeToAnsel());

            LOGGER.debug("Parsing MARCXML records...");

            while (aNodeIterator.hasNext()) {
                final NodeValue next = (NodeValue) aNodeIterator.nextItem();
                final Node node = next.getNode();

                // Check whether a document or element is being passed in
                if (node.getNodeName().equals("#document")) {
                    final String childName = node.getFirstChild().getLocalName();

                    if (!childName.equals("record")) {
                        throw new XPathException(this, ErrorCodes.MARC0006, ErrorCodes.MARC0006.getDescription(),
                                new StringValue(childName));
                    }
                } else if (!node.getLocalName().equals("record")) {
                    throw new XPathException(this, ErrorCodes.MARC0006, ErrorCodes.MARC0006.getDescription(),
                            new StringValue(node.getLocalName()));
                }

                try {
                    serializer.toSAX(next);
                } catch (final MarcException details) {
                    throw new XPathException(ErrorCodes.MARC0007, ErrorCodes.MARC0007.getDescription(),
                            new StringValue(details.getMessage()), details);
                }

                if (recordStack.hasNext()) {
                    final Record record = recordStack.pop();
                    String recordId = record.getControlNumber();

                    try {
                        if (LOGGER.isDebugEnabled() && recordId != null) {
                            LOGGER.debug("Writing MARC record: " + recordId.trim());
                        }

                        writer.write(record);
                    } catch (final MarcException details) {
                        if (recordId != null) {
                            recordId = recordId.trim();
                        } else {
                            recordId = "UNKNOWN RECORD ID";
                        }

                        throw new XPathException(this, ErrorCodes.MARC0007, ErrorCodes.MARC0007.getDescription(),
                                new StringValue(recordId), details);
                    }
                } else {
                    throw new XPathException(this, ErrorCodes.MARC0008, ErrorCodes.MARC0008.getDescription());
                }
            }
        } catch (final SAXException details) {
            throw new XPathException(ErrorCodes.MARC0008, ErrorCodes.MARC0008.getDescription(), details);
        } catch (final IOException details) {
            throw new XPathException(ErrorCodes.MARC0008, ErrorCodes.MARC0008.getDescription(), details);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}
