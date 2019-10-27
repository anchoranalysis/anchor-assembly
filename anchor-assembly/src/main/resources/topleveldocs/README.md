i# To run Anchor.



## 1. Ensure you have the right Java Runtime Environment (JRE) and add Anchor to Path

You need JRE version 11 or above installed. This is a fairly-recent version, and isn't installed yet on the HPC. 

* Make sure your `$JAVA_HOME` and `$PATH` environment variables point to the right version of Java by
editing your $HOME/.bash_profile

* We'll also add the Anchor binary to the path, and define `$ANCHOR_HOME`

~~~~
export JAVA_HOME=/SOME_PATH_TO_JDK
export PATH=/SOME_PATH_TO_JDK/bin:/SOME_PATH_TO_ANCHOR/bin/:$PATH
export ANCHOR_HOME=/SOME_PATH_TO_ANCHOR/
~~~~



## 2.  Create a configuration file at

* On Windows: `C:\Users\feehano\.anchor\rootPaths.xml` (replacing your username as makes sense)
* On Linux:  `$HOME/.anchor/rootPaths.xml`

*Hint*: Windows explorer doesn't like creating directories beginning with .  so a trick is to use the command-prompt and the rename command.

The file contents should be as follows, but:
1. replace the first path to wherever your pstore `ibdia` directory is mapped (e.g. M:\ibdia\).
2. replace the second path to some private directory. This is where output is created when debug-mode is activated.



e.g. on Linux

~~~~
<?xml version="1.0" encoding="ISO-8859-1" ?>
<config>
<bean config-class="java.util.List" config-factory="list">
	<item name="ibd" debug="false" path="/pstore/data/ibdia/" config-class="org.anchoranalysis.io.bean.root.RootPath"/>
	<item name="ibd" debug="true" path="/pstore/home/feehano/anchorDebug" config-class="org.anchoranalysis.io.bean.root.RootPath"/>
</bean>
</config>
~~~~



e.g. on Windows

~~~~
<?xml version="1.0" encoding="ISO-8859-1" ?>
<config>
<bean config-class="java.util.List" config-factory="list">
	<item name="ibd" debug="false" path="M:\ibdia\" config-class="org.anchoranalysis.io.bean.root.RootPath"/>
	<item name="ibd" debug="true" path="C:\Users\feehano\Data\AnchorDebug\" config-class="org.anchoranalysis.io.bean.root.RootPath"/>
</bean>
</config>
~~~~



## Installation Complete - How to use the command-line tool?

Congratulations anchor is now ready to run!

Just type

```
anchor
```

in a directory with images.


### Debug mode

To run anchor in debug mode, just add the -debug flag

```
anchor -debug <path to some experiment XML>
```

This can be used to run the experiment on a single input (rather than one or more datasets) and the output will instead be produced in your debug directory (and won't override any existing pstore results).

To **change which single input being used in debug mode**, change the *debug* attribute (and *datasetSpecific* attribute if there are multiple datasets). 

To **change which datasets are being run in normal mode**, select the appropriate subset of elements
in the *datasets* element.


## How to use the GUI?

Type `anchorGUI` in Windows or Linux (assuming Anchor is on your path)

Drag and drop (or `File -> Open File`) an appropriate XML file onto the GUI, and this will load images (or other contents).

After loading, then double-click or right-click to open a particular image.

