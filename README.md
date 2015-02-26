# FreeLib-MARC4J-eXist [![Build Status](https://travis-ci.org/ksclarke/freelib-marc4j-exist.png?branch=master)](https://travis-ci.org/ksclarke/freelib-marc4j-exist)

This is an extension for [eXist-db](http://exist-db.org/) (a native XML database) that allows the reading and writing of MARC records into and out from the database. The product of the project's build is an XAR file which can be used to install the freelib-marc4j-exist code into an eXist database.  You can either build the project yourself, or install a previously built XAR file from the project's GitHub [releases](https://github.com/ksclarke/freelib-marc4j-exist/releases) page.

## Getting Started

Once you have installed the project's XAR file in your eXist-db, you should be able to run some simple XQueries that will read in MARC as MARCXML.  For instance:

    xquery version "3.0";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    
    (: Read the MARC records into memory as MARCXML records :)
    let $marc := marc:read('/path/to/marc-records.mrc')
    
    (: Then write them with xmldb functions or do something else with them :)
    return $marc

If you have more records than will fit into memory, you may want to write them directly into a collection in the database, where you can do further work with them (moving them into other collections or doing further munging of them).  For instance:

    xquery version "3.0";
    
    declare namespace marcxml="http://www.loc.gov/MARC21/slim";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    
    (: Read the MARC records into the 'marc-records' collection as MARCXML records :)
    let $result := marc:store('/path/to/marc-records.mrc', 'marc-records')
    
    (: You can then query your collection and do things with the stored MARCXML records :)
    let $leaders := collection('/db/marc-records')//marcxml:leader
    return count($leaders)

You can also write out MARCXML records from your eXist database as MARC records (and then load them into your ILS if you want); for instance:

    xquery version "3.0";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    
    let $record :=
      <record xmlns="http://www.loc.gov/MARC21/slim">
        <leader>01142cam  2200301 a 4500</leader>
        <controlfield tag="001">   92005291 </controlfield>
        <controlfield tag="003">DLC</controlfield>
        <controlfield tag="005">19930521155141.9</controlfield>
        <controlfield tag="008">920219s1993    caua   j      000 0 eng  </controlfield>
        <datafield tag="010" ind1=" " ind2=" ">
          <subfield code="a">   92005291 </subfield>
        </datafield>
        <datafield tag="020" ind1=" " ind2=" ">
          <subfield code="a">0152038655 :</subfield>
          <subfield code="c">$15.95</subfield>
        </datafield>
        <datafield tag="040" ind1=" " ind2=" ">
          <subfield code="a">DLC</subfield>
        </datafield>
        <datafield tag="050" ind1="0" ind2="0">
          <subfield code="a">PS3537.A618</subfield>
          <subfield code="b">A88 1993</subfield>
        </datafield>
        <datafield tag="100" ind1="1" ind2=" ">
          <subfield code="a">Sandburg, Carl,</subfield>
          <subfield code="d">1878-1967.</subfield>
        </datafield>
        <datafield tag="245" ind1="1" ind2="0">
          <subfield code="a">Arithmetic /</subfield>
          <subfield code="c">Carl Sandburg ; illustrated as an anamorphic adventure by Ted Rand.</subfield>
        </datafield>
        <datafield tag="250" ind1=" " ind2=" ">
          <subfield code="a">1st ed.</subfield>
        </datafield>
        <datafield tag="260" ind1=" " ind2=" ">
          <subfield code="a">San Diego :</subfield>
          <subfield code="b">Harcourt Brace Jovanovich,</subfield>
          <subfield code="c">c1993.</subfield>
        </datafield>
        <datafield tag="300" ind1=" " ind2=" ">
          <subfield code="a">1 v. (unpaged) :</subfield>
          <subfield code="b">ill. (some col.) ;</subfield>
          <subfield code="c">26 cm.</subfield>
        </datafield>
        <datafield tag="650" ind1=" " ind2="0">
          <subfield code="a">Arithmetic</subfield>
          <subfield code="x">Juvenile poetry.</subfield>
        </datafield>
        <datafield tag="650" ind1=" " ind2="0">
          <subfield code="a">Children's poetry, American.</subfield>
        </datafield>
        <datafield tag="650" ind1=" " ind2="1">
          <subfield code="a">Arithmetic</subfield>
          <subfield code="x">Poetry.</subfield>
        </datafield>
        <datafield tag="650" ind1=" " ind2="1">
          <subfield code="a">American poetry.</subfield>
        </datafield>
        <datafield tag="700" ind1="1" ind2=" ">
          <subfield code="a">Rand, Ted,</subfield>
          <subfield code="e">ill.</subfield>
        </datafield>
      </record>
    return marc:write($record, '/path/to/marc-record.mrc')

If you want to write out more than one record (which you probably will), you'll need to pass a sequence of MARC records into the write function (not a single MARCXML collection).  For instance:

    xquery version "3.0";
    
    declare namespace marcxml="http://www.loc.gov/MARC21/slim";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    import module namespace util="http://exist-db.org/xquery/util";
    import module namespace file="http://exist-db.org/xquery/file";
    
    (: First, read in some MARCXML records from the file system :)
    let $record := util:parse(file:read('/path/to/marc-records.xml'))
    
    (: Then write them out as MARC records :)
    return marc:write($record//marcxml:record, '/tmp/marc-record.mrc')

If you are interested in seeing some more examples, take a look at the tests in the [src/test/xqueries](https://github.com/ksclarke/freelib-marc4j-exist/tree/master/src/test/xqueries) folder.  There are multiple tests (i.e., examples) in each XQuery file. There is also an XQuery script there that demonstrates programmatically loading a XAR file.

## Building the Project

The project is a Java project and is built using [Maven](https://maven.apache.org/).  You will need a JDK (version 7 or greater) and Maven (version 3 or greater) installed before you'll be able to build the project.  To build the project, check it out from GitHub and run Maven from within the project directory:

    git clone https://github.com/ksclarke/freelib-marc4j-exist.git
    cd freelib-marc4j-exist
    mvn install

After you do that you should be able to find the XAR file in the project's `target` directory.  It will be named something like `freelib-marc4j-exist-${VERSION}.xar`.

## Installing the XAR

The eXist database comes with a very nice Web-based administrative interface.  It makes installing XAR files really easy.  You just need to visit the administrative dashboard and select the 'Package Manager'.

![The eXist-db dashboard](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-dashboard.png)

To install a new XAR, select the icon in the upper left with a plus sign on it.  When you do, you'll be presented with a dialog that will allow you to upload a XAR file into the database.  Once you've clicked the 'Upload' button, you can navigate to the XAR file on your local file system.

![Uploading a XAR file](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-xar-upload.png)

After the XAR file has been successfully uploaded, you'll then see the 'MAchine Readable Cataloging Library' as one of the installed libraries in your eXist-db dashboard.

![The installed MARC library](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-installed-xar.png)

Now, you are ready to read and write some MARC records!

## Potential Gotchas

There are not any known gotchas at this time, but since this is the first version of this library I'm sure there are some bugs (or at the very least some things that could be done better).  Please feel free to share your experiences using the library so that I can make it better.

**Edit:** Oh, one gotcha... the project's build won't run yet on Windows. If you're using Windows, you can just grab the XAR file from the [releases](https://github.com/ksclarke/freelib-marc4j-exist/releases) page, and I will update the build scripts so that it builds on Windows too in the near future.

## License

[GNU Lesser General Public License, Version 3.0 (or any later version)](LICENSE.txt)

## Contact

If you have questions about [freelib-marc4j-exist](http://github.com/ksclarke/freelib-marc4j-exist) feel free to ask them on the FreeLibrary Projects [mailing list](https://groups.google.com/forum/#!forum/freelibrary-projects); or, if you encounter a problem, please feel free to [open an issue](https://github.com/ksclarke/freelib-marc4j-exist/issues "GitHub Issue Queue") in the project's issue queue.
