tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()


def occurrences(haystack, needle) {
    return haystack.split(needle, -1).length - 1
}
assert occurrences(tree.text, 'com.google.guava:guava:jar:25.1-jre') == 2
assert occurrences(tree.text, 'com.google.guava:guava:jar') == 2
assert tree.text.contains('com.google.guava:guava:jar:25.1-jre (version managed from 19.0)')

assert tree.text.contains("io.dropwizard.metrics:metrics-core:jar:3.1.2 <optional> (optional managed from false)");
assert tree.text.contains("org.xerial.snappy:snappy-java:jar:1.1.2.6 (optional managed from true)");

return true