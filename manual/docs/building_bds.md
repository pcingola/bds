# Install

Run the `scripts/install.sh` script

# Build Jar

### Option 1: Ant

Just run the ant command:
```
ant
```

### Option 2: Maven

These are used to build the JAR file

**Build executable jar with dependencies**

Jar with dependencias in directory 'target', e.g. `target/bds-2.3-jar-with-dependencies.jar`
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

