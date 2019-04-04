tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()
assert tree.text.contains('└── com.google.guava:guava:jar:25.1-jre')

return true