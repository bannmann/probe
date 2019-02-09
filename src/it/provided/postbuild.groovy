tree = new File(basedir, 'probe-tree.txt')

assert tree.exists()
assert tree.text.contains('org.slf4j:slf4j-api:jar:1.5.6')

return true