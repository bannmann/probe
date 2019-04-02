def actual = new File(basedir, 'probe-tree.txt')
def expected = new File(basedir, 'probe-tree.expected')

assert actual.exists()
assert expected.exists()
assert actual.text == expected.text