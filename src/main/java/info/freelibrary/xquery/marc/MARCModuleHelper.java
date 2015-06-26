
package info.freelibrary.xquery.marc;

import java.io.File;
import java.net.URI;

import org.exist.xquery.XPathException;
import org.exist.xquery.value.StringValue;

/**
 * Helper class for MARCModule
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 */
public class MARCModuleHelper {

    private MARCModuleHelper() {
    }

    /**
     * Convert path (URL, file path) to a File object.
     *
     * @param aFileId A path to a MARC file written as an OS specific path or as a file URL
     * @return A MARC <code>File</code> object
     * @throws XPathException If the supplied file ID cannot be used
     */
    public static File getFile(final String aFileId) throws XPathException {
        File baseDir = null;

        if (aFileId.startsWith("file:")) {
            try {
                baseDir = new File(new URI(aFileId));
            } catch (final Exception details) {
                throw new XPathException(ErrorCodes.MARC0011, ErrorCodes.MARC0011.getDescription(), new StringValue(
                        aFileId), details);
            }
        } else {
            baseDir = new File(aFileId);
        }

        return baseDir;
    }

}
