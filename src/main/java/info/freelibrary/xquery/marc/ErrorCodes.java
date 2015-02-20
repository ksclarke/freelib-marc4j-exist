
package info.freelibrary.xquery.marc;

import org.exist.dom.QName;
import org.exist.xquery.ErrorCodes.ErrorCode;

public class ErrorCodes {

    public static ErrorCode MARC0001 = new MARCErrorCode("MARC0001", "User does not have DBA privileges");

    public static ErrorCode MARC0002 = new MARCErrorCode("MARC0002",
            "Cannot write MARC file because supplied path is a directory");

    public static ErrorCode MARC0003 = new MARCErrorCode("MARC0003",
            "Cannot write MARC file because the file already exists");

    public static ErrorCode MARC0004 = new MARCErrorCode("MARC0004", "Cannot write to supplied file path");

    public static ErrorCode MARC0005 = new MARCErrorCode("MARC0005", "Error writing to supplied file path");

    public static ErrorCode MARC0006 = new MARCErrorCode("MARC0006", "Expected MARC 'record' but found another node");

    public static ErrorCode MARC0007 = new MARCErrorCode("MARC0007", "MARC record exception");

    public static ErrorCode MARC0008 = new MARCErrorCode("MARC0008", "SAX parsing did not produce a MARC record");

    public static ErrorCode MARC0009 = new MARCErrorCode("MARC0009", "SAXException while writing the MARC record");

    public static ErrorCode MARC0010 = new MARCErrorCode("MARC0010", "IOException while writing the MARC record");

    public static ErrorCode MARC0011 = new MARCErrorCode("MARC0011", "Supplied MARC URI is not a valid identifier");

    public static ErrorCode MARC0012 = new MARCErrorCode("MARC0012", "IOException while reading the MARC record");

    public static class MARCErrorCode extends ErrorCode {

        private MARCErrorCode(final String aCode, final String aMessage) {
            super(new QName(aCode, MARCModule.NAMESPACE_URI, MARCModule.PREFIX), aMessage);
        }

    }
}
