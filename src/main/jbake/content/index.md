title=Welcome to the FreeLib-MARC4J-eXist Project
date=2015-06-18
type=page
status=published
~~~~~~

FreeLib-MARC4J-eXist is an extension library for [eXist-db](http://exist-db.org/) (a native XML database). It allows the reading and writing of MARC records into and out from the database. It's packaged as a XAR file (which can be used to install the freelib-marc4j-exist code into an eXist database).  You can choose to build the XAR file yourself or install a previously built file from the project's GitHub [releases](https://github.com/ksclarke/freelib-marc4j-exist/releases) page.

Once you've built (or downloaded a pre-built version of) the XAR file and installed it, check out some examples of how to use FreeLib-MARC4J-eXist on the [Introduction](introduction.html) page.

<script>
xmlhttp=new XMLHttpRequest();
xmlhttp.open("GET", "http://freelibrary.info/mvnlookup.php?project=freelib-marc4j-exist", false);
xmlhttp.send();
$version = xmlhttp.responseText;
</script>

## Building the Project

The project is a Java project and is built using Maven.  You will need a [JDK](http://openjdk.java.net/) (version 7 or greater) and [Maven](https://maven.apache.org/) (version 3 or greater) installed before you'll be able to build the project.  To build the project, check it out from GitHub and run Maven from within the project directory:

    git clone https://github.com/ksclarke/freelib-marc4j-exist.git
    cd freelib-marc4j-exist
    mvn install

<br/>After that, you should be able to find the newly built XAR file -- <code>freelib-marc4j-exist-<script>document.write($version);</script><noscript>${version}</noscript>.xar</code> -- in the project's <code>target</code> directory.

## Installing the XAR

The eXist database comes with a very nice Web-based administrative interface.  It makes installing XAR files really easy.  To begin, visit the database's administrative dashboard and select the 'Package Manager'.

![The eXist-db dashboard](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-dashboard.png)

To install a new XAR, select the icon in the upper left with a plus sign on it.  When you do, you'll be presented with a dialog that will allow you to upload a XAR file into the database.  Once you've clicked the 'Upload' button, you can navigate to the XAR file on your local file system.

![Uploading a XAR file](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-xar-upload.png)

After the XAR file has been successfully uploaded, you'll then see the 'MAchine Readable Cataloging Library' as one of the installed libraries in your eXist-db dashboard.

![The installed MARC library](https://raw.githubusercontent.com/ksclarke/freelib-marc4j-exist/master/src/site/images/exist-installed-xar.png)

Now, you are ready to read and write some MARC records!

## Potential Gotchas

<br/>There are two known gotchas at this time.

The first is that FreeLib-MARC4J-eXist won't build against the bleeding edge code from the eXist-db GitHub repository.  It must be built against the latest stable version of eXist-db.  

The second is that the project will not yet build on Windows.  If you're using Windows, you can just grab a pre-built XAR file from the [releases](https://github.com/ksclarke/freelib-marc4j-exist/releases) page.  I'll add support for building on Windows in the near future.

## License

<br/>[GNU Lesser General Public License, Version 3.0 (or any later version)](LICENSE.txt)

## Contact

<br/>If you have questions about [freelib-marc4j-exist](http://github.com/ksclarke/freelib-marc4j-exist) feel free to ask them on the FreeLibrary Projects [mailing list](https://groups.google.com/forum/#!forum/freelibrary-projects); or, if you encounter a problem, please feel free to [open an issue](https://github.com/ksclarke/freelib-marc4j-exist/issues "GitHub Issue Queue") in the project's issue queue.