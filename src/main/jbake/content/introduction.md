title=Introduction
date=2015-06-18
type=page
status=published
~~~~~~

Once you have installed the project's XAR file in your eXist-db, you should be able to run some simple XQueries that will read in MARC as MARCXML.  For instance:

    xquery version "3.0";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    
    (: Read the MARC records into memory as MARCXML records :)
    let $marc := marc:read('/path/to/marc-records.mrc')
    
    (: Then write them with xmldb functions or do something else with them :)
    return $marc

<br/>If you have more records than will fit into memory, you may want to write them directly into a collection in the database, where you can do further work with them (moving them into other collections or doing further munging of them).  For instance:

    xquery version "3.0";
    
    declare namespace marcxml="http://www.loc.gov/MARC21/slim";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    
    (: Read the MARC records into the 'marc-records' collection as MARCXML records :)
    let $result := marc:store('/path/to/marc-records.mrc', 'marc-records')
    
    (: You can then query your collection and do things with the stored MARCXML records :)
    let $leaders := collection('/db/marc-records')//marcxml:leader
    return count($leaders)

<br/>You can also write out MARCXML records from your eXist database as MARC records (and then load them into your ILS if you want); for instance:

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

<br/>If you want to write out more than one record (which you probably will), you'll need to pass a sequence of MARC records into the write function (not a single MARCXML collection).  For instance:

    xquery version "3.0";
    
    declare namespace marcxml="http://www.loc.gov/MARC21/slim";
    
    import module namespace marc="http://freelibrary.info/xquery/marc";
    import module namespace util="http://exist-db.org/xquery/util";
    import module namespace file="http://exist-db.org/xquery/file";
    
    (: First, read in some MARCXML records from the file system :)
    let $record := util:parse(file:read('/path/to/marc-records.xml'))
    
    (: Then write them out as MARC records :)
    return marc:write($record//marcxml:record, '/tmp/marc-record.mrc')

<br/>If you are interested in seeing some more examples, take a look at the tests in the [src/test/xqueries](https://github.com/ksclarke/freelib-marc4j-exist/tree/master/src/test/xqueries) folder.  There are multiple tests (i.e., examples) in each XQuery file. There is also an XQuery script there that demonstrates programmatically loading a XAR file.
