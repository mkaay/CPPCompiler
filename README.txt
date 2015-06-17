test suite:

./gradlew test

llvm assembly from good sources are in ./llvm_out/

for typechecker:

single file:

./gradlew run -Pfile=<path_to_source>

ex.

./gradlew run -Pfile=src/test/resources/good/core001.cc