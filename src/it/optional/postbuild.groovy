tree = new File(basedir, 'probe-tree.txt')
expected = new File(basedir, 'probe-tree.expected')

assert tree.exists()

// remove end of line comments
expectedTree = expected.text.replaceAll(/ +#.*/, "")

assert tree.text == expectedTree

return true