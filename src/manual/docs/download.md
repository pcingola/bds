# Download & Install
Binary distributions are available for Linux and macOS.

<p>
<a class="btn btn-large btn-primary" href="https://github.com/pcingola/bds/releases/latest/download/bds_linux.tar.gz">Download Linux</a>
<a class="btn btn-large btn-primary" href="https://github.com/pcingola/bds/releases/latest/download/bds_macos.tar.gz">Download macOS</a>
</p>
<p>
<a class="btn btn-large btn-primary" href="https://github.com/pcingola/bds">GitHub</a>
</p>

###Install
Uncompress the binary at your `HOME` directory
```
cd 
tar -xvzf path/to/bds*.tar.gz
```

**Requirements:** In order to run bds, you need `Java 12` or higher.

**Note:** `bds`'s directory `.bds` is 'hidden', since the name starts with a dot.
Add `bds`'s directory to your `PATH`, by adding the following line to your `.bashrc` or `.bash_profile`
```
export PATH=$PATH:$HOME/.bds
```

###Installing from source
The source code is available at GitHub, here we show how to compile and install.

**Requirements:** In order to compile bds, you need

- [Java](http://java.com) version 12 or higher
- [Go](http://golang.org/) version 1.16 or higher
- [Ant](http://ant.apache.org/) version 1.7 or higher
- [Maven](https://maven.apache.org/) version 3.8 or higher


The source code is available on [Github](https://github.com/pcingola/bds).
Download the project as follows:
				
				
```
# Clone repository
$ git clone https://github.com/pcingola/bds.git
```

Once you downloaded the project, you can install using the `install.sh` script:
```
# Run 'install script
cd bds
./scripts/install.sh
```
				
Then you should add `bds` to your PATH. Edit your `.bashrc` (or `.bash_profile`) and add the following lines
```
# bds
export PATH=$PATH:$HOME/.bds/
```
				
**Optional:** If you have root access, you can do the following (to get `bds` available everywhere in your system)
```
su -
cd /usr/bin/
ln -s /home/your_user/.bds/bds
```

### Build Jar: Solving missing Java libraries

These are used to build the JAR file

**Build executable jar with dependencies**

Jar with dependencies in directory 'target', e.g. `target/bds-VERSION-jar-with-dependencies.jar`
```
cd mvn
mvn clean assembly:assembly
```

**Copy depdencies**

Libraries are copied to: `target/dependency`
```
cd mvn
mvn dependency:copy-dependencies
```

### Creating manual pages

Manual pages are written in Markdown (`*.md`) and HTML is created using `mkdocs` package.
The source Markdown documents are located in `src/manual/docs/`.

To build the documentation, run:
```
./scripts/make_documentation.sh
```

###License
bds is open source.

