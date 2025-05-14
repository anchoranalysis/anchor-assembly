# anchor-assembly

This is part of the source-distribution for the [Anchor Image Analysis](http://www.anchoranalysis.org) software.

A distribution forms a useful functioning version of system
- binaries
- documentation
etc.

It creates an [Anchor distribution](https://www.anchoranalysis.org/developer_guide_anchor_distribution.html) by collating JARs and other files across repositories.

The source code uses [Project Lombok](https://projectlombok.org/) to reduce [boilerplate](https://en.wikipedia.org/wiki/Boilerplate_code).

Please see:

* [Javadoc](https://www.anchoranalysis.org/javadoc/) for Anchor project, as a whole.
* The repository's [developer documentation](https://www.anchoranalysis.org/developer_guide_repositories_anchor_assembly.html).
* The repository's [SonarCloud](https://sonarcloud.io/summary/overall?id=anchoranalysis_anchor-assembly).

## How to create a distribution

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


## What is Anchor?

Anchor is a platform for image analysis, developed by [Owen Feehan](http://www.owenfeehan.com) at:

* ETH Zurich
* University of Zurich
* Hoffmann la Roche
* ongoing as a open-source personal/community project

## Documentation

Please consider:

* the [user guide](https://www.anchoranalysis.org/user_guide.html)
* the [developer guide](https://www.anchoranalysis.org/developer_guide.html), especially [anchor-assembly](https://www.anchoranalysis.org/developer_guide_repositories_anchor_assembly.html) for an overview of all modules in this repository.

Before reading the source code, please:

* Understand Maven and [multi-module projects](https://www.anchoranalysis.org/developer_guide_environment_maven.html).
* Consider Anchor's [architecture](https://www.anchoranalysis.org/developer_guide_architecture_overview.html).
* Consider the role of each [module](https://www.anchoranalysis.org/developer_guide_repositories_anchor.html) in the repository.
