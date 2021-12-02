# Distributions

The three files `windows.xml`, `macos.xml` and `linux.xml` are
almost identical, except:

- different names, to produce differerent output packages.
- different package formats (.zip versus .tar.gz)
- the Windows distribution includes a .exe launcher
- changing permissions on Linux/MacOS but not on Windows
- deleting either the CPU or GPU version of the ONNX Runtime,
so that the GPU remains for Windows and Linux, and the CPU remains for MacOS. 

The last point is important, and will prevent several parts of the source code from succcessfully executing, if not adhered to.
