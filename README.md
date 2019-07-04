# Hash Rename

Rename files to their hash, optionally - compress filename, dedupe. 

### Command line
```
path                target path
-dedupe             delete duplicates by checksum, off by default
-extensions         drop extensions, off by default
-format <format>    base64, raw or base36 by default
-hash <algorithm>   digest algorithm, MD5 by default
-recursive          tree walk, off by default 
```
### Examples
```
hashrename d:\files -dedupe -recursive
hashrename -hash SHA-1 -format base64 \files \images
```
sbt assembly to build an executable JAR.
