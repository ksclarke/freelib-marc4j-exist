
package info.freelibrary.xquery.marc;

import java.util.List;
import java.util.Map;

import org.exist.xquery.AbstractInternalModule;
import org.exist.xquery.FunctionDef;
import org.exist.xquery.XQueryContext;

/**
 * An extension module for reading and writing MARC (MAchine Readable Cataloging) records from and to the file system.
 *
 * @author <a href="mailto:ksclarke@gmail.com">Kevin S. Clarke</a>
 * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[], java.util.Map)
 */
public class MARCModule extends AbstractInternalModule {

    public final static String NAMESPACE_URI = "http://freelibrary.info/xquery/marc";

    public final static String PREFIX = "marc";

    public final static String INCLUSION_DATE = "2015-02-18";

    public final static String RELEASED_IN_VERSION = "eXist-2.2";

    private final static FunctionDef[] FUNCTIONS = new FunctionDef[] {
        new FunctionDef(ReadFromFile.SIGNATURES[0], ReadFromFile.class),
        new FunctionDef(ReadFromFile.SIGNATURES[1], ReadFromFile.class),
        new FunctionDef(WriteToFile.SIGNATURES[0], WriteToFile.class) };

    /**
     * Creates a new MARC module.
     *
     * @param aParamMap A map of parameters with which to initialize the <code>MARCModule</code>.
     */
    public MARCModule(final Map<String, List<? extends Object>> aParamMap) {
        super(FUNCTIONS, aParamMap);
    }

    @Override
    public String getNamespaceURI() {
        return (NAMESPACE_URI);
    }

    @Override
    public String getDefaultPrefix() {
        return (PREFIX);
    }

    @Override
    public String getDescription() {
        return ("An XQuery module for reading and writing MARC records from and to the file system.");
    }

    @Override
    public String getReleaseVersion() {
        return RELEASED_IN_VERSION;
    }

    /**
     * Resets the module context.
     *
     * @param aXQueryContext The XQueryContext
     */
    @Override
    public void reset(final XQueryContext aXQueryContext) {
        super.reset(aXQueryContext);
    }
}
