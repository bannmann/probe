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
//                         ├── junit:junit:jar:4.10
assert tree.text.contains('│   └── org.hamcrest:hamcrest-core:jar:1.1')
//                         └── org.hamcrest:hamcrest-library:jar:1.3
assert tree.text.contains('    └── org.hamcrest:hamcrest-core:jar:1.1 {mediated from 1.3}')

/*
 * netty-common is mediated (4.1.9.Final via netty-buffer wins over 4.0.44.Final via cassandra-driver-core).
 * only its scope is managed. make sure probe does not believe the version was managed, too.
 */
def nettyCommon = tree.text.findAll(".*io.netty:netty-common:jar.*")
nettyCommon.each {
    assert it.contains('version managed from') == false
}

return true