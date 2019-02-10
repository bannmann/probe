tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()


/*
 * conflict between direct and transitive dependency (via cassandra-driver-core)
 */
//                         com.github.bannmann.maven.probe.its:conflicts:jar:0.0.1-SNAPSHOT
assert tree.text.contains('├── com.google.guava:guava:jar:26.0-jre')
//                         ├── com.datastax.cassandra:cassandra-driver-core:jar:3.2.0
assert tree.text.contains('│   ├── com.google.guava:guava:jar:26.0-jre {mediated from 19.0}')


/*
 * conflict of transitive dependency via two different paths (junit and hamcrest-library)
 */
//                         com.github.bannmann.maven.probe.its:conflicts:jar:0.0.1-SNAPSHOT
//                         ├── org.hamcrest:hamcrest-library:jar:1.3
assert tree.text.contains('│   └── org.hamcrest:hamcrest-core:jar:1.3')
//                         └── junit:junit:jar:4.10
assert tree.text.contains('    └── org.hamcrest:hamcrest-core:jar:1.3 {mediated from 1.1}')


return true