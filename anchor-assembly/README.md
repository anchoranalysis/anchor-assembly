# anchor-assembly

A project to combine Anchor JARs into a distribution (.tar.gz) that 
forms a useful functioning version of system
- binaries
- documentation
etc.

After running the 'package' goal in maven e.g.
> mvn package

...the distribution can then be found in:
> target/anchor-assembly-<version-bin>

After running the 'deploy' goal in maven, the distribution is copied into the ${anchor.home.deploy} directory

## Folder structure

Assembly descriptors
> src/assembly/

Applications used to help launch anchor with the appropriate JVM
> src/main/resources/helperapps

Documents that are placed in the top-level of the distribution
> src/main/resources/topleveldocs

## Helper apps